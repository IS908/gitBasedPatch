package com.dcits.modelbank.jgit;

import com.dcits.modelbank.MyException.GitNoChangesException;
import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.jgit.helper.PullEnum;
import com.dcits.modelbank.model.FileDiffEntry;
import com.dcits.modelbank.utils.Const;
import com.dcits.modelbank.utils.DateUtil;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created on 2017-11-06 19:50.
 *
 * @author kevin
 */
public class GitHandlerImpl extends GitHandler {
    private static final Logger logger = LoggerFactory.getLogger(GitHandlerImpl.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private BaseFilePathHandler baseFilePathHandler;

    public GitHandlerImpl(GitHelper gitHelper) {
        super(gitHelper);
    }

    /**
     * 判断是否存在该Tag
     *
     * @param tagName
     * @return
     */
    private boolean tagExists(Git git, String tagName) throws GitAPIException {
        if (Objects.equals(null, tagName) || tagName.trim().length() < 1) return false;
        List<Ref> tagList = git.tagList().call();
        for (Ref ref : tagList) {
            String tag = ref.getName().substring(10);
            if (Objects.equals(tagName.trim(), tag)) return true;
        }
        return false;
    }

    @Override
    public int commitTimeOfTag(String tag) {
        int commitTime = -1;
        try (Repository repository = gitHelper.openJGitRepository(); Git git = new Git(repository)) {
            if (!tagExists(git, tag)) return commitTime;
            List<Ref> tagList = git.tagList().call();
            for (Ref ref : tagList) {
                if (!Objects.equals(tag, ref.getName().substring(10).trim())) continue;
                // fetch all commits for this tag
                LogCommand log = git.log();
                Ref peeledRef = git.getRepository().peel(ref);
                if (peeledRef.getPeeledObjectId() != null) {
                    log.add(peeledRef.getPeeledObjectId());
                } else {
                    log.add(ref.getObjectId());
                }
                Iterable<RevCommit> logs = log.call();
                Iterator<RevCommit> revCommitIterator = logs.iterator();
                if (revCommitIterator.hasNext()) {
                    RevCommit rev = revCommitIterator.next();
                    commitTime = rev.getCommitTime();
                }
                break;
            }
        } catch (GitAPIException | MissingObjectException | IncorrectObjectTypeException e) {
            e.printStackTrace();
        }
        return commitTime;
    }

    @Override
    public BlameResult fileBlame(String commitID, String file) {
        BlameResult blame = null;
        try (Git git = gitHelper.getGitInstance();
             Repository repository = gitHelper.openJGitRepository()) {
            ObjectId commit = repository.resolve(commitID + "^{commit}");
            BlameCommand blamer = git.blame();
            blamer.setStartCommit(commit)
                    .setFilePath(file)
                    .setFollowFileRenames(true);
            blame = blamer.call();
//            blame.computeRange(0, 10);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return blame;
    }

    /**
     * @param git
     * @param beginTag
     * @param endTag
     * @return
     */
    private List<RevCommit> getLogRevCommitBetweenTag(Git git, String beginTag, String endTag) {
        int begin = this.commitTimeOfTag(beginTag);
        int end = this.commitTimeOfTag(endTag);
        logger.info("起始Tag：" + beginTag + " 时间戳：" + begin);
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(1024);
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                int commitTime = revCommit.getCommitTime();
                if (commitTime <= end && commitTime >= begin && revCommit.getParentCount() == 1) {
                    commits.add(revCommit);
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commits;
    }

    private List<RevCommit> getLogRevCommitBetweenTag(Git git, String beginTag) {
        int begin = this.commitTimeOfTag(beginTag);
        logger.info("起始Tag：" + beginTag + " 时间戳：" + begin);
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(512);
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                int commitTime = revCommit.getCommitTime();
                if (commitTime >= begin && revCommit.getParentCount() == 1) {
                    commits.add(revCommit);
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commits;
    }

    /**
     * 获取当天提交记录的 RevCommit
     *
     * @return
     */
    private List<RevCommit> getLogRevCommitToday(Git git) {
        long todayBeginTimestamp = DateUtil.getDayBeginTimestamp(new Date());
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(512);
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                if (revCommit.getCommitTime() > todayBeginTimestamp && revCommit.getParentCount() == 1) {
                    commits.add(revCommit);
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commits;
    }

    @Override
    public Iterable<RevCommit> showLogToday() {
//        getLogRevCommitToday();
        Iterable<RevCommit> logCommit = null;
        try (Git git = gitHelper.getGitInstance()) {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(128);
            logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                System.out.println(revCommit.getCommitTime());
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return logCommit;
    }

    @Override
    public boolean stash() {
        boolean res = false;
        try (Git git = gitHelper.getGitInstance()) {
            // push the changes to a new stash
            RevCommit stash = git.stashCreate().call();
            logger.info(stash.toString());
            res = true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Collection<RevCommit> stashList() {
        try (Git git = gitHelper.getGitInstance()) {
            // list the stashes
            Collection<RevCommit> stashes = git.stashList().call();
            for (RevCommit rev : stashes) {
                System.out.println("Found stash: " + rev + ": " + rev.getFullMessage());
            }
            return stashes;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObjectId unstash(int index) {
        try (Git git = gitHelper.getGitInstance()) {
            Collection<RevCommit> stashes = stashList();
            if (stashes.size() < index) {
                throw new IndexOutOfBoundsException("索引序号超出stash列表范围");
            }
            int count = 0;
            Iterator<RevCommit> iterator = stashes.iterator();
            while (iterator.hasNext() && count < index) {
                iterator.next();
            }
            ObjectId applied = git.stashApply().setStashRef(iterator.next().getName()).call();
            return applied;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FetchResult fetch() {
        FetchResult result = null;
        try (Git git = gitHelper.getGitInstance()) {
            result = git.fetch().setCheckFetchedObjects(true).call();
            System.out.println("Messages: " + result.getMessages());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public PullResult pull() {
        return pull(PullEnum.MERGE);
    }

    @Override
    public PullResult pull(PullEnum type) {
        PullResult pullResult = null;
        try (Git git = gitHelper.getGitInstance()) {
            PullCommand pull = git.pull();
            if (Objects.equals(PullEnum.REBASE, type)) pull.setRebase(true);
            pullResult = pull.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return pullResult;
    }

    @Override
    public boolean push() {
        boolean res = false;
        try (Git git = gitHelper.getGitInstance()) {

            git.lsRemote().call();
            Iterable<PushResult> results = git.push().call();
            Iterator<PushResult> iterator = results.iterator();
            while (iterator.hasNext()) {
                PushResult push = iterator.next();
                logger.info(push.toString());
            }
            res = true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String commitAndPushAllChanges(String note, boolean pushFlag) {
        try (Git git = gitHelper.getGitInstance()) {
            if (git.status().call().isClean()) {
                throw new GitNoChangesException("提交的文件内容都没有被修改，不能提交");
            }
            git.add().addFilepattern(".").call();
            SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat hm = new SimpleDateFormat("HHmm");
            git.commit()
                    .setMessage(note + "_" + ymd.format(new Date()) + "_" + hm.format(new Date()))
                    .call();
            if (pushFlag) git.push().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (GitNoChangesException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String commitAndPush(List<String> fileList, String note, boolean pushFlag) {
        try (Git git = gitHelper.getGitInstance()) {
            if (git.status().call().isClean()) {
                throw new GitNoChangesException("提交的文件内容都没有被修改，不能提交");
            }
            List<DiffEntry> diffEntries = git.diff()
                    .setPathFilter(PathFilterGroup.createFromStrings(fileList))
                    .setShowNameAndStatusOnly(true)
                    .call();
            if (diffEntries == null || diffEntries.size() == 0) {
                throw new GitNoChangesException("提交的文件内容都没有被修改，不能提交");
            }
            //被修改过的文件
            List<String> updateFiles = new ArrayList<>();
            DiffEntry.ChangeType changeType;
            for (DiffEntry entry : diffEntries) {
                changeType = entry.getChangeType();
                switch (changeType) {
                    case ADD:
                    case COPY:
                    case RENAME:
                    case MODIFY:
                        updateFiles.add(entry.getNewPath());
                        break;
                    case DELETE:
                        updateFiles.add(entry.getOldPath());
                        break;
                    default:
                        break;
                }
            }
            //1、将工作区的内容更新到暂存区
            AddCommand addCmd = git.add();
            for (String file : updateFiles) {
                addCmd.addFilepattern(file);
            }
            addCmd.call();
            //2、commit
            CommitCommand commitCmd = git.commit();
            for (String file : updateFiles) {
                commitCmd.setOnly(file);
            }
            RevCommit revCommit = commitCmd
                    .setCommitter("JGIT", "JGIT@dcits.com")
                    .setMessage(note)
                    .call();
            if (pushFlag) git.push().call();
            return revCommit.getName();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (GitNoChangesException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<DiffEntry> showBranchDiff(String fromBranch, String toBranch) {
        List<DiffEntry> list = null;
        try (Git git = gitHelper.getGitInstance();
             Repository repository = gitHelper.openJGitRepository()) {
            ObjectId previousHead = repository.resolve(Const.REFS_HEADS + fromBranch);
            ObjectId head = repository.resolve(Const.REFS_HEADS + toBranch);

            ObjectId branchFrom = repository.resolve(previousHead.getName() + "^{tree}");
            ObjectId branchTo = repository.resolve(head.getName() + "^{tree}");

            ObjectReader reader = repository.newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, branchFrom);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, branchTo);

            list = git.diff()
                    .setOldTree(oldTreeIter)
                    .setNewTree(newTreeIter)
                    .call();
            return list;
        } catch (IOException | GitAPIException e) {
            logger.error(e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, List<FileDiffEntry>> getCommitsLogByFile() {
        Map<String, List<FileDiffEntry>> files = new HashMap<>();
        try (Git git = gitHelper.getGitInstance();
             Repository repository = gitHelper.openJGitRepository()) {

            List<RevCommit> commits = this.getLogRevCommitToday(git);
            List<FileDiffEntry> fileDiffEntries = this.getFileDiffEntryByCommit(commits, repository, git);
            for (FileDiffEntry entry : fileDiffEntries) {
                String fullPath = entry.getFullPath();
                List<FileDiffEntry> diffList = files.get(fullPath);
                if (Objects.equals(null, diffList)) diffList = new ArrayList<>();
                diffList.add(entry);
                files.put(fullPath, diffList);
            }
        }
        return files;
    }

    @Override
    public Map<String, List<FileDiffEntry>> getCommitsLogByFile(String tagStart, String tagEnd) {
        Map<String, List<FileDiffEntry>> files = new HashMap<>();
        try (Repository repository = gitHelper.openJGitRepository(); Git git = new Git(repository)) {

            List<RevCommit> commits;
            if (Objects.equals(tagEnd, null)) {
                commits = this.getLogRevCommitBetweenTag(git, tagStart);
            } else {
                commits = this.getLogRevCommitBetweenTag(git, tagStart, tagEnd);
            }
            List<FileDiffEntry> fileDiffEntries = this.getFileDiffEntryByCommit(commits, repository, git);
            for (FileDiffEntry entry : fileDiffEntries) {
                String fullPath = entry.getFullPath();
                List<FileDiffEntry> diffList = files.get(fullPath);
                if (Objects.equals(null, diffList)) diffList = new ArrayList<>();
                diffList.add(entry);
                files.put(fullPath, diffList);
            }
        }
        return files;
    }

    /**
     * 按文件为单位将commit信息重新分组
     *
     * @param list
     * @param repository
     * @param git
     * @return
     */
    private List<FileDiffEntry> getFileDiffEntryByCommit(List<RevCommit> list, Repository repository, Git git) {
        List<FileDiffEntry> fileChangeLogList = new ArrayList<>();

        try {
            for (RevCommit commit : list) {
                RevCommit parentCommitId = commit.getParent(commit.getParentCount() - 1);
                ObjectId branchFrom = repository.resolve(parentCommitId.getName() + "^{tree}");
                ObjectId branchTo = repository.resolve(commit.getName() + "^{tree}");
                ObjectReader reader = repository.newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, branchFrom);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, branchTo);

                List<DiffEntry> diffs = git.diff()
                        .setOldTree(oldTreeIter)
                        .setNewTree(newTreeIter)
                        .call();
                for (DiffEntry entry : diffs) {
                    FileDiffEntry fileDiffEntry = diffEntry2FileDiffEntry(entry, commit);
                    fileChangeLogList.add(fileDiffEntry);
                }
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
        return fileChangeLogList;
    }

    /**
     * Model数据转换
     *
     * @param entry  DiffEntry实体
     * @param commit RevCommit实体
     * @return 转换后的FileDiffEntry实体
     */
    private FileDiffEntry diffEntry2FileDiffEntry(DiffEntry entry, RevCommit commit) {
        FileDiffEntry fileDiffEntry = new FileDiffEntry();

        String fullPath = entry.getPath(DiffEntry.Side.NEW);
        String fileType = baseFilePathHandler.getFileType(fullPath);
        String pkgPath = baseFilePathHandler.getPkgPath(fullPath, fileType);
        fullPath = gitHelper.getSourceDir() + fullPath;
        logger.info("文件全路径：" + fullPath);
        String moduleName = baseFilePathHandler.getModuleName(fullPath);

        fileDiffEntry.setFullPath(fullPath);
        fileDiffEntry.setPkgPath(pkgPath);
        fileDiffEntry.setModule(moduleName);
        fileDiffEntry.setType(fileType);

        fileDiffEntry.setAuthor(commit.getAuthorIdent().getName());
        fileDiffEntry.setTimestamp(sdf.format(commit.getCommitterIdent().getWhen()));
        fileDiffEntry.setDesc(commit.getFullMessage());
        fileDiffEntry.setVersion(entry.getId(DiffEntry.Side.NEW).name());
        fileDiffEntry.setChangeType(entry.getChangeType().name());
        return fileDiffEntry;
    }


    public List<List<DiffEntry>> getChangesByCommit(List<RevCommit> list, Repository repository, Git git) {
        List<List<DiffEntry>> changeList = new ArrayList<>();
        try {
            for (RevCommit commit : list) {
                RevCommit parentCommitId = commit.getParent(commit.getParentCount() - 1);
                ObjectId branchFrom = repository.resolve(parentCommitId.getName() + "^{tree}");
                ObjectId branchTo = repository.resolve(commit.getName() + "^{tree}");
                ObjectReader reader = repository.newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, branchFrom);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, branchTo);

                List<DiffEntry> diffs = git.diff()
                        .setOldTree(oldTreeIter)
                        .setNewTree(newTreeIter)
                        .call();
                changeList.add(diffs);
            }
        } catch (AmbiguousObjectException e) {
            e.printStackTrace();
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return changeList;
    }

    @Override
    public List<DiffEntry> showCommitDiff(String fromCommitId, String toCommitId) {
        // TODO: 2017/11/7 比较两个提交之间的差异
        try (Repository repository = gitHelper.openJGitRepository()) {
        }
        return null;
    }

    @Override
    public boolean rollBackPreRevision(List<DiffEntry> diffEntries, String revision, String note) {
        // TODO: 2017/11/7 回滚到一个指定版本
        if (Objects.equals(null, diffEntries)) {
            logger.info("");
            return false;
        }
        try (Git git = gitHelper.getGitInstance()) {

        }
        return false;
    }

    /**
     * @param repository
     * @param commit
     * @param path
     * @return
     * @throws IOException
     */
    private List<String> readElementsAt(Repository repository, String commit, String path) throws IOException {
        RevCommit revCommit = buildRevCommit(repository, commit);

        // and using commit's tree find the path
        RevTree tree = revCommit.getTree();
        System.out.println("Having tree: " + tree + " for commit " + commit);

        List<String> items = new ArrayList<>();

        // shortcut for root-path
        if (path.isEmpty()) {
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(false);
                treeWalk.setPostOrderTraversal(false);

                while (treeWalk.next()) {
                    items.add(treeWalk.getPathString());
                }
            }
        } else {
            // now try to find a specific file
            try (TreeWalk treeWalk = buildTreeWalk(repository, tree, path)) {
                if ((treeWalk.getFileMode(0).getBits() & FileMode.TYPE_TREE) == 0) {
                    throw new IllegalStateException("Tried to read the elements of a non-tree for commit '" + commit + "' and path '" + path + "', had filemode " + treeWalk.getFileMode(0).getBits());
                }

                try (TreeWalk dirWalk = new TreeWalk(repository)) {
                    dirWalk.addTree(treeWalk.getObjectId(0));
                    dirWalk.setRecursive(false);
                    while (dirWalk.next()) {
                        items.add(dirWalk.getPathString());
                    }
                }
            }
        }
        return items;
    }

    private RevCommit buildRevCommit(Repository repository, String commit) throws IOException {
        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            return revWalk.parseCommit(ObjectId.fromString(commit));
        }
    }

    private TreeWalk buildTreeWalk(Repository repository, RevTree tree, final String path) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(repository, path, tree);

        if (treeWalk == null) {
            throw new FileNotFoundException("Did not find expected file '" + path + "' in tree '" + tree.getName() + "'");
        }
        return treeWalk;
    }

    public void setBaseFilePathHandler(BaseFilePathHandler baseFilePathHandler) {
        this.baseFilePathHandler = baseFilePathHandler;
    }
}

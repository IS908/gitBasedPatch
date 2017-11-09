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
import org.springframework.stereotype.Service;

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
@Service("gitHandler")
public class GitHandlerImpl implements GitHandler {
    private static final Logger logger = LoggerFactory.getLogger(GitHandlerImpl.class);

    @Resource
    private GitHelper gitHelper;

    public GitHelper getGitHelper() {
        return gitHelper;
    }

    public void setGitHelper(GitHelper gitHelper) {
        this.gitHelper = gitHelper;
    }

    /**
     * 私有构造方法，进行一次初始化
     */
    private GitHandlerImpl() {
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
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (AmbiguousObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return blame;
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
            logCmd.setMaxCount(128);
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                if (revCommit.getCommitTime() > todayBeginTimestamp) {
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

    /**
     * 显示具体一天的提交日志
     *
     * @param date
     * @return
     */
    private Iterator<RevCommit> showLogByDay(Date date) {

        try (Git git = gitHelper.getGitInstance()) {

        }

        return null;
    }

    @Override
    public boolean checkoutBranch(String branch) {
        // TODO: 2017/11/8 分支切换，若本地存在该分支，直接切换；若本地没有该分支，远端有该分支，签出远程分支；若本地及远端均没有该分支，则检出新分支
        boolean res = false;
        try (Git git = gitHelper.getGitInstance()) {
            if (branchExists(git, branch)) {
                checkoutFromLocalBranch(git, branch);
            } else {

            }
            Ref ref = git.checkout().setName(branch).call();
            System.out.println(ref.getObjectId());
            res = true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 本地已有分支的切换
     *
     * @param git
     * @param branch 切换的分支名
     * @return
     */
    private boolean checkoutFromLocalBranch(Git git, String branch) {
        // TODO: 2017/11/8 本地已有分支的切换
        return false;
    }

    /**
     * 判断本地是否存在该分支
     *
     * @param git
     * @param branch
     * @return
     * @throws GitAPIException
     */
    private boolean branchExists(Git git, String branch) throws GitAPIException {
        List<Ref> call = git.branchList().call();
        for (Ref ref : call) {
            if (Objects.equals(Const.REFS_HEADS + branch, ref.getName())) return true;
        }
        return false;
    }

    /**
     * 检出一个新的分支
     *
     * @param branchName 本地新分支名
     * @param remote     跟踪远程分支名(若为null，则只在本地创建新分支)
     * @return
     */
    private boolean checkoutNewBranch(String branchName, String remote) {
        // TODO: 2017/11/7 checkout一个新分支
        try (Git git = gitHelper.getGitInstance()) {
        }
        return false;
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
        } catch (InvalidRefNameException e) {
            e.printStackTrace();
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
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
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
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
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
    public Map<String, List<DiffEntry>> test() {
        Map<String, List<DiffEntry>> files = new HashMap<>();
        try (Git git = gitHelper.getGitInstance();
             Repository repository = gitHelper.openJGitRepository()) {

            List<RevCommit> commits = this.getLogRevCommitToday(git);
            List<List<DiffEntry>> lists = this.getChangesByCommit(commits, repository, git);
            for (List<DiffEntry> list : lists) {
                for (DiffEntry entry : list) {
                    String filePath = entry.getNewPath();
                    List<DiffEntry> diffList = files.get(filePath);
                    if (Objects.equals(null, diffList)) diffList = new ArrayList<>();
                    diffList.add(entry);
                    files.put(filePath, diffList);
                }
            }
        }
        return files;
    }

    /**
     *
     * @param list
     * @param repository
     * @param git
     * @return
     */
    private List<List<FileDiffEntry>> getFileDiffEntryByCommit(List<RevCommit> list, Repository repository, Git git) {
        List<List<FileDiffEntry>> fileChangeLogList = new ArrayList<>();
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

                }
//                changeList.add(diffs);
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
        return fileChangeLogList;
    }

    private FileDiffEntry diffEntry2FileDiffEntry(DiffEntry entry, RevCommit commit) {
        FileDiffEntry fileDiffEntry = new FileDiffEntry();
        fileDiffEntry.setFilePath(entry.getNewPath());
        fileDiffEntry.setName(entry.getOldId().name());
        return null;
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

}

package com.dcits.modelbank.jgit;

import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.model.FileDiffEntry;
import com.dcits.modelbank.model.MyProperties;
import com.dcits.modelbank.utils.Const;
import com.dcits.modelbank.utils.DateUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
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

    @Resource
    private MyProperties myProperties;

    private BaseFilePathHandler baseFilePathHandler;

    public GitHandlerImpl(GitHelper gitHelper) {
        super(gitHelper);
    }

    public void setMyProperties(MyProperties myProperties) {
        this.myProperties = myProperties;
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

    /**
     * 根据TagName获取对应的版本号
     * @param tagName Tag名称
     * @return
     */
    private RevCommit getIdByTag(String tagName) {
        try (Repository repository = gitHelper.openJGitRepository(); Git git = new Git(repository)) {
//            判断是否存在该tag
            if (!tagExists(git, tagName)) return null;
            List<Ref> tagList = git.tagList().call();
            for (Ref ref : tagList) {
                if (!Objects.equals(tagName, ref.getName().substring(10).trim())) continue;
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
                    return rev;
                }
                break;
            }
        } catch (GitAPIException | MissingObjectException | IncorrectObjectTypeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int commitTimeOfTag(String tag) {
        return this.getIdByTag(tag).getCommitTime();
    }

    private List<RevCommit> getLogRevCommitByTag(Git git, String beginTag) {
        String tagStartId = this.getIdByTag(beginTag).getId().getName();
        logger.info("起始Tag：" + beginTag + " 对应版本号为：" + tagStartId);
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(myProperties.getLogCount());
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                if (Objects.equals(revCommit.getId().name(), tagStartId)) break;
                if (revCommit.getParentCount() != 1) continue;
                commits.add(revCommit);
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commits;
    }

    /**
     * 根据tagName对应的版本ID获取提交日志
     * @param git
     * @param beginTag
     * @param endTag
     * @return
     */
    private List<RevCommit> getLogRevCommitByTag(Git git, String beginTag, String endTag) {
        String tagStartId = this.getIdByTag(beginTag).getId().name();
        String tagEndId = this.getIdByTag(endTag).getId().name();
        logger.info("起始Tag：" + beginTag + " 相对应的版本号：" + tagStartId);
        logger.info("截止Tag：" + endTag + " 相对应的版本号：" + tagEndId);
        List<RevCommit> commits = new ArrayList<>();
        // tagStart 与 tagEnd 版本号一样时，说明两个 tag 之间没有改动，直接返回
        if (Objects.equals(tagStartId, tagEndId)) {
            return commits;
        }

        try {
            LogCommand logCmd = git.log();
//            logCmd.setRevFilter(RevFilter.NO_MERGES );    // tagStart、tagEnd有可能是标记在merge记录中，导致下面的比较出现问题，故排除此过滤
            logCmd.setMaxCount(myProperties.getLogCount());
            Iterable<RevCommit> logCommit = logCmd.call();
            Iterator<RevCommit> iterator = logCommit.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                if (Objects.equals(revCommit.getId().name(), tagEndId)) {
                    if (revCommit.getParentCount() == 1) commits.add(revCommit);
                    break;
                }
            }
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                if (Objects.equals(revCommit.getId().name(), tagStartId)) break;
                if (revCommit.getParentCount() != 1) continue;
                commits.add(revCommit);
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commits;
    }

    /**
     * @param git
     * @param beginTag
     * @param endTag
     * @return
     */
    @Deprecated
    private List<RevCommit> getLogRevCommitBetweenTag(Git git, String beginTag, String endTag) {
        this.getLogRevCommitByTag(git, beginTag, endTag);
        int begin = this.commitTimeOfTag(beginTag);
        int end = this.commitTimeOfTag(endTag);
        logger.info("起始Tag：" + beginTag + " 时间戳：" + begin);
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setRevFilter(RevFilter.NO_MERGES );
            logCmd.setMaxCount(myProperties.getLogCount());
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

    /**
     *
     * @param git
     * @param beginTag
     * @return
     */
    @Deprecated
    private List<RevCommit> getLogRevCommitByTags(Git git, String beginTag) {
        int begin = this.commitTimeOfTag(beginTag);
        logger.info("起始Tag：" + beginTag + " 时间戳：" + begin);
        List<RevCommit> commits = new ArrayList<>(64);
        try {
            LogCommand logCmd = git.log();
            logCmd.setMaxCount(myProperties.getLogCount());
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
            logCmd.setMaxCount(myProperties.getLogCount());
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
                commits = this.getLogRevCommitByTag(git, tagStart);
            } else {
                commits = this.getLogRevCommitByTag(git, tagStart, tagEnd);
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
                    /**
                     * fileTypeSkip() 函数进行过滤、简单修正等操作
                     */
                    if (!fileTypeSkip(fileDiffEntry)) continue;
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
     * 进行过滤、简单修正等操作
     *
     * @param fileDiffEntry
     * @return
     */
    private boolean fileTypeSkip(FileDiffEntry fileDiffEntry) {
        // TODO: 2017/12/7 进行过滤、简单修正等操作，遇到不同场景需特殊适配，不断完善
        String fileType = fileDiffEntry.getType();
        boolean flag = true;

        // 过滤掉部分不关注类型
        switch (fileType) {
            case "sql":
                flag = false;
                break;
            default:
                break;
        }

        // 过滤掉部分路径
        String fullPath = fileDiffEntry.getFullPath();
        if (fullPath.contains("/src/test/")
                || fullPath.endsWith("ModelBank" + File.separator + "SmartEnsemble")) {
            logger.info(fullPath + " 文件不进入打包目标码，忽略！");
            flag = false;
        }

        if (fullPath.contains("/modelBank-all-integration/src/main/config/")) {
            String[] strs = fullPath.split("/src/main/config/");
            fileDiffEntry.setModule(Const.CONF + strs[strs.length - 1]);
        }

        if (fullPath.contains("/modelBank-all-integration/src/main/scripts/")) {
            String[] strs = fullPath.split("/src/main/scripts/");
            fileDiffEntry.setModule(Const.BIN + strs[strs.length - 1]);
        }

        // 修正部分packagePath
        String module = fileDiffEntry.getModule();
        if (module.endsWith(".jar")) {
            fileDiffEntry.setModule(Const.LIB + module);
        }
        return flag;
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

    public void setBaseFilePathHandler(BaseFilePathHandler baseFilePathHandler) {
        this.baseFilePathHandler = baseFilePathHandler;
    }
}

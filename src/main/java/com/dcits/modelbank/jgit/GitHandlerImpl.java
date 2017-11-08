package com.dcits.modelbank.jgit;

import com.dcits.modelbank.MyException.GitNoChangesException;
import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.jgit.helper.PullEnum;
import com.dcits.modelbank.utils.Const;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
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
    public boolean checkoutBranch(String branch) {
        boolean res = false;
        try (Git git = gitHelper.getGitInstance()) {
            List<Ref> call = git.branchList().call();
            boolean flag = false;
            for (Ref ref : call) {
                if (Objects.equals(Const.REFS_HEADS + branch, ref.getName())) flag = true;
            }
            if (!flag) return res;
            Ref ref = git.checkout().setName(branch).call();
            res = true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean checkoutNewBranch(String branch, String origin) {
        // TODO: 2017/11/7 checkout一个新分支
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
        try (Git git = gitHelper.getGitInstance()) {
            Repository repository = git.getRepository();
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
    public List<DiffEntry> showCommitDiff(String fromCommitId, String toCommitId) {
        // TODO: 2017/11/7 比较两个提交之间的差异
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

package com.dcits.modelBank.jgit;

import com.dcits.modelBank.MyException.GitNoChangesException;
import com.dcits.modelBank.jgit.helper.GitHelper;
import com.dcits.modelBank.utils.Const;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 2017-11-06 19:50.
 *
 * @author kevin
 */
public class GitUtilImpl implements GitUtil {
    private static final Logger logger = LoggerFactory.getLogger(GitUtilImpl.class);

    private static GitUtilImpl instance = new GitUtilImpl();

    /**
     * 私有构造方法，进行一次初始化
     */
    private GitUtilImpl() {
    }

    @Override
    public String commitAndPushAllChanges(String note, boolean pushFlag) {
        try (Git git = new Git(GitHelper.openJGitRepository())) {
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
        try (Git git = new Git(GitHelper.openJGitRepository())) {
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
//            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<DiffEntry> showDiffFilesByBranches(String fromBranch, String toBranch) {
        List<DiffEntry> list = null;
        try (Git git = new Git(GitHelper.openJGitRepository())) {
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
    public List<DiffEntry> showDiffFilesByCommits(String fromCommitId, String toCommitId) {
        return null;
    }

    @Override
    public boolean rollBackPreRevision(List<DiffEntry> diffEntries, String revision, String note) {
        return false;
    }

    /**
     *
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

    /**
     * 获取 GitUtilImpl 实例
     *
     * @return
     * @throws IOException
     */
    public static GitUtilImpl getInstance() {
        return instance;
    }
}

package com.dcits.modelBank.jgit;

import com.dcits.modelBank.jgit.helper.GitHelper;
import com.dcits.modelBank.utils.Const;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
public class GitUtil {
    private static final Logger logger = LoggerFactory.getLogger(GitUtil.class);

    private static GitUtil instance = new GitUtil();
    private static Git git;

    static {
        File directory = new File("");// 参数为空
        try {
            String baseDir = directory.getCanonicalPath();
            System.setProperty("baseDir", baseDir);
            Repository existingRepo = new FileRepositoryBuilder()
                    .setGitDir(new File(System.getProperty(Const.BASE_DIR) + "\\" + Const.GIT))
                    .build();
            git = new Git(existingRepo);
        } catch (IOException e) {
            logger.error("error:{}", e);
        }
    }

    /**
     * 私有构造方法，进行一次初始化
     */
    private GitUtil() {
    }

    public void commitAndPush(String note, boolean pushFlag) {
        try {
            if (!git.status().call().isClean()) {
                git.add().addFilepattern(".").call();
                SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat hm = new SimpleDateFormat("HHmm");
                git.commit()
                        .setMessage(note + "_" + ymd.format(new Date()) + "_" + hm.format(new Date()))
                        .call();
                if (pushFlag) {
                    git.push().call();
                }
                logger.info("------succeed add,commit,push files . to repository at " + git.getRepository().getDirectory());
            } else {  //clean
                logger.info("\n-------code is clean------");
            }
        } catch (AbortedByHookException e) {
            e.printStackTrace();
        } catch (ConcurrentRefUpdateException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (UnmergedPathsException e) {
            e.printStackTrace();
        } catch (NoFilepatternException e) {
            e.printStackTrace();
        } catch (NoMessageException e) {
            e.printStackTrace();
        } catch (WrongRepositoryStateException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    /**
     * 比较两个分支之间的diff文件列表
     *
     * @param from 旧版分支
     * @param to   新版分支
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public List<DiffEntry> showBranchesDiffFileList(String from, String to) {
        List<DiffEntry> list = null;
        try (Git git = new Git(GitHelper.openJGitRepository())){
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve(Const.REFS_HEADS + to);
            ObjectId previousHead = repository.resolve(Const.REFS_HEADS + from);

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
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (AmbiguousObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void showDiffBySingleFile(List<DiffEntry> list) {

        try {
            Iterable<RevCommit> iterable = git.log().call();
            Iterator<RevCommit> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                RevCommit revCommit = iterator.next();
                for (DiffEntry entry : list) {
                    List<String> files = this.readElementsAt(git.getRepository(), revCommit.getName(), "");
                    for (String file : files) {
                        System.out.println(file);
                    }
                    System.out.println();
                }
//                System.out.println(revCommit.getName());
//                System.out.println(revCommit.toString());
//                System.out.println(revCommit.getCommitTime());
//                System.out.println(revCommit.getAuthorIdent());
//                System.out.println(revCommit.getCommitterIdent());
//                System.out.println(revCommit.getTree());
//                System.out.println(revCommit.getType());
//                System.out.println(revCommit.getFooterLines());
//                for (RevCommit revCommit1 : revCommit.getParents()) {
//                    System.out.println("Parents : " + revCommit1);
//                }
//                System.out.println();
            }

        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
     * 获取 GitUtil 实例
     *
     * @return
     * @throws IOException
     */
    public static GitUtil getInstance() {
        return instance;
    }
}

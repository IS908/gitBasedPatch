package com.dcits.modelbank.jgit.helper;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2017-11-07 16:11.
 *
 * @author kevin
 */
public class GitHelperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHelperTest.class);
    private GitHelper gitHelper;

    @Test
    public void openJGitRepository() {
        Repository repository = gitHelper.openJGitRepository();
        try (Git git = new Git(repository)) {
            if (git.status().call().isClean()) {
                System.out.println("clean");
            }else {
                System.out.println("not clean");
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        Repository repository1 = gitHelper.openJGitRepository();
    }
}
package com.dcits.modelBank.jgit.helper;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created on 2017-11-07 16:11.
 *
 * @author kevin
 */
public class GitHelperTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHelperTest.class);
    @Test
    public void openJGitRepository() {
        Repository repository = GitHelper.openJGitRepository();
        try (Git git = new Git(repository)) {
            if (git.status().call().isClean()) {
                System.out.println("clean");
            }else {
                System.out.println("not clean");
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        Repository repository1 = GitHelper.openJGitRepository();
    }
}
package com.dcits.modelBank.jgit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created on 2017-11-07 21:16.
 *
 * @author kevin
 */
public class GitUtilTest {
    private GitUtil gitUtil;
    @Before
    public void setUp() throws Exception {
        gitUtil = GitUtilImpl.getInstance();
    }

    @Test
    public void commitAndPushAllChanges() throws Exception {
        gitUtil.commitAndPushAllChanges("kevin", true);
    }

    @Test
    public void commitAndPush() throws Exception {
    }

    @Test
    public void showDiffFilesByBranches() throws Exception {
    }

    @Test
    public void showDiffFilesByCommits() throws Exception {
    }

    @Test
    public void rollBackPreRevision() throws Exception {
    }

}
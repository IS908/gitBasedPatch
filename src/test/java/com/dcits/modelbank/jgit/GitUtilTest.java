package com.dcits.modelbank.jgit;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void commitAndPushAllChanges() {
        gitUtil.commitAndPushAllChanges("kevin", true);
    }

    @Test
    public void commitAndPush() throws Exception {
        List<String> files = new ArrayList<>();
        files.add("src/test/java/com/dcits/modelbank/jgit/GitUtilTest.java");
        gitUtil.commitAndPush(files, "commitAndPush", true);
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
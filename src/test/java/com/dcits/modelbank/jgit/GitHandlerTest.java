package com.dcits.modelbank.jgit;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on 2017-11-07 21:16.
 *
 * @author kevin
 */
public class GitHandlerTest {
    private ApplicationContext context;
    private GitHandler gitHandler;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        gitHandler = (GitHandler) context.getBean("gitHandler");
    }

    @Test
    public void commitAndPushAllChanges() {
        gitHandler.commitAndPushAllChanges("kevin", true);
    }

    @Test
    public void commitAndPush() throws Exception {
        List<String> files = new ArrayList<>();
        files.add("src/test/java/com/dcits/modelbank/jgit/GitHandlerTest.java");
        gitHandler.commitAndPush(files, "commitAndPush", true);
    }

    @Test
    public void checkoutBranch() throws Exception {

    }

    @Test
    public void checkoutNewBranch() throws Exception {
    }

    @Test
    public void stash() throws Exception {
        boolean flag = gitHandler.stash();
        Assert.assertEquals(true, flag);
    }

    @Test
    public void stashList() throws Exception {
        Collection<RevCommit> stashes = gitHandler.stashList();
        for (RevCommit revCommit : stashes) {
            System.out.println(revCommit.getName());
            System.out.println(revCommit);
        }
    }

    @Test
    public void unstash() throws Exception {
    }

    @Test
    public void fetch() throws Exception {
    }

    @Test
    public void pull() throws Exception {
    }

    @Test
    public void pull1() throws Exception {
    }

    @Test
    public void push() throws Exception {
    }

    @Test
    public void commitAndPushAllChanges1() throws Exception {
    }

    @Test
    public void commitAndPush1() throws Exception {
    }

    @Test
    public void showBranchDiff() throws Exception {
    }

    @Test
    public void showCommitDiff() throws Exception {
    }

    @Test
    public void rollBackPreRevision1() throws Exception {
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
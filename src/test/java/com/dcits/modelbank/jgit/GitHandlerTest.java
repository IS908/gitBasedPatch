package com.dcits.modelbank.jgit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
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
    public void showDiffFilesByBranches() throws Exception {

    }

    @Test
    public void showDiffFilesByCommits() throws Exception {

    }

    @Test
    public void rollBackPreRevision() throws Exception {

    }

}
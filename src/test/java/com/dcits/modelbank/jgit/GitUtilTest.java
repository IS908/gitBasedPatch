package com.dcits.modelbank.jgit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017-11-07 21:16.
 *
 * @author kevin
 */
public class GitUtilTest {
    private ApplicationContext context;
    private GitUtil gitUtil;
    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        gitUtil = (GitUtil) context.getBean("gitUtil");
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
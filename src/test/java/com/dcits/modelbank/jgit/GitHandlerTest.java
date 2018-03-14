package com.dcits.modelbank.jgit;

import org.eclipse.jgit.diff.DiffEntry;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created on 2017-11-07 21:16.
 *
 * @author kevin
 */
public class GitHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(GitHandlerTest.class);
    private ApplicationContext context;
    private GitHandler gitHandler;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        gitHandler = (GitHandler) context.getBean("gitHandler");
    }

    @Test
    public void showBranchDiff() throws Exception {
        List<DiffEntry> diffs = gitHandler.showBranchDiff("master", "develop");
        for (DiffEntry entry : diffs) {
            System.out.println(entry + " ====> " + entry.getNewId());
        }
    }

    @Test
    public void showCommitDiff() throws Exception {
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

    @Test
    public void test() {
        gitHandler.getCommitsLogByFile();
    }
}
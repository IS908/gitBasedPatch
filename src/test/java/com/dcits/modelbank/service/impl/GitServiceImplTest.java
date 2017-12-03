package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.service.GitService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created on 2017-11-08 23:46.
 *
 * @author kevin
 */
public class GitServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(GitServiceImplTest.class);

    private ApplicationContext context;
    private GitService gitService;

    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        gitService = (GitService) context.getBean("gitService");
    }

    @Test
    public void genChangesFileListToday() throws Exception {
        /**
         * Main_xml
          */
        gitService.genChangesFileListToday();
    }

    @Test
    public void genChangesFileListByTags() {
        String tagStart = "SmartEnsemble_Full_20171202_04";
        String tagEnd = "SmartEnsemble_Full_20171203_01";
        gitService.genChangesFileListBetweenTag(tagStart, tagEnd);
    }

    @Test
    public void patchFileExecute() {
        /**
         * Main_zip
         */
        gitService.patchFileExecute();
    }

    @Test
    public void getCommitTimeByTag() {
        gitService.getCommitTimeByTag("SmartEnsemble_Full_20171202_04");
    }
}
package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.service.GitService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.*;

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
        gitService.genChangesFileListToday();
    }

    @Test
    public void getFileModelFromXml() {
        List<FileModel> list = gitService.getFileModelFromXml();
        logger.info("确认后的增量文件总数：" + list.size());
        for (FileModel model: list) {
            logger.info(model.toString());
        }
    }





}
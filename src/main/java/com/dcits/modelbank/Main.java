package com.dcits.modelbank;

import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import javax.xml.transform.Source;

/**
 * Created on 2017-11-15 15:34.
 *
 * @author kevin
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApplicationContext context;
    private GitService gitService;

    public Main() {
        this.context = new ClassPathXmlApplicationContext("applicationContext.xml");
        gitService = (GitService) context.getBean("gitService");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("默认操作命令如下：");
        }

        Main main = new Main();
        main.gitService.genChangesFileListToday();
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public void setGitService(GitService gitService) {
        this.gitService = gitService;
    }
}

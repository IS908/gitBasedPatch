package com.dcits.modelbank;

import com.dcits.modelbank.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created on 2017-11-15 15:34.
 *
 * @author kevin
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApplicationContext context;
    private GitService gitService;

    private Main() {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        gitService = (GitService) context.getBean("gitService");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("请输入命令参数，默认操作命令如下：");
            System.out.println("a：生成增量描述文件；");
            System.out.println("b：进行增量文件抽取；");
            return;
        }
        Main main = new Main();
        String cmd = args[0].trim();
        switch (cmd) {
            case "xml":
                main.gitService.genChangesFileListToday();
                System.out.println("增量描述文件抽取完毕！");
                break;
            case "zip":
                main.gitService.patchFileExecute();
                break;
            default:
                System.out.println("输入指令超出范围！");
                break;
        }
    }
}

package com.dcits.modelbank;

import com.dcits.modelbank.extract.PatchExtractHandler;
import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.service.GitService;
import com.dcits.modelbank.utils.XmlBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * Created on 2017-11-15 15:34.
 *
 * @author kevin
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApplicationContext context;
    private GitService gitService;

    private Main(String gitDir, String sourceDir, String targetDir, String resultDir) {
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        resultDir = resultDir.endsWith(File.separator) ? resultDir : resultDir + File.separator;
        // 设置类的初始值的设定
        GitHelper gitHelper = context.getBean(GitHelper.class);
        gitHelper.setRootDir(gitDir);
        XmlBulider xmlBulider = context.getBean(XmlBulider.class);
        xmlBulider.setXmlFilePath(resultDir);
        // 输入输出目录的设定
        PatchExtractHandler extractHandler = context.getBean(PatchExtractHandler.class);
        extractHandler.setSourceDir(sourceDir);
        extractHandler.setTargetDir(targetDir);
        extractHandler.setResultDir(resultDir);

        // 获取类实例
        gitService = context.getBean(GitService.class);
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("请指定命令参数，默认操作命令如下：[xml/zip] [gitDir] [sourceDir] [targetDir] [resultDir]");
            System.out.println("xml：生成增量描述文件/zip：进行增量文件抽取；");
            System.out.println("gitDir：项目中.git文件夹路径");
            System.out.println("sourceDir：进行增量文件抽取；");
            System.out.println("targetDir：进行增量文件抽取；");
            System.out.println("resultDir：进行增量文件抽取。");
            return;
        }
        Main main = new Main(args[1].trim(), args[2].trim(), args[3].trim(), args[4].trim());
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

package com.dcits.modelbank;

import com.dcits.modelbank.extract.BasePatchExtractHandler;
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
    private final String gitDir = ".git";
    private final String target = "target";

    private Main(String[] paths) {
        String baseDir = paths[1].trim();
        baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir + File.separator;
        String gitDir = baseDir + this.gitDir;
        String sourceDir = baseDir;
        String targetDir = baseDir + this.target;
        String resultDir = baseDir + this.target;
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        // 设置类的初始值的设定
        GitHelper gitHelper = context.getBean(GitHelper.class);
        gitHelper.setRootDir(gitDir);
        XmlBulider xmlBulider = context.getBean(XmlBulider.class);
        xmlBulider.setXmlFilePath(resultDir);
        // 输入输出目录的设定
        BasePatchExtractHandler extractHandler = context.getBean(BasePatchExtractHandler.class);
        extractHandler.setSourceDir(sourceDir);
        extractHandler.setTargetDir(targetDir);
        extractHandler.setResultDir(resultDir);

        // 获取类实例
        gitService = context.getBean(GitService.class);
    }

    public static void main(String[] args) {
        if (args.length > 4 || args.length < 3) {
            System.out.println("请指定命令参数，默认操作命令如下：[xml/zip] [gitDir] [sourceDir] [targetDir] [resultDir]");
            System.out.println("xml：生成增量描述文件/zip：进行增量文件抽取；");
            System.out.println("baseDir：抽取增量项目跟路径");
            System.out.println("beginTag：抽取增量起始Tag；");
            System.out.println("endTag：抽取增量结束Tag【该参数为空则取到当前时间的记录】");
            return;
        }
        Main main = new Main(args);
        String cmd = args[0].trim();
        String startTag = args[2].trim();
        String endTag = null;
        endTag = (args.length == 8) ? args[3].trim() : null;
        switch (cmd) {
            case "xml":
                main.gitService.genChangesFileListBetweenTag(startTag, endTag);
                System.out.println("增量描述文件抽取生成完毕！");
                break;
            case "zip":
                main.gitService.patchFileExecute();
                System.out.println("增量包生成完毕！");
                break;
            default:
                System.out.println("输入指令超出范围！");
                break;
        }
    }
}

package com.dcits.modelbank;

import com.dcits.modelbank.extract.BasePatchExtractHandler;
import com.dcits.modelbank.jgit.helper.GitHelper;
import com.dcits.modelbank.service.GitService;
import com.dcits.modelbank.service.GitServices;
import com.dcits.modelbank.service.PatchFileService;
import com.dcits.modelbank.utils.XmlBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Objects;

/**
 * Created on 2017-11-15 15:34.
 *
 * @author kevin
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ApplicationContext context;
    private GitServices gitServices;
    private PatchFileService patchFileExecute;
    private String baseDir;

    private Main(String[] paths) {
        baseDir = paths[1].trim();
        baseDir = baseDir.endsWith("/") ? baseDir : baseDir + "/";
        this.context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");

        /**
         * 一个GitHelper指定一个本地Git配置库：
         * 1、gitHelper的.git日志文件夹相对路径改为绝对路径
         * 2、gitHelper的源码跟目录相对路径转为绝对路径（相对路径@标识当前路径）
         */
        gitServices = context.getBean(GitServices.class);
        for (GitService gitService : gitServices.getGitServices()) {
            GitHelper gitHelper = gitService.getGitHandler().getGitHelper();
            gitHelper.setRootDir(baseDir + gitHelper.getRootDir());
            String sourceDir = gitHelper.getSourceDir();
            sourceDir = baseDir + (Objects.equals("@", sourceDir) ? "" : sourceDir);
            gitHelper.setSourceDir(sourceDir.endsWith("/") ?
                    sourceDir : sourceDir + "/");
            XmlBulider xmlBulider = gitService.getXmlBulider();
            xmlBulider.setXmlFilePath(baseDir + xmlBulider.getXmlFilePath());
        }

        // 输入输出目录的设定
        BasePatchExtractHandler extractHandler = context.getBean(BasePatchExtractHandler.class);
        extractHandler.setTargetDir(baseDir + extractHandler.getTargetDir());
        extractHandler.setResultDir(baseDir + extractHandler.getResultDir());

        patchFileExecute = context.getBean(PatchFileService.class);

    }

    public static void main(String[] args) {
        if (args.length > 4 || args.length < 3) {
            System.out.println("请指定命令参数，默认操作命令如下：[xml/zip] [gitDir] [sourceDir] [clazzDir] [resultDir]");
            System.out.println("xml：生成增量描述文件/zip：进行增量文件抽取；");
            System.out.println("baseDir：抽取增量项目跟路径");
            System.out.println("beginTag：抽取增量起始Tag；");
            System.out.println("endTag：抽取增量结束Tag【该参数为空则取到当前时间的记录】");
            return;
        }
        Main main = new Main(args);
        String cmd = args[0].trim();
        String startTag = args[2].trim();
        String endTag = args.length == 4 ? args[3].trim() : null;
        switch (cmd) {
            case "xml":
                main.genXmlPatchList(startTag, endTag);
                System.out.println("增量描述文件抽取生成完毕！");
                break;
            case "zip":
                main.patchFileExecute.patchFileExecute(main.baseDir);
                System.out.println("增量包生成完毕！");
                break;
            default:
                System.out.println("输入指令超出范围！");
                break;
        }
    }

    /**
     * 进行增量描述文件抽取
     *
     * @param startTag
     * @param endTag
     */
    private void genXmlPatchList(String startTag, String endTag) {
        for (GitService gitService : this.gitServices.getGitServices()) {
            gitService.genChangesFileListBetweenTag(startTag, endTag);
        }
    }

}

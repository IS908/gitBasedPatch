package com.dcits.modelbank.extract;

import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.utils.FileUtil;
import com.dcits.modelbank.utils.XmlBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 根据增量文件列表抽取增量文件
 * Created on 2017-11-10 16:07.
 *
 * @author kevin
 */
public abstract class PatchExtractHandler {
    private static final Logger logger = LoggerFactory.getLogger(PatchExtractHandler.class);

    protected String sourceDir;
    protected String targetDir;
    protected String resultDir;
    @Resource
    protected XmlBulider xmlBulider;

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir.endsWith("/") ? sourceDir : sourceDir + "/";
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public void setXmlBulider(XmlBulider xmlBulider) {
        this.xmlBulider = xmlBulider;
    }

    public void execute() {
        // 解析增量描述文件，获取增量文件列表
        List<FileModel> list = xmlBulider.getExtractFiles();
        // 获取所有要抽取的包名（增量报名）
        Set<String> set = this.getAllPackageName(list);
        logger.info("增量jar包数量：" + set.size());
        for (String pName : set) {
            logger.info(pName);
        }
        // 开始将增量

    }

    protected abstract void fileTransfer(Set<String> set);

    public Set<String> getAllPackageName(List<FileModel> list) {
        Set<String> set = new HashSet<>();
        for (FileModel file : list) {
            String filePath = this.sourceDir + file.getPath();
            if (!isFileInPackage(filePath)) continue;

            String pomPath = FileUtil.findFilePath(filePath, "pom.xml");
            if (Objects.equals(null, pomPath) || Objects.equals("", pomPath)) continue;
            String packageName = xmlBulider.pom2PackageName(pomPath);
            set.add(packageName);
        }
        return set;
    }

    // TODO: 2017/11/10 执行流程
    /**
     * 1、解析增量描述XML描述文件，过滤出开发人员确认跟进生产的增量文件列表；
     * 2、进入 sourceDir 目录下，对每一个增量文件，确定其最近的 pom.xml 文件；
     * 3、根据 pom 文件的 groupId + artifactId + version + packaging这四要素，确定该文件打包后所在的 jar 包名称；
     * 4、通过增量 jar 包列表，抽取增量 jar 包到指定临时目录下。
     */

    /**
     * 判定文件是否进入打包文件中
     *
     * @param filePath
     * @return
     */
    protected boolean isFileInPackage(String filePath) {
        return filePath.contains("/src/main");
    }
}

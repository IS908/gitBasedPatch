package com.dcits.modelbank.extract;

import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.utils.XmlBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * 根据增量文件列表抽取增量文件
 * Created on 2017-11-10 16:07.
 *
 * @author kevin
 */
public abstract class BasePatchExtractHandler {
    private static final Logger logger = LoggerFactory.getLogger(BasePatchExtractHandler.class);

    protected String targetDir;
    protected String resultDir;
    @Resource
    protected XmlBulider xmlBulider;

    public void setXmlBulider(XmlBulider xmlBulider) {
        this.xmlBulider = xmlBulider;
    }

    /**
     * 定义执行流程
     */
    public void execute() {
        // 解析增量描述文件，所有jar包
        Set<String> set = xmlBulider.getExtractFiles();
        // 开始增量文件抽取操作
        fileTransfer(set);
    }

    /**
     * 执行文件增量抽取
     *
     * @param set
     */
    protected abstract void fileTransfer(Set<String> set);

    /**
     * 获得所有包名
     * @param list
     * @return
     */
    protected abstract Set<String> getAllPackageName(List<FileModel> list);

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir.endsWith(File.separator) ?
                targetDir : (targetDir + File.separator);
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir.endsWith(File.separator) ?
                resultDir : resultDir + File.separator;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public String getResultDir() {
        return resultDir;
    }
}

package com.dcits.modelbank.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created on 2017-12-11 18:16.
 *
 * @author kevin
 */
@Component
public class MyProperties {
    /**
     * 应用名称
     */
    private String patchFolderName;
    /**
     * 生成的压缩包名称
     */
    private String patchZipName;
    /**
     * 第三方包替换列表文件存放目录
     */
    private String checkListDir;
    /**
     * 删除列表名称
     */
    private String deleteList;
    /**
     * 编译结果目标路径
     */
    protected String clazzDir;
    /**
     * 输出结果目录
     */
    protected String resultDir;
    /**
     * 结果存放目录
     */
    protected String patchFileDir;

    public String getPatchFolderName() {
        return patchFolderName;
    }

    @Value("${patch.folder.name}")
    public void setPatchFolderName(String patchFolderName) {
        this.patchFolderName = patchFolderName;
    }

    public String getPatchZipName() {
        return patchZipName;
    }

    @Value("${patch.zip.name}")
    public void setPatchZipName(String patchZipName) {
        this.patchZipName = patchZipName;
    }

    public String getCheckListDir() {
        return checkListDir;
    }

    @Value("${check.list.file.dir}")
    public void setCheckListDir(String checkListDir) {
        this.checkListDir = checkListDir;
    }

    public String getDeleteList() {
        return deleteList;
    }

    @Value("${deleteList.name}")
    public void setDeleteList(String deleteList) {
        this.deleteList = deleteList;
    }

    public String getClazzDir() {
        return clazzDir;
    }

    @Value("${maven.clazz.dir}")
    public void setClazzDir(String clazzDir) {
        this.clazzDir = clazzDir.endsWith("/") ? clazzDir : clazzDir + "/";
    }

    public String getResultDir() {
        return resultDir;
    }

    @Value("${maven.target.dir}")
    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    public String getPatchFileDir() {
        return patchFileDir;
    }

    @Value("${check.list.file.dir}")
    public void setPatchFileDir(String patchFileDir) {
        this.patchFileDir = patchFileDir;
    }
}

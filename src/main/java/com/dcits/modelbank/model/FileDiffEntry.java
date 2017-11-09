package com.dcits.modelbank.model;

/**
 * Created on 2017-11-09 19:30.
 *
 * @author kevin
 */
public class FileDiffEntry {
    /**
     * 文件在jar包中的路径
     */
    private String pkgPath;
    /**
     * 文件所属模块
     */
    private String module;
    /**
     * 文件的全路径
     */
    private String fullPath;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 修改人
     */
    private String author;
    /**
     * 修改对应的版本号
     */
    private String version;
    /**
     * 该次提交的备注信息
     */
    private String desc;
    /**
     * 对应版本提交的时间
     */
    private String timestamp;
    /**
     * 提交文件对应的修改类型
     */
    private String changeType;

    public String getPkgPath() {
        return pkgPath;
    }

    public void setPkgPath(String pkgPath) {
        this.pkgPath = pkgPath;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
}

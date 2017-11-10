package com.dcits.modelbank.extract;

/**
 * 根据增量文件列表抽取增量文件
 * Created on 2017-11-10 16:07.
 *
 * @author kevin
 */
public abstract class PatchExtractHandler {
    protected String sourceDir;
    protected String targetDir;
    protected String resultDir;

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    // TODO: 2017/11/10 执行流程
    /**
     * 1、解析增量描述XML描述文件，过滤出开发人员确认跟进生产的增量文件列表；
     * 2、进入 sourceDir 目录下，对每一个增量文件，确定其最近的 pom.xml 文件；
     * 3、根据 pom 文件的 groupId + artifactId + version + packaging这四要素，确定该文件打包后所在的 jar 包名称；
     * 4、通过增量 jar 包列表，抽取增量 jar 包到指定临时目录下。
     */


}

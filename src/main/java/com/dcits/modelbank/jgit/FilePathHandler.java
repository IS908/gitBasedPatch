package com.dcits.modelbank.jgit;

/**
 * Created on 2017-11-10 11:28.
 *
 * @author kevin
 */
public abstract class FilePathHandler {
    /**
     * 根据文件相对路径及文件类型获取文件打包后的包内路径
     *
     * @param fullPath 文件相对路径
     * @param fileType 文件类型
     * @return 该文件打包后的包内路径
     */
    public abstract String getPkgPath(String fullPath, String fileType);

    /**
     * 根据文件路径获取文件类型，即截取后缀名
     *
     * @param fullPath 文件路径
     * @return 文件类型
     */
    public abstract String getFileType(String fullPath);

    /**
     * 根据文件相对路径及文件类型获取文件所在大类
     *
     * @param fullPath 文件路径
     * @param fileType 文件类型
     * @return 文件所在模块大类
     */
    public String getModuleName(String fullPath, String fileType){
        return "Ensemble";
    }
}

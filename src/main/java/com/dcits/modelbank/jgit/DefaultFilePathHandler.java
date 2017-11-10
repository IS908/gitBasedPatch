package com.dcits.modelbank.jgit;

/**
 * Created on 2017-11-10 11:33.
 *
 * @author kevin
 */
public class DefaultFilePathHandler extends FilePathHandler {
    @Override
    public String getPkgPath(String fullPath, String fileType) {
        String pkgPath = fullPath;
        switch (fileType) {
            case "java":
                pkgPath = fullPath;
                break;
            case "xml":
                break;
            case "properties":
                break;
            case "ignore":
                break;
            case "jar":
                break;
            default:
                pkgPath = fullPath;
                break;
        }
        return pkgPath;
    }

    @Override
    public String getFileType(String filePath) {
        String[] strs = filePath.split("\\.");
        int len = strs.length;
        return len < 1 ? filePath : strs[len - 1];
    }
}

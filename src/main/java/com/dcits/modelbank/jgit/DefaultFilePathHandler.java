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
                pkgPath = this.getJavaFilePath(fullPath);
                break;
            case "xml":
                pkgPath = this.getXmlFilePath(fullPath);
                break;
            case "properties":
                pkgPath = this.getPropertyFilePath(fullPath);
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

    /**
     * @author qiqsa
     * @param fullPath
     * @return javaPath
     * @desc get java Class path
     */
    private String getJavaFilePath(String fullPath) {
        String javaPath = null;
        if(fullPath != null){
            String [] pathArray = fullPath.split("src/main/java/");
            if(pathArray != null && pathArray.length > 0){
                javaPath = pathArray[1];
            }
        }
        return javaPath;
    }

    /**
     *
     * @param fullPath
     * @return
     */
    private String getXmlFilePath(String fullPath){
        String xmlFilePath = null;
        if(fullPath != null){
            String []pathArry = null;
            if(fullPath.contains("main/resources/")){
                pathArry = fullPath.split("main/resources/");
                if(pathArry.length > 0){
                    xmlFilePath =  pathArry[1];
                }
            }else if(fullPath.contains("main/config/")){
                pathArry = fullPath.split("main/config/");
                if(pathArry.length > 0){
                    xmlFilePath =  pathArry[1];
                }
            }
        }
        return xmlFilePath;
    }

    /**
     * @author qiqsa
     * @param fullPath
     * @return properties path
     * @desc get properties file path
     */
    private String getPropertyFilePath(String fullPath){
        String propertyFilePath = null;
        if(fullPath != null){
            String []pathArray = null;
            pathArray = fullPath.split("main/resources/");
            if(pathArray.length > 0){
                propertyFilePath = pathArray[1];
            }
        }
        return propertyFilePath;
    }

    @Override
    public String getFileType(String filePath) {
        String[] strs = filePath.split("\\.");
        int len = strs.length;
        return len < 1 ? filePath : strs[len - 1];
    }


}

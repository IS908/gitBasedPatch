package com.dcits.modelbank.jgit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created on 2017-11-10 11:33.
 *
 * @author kevin
 */
public class DefaultFilePathHandler extends FilePathHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFilePathHandler.class);

    private static final String EMPTY = "";
    private static final String JAVA_SPLIT = "main/java/";
    private static final String XML_MAIN_SPLIT = "main/resources/";
    private static final String XML_MAPPER_SPLIT = "main/config/";
    private static final String PROPERTIES_MAIN_SPLIT = "main/resources/";
    private static final String RULE_SPLIT = "main/config/";
    private static final String POM="pom.xml";

    @Override
    public String getPkgPath(String fullPath, String fileType) {
        String pkgPath;
        switch (fileType) {
            case "java":        // *.java
                pkgPath = this.getJavaPkgPath(fullPath);
                break;
            case "xml":         // *.xml
                pkgPath = this.getXmlPkgPath(fullPath);
                break;
            case "properties":  // *.properties
                pkgPath = this.getPropertyPkgPath(fullPath);
                break;
            case "rule":        // *.rule
                pkgPath = this.getRulePkgPath(fullPath);
                break;
            case "groovy":      // *.groovy
                pkgPath = this.getGroovyPkgPath(fullPath);
                break;
            case "sql":
            case "sh":
            case "bat":
            case "txt":
            case "log":
            case "jar":
            case "xls":
            case "xlsx":
            case "doc":
            case "docx":
            case "ignore":
            default:
                pkgPath = fullPath;
                break;
        }
        return pkgPath;
    }

    /**
     * 获得 CheckRule 的 Groovy 文件打包后的包内路径
     * @param fullPath
     * @return
     */
    private String getGroovyPkgPath(String fullPath) {
        return this.getRulePkgPath(fullPath);
    }

    /**
     * 获得 Rule 文件打包后的包内路径
     *
     * @param fullPath 文件路径
     * @return 包内路径
     */
    private String getRulePkgPath(String fullPath) {
        if (Objects.equals(null, fullPath)) return null;
        String rulePath = EMPTY;
        String[] pathArray = fullPath.split(RULE_SPLIT);
        if (!Objects.equals(null, pathArray) && pathArray.length > 0) {
            rulePath = pathArray[pathArray.length - 1];
        }
        return rulePath;
    }

    /**
     * 获得 java 文件打包后的包内路径
     *
     * @param fullPath
     * @return javaPath
     * @author qiqsa
     * @desc get java Class path
     */
    private String getJavaPkgPath(String fullPath) {
        if (Objects.equals(null, fullPath)) return null;
        String javaPath = EMPTY;
        String[] pathArray = fullPath.split(JAVA_SPLIT);
        if (!Objects.equals(null, pathArray) && pathArray.length > 0) {
            javaPath = pathArray[pathArray.length - 1];
        }
        pathArray = javaPath.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pathArray.length - 1; ++i) {
            sb.append(pathArray[i]);
        }
        sb.append(".class");
        return sb.toString();
    }

    /**
     * 获得 XML 文件打包后的包内路径
     *
     * @param fullPath
     * @return
     */
    private String getXmlPkgPath(String fullPath) {
        if (Objects.equals(null, fullPath)) return null;
        String xmlFilePath = null;
        String[] pathArry;
        if (fullPath.contains(XML_MAIN_SPLIT)) {
            pathArry = fullPath.split(XML_MAIN_SPLIT);
            xmlFilePath = pathArry[pathArry.length - 1];
        } else if (fullPath.contains(XML_MAPPER_SPLIT)) {
            pathArry = fullPath.split(XML_MAPPER_SPLIT);
            xmlFilePath = pathArry[pathArry.length - 1];
        } else if (fullPath.endsWith(POM)) {
            // TODO: 2017/11/12 pom文件的修改，涉及版本升级依赖版本号的变更，此时不能打增量版本，需要打全量版本。
        }
        return xmlFilePath;
    }

    /**
     * 获得 properties 文件打包后的包内路径
     *
     * @param fullPath
     * @return properties path
     * @author qiqsa
     * @desc get properties file path
     */
    private String getPropertyPkgPath(String fullPath) {
        if (Objects.equals(null, fullPath)) return fullPath;
        String propertyPkgPath = null;
        if (fullPath.contains(PROPERTIES_MAIN_SPLIT)) {
            String[] pathArray = fullPath.split(PROPERTIES_MAIN_SPLIT);
            propertyPkgPath = pathArray[pathArray.length - 1];
        }
        return propertyPkgPath;
    }

    @Override
    public String getFileType(String filePath) {
        String[] strs = filePath.split("\\.");
        int len = strs.length;
        return len < 1 ? filePath : strs[len - 1];
    }


}

package com.dcits.modelbank.jgit;

import com.dcits.modelbank.utils.FileUtil;
import com.dcits.modelbank.utils.XmlBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created on 2017-11-10 11:33.
 *
 * @author kevin
 */
public class DefaultBaseFilePathHandler extends BaseFilePathHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBaseFilePathHandler.class);

    private XmlBulider xmlBulider;

    private static final String EMPTY = "";
    private static final String JSP_SPLIT = "/main/";
    private static final String JAVA_SPLIT = "main/java/";
    private static final String XML_MAIN_SPLIT = "main/resources/";
    private static final String XML_MAPPER_SPLIT = "main/config/";
    private static final String PROPERTIES_MAIN_SPLIT = "main/resources/";
    private static final String RULE_SPLIT = "main/config/";
    private static final String POM="pom.xml";

    @Override
    public String getPkgPath(String fullPath, String fileType) {
        if (fullPath.contains("/webapp/")) {
            return this.getWebAppPkgPath(fullPath);
        }
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
            case "jsp":
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

    private String getWebAppPkgPath(String fullPath) {
        if (Objects.equals(null, fullPath)) return null;
        String jspPath = EMPTY;
        String[] pathArray = fullPath.split(JSP_SPLIT);
        if (!Objects.equals(null, pathArray) && pathArray.length > 0) {
            jspPath = pathArray[pathArray.length - 1];
        }
        return jspPath;
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

    /**
     * 1、解析增量描述XML描述文件，过滤出开发人员确认跟进生产的增量文件列表；
     * 2、进入 sourceDir 目录下，对每一个增量文件，确定其最近的 pom.xml 文件；
     * 3、根据 pom 文件的 groupId + artifactId + version + packaging这四要素，确定该文件打包后所在的 jar 包名称；
     * 4、通过增量 jar 包列表，抽取增量 jar 包到指定临时目录下。
     */
    @Override
    public String getModuleName(String fullPath) {
        logger.info("FilePath：" + fullPath);
        if (!isFileInPackage(fullPath)) return "";
        String pomPath = FileUtil.findFilePath(fullPath, "pom.xml");
        if (Objects.equals(null, pomPath) || Objects.equals("", pomPath)) return "";
        return xmlBulider.pom2PackageName(pomPath);
    }

    /**
     * 判定文件打包时是否打入jar包
     *
     * @param filePath
     * @return
     */
    private boolean isFileInPackage(String filePath) {
        return filePath.contains("/src/main");
    }

    public XmlBulider getXmlBulider() {
        return xmlBulider;
    }

    public void setXmlBulider(XmlBulider xmlBulider) {
        this.xmlBulider = xmlBulider;
    }
}

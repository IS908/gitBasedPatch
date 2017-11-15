package com.dcits.modelbank.utils;

import com.dcits.modelbank.model.FileModel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.*;

/**
 * @author qiqsa
 * @date :20171107
 * @desc create xml file
 */
public class XmlBulider {
    private static final Logger logger = LoggerFactory.getLogger(XmlBulider.class);

    private String xmlFilePath;
    private String[] authorAttr;

    public XmlBulider(String xmlFilePath, String[] authorAttr) {
        this.xmlFilePath = xmlFilePath.endsWith("/") ? xmlFilePath : xmlFilePath + "/";
        this.authorAttr = authorAttr;
    }

    /**
     * 读取pom文件获得该pom文件对应的打包后的文件名
     *
     * @param pomPath pom文件路径
     * @return 打包后的包名
     */
    public String pom2PackageName(String pomPath) {
        Document document = this.xmlReader(pomPath);
        Element root = document.getRootElement();

        Element packaging = root.element("packaging");
        String pkgType = Objects.equals(null, packaging)?"jar":packaging.getText();
        Element artifactId = root.element("artifactId");
        Element version = root.element("version");

        if (Objects.equals(null, version) && !Objects.equals(null, root.element("parent"))) {
            Element parent = root.element("parent");
            version = parent.element("version");
        }

        return (artifactId.getText() + "-" + version.getText() + "." + pkgType);
    }

    /**
     * 读取增量描述文件，筛选出确认上版本的增量文件列表
     *
     * @return
     */
    public List<FileModel> getExtractFiles() {
        List<FileModel> list = new ArrayList<>();

        String fileName = xmlFilePath + "patch" + DateUtil.getRunDate() + ".xml";
        Document document = this.xmlReader(fileName);
        Element rootElement = document.getRootElement();

        Element fileElement, authorElement, checkElement;
        for (Iterator file = rootElement.elementIterator("file"); file.hasNext(); ) {
            fileElement = (Element) file.next();
            for (Iterator check = fileElement.element("authors").elementIterator("author"); check.hasNext(); ) {
                authorElement = (Element) check.next();
                checkElement = authorElement.element("check");
                if (Objects.equals("true", checkElement.getText())) {
                    FileModel model = new FileModel();
                    model.setPath(fileElement.attributeValue("fullPath"));
                    model.setModule(fileElement.attributeValue("model"));
                    model.setName(fileElement.attributeValue("pkgPath"));
                    model.setType(fileElement.attributeValue("type"));
                    list.add(model);
                    break;
                }
            }
        }

        return list;
    }

    /**
     * 将信息转换为XML
     *
     * @param list FileModel
     */
    public void entity2XmlFile(List<FileModel> list) {
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("files");
        Element descElement = rootElement.addElement("description");
        descElement.addAttribute("desc", "description");
        for (FileModel model : list) {
            Element fileElement = rootElement.addElement("file");
            fileElement.addAttribute("fullPath", model.getPath());
            fileElement.addAttribute("model", model.getModule());
            fileElement.addAttribute("pkgPath", model.getName());
            fileElement.addAttribute("type", model.getType());
            Element authors = fileElement.addElement("authors");
            for (Map<String, String> map : model.getAuthors()) {
                Element author = authors.addElement("author");
                for (String attr : authorAttr) {
                    if (Objects.equals(null, map.get(attr))) continue;
                    author.addElement(attr).setText(map.get(attr));
                }
            }
        }
        this.xmlWriter(document, true);
    }

    /**
     * Xml写入文件
     *
     * @param rootElement 带有信息XML的Document
     * @param format      格式化标志
     */
    private void xmlWriter(Document rootElement, boolean format) {
        // 输入格式化 XML
        OutputFormat formater = new OutputFormat();
        formater.setIndent(format);
        formater.setNewlines(format);
        formater.setEncoding("utf-8");

        // 生成文件路径及文件名
        String runDate = DateUtil.getRunDate();
        String fileName = xmlFilePath + "patch" + runDate + ".xml";

        // 开始写入到文件
        try (Writer fileWriter = new FileWriter(fileName)) {
            XMLWriter xmlWriter = new XMLWriter(fileWriter, formater);
            xmlWriter.write(rootElement);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取XML文件为Document
     *
     * @param filePath
     * @return
     */
    private Document xmlReader(String filePath) {
        Document document = null;
        try {
            File file = new File(filePath);
            SAXReader reader = new SAXReader();
            reader.setEncoding("utf-8");
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }


    public void setAuthorAttr(String[] authorAttr) {
        this.authorAttr = authorAttr;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }
}

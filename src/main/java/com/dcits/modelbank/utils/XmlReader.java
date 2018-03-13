package com.dcits.modelbank.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created on 2017-12-07 10:56.
 *
 * @author kevin
 */
public class XmlReader {
    private static final Logger logger = LoggerFactory.getLogger(XmlBulider.class);

    public static Set<String> getAllModuleName(Set<File> files) {
        Set<String> patchSet = new HashSet<>();
        if (Objects.equals(null, files) || files.size() < 1) return patchSet;
        Document document;
        try {
            for (File patchFile : files) {
                logger.info("开始解析：" + patchFile.getName());
                SAXReader reader = new SAXReader();
                reader.setEncoding("utf-8");
                document = reader.read(patchFile);
                Element rootElement = document.getRootElement();
                Element fileElement;
                for (Iterator file = rootElement.elementIterator("file"); file.hasNext(); ) {
                    fileElement = (Element) file.next();
                    String packageName = fileElement.attributeValue("module");
                    if (Objects.equals(null, packageName) || Objects.equals("", packageName)) continue;
                    patchSet.add(packageName);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return patchSet;
    }

    public static Map<String, List<String>> getCommitLogAndFileList(Set<File> files) throws DocumentException {
        Map<String, List<String>> commitLog = new HashMap<>();
        if (Objects.equals(null, files) || files.size() < 1) return commitLog;
        Document document;
        for (File patchFile : files) {

        }
        return null;
    }

    /**
     * 读取XML文件为Document
     *
     * @param filePath
     * @return
     */
    private static Document xmlReader(String filePath) {
        Document document = null;
        try {
            File file = new File(filePath);
            SAXReader reader = new SAXReader();
            reader.setEncoding("utf-8");
            document = reader.read(file);
            logger.info(document.toString());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }
}

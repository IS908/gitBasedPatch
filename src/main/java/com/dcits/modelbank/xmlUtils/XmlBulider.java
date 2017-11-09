package com.dcits.modelbank.xmlUtils;

import com.dcits.modelbank.model.FileModel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiqsa
 * @date :20171107
 * @desc create xml file
 */
public class XmlBulider {

    private String filePtah;

    //生成xml文件入口
    public void execute(List<FileModel> list) {
        createXmlFile(list);
    }

    private void createXmlFile(List<FileModel> list) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlVersion("1.0");

            Element root = document.createElement("files");
            document.appendChild(root);
            //
            Element authorsElement = null;
            Element fileElement = null;
            for (FileModel fm : list) {
                List<Map<String, String>> mapList = fm.getAuthors();
                //设置file标签属性
                fileElement = document.createElement("file");
                fileElement.setAttribute("name", fm.getName());
                fileElement.setAttribute("model", fm.getModule());
                fileElement.setAttribute("type", fm.getType());
                fileElement.setAttribute("path", fm.getPath());

                authorsElement = document.createElement("authors");

                //将authors标签添加在file标签内部
                fileElement.appendChild(authorsElement);

                for (Map map : mapList) {
                    Element authorElement = document.createElement("author");
                    authorsElement.appendChild(authorElement);
                    for (Object key : map.keySet()) {
                        Element propertiesElement = document.createElement(key.toString());
                        propertiesElement.setTextContent(map.get(key.toString()).toString());
                        authorElement.appendChild(propertiesElement);
                    }
                }
                root.appendChild(fileElement);
            }
            //开始把Document映射到文件
            TransformerFactory transFactory = TransformerFactory.newInstance();
            transFactory.setAttribute("indent-number", new Integer(2));
            Transformer transFormer = transFactory.newTransformer();
            transFormer.setOutputProperty(OutputKeys.INDENT, "yes");

            //设置输出结果
            DOMSource domSource = new DOMSource(document);

            //生成xml文件
            File file = new File(filePtah);

            //判断是否存在,如果不存在,则创建
            if (!file.exists()) {
                file.createNewFile();
            }
            //文件输出流
            FileOutputStream out = new FileOutputStream(file);

            //设置输入源
            StreamResult xmlResult = new StreamResult(out);

            //输出xml文件 控制xml文件格式
            // transFormer.transform(domSource, xmlResult);
            transFormer.transform(domSource, new StreamResult(new BufferedWriter(new OutputStreamWriter(out))));

        } catch (Exception e) {
            throw new RuntimeException("Throw exception when create xml file[" + e.getMessage() + "]");
        } finally {

        }
    }

    public String getFilePtah() {
        return filePtah;
    }

    public void setFilePtah(String filePtah) {
        this.filePtah = filePtah;
    }
}

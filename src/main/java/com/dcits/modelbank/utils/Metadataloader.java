package com.dcits.modelbank.utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created on 2017-11-10 17:26.
 *
 * @author kevin
 */
public class Metadataloader {
    private static final Logger logger = LoggerFactory.getLogger(Metadataloader.class);

    /**
     * 进行XML解析
     *
     * @param flag     是否递归解析XML内部元素
     * @param filePath 文件路径
     * @throws Exception
     */
    public void parseXml(boolean flag, String filePath) throws Exception {
        File file = new File(filePath);
        InputStream is = new FileInputStream(file);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);
        Element root = document.getRootElement();
        Iterator itr = root.elementIterator();
        HashMap<String, Object> hashmap = null;
        logger.info(String.valueOf(flag));
        if (flag) {
            while (itr.hasNext()) {
                Element curNode = (Element) itr.next();
                logger.info(String.valueOf(curNode.elements().size()));
                logger.info(curNode.getName());
                logger.info(curNode.getPath());
                parseXMLItem(curNode.getPath(), curNode, hashmap);
            }
        } else {
            parseAndFindXmlItem("id", root, hashmap);
        }

    }

    /**
     * 解析XML组件
     *
     * @param currPath
     * @param node
     * @param map
     */
    private void parseXMLItem(String currPath, Element node, HashMap<String, Object> map) {
        if (Objects.equals(null, map)) map = new HashMap<>();
        if (node.attributeCount() > 0) {
            for (int i = 0; i < node.attributeCount(); i++) {
                Attribute attr = node.attribute(i);
                String attrName = attr.getName();
                String attrValue = attr.getValue();
                map.put(currPath + "/@" + attrName, attrValue);
                logger.info("deubg" + currPath + "/@" + attrName + "=" +
                        map.get(currPath + "/@" + attrName));
            }
        }
        if (isOnlyNodeAndNoAttr(node)) {
            map.put(currPath + "/@" + node.getName(), node.getStringValue());
            logger.info("deubg" + currPath + "/@" + node.getName() + "=" +
                    map.get(currPath + "/@" + node.getName()));
        }
    }

    /**
     * 解析及查找XML元素
     *
     * @param nodeName
     * @param root
     * @param map
     */
    private void parseAndFindXmlItem(String nodeName, Element root, HashMap<String, Object> map) {
        Element node = root.element(nodeName);
        logger.info(node.getName());
        logger.info(node.getStringValue());
    }

    /**
     * 判断是否是末端节点
     *
     * @param node
     * @return
     */
    private boolean isOnlyNode(Element node) {
        return (node.elements() == null) || (node.elements().size() == 0);
    }

    /**
     * 判断是否是末端节点且节点属性为空
     *
     * @param node
     * @return
     */
    private boolean isOnlyNodeAndNoAttr(Element node) {
        return (isOnlyNode(node) && (node.attributeCount() == 0));
    }

    public void parseXMLValue() {

    }

//      private void _load(InputStream in, HashMap<String, IMetaData> maps)
//    throws SAXException, IOException, ParserConfigurationException
//  {
//    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
//    Element root = doc.getDocumentElement();
//    NodeList children = root.getChildNodes();
//    for (int i = 0; i < children.getLength(); i++)
//    {
//      Node child = children.item(i);
//      if (child.getNodeType() == 1)
//      {
//        String metaNodeid = child.getNodeName();
//        IMetaData metaData = _readMetaData(child);
//        maps.put(metaNodeid, metaData);
//      }
//    }
//  }

//  private IMetaData _readMetaData(Node metaDataNode)
//  {
//    IMetaData metaData = null;
//    String metadataName = metaDataNode.getNodeName();
//    Properties pro = new Properties();
//    NamedNodeMap attrs = metaDataNode.getAttributes();
//    for (int i = 0; i < attrs.getLength(); i++)
//    {
//      Node attrNode = attrs.item(i);
//      String name = attrNode.getNodeName();
//      String value = attrNode.getNodeValue();
//      pro.put(name, value);
//    }
//    metaData = new MetaDataImpl(metadataName, pro);
//    return metaData;
//  }

//    public static void main(String[] args) throws Exception {
//
//        Metadataloader loader = new Metadataloader();
//        loader.parseXml(false,EnvParameters.FILEPATH);
//
//    }
}

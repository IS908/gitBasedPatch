package com.dcits.modelbank.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;

/**
 * Created on 2017-11-10 17:49.
 *
 * @author kevin
 */
public class XMLInterpretiveParserHelper {
    private static final Logger logger = LoggerFactory.getLogger(XMLInterpretiveParserHelper.class);
    private HashMap<String, Properties> channelMap = null;
    private String encode = null;
    private String DEFAULT_ENCODING = "utf-8";
    private byte[] originaldata = null;
    private Stack<String> arrayStack = new Stack();
    private Document document = DocumentHelper.createDocument();

    public XMLInterpretiveParserHelper(String encode, byte[] bytes) {
        this.encode = encode;
        if (this.encode == null) {
            this.encode = this.DEFAULT_ENCODING;
        }
        this.originaldata = bytes;
    }

    public XMLInterpretiveParserHelper() {

        logger.info("abc");
    }

//    public HashMap<String, Object> parseXMLRoot(InputStream in, HashMap<String, Properties> channelMap)
//            throws InterpretiveException {
//        return parseXMLRoot(in, channelMap, null);
//    }

//    public HashMap<String, Object> parseXMLRoot(InputStream in,
//                                                HashMap<String, Properties> channelMap,
//                                                String prePath) {
//        this.channelMap = channelMap;
//        try {
//            SAXReader saxReader = new SAXReader();
//            Document document = saxReader.read(in);
//            Element root = document.getRootElement();
//
//
//            HashMap<String, Object> map = new HashMap();
//
//            String curPath = "/";
//            if ((prePath != null) && (prePath.trim().length() > 0)) {
//                curPath = prePath;
//            }
//            parseXMLItem(curPath, root, map);
//            return map;
//        } catch (Exception e) {
//            String msg = "解析XML报文失败";
//            logger.error(msg + e.getCause().getMessage());
//        }
//        return null;
//    }

//    private void parseXMLItem(String path, Element node, HashMap<String, Object> map) {
//        path = makePath(node, path);
//        if (node.attributeCount() > 0) {
//            for (int i = 0; i < node.attributeCount(); i++) {
//                Attribute attr = node.attribute(i);
//                String attrName = attr.getName();
//                String attrValue = attr.getValue();
//                map.put(path + "/@" + attrName, attrValue);
//            }
//        }
//        if (isLeaf(node)) {
//            Object value = node.getText();
//
//            String channelPath = path.replaceAll("\\[\\d*\\]", "");
//
//            Properties pro = (Properties) this.channelMap.get(channelPath);
////      if ((value == null) || (value.length() == 0)) {
//            if ((value == null)) {
//                return;
//            }
//            value = ParsePackTools.parsesDataWithAttr((String) value, pro, path);
//
//            map.put(path, value);
//        } else if (isArrayNode(path)) {
//            if (isStructArrayNode(path)) {
//                List brothers = node.getParent().elements(node.getName());
//                for (int i = 0; i < brothers.size(); i++) {
//                    Element brother = (Element) brothers.get(i);
//                    String p = path + "[" + i + "]";
//                    List children = brother.elements();
//                    for (int j = 0; j < children.size(); j++) {
//                        Element child = (Element) children.get(j);
//                        parseXMLItem(p, child, map);
//                    }
//                }
//            } else {
//                List brothers = node.getParent().elements(node.getName());
//                for (int i = 0; i < brothers.size(); i++) {
//                    Element brother = (Element) brothers.get(i);
//
//                    List children = brother.elements();
//                    String p = "";
//                    for (int j = 0; j < children.size(); j++) {
//                        p = path + "[" + j + "]";
//                        Element child = (Element) children.get(j);
//                        parseXMLItem(p, child, map);
//                    }
//                }
//            }
//        } else {
//            List children = node.elements();
//            for (int i = 0; i < children.size(); i++) {
//                Element child = (Element) children.get(i);
//                parseXMLItem(path, child, map);
//            }
//        }
//    }

//    private boolean isArrayNode(String path) {
//        String channelPath = path.replaceAll("\\[\\d*\\]", "");
//
//        Properties pro = (Properties) this.channelMap.get(channelPath);
//        if ((pro != null) && ("array".equalsIgnoreCase(pro.getProperty("type")))) {
//            return true;
//        }
//        pro = ParsePackTools.getAttribute(pro);
//        if ((pro != null) && ("array".equalsIgnoreCase(pro.getProperty("type")))) {
//            return true;
//        }
//        return false;
//    }

//    private boolean isStructArrayNode(String path) {
//        String channelPath = path.replaceAll("\\[\\d*\\]", "");
//
//        Properties pro = (Properties) this.channelMap.get(channelPath);
//        if ((pro != null) && (("true".equalsIgnoreCase(pro.getProperty("isStruct"))) || ("true".equalsIgnoreCase(pro.getProperty("is_struct"))))) {
//            return true;
//        }
//        pro = ParsePackTools.getAttribute(pro);
//        if ((pro != null) && (("true".equalsIgnoreCase(pro.getProperty("isStruct"))) || ("true".equalsIgnoreCase(pro.getProperty("is_struct"))))) {
//            return true;
//        }
//        return false;
//    }

    private String makePath(Element node, String path) {
        String ret = path;
        ret = ret.length() > 1 ? ret + "/" : ret;
        return ret += node.getName();
    }

//    private boolean isLeaf(Element node) {
//        return (node.elements() == null) || (node.elements().size() == 0);
//    }
//
//    public HashMap<String, Object> parseXMLRootSwitch(InputStream in, Element mdtRoot) {
//        HashMap<String, Object> map = new HashMap();
//        String currPath = "/" + mdtRoot.getName();
//        try {
//            SAXReader saxReader = new SAXReader();
//            this.document = saxReader.read(in);
//        } catch (Exception e) {
//            String msg = "解析XML报文失败";
//            ParsePackTools.setErrorCodeAndThrow("BR0048", msg, e, logger);
//        }
//        return parseXMLRootSwitch(map, mdtRoot, currPath);
//    }

//    public HashMap<String, Object> parseXMLRootSwitch(HashMap<String, Object> map, Element mdtRoot, String prePath) {
//        try {
//            String curPath = "/";
//            if ((prePath != null) && (prePath.trim().length() > 0)) {
//                curPath = prePath;
//            }
//            Iterator itr = mdtRoot.elementIterator();
//            while (itr.hasNext()) {
//                Element curNode = (Element) itr.next();
//                parseXMLSwitchItem(curPath, curNode, map, false);
//            }
//            return map;
//        } catch (Exception e) {
//            String msg = "解析XML报文失败";
//            ParsePackTools.setErrorCodeAndThrow("BR0048", msg, e, logger);
//        }
//        return null;
//    }

//    private void parseXMLSwitchItem(String path, Element node, HashMap<String, Object> map, boolean ignorePath) {
//        boolean isArray = "array".equalsIgnoreCase(node.attributeValue("type"));
//
//        String isStructArray_old = node.attributeValue("is_struct");
//        boolean isStruct;
//        if (isStructArray_old != null) {
//            isStruct = "true".equals(isStructArray_old);
//        } else {
//            isStruct = "true".equals(node.attributeValue("isStruct"));
//        }
//        boolean isSwitch = node.attributeValue("switchfield") != null;
//
//
//        String currMsgPath = "";
//        if ((isSwitch) || (ignorePath)) {
//            currMsgPath = path;
//        } else {
//            currMsgPath = makePath(node, path);
//        }
//        if (isLeaf(node)) {
//            Object value = null;
//            Properties pro = ParsePackTools.getProperties(node);
//
//            String currMsgXpath = getDocXPath(currMsgPath);
//            value = this.document.selectSingleNode(currMsgXpath) == null ? "" : this.document.selectSingleNode(currMsgXpath).getText();
//            if ((value == null)) {
//                return;
//            }
//            value = ParsePackTools.parsesDataWithAttr((String) value, pro, currMsgPath);
//            map.put(getChannelPath(node), value);
//        } else {
//            if (isSwitch) {
//                dealSwitch(currMsgPath, node, map);
//                return;
//            }
//            if (isArray) {
//                String metadataid = node.attributeValue("metadataid");
//                if (metadataid == null) {
//                    throw new InterpretiveException("metadataid 不能为空：" + node.getPath());
//                }
//                String arrayMdtPath = getChannelPath(node);
//                List list = this.document.selectNodes(getDocXPath(currMsgPath));
//
//                String arrayMsgPath = "";
//                if (isStruct) {
//                    if ((list != null) && (list.size() > 0)) {
//                        for (int i = 0; i < list.size(); i++) {
//                            arrayMsgPath = currMsgPath + "[" + i + "]";
//
//                            this.arrayStack.push(arrayMdtPath + "[" + i + "]");
//
//                            List children = node.elements();
//                            for (int j = 0; j < children.size(); j++) {
//                                Element child = (Element) children.get(j);
//                                parseXMLSwitchItem(arrayMsgPath, child, map, false);
//                            }
//                            this.arrayStack.pop();
//                        }
//                    }
//                } else {
//                    Element subNode = (Element) node.elements().get(0);
//                    String subArrayMdtPath = getChannelPath(subNode);
//                    String subCurrMsgPath = makePath(subNode, currMsgPath);
//                    List subList = this.document.selectNodes(getDocXPath(subCurrMsgPath));
//                    String subArrayMsgPath = "";
//                    if ((subList != null) && (subList.size() > 0)) {
//                        for (int m = 0; m < subList.size(); m++) {
//                            subArrayMsgPath = subCurrMsgPath + "[" + m + "]";
//
//                            this.arrayStack.push(subArrayMdtPath + "[" + m + "]");
//
//                            List subChildren = subNode.elements();
//                            for (int n = 0; n < subChildren.size(); n++) {
//                                Element subChild = (Element) subChildren.get(n);
//                                parseXMLSwitchItem(subArrayMsgPath, subChild, map, false);
//                            }
//                            this.arrayStack.pop();
//                        }
//                    }
//                }
//            } else {
//                List children = node.elements();
//                for (int i = 0; i < children.size(); i++) {
//                    Element child = (Element) children.get(i);
//                    parseXMLSwitchItem(currMsgPath, child, map, false);
//                }
//            }
//        }
//    }

//    private void dealSwitch(String path, Element node, HashMap<String, Object> map) {
//        logger.info("处理分支节点：" + node.getName());
//
//        String runNodeName = getSwitchRunNodeName(node, this.originaldata, map, this.encode);
//        logger.info("将要处理的分支为：" + runNodeName);
//        if (runNodeName == null) {
//            logger.warn("分支节点：" + node.getName() + " 未找到匹配的拆包节点。");
//            return;
//        }
//        Element child = node.element(runNodeName);
//        if (child == null) {
//            logger.warn("节点：" + runNodeName + " 未找到。");
//        } else {
//            parseXMLSwitchItem(path, child, map, true);
//        }
//    }

//    public static String getSwitchRunNodeName(Element node, byte[] msgs, HashMap<String, Object> map, String encoding) {
//        String switchField = node.attributeValue("switchfield");
//        logger.debug("处理分支节点," + node.getName() + "，分支取值路径：" + switchField);
//
//
//        Object switchValue = map.get(switchField);
//        logger.debug("在已拆包元素中查找对应路径值：" + switchValue);
//        if (switchValue == null) {
//            Element switchNode = (Element) node.selectSingleNode(switchField);
//            if (switchNode == null) {
//                String msg = "分支取值路径节点：" + switchField + " 未找到";
//                ParsePackTools.setErrorCodeAndThrow("BR0048", msg, null,
//                        logger);
//            } else {
//                String xpath = switchNode.attributeValue("xpath");
//                logger.debug("报文中根据xpath[" + xpath + "]查找对应值。");
//                if ((xpath != null) && (xpath.trim().length() > 0)) {
//                    try {
//                        String source = new String(msgs, encoding);
//                        StringReader sr = new StringReader(source);
//                        Document doc = DocumentHelper.createDocument();
//                        SAXReader reader = new SAXReader();
//                        doc = reader.read(sr);
//                        switchValue = doc.selectSingleNode(xpath);
//                    } catch (IOException e) {
//                        String msg = "报文中根据xpath[" + xpath + "查找对应值时出异常";
//                        ParsePackTools.setErrorCodeAndThrow("BR0048", msg, e,
//                                logger);
//                    } catch (DocumentException e) {
//                        String msg = "报文中根据xpath[" + xpath + "查找对应值时出异常";
//                        ParsePackTools.setErrorCodeAndThrow("BR0048", msg, e,
//                                logger);
//                    }
//                    logger.debug("在报文中查找到对应值：" + switchValue);
//                } else {
//                    String msg = "分支节点：" + switchField + " 的属性[" +
//                            xpath + "] 配置异常";
//                    ParsePackTools.setErrorCodeAndThrow("BR0048", msg, null,
//                            logger);
//                }
//            }
//        }
//        if (switchValue == null) {
//            String msg = "分支节点取值路径：" + switchField + " 对应的值未找到";
//            ParsePackTools.setErrorCodeAndThrow("BR0048", msg, null,
//                    logger);
//        } else {
//            String switchmode = node.attributeValue("switchmode");
//            if (switchmode == null) {
//                String msg = "分支节点：" + node.getName() + " 对应的属性[ " + "switchmode" + " ]未找到";
//                ParsePackTools.setErrorCodeAndThrow("BR0048", msg, null,
//                        logger);
//            } else {
//                String[] mode = switchmode.split(";");
//                String otherValue = null;
//                for (int i = 0; i < mode.length; i++) {
//                    String modeDesc = mode[i].trim();
//                    String[] items = modeDesc.split(":");
//                    String value = items[0].trim();
//                    String switchNodeName = items[1].trim();
//                    if ("other".equals(value)) {
//                        otherValue = switchNodeName;
//                    } else {
//                        if ((value.startsWith("!")) && (!switchValue.equals(value.substring(1)))) {
//                            return switchNodeName;
//                        }
//                        if (switchValue.equals(value)) {
//                            return switchNodeName;
//                        }
//                    }
//                }
//                return otherValue;
//            }
//        }
//        return null;
//    }

    private String getChannelPath(Element node) {
        String path = node.getPath();
        if (this.arrayStack.size() > 0) {
            String trimPath = (this.arrayStack.peek()).replaceAll("\\[\\d*\\]", "");
            if (path.indexOf(trimPath) != -1) {
                path = this.arrayStack.peek() + path.substring(trimPath.length());
            }
        }
        return path;
    }

    private String getDocXPath(String currPath) {
        int begin = currPath.indexOf("[");
        if (begin != -1) {
            int end = currPath.indexOf("]");
            StringBuffer sb = new StringBuffer();
            int i = Integer.parseInt(currPath.substring(begin + 1, end)) + 1;
            sb.append(currPath.substring(0, begin + 1))
                    .append(String.valueOf(i))
                    .append(currPath.substring(end, end + 1))
                    .append(getDocXPath(currPath.substring(end + 1)));
            return sb.toString();
        }
        return currPath;
    }
}

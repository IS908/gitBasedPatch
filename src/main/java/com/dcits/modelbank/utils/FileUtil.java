package com.dcits.modelbank.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;
import java.util.Set;

/**
 * Created on 2017-11-10 17:01.
 *
 * @author kevin
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 查找该文件对应的pom文件
     *
     * @param filePath     指定的文件的绝对目录
     * @param findFileName 要查找的文件名
     * @return
     */
    public static String findFilePath(String filePath, String findFileName) {
        String[] pomPath = filePath.split("src/main/");
        if (Objects.equals(null, pomPath) || pomPath.length < 2) return null;
        String pom = pomPath[0] + "pom.xml";
        File file = new File(pom);
        if (!file.exists()) return null;// TODO: 2017/11/14 文件不存在抛出异常
        return pom;
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean isFileInPackage(String filePath) {
        return filePath.contains("/src/main");
    }

    /**
     * 读取文件内容到输入流
     *
     * @param filePath
     * @return
     */
    public static InputStream readFileStream(String filePath) {
        try {
            FileInputStream fs = new FileInputStream(filePath);
            return fs;
        } catch (FileNotFoundException e) {
            if (logger.isErrorEnabled())
                logger.error(e.getCause().getMessage());
            return null;
        }
    }

    /**
     * 读取文件
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        File file = new File(filePath);

        String str = null;
        try (FileReader fReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fReader)) {
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            str = sb.toString();
        } catch (IOException e) {
            if (logger.isErrorEnabled())
                logger.error(e.getCause().getMessage());
        }
        return str;
    }

    /**
     * 写文件
     *
     * @param path
     * @param str
     * @throws IOException
     */
    public static void writeFile(String path, String str) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try (FileWriter fw = new FileWriter(file);
             PrintWriter out = new PrintWriter(fw)) {
            out.write(str);
            out.println();
        } catch (IOException e) {
            if (logger.isErrorEnabled())
                logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 根据maven编译文件目录及增量jar包清单进行文件筛选
     *
     * @param path maven编译文件目录
     * @param set  增量文件清单
     */
    public static void filterFile(String path, Set<String> set) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                logger.info(file.getName() + "文件夹是空的");
                return;
            }
            for (File file2 : files) {
                if (file2.isDirectory()) {
//                    logger.info("文件夹:" + file2.getAbsolutePath());
                    filterFile(file2.getAbsolutePath(), set);
                } else {
                    if (set.contains(file2.getName())) {
                        logger.info("抽取增量包：" + file2.getName());
                        continue;
                    }
                    file2.delete();
                }
            }
        }
    }

    public static void deleteEmptyFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                file.delete();
                return;
            }
            for (File file1: files) {

            }
        }
    }

    /**
     * 写文件
     *
     * @param path
     * @param content
     * @param encoding
     * @return
     */
    public static boolean writeFile(String path, String content, String encoding) {
        boolean result = false;
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(content.getBytes(encoding));
            output.flush();
            result = true;
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error(e.getCause().getMessage());
        }
        return result;
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static boolean createDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取文件总行数
     *
     * @param filePath
     * @return
     */
    public static int getRowCount(String filePath) {
        int count = 0;
        try (InputStream input = new FileInputStream(new File(filePath));
             BufferedReader b = new BufferedReader(new InputStreamReader(input))) {
            while (b.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            count = -1;
            if (logger.isErrorEnabled())
                logger.error(e.getCause().getMessage());
        }
        return count;
    }

    /**
     * 获取文件最后一行数据
     *
     * @param filePath 文件所在路径
     * @return
     */
    public static String getLastRow(String filePath) {
        String lastRow = null;
        try (InputStream input = new FileInputStream(new File(filePath));
             BufferedReader b = new BufferedReader(new InputStreamReader(input))) {
            while (true) {
                String str = b.readLine();
                if (Objects.equals(null, str) || Objects.equals("", str.trim())) {
                    break;
                } else lastRow = str;
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getCause().getMessage());
            }
        }
        return lastRow;
    }

}

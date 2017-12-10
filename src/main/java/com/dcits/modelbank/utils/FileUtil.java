package com.dcits.modelbank.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
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

    public static void fileReadLine(String pathFile, Set<String> lines) {
        File file = new File(pathFile);
        if (!file.exists() || !file.isFile()) return;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(pathFile)), "UTF-8"));
            String lineTxt;
            while ((lineTxt = br.readLine()) != null) {
                String[] records = lineTxt.split(",");
                for (String record : records) {
                    if (Objects.equals(null, record)) continue;
                    lines.add(record);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void writeFile(String writeFile, Set<String> content) {
        File file = new File(writeFile);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        /* 输出数据 */
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
            for (String line : content) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            logger.info(e.getMessage() + " : " + e.getCause());
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
                return;
            }
            for (File file2 : files) {
                if (file2.isDirectory()) {
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

    /**
     * 复制一个目录及其子目录到另外一个目录
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (!src.isDirectory()) return;
        if (!dest.exists()) {
            dest.mkdir();
        }
        String files[] = src.list();
        for (String file : files) {
            File srcFile = new File(src, file);
            File destFile = new File(dest, file);
            // 递归复制
            copyFolder(srcFile, destFile);
        }
    }

    /**
     * 复制给定文件列表中的文件到指定目录
     *
     * @param src
     * @param dest
     * @param set  待移动文件相对路径
     */
    public static void mvFile(String baseDir, String src, String dest, Set<String> set) {
        baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir + File.separator;
        src = baseDir + (src.endsWith(File.separator) ? src : src + File.separator);
        dest = baseDir + (dest.endsWith(File.separator) ? dest : dest + File.separator);
        logger.info("源目录：" + src + "，目标目录：" + dest);
        File srcFile;
        for (String file : set) {
            srcFile = new File(src + file);
            if (!srcFile.exists()) continue;
            logger.info("抽取文件：" + srcFile.getName());
            srcFile.renameTo(new File(dest + file));
        }
    }

    public static void mvFile(Set<File> srcFiles, String dest) {
        for (File f : srcFiles) {
            if (!f.exists()) continue;
            logger.info("抽取文件：" + f.getName());
            f.renameTo(new File(dest + f.getName()));
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

    public static Set<File> getMatchedFiles(String baseDir, Set<String> matchPatchList) {
        Set<File> fileSet = new HashSet<>();
        File filePath;
        for (String relativePath : matchPatchList) {
            if (!relativePath.contains("/")) continue;  // TODO: 2017/12/8 没有 / 分隔符，待处理
            int index = relativePath.lastIndexOf('/');
            String dir = baseDir + relativePath.substring(0, index);
            String matchPrefix = relativePath.substring(index + 1, relativePath.length() - 1);
            filePath = new File(dir);
            if (!filePath.exists()) continue;
            File[] listFile = filePath.listFiles();
            if (Objects.equals(null, listFile)) continue;
            for (File f : listFile) {
                if (Objects.equals(null, f)) continue;
                if (f.getName().contains(matchPrefix)) {
                    fileSet.add(f);
                }
            }
        }

        return fileSet;
    }

    public static void main(String[] args) {
        String str = "lib/limarket-*";
        int index = str.lastIndexOf('/');
        System.out.println(str.substring(0, index));
        System.out.println(str.substring(index + 1));
    }
}

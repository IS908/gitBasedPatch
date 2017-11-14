package com.dcits.modelbank.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * Created on 2017-11-10 17:01.
 *
 * @author kevin
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void main(String[] args) {
        String filePath = FileUtil.findFilePath("D:/fuxin/ModelBank/SmartEnsemble/Ensemble/modules/ensemble-rb/online/src/main/java/com/dcits/ensemble/rb/online/service/mbsdcore/Core10000101.java", "pom.xml");
        System.out.println(filePath);
    }

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

//    /**
//     * 获取文件偏移量与实际读取行数，缓冲区默认64K
//     *
//     * @param file 文件路径
//     * @param pos  字节偏移量
//     * @param num  记录数
//     * @return
//     */
//    private static FilePositions bufferedRandomAccessFileRowCount(File file, long pos, int num) {
//        return bufferedRandomAccessFileRowCount(file, pos, num, -1);
//    }

//    /**
//     * 获取文件偏移量与实际读取行数，自定义缓冲区大小
//     *
//     * @param file 文件
//     * @param pos  字节偏移量
//     * @param num  记录数
//     * @param size 缓冲区大小
//     * @return
//     */
//    private static FilePositions bufferedRandomAccessFileRowCount(File file, long pos, int num, int size) {
//        BufferedRandomAccessFile reader = null;
//        FilePositions res = null;
//        int count = 0;
//        try {
//            if (size < 0) {
//                reader = new BufferedRandomAccessFile(file, "r");
//            } else {
//                reader = new BufferedRandomAccessFile(file, "r", size);
//            }
//            reader.seek(pos);
//            for (int i = 0; i < num; i++) {
//                String pin = reader.readLine();
//                if (Objects.equals(null, str) || Objects.equals("", str.trim())) {
//                    break;
//                }
//                count++;
//            }
//            res = new FilePositions(reader.getFilePointer(), count);
//        } catch (Exception e) {
//            if (logger.isErrorEnabled())
//                logger.error(e.getCause().getMessage());
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                if (logger.isErrorEnabled())
//                    logger.error(e.getCause().getMessage());
//            }
//        }
//        return res;
//    }


//    /**
//     * 通过BufferedRandomAccessFile获取文件偏移量与实际读取行数，默认缓冲区64K
//     *
//     * @param file 文件
//     * @param pos  字节偏移量
//     * @param num  记录数
//     * @return
//     */
//    public static List<String> bufferedRandomAccessFileReadRows(File file, String encoding, long pos, int num) {
//        return bufferedRandomAccessFileReadRows(file, encoding, pos, num, -1);
//    }

//    /**
//     * 通过BufferedRandomAccessFile获取文件偏移量与实际读取行数，自定义缓冲区大小
//     *
//     * @param file 文件
//     * @param pos  字节偏移量
//     * @param num  记录数
//     * @param size 缓冲区大小
//     * @return
//     */
//    public static List<String> bufferedRandomAccessFileReadRows(File file, String encoding, long pos, int num, int size) {
//        List<String> pins = new ArrayList();
//        BufferedRandomAccessFile reader = null;
//        try {
//            if (size < 0) {
//                reader = new BufferedRandomAccessFile(file, "r");
//            } else {
//                reader = new BufferedRandomAccessFile(file, "r", size);
//            }
//            reader.seek(pos);
//            for (int i = 0; i < num; i++) {
//                String pin = reader.readLine();
//                if (Objects.equals(null, str) || Objects.equals("", str.trim())) {
//                    break;
//                }
//                pins.add(new String(pin.getBytes("8859_1"), encoding));
//            }
//        } catch (Exception e) {
//            if (logger.isErrorEnabled())
//                logger.error(e.getCause().getMessage());
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                if (logger.isErrorEnabled())
//                    logger.error(e.getCause().getMessage());
//            }
//        }
//        return pins;
//    }


//    /**
//     * 获取文件总行数
//     *
//     * @param filePath
//     * @return
//     */
//    public static TwoTuple<Long, List<FilePositions>> getRowCount(String filePath, int splitNum) {
//        return getRowCount(filePath, splitNum, -1);
//    }

//    /**
//     * 获取文件总行数
//     *
//     * @param filePath
//     * @return
//     */
//    public static TwoTuple<Long, List<FilePositions>> getRowCount(String filePath, int splitNum, int size) {
//        long pos = 0L;
//        long total = 0L;
//        List<FilePositions> lSpilt = new ArrayList();
//        while (true) {
//            FilePositions fPos;
//            if (size < 0)
//                fPos = bufferedRandomAccessFileRowCount(new File(filePath), pos, splitNum);
//            else
//                fPos = bufferedRandomAccessFileRowCount(new File(filePath), pos, splitNum, size);
//            int count = fPos.getNum();
//            total += count;
//            lSpilt.add(new FilePositions(pos, count));
//            if (count == 0) {
//                break;
//            }
//            if (count < splitNum) {
//                break;
//            }
//            pos = fPos.getPos();
//        }
//        return new TwoTuple<>(total, lSpilt);
//    }


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

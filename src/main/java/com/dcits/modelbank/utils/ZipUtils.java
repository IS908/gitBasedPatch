package com.dcits.modelbank.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

/**
 * Created on 2017-11-15 17:19.
 *
 * @author kevin
 */
public class ZipUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    public static void zip(String baseDir, String rootDir, String zip) {
        baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir + File.separator;
        zip(baseDir + rootDir, baseDir + zip);
    }

    public static void zip(String rootDir, String zip) {
        File rootFile = new File(rootDir);
        File zipFile = new File(zip);
        ZipUtil.pack(rootFile, zipFile);
    }
}

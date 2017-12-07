package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.service.PatchFileService;
import com.dcits.modelbank.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created on 2017-12-07 00:36.
 *
 * @author kevin
 */
public class PatchFileServiceImpl extends PatchFileService {
    private static final Logger logger = LoggerFactory.getLogger(PatchFileServiceImpl.class);

    public PatchFileServiceImpl(String patchFileDir, List<String> patchFilePrefix) {
        super(patchFileDir, patchFilePrefix);
    }

    @Override
    public void patchFileExecute(String baseDir) {
        String runDate = DateUtil.getRunDate();
        baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir + File.separator;
        File file = new File(baseDir + patchFileDir);
        if (!file.exists()) {
            logger.info(file.getAbsolutePath() + " - 目录不存在！");
            return;
        }

        Set<String> set = new HashSet<>();
        for (String prefix : patchFilePrefix) {
            set.add(prefix + runDate + ".txt");
            set.add(prefix + runDate + ".xml");
        }
        Set<File> xmlFilePaths = new HashSet<>();
        Set<File> txtFilePaths = new HashSet<>();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) continue;
            logger.info(f.getName());
            if (set.contains(f.getName()) && f.getName().endsWith(".xml")) xmlFilePaths.add(f);
            else if (set.contains(f.getName()) && f.getName().endsWith(".txt")) txtFilePaths.add(f);
        }
        Set<String> patchFileList = XmlReader.getAllModuleName(xmlFilePaths);

        try {
            for (File f : txtFilePaths) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String lineTxt;
                while ((lineTxt = br.readLine()) != null) {
                    String[] names = lineTxt.split(",");
                    for (String name : names) {
                        name = name.trim();
                        if (Objects.equals("", name)) continue;
                        patchFileList.add(name);
                    }
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tmpDir = resultDir + Const.PATCH_TMP_FOLDER;
        File insTmpDir = new File(baseDir + tmpDir);
        if (insTmpDir.exists()) {
            logger.info("delete ：" + insTmpDir.getAbsolutePath());
            insTmpDir.delete();
        }
        insTmpDir.mkdir();
        logger.info("mkdir :" + insTmpDir.getAbsolutePath());
        try {
            FileUtil.copyFolder(new File(baseDir + clazzDir), insTmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.copyFile(baseDir, clazzDir, tmpDir, patchFileList);
        ZipUtils.zip(baseDir + tmpDir, baseDir + resultDir + Const.PATCH_ZIP_NAME);

        // TODO: 2017/12/7 进行增量文件的抽取
    }
}

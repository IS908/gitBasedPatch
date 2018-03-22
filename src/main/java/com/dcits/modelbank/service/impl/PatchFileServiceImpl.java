package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.service.PatchFileService;
import com.dcits.modelbank.utils.*;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
    public void patchFileExecute(String baseDir, String fileNumber) {
        String runDate = DateUtil.getRunDate();
        File file = new File(baseDir + patchFileDir);
        if (!file.exists()) {
            logger.info(file.getAbsolutePath() + " - 目录不存在！");
            return;
        }

        Set<String> set = new HashSet<>();
        for (String prefix : patchFilePrefix) {
            set.add(runDate + prefix + ".xml");
        }

        Set<File> xmlFilePaths = new HashSet<>();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) continue;
            if (set.contains(f.getName()) && f.getName().endsWith(".xml")) xmlFilePaths.add(f);
        }
        Set<String> specificPatchSet = XmlReader.getAllModuleName(xmlFilePaths);

        Set<String> matchPatchSet = new HashSet<>(), deletePatchSet = new HashSet<>();
        String[] fileNumbers = fileNumber.split("#");
        String[] filePaths = new String[fileNumbers.length];
        for (int i = 0; i < fileNumbers.length; i++) {
            filePaths[i] = baseDir + myProperties.getCheckListDir() + "/" +
                    fileNumbers[i] + myProperties.getCheckListSurfix() + ".yml";
            yamlPaser(filePaths[i], specificPatchSet, matchPatchSet, deletePatchSet);
        }
//        String filePath = baseDir + myProperties.getCheckListDir() + "/" +
//                fileNumber + myProperties.getCheckListSurfix() + ".yml";
//        logger.info(fileNumber);
//        logger.info("yml:" + filePath);
        // 模糊匹配列表和删除列表的处理
//        yamlPaser(filePath, specificPatchSet, matchPatchSet, deletePatchSet);

        // 创建增量文件临时存放目录
        String tmpDir = myProperties.getResultDir() + "/" + Const.PATCH_TMP_FOLDER;
        File insTmpDir = new File(baseDir + tmpDir);
        File insAppDir = new File(insTmpDir + "/" + myProperties.getPatchFolderName());
        if (insTmpDir.exists()) {
            logger.info("delete ：" + insTmpDir.getAbsolutePath());
            insTmpDir.delete();
        }
        insTmpDir.mkdir();
        insAppDir.mkdir();

        // 复制部署包增量文件目录结构
        logger.info("mkdir :" + insTmpDir.getAbsolutePath());
        try {
            FileUtil.copyFolder(new File(baseDir + myProperties.getClazzDir()), insAppDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 移动具体文件的增量文件到临时存放目录
        FileUtil.mvFile(baseDir, myProperties.getClazzDir(),
                tmpDir + "/" + myProperties.getPatchFolderName(),
                specificPatchSet);

        /**
         * 开始进行前缀的模糊匹配
         * 1、遍历目标码文件目录
         * 2、模糊匹配到增量文件后将其移动到增量文件临时目录
         */
        Set<File> matchedFiles = FileUtil.getMatchedFiles(
                baseDir + myProperties.getClazzDir(), matchPatchSet);

        String libDir = baseDir + tmpDir + "/" + myProperties.getPatchFolderName() + "/lib/";
        // 将匹配的文件移动到目标目录下
        FileUtil.mvFile(matchedFiles, libDir);
        String deleteListDir = baseDir +
                myProperties.getCheckListDir() + "/" +
                myProperties.getDeleteList();
        FileUtil.fileReadLine(deleteListDir, deletePatchSet);

        String deleteList = baseDir.concat(tmpDir).concat("/")
                .concat(myProperties.getPatchFolderName()).concat("/")
                .concat(myProperties.getDeleteList());
        FileUtil.writeFile(deleteList, deletePatchSet);

        String zipName = myProperties.getResultDir() + "/" + myProperties.getPatchZipName();
        ZipUtils.zip(baseDir, tmpDir, zipName);
    }

    /**
     * yaml文件解析，解析提交文件列表
     *
     * @param filePath
     * @param matchPatchSet
     * @param deletePatchSet
     */
    private void yamlPaser(String filePath, Set<String> specificFileSet,
                           Set<String> matchPatchSet, Set<String> deletePatchSet) {
        Yaml yaml = new Yaml();
        File ymlFile = new File(filePath);

        if (ymlFile.exists()) {
            logger.info("开始解析yml文件：" + ymlFile.getName());
            try {
                HashMap<String, List<String>> map = yaml.loadAs(new FileInputStream(ymlFile), HashMap.class);
                List<String> deleteFiles = map.get("deleteFile");
                List<String> matchFiles = map.get("matchFile");

                if (!Objects.equals(deleteFiles, null)) {
                    logger.info("deleteFile:" + deleteFiles.toString());
                    for (String tmp : deleteFiles) deletePatchSet.add(tmp);
                }
                if (!Objects.equals(matchFiles, null)) {
                    logger.info("matchFile:" + matchFiles.toString());
                    for (String tmp : matchFiles) {
                        if (!tmp.endsWith("*")) {
                            specificFileSet.add(tmp);
                        } else if (this.pathPattern(tmp)) {
                            matchPatchSet.add(tmp);
                            deletePatchSet.add(tmp);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("=================!!!!!未找到" +
                    ymlFile.getName() +
                    "文件!!!!!=================");
        }
    }

    /**
     * 模糊路径匹配规则校验
     *
     * @param path 带校验路径
     * @return
     */
    private boolean pathPattern(String path) {
        if (!path.endsWith("*")) return false;
        String[] paths = path.split("/");
        String str = paths[paths.length - 1];
        return !Objects.equals(str.substring(0, 1), "*");
    }

}

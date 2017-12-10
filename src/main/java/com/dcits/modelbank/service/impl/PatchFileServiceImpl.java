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
        File file = new File(baseDir + patchFileDir);
        if (!file.exists()) {
            logger.info(file.getAbsolutePath() + " - 目录不存在！");
            return;
        }

        Set<String> set = new HashSet<>();
        for (String prefix : patchFilePrefix) {
            set.add(runDate + prefix + ".txt");
            set.add(runDate + prefix + ".xml");
        }
        Set<File> xmlFilePaths = new HashSet<>();
        Set<File> txtFilePaths = new HashSet<>();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) continue;
//            logger.info(f.getName());
            if (set.contains(f.getName()) && f.getName().endsWith(".xml")) xmlFilePaths.add(f);
            else if (set.contains(f.getName()) && f.getName().endsWith(".txt")) txtFilePaths.add(f);
        }
        Set<String> specificPatchList = XmlReader.getAllModuleName(xmlFilePaths);
        Set<String> matchPatchList = new HashSet<>(), deletePatchList = new HashSet<>();

        // 进行txt文件的处理
        txtFileList(txtFilePaths, specificPatchList, matchPatchList, deletePatchList);


        // 创建增量文件临时存放目录
        String tmpDir = resultDir + Const.PATCH_TMP_FOLDER;
        File insTmpDir = new File(baseDir + tmpDir);
        if (insTmpDir.exists()) {
            logger.info("delete ：" + insTmpDir.getAbsolutePath());
            insTmpDir.delete();
        }
        insTmpDir.mkdir();
        // 复制部署包增量文件目录结构
        logger.info("mkdir :" + insTmpDir.getAbsolutePath());
        try {
            FileUtil.copyFolder(new File(baseDir + clazzDir), insTmpDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 移动具体文件的增量文件到临时存放目录
        FileUtil.mvFile(baseDir, clazzDir, tmpDir, specificPatchList);

        /**
         * 开始进行前缀的模糊匹配
         * 1、遍历目标码文件目录
         * 2、模糊匹配到增量文件后将其移动到增量文件临时目录
         */
        Set<File> matchedFiles = FileUtil.getMatchedFiles(
                baseDir + clazzDir, matchPatchList);

        // 将匹配的文件移动到目标目录下
        FileUtil.mvFile(matchedFiles, baseDir + tmpDir + "/lib/");
        FileUtil.fileReadLine(baseDir + "dbPatch/checkList/" + Const.DELETE_LIST, deletePatchList);

        FileUtil.writeFile(baseDir + tmpDir + "/" + Const.DELETE_LIST, deletePatchList);

        ZipUtils.zip(baseDir, tmpDir, resultDir + Const.PATCH_ZIP_NAME);
    }

    /**
     * txt 日志文件的处理，增加符号* 的通配
     *
     * @param txtFilePaths      待解析的文件列表
     * @param specificPatchList 具体的增量Jar包列表
     * @param matchPatchList    模糊匹配的增量Jar包列表
     * @param deletePatchList   增量部署中要删除的旧包列表
     */
    private void txtFileList(Set<File> txtFilePaths, Set<String> specificPatchList,
                             Set<String> matchPatchList, Set<String> deletePatchList) {
        try {
            for (File f : txtFilePaths) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String lineTxt;
                while ((lineTxt = br.readLine()) != null) {
                    String[] records = lineTxt.split(",");
                    for (String record : records) {
                        patchListRowDeal(record.trim(), specificPatchList, matchPatchList, deletePatchList);
                    }
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void patchListRowDeal(String row, Set<String> specificPatchList,
                                  Set<String> matchPatchList, Set<String> deletePatchList) {
        if (Objects.equals(null, row) || Objects.equals("", row)) return;
        if (row.endsWith("*")) {
            matchPatchList.add(row);
            deletePatchList.add(row);
        } else {
            specificPatchList.add(row);
        }
    }

}

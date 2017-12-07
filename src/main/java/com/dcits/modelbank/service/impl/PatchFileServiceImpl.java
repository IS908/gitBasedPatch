package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.service.PatchFileService;
import com.dcits.modelbank.utils.DateUtil;
import com.dcits.modelbank.utils.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
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
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) continue;
            logger.info(f.getName());
            if (set.contains(f.getName()) && f.getName().endsWith(".xml")) xmlFilePaths.add(f);
        }
        Set<String> patchFileList = XmlReader.getAllModuleName(xmlFilePaths);

        // TODO: 2017/12/7 进行增量文件的抽取
    }
}

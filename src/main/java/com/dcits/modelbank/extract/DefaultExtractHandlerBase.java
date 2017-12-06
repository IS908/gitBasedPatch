package com.dcits.modelbank.extract;

import com.dcits.modelbank.model.FileModel;
import com.dcits.modelbank.utils.DateUtil;
import com.dcits.modelbank.utils.FileUtil;
import com.dcits.modelbank.utils.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created on 2017-11-10 16:14.
 *
 * @author kevin
 */
@Service("patchExtractHandler")
public class DefaultExtractHandlerBase extends BasePatchExtractHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultExtractHandlerBase.class);

    private String sourceDir = "";

    @Override
    protected void fileTransfer(Set<String> set) {
        logger.info("target目录：" + super.targetDir);
        logger.info("result目录：" + super.resultDir);
        // 将增量jar包列表输出到文件
        FileUtil.writeFile(resultDir + File.separator + DateUtil.getRunDate() + ".txt", set.toString().replace(", ", "\n"));
        FileUtil.filterFile(targetDir, set);
        String fileName = resultDir + "app_modelbank_ins.zip";
        try {
            ZipUtils.zip(targetDir, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.warn("增量抽取完毕，抽取的增量包文件数量为： " + set.size());
    }

    @Override
    protected Set<String> getAllPackageName(List<FileModel> list) {
        Set<String> set = new HashSet<>();
        for (FileModel file : list) {
            String filePath = this.sourceDir + file.getPath();
            logger.info("FilePath：" + filePath);
            if (!isFileInPackage(filePath)) continue;

            String pomPath = FileUtil.findFilePath(filePath, "pom.xml");
            if (Objects.equals(null, pomPath) || Objects.equals("", pomPath)) continue;
            String packageName = xmlBulider.pom2PackageName(pomPath);
            logger.info("packageName：" + packageName);
            set.add(packageName);
        }

        logger.info("增量文件所在的jar包数量为：" + set.size());
        return set;
    }
}

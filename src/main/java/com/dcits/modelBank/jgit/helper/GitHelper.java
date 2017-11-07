package com.dcits.modelBank.jgit.helper;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created on 2017-11-07 15:33.
 *
 * @author kevin
 */
public class GitHelper {
    private static final Logger logger = LoggerFactory.getLogger(GitHelper.class);

    // TODO: 2017/11/7 将该部分代码放在启动主类时自动加载处
    static {
        File directory = new File("");// 参数为空
        try {
            String baseDir = directory.getCanonicalPath();
            System.setProperty("baseDir", baseDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得git仓库句柄
     * @return
     * @throws IOException
     */
    public static Repository openJGitRepository() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            return builder
                    .setGitDir(new File(System.getProperty("baseDir") + "\\.git"))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static Repository createNewRepository() throws IOException {
//        // prepare a new folder
//        File localPath = File.createTempFile("TestGitRepository", "");
//        if(!localPath.delete()) {
//            throw new IOException("Could not delete temporary file " + localPath);
//        }
//
//        // create the directory
//        Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
//        repository.create();
//
//        return repository;
//    }
}

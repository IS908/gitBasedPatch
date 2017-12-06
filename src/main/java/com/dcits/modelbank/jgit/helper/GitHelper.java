package com.dcits.modelbank.jgit.helper;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created on 2017-11-07 15:33.
 *
 * @author kevin
 */
public class GitHelper {
    private static final Logger logger = LoggerFactory.getLogger(GitHelper.class);

    private String rootDir;
    private String sourceDir;

    /**
     * 获得git仓库句柄
     *
     * @return
     * @throws IOException
     */
    public Repository openJGitRepository() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            logger.info("openJGitRepository once ...");
            return builder
                    .setFS(FS.DETECTED)
                    .setGitDir(new File(this.rootDir))
                    .readEnvironment()
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个新的Git实例
     *
     * @return
     */
    public Git getGitInstance() {
        logger.info("gen Git Instance");
        Git git = new Git(this.openJGitRepository());
        return git;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }
}

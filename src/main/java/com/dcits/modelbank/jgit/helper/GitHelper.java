package com.dcits.modelbank.jgit.helper;

import com.dcits.modelbank.utils.Const;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
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
@Component
public class GitHelper {
    private static final Logger logger = LoggerFactory.getLogger(GitHelper.class);

    private String rootDir;
    private String userName;
    private String userEmail;
    private String remoteUrl;
    private CredentialsProvider credentialsProvider;

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    /**
     * 获得git仓库句柄
     *
     * @return
     * @throws IOException
     */
    public Repository openJGitRepository() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            return builder
                    .setFS(FS.DETECTED)
                    .setGitDir(new File(this.rootDir + "\\" + Const.GIT))
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
        Git git = new Git(this.openJGitRepository());
        return git;
    }
}

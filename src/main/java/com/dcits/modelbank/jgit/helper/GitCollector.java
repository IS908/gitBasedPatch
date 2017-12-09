package com.dcits.modelbank.jgit.helper;

import java.util.Map;

/**
 * Created on 2017-12-06 19:31.
 *
 * @author kevin
 */
public class GitCollector {
    /**
     * 工程名称
     */
    private String title;
    /**
     * 工程中包含的所有Git配置库
     */
    private Map<String, GitHelper> gitHelpers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, GitHelper> getGitHelpers() {
        return gitHelpers;
    }

    public void setGitHelpers(Map<String, GitHelper> gitHelpers) {
        this.gitHelpers = gitHelpers;
    }
}

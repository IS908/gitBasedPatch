package com.dcits.modelbank.jgit.helper;

import java.util.Map;

/**
 * Created on 2017-12-05 22:24.
 *
 * @author kevin
 */
public class GitCollector {
    private String title;
    private Map<String, GitHelper> collectors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, GitHelper> getCollectors() {
        return collectors;
    }

    public void setCollectors(Map<String, GitHelper> collectors) {
        this.collectors = collectors;
    }
}

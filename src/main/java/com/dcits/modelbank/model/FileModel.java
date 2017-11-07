package com.dcits.modelbank.model;

import java.util.List;
import java.util.Map;

/**
 * Created on 2017-11-07 17:09.
 *
 * @author kevin
 */
public class FileModel {
    private String name;
    private String module;
    private String path;
    private String type;

    public FileModel() {
    }

    public FileModel(String name, String module, String path, String type, List<Map<String, Map>> authors) {
        this.name = name;
        this.module = module;
        this.path = path;
        this.authors = authors;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map<String, Map>> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Map<String, Map>> authors) {
        this.authors = authors;
    }

    private List<Map<String, Map>> authors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

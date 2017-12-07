package com.dcits.modelbank.service;

import com.dcits.modelbank.extract.BasePatchExtractHandler;

import java.util.List;

/**
 * Created on 2017-12-07 00:31.
 *
 * @author kevin
 */
public abstract class PatchFileService {
    protected BasePatchExtractHandler basePatchExtractHandler;

    protected String patchFileDir;
    protected List<String> patchFilePrefix;

    public PatchFileService(String patchFileDir, List<String> patchFilePrefix) {
        this.patchFileDir = patchFileDir;
        this.patchFilePrefix = patchFilePrefix;
    }

    /**
     * 进行增量文件的抽取
     */
    public abstract void patchFileExecute(String baseDir);

    public BasePatchExtractHandler getBasePatchExtractHandler() {
        return basePatchExtractHandler;
    }

    public void setBasePatchExtractHandler(BasePatchExtractHandler basePatchExtractHandler) {
        this.basePatchExtractHandler = basePatchExtractHandler;
    }
}

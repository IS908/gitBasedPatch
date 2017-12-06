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

    protected List<String> patchFileNames;

    public PatchFileService(List<String> patchFileNames) {
        this.patchFileNames = patchFileNames;
    }

    /**
     * 进行增量文件的抽取
     */
    public abstract void patchFileExecute();

    public BasePatchExtractHandler getBasePatchExtractHandler() {
        return basePatchExtractHandler;
    }

    public void setBasePatchExtractHandler(BasePatchExtractHandler basePatchExtractHandler) {
        this.basePatchExtractHandler = basePatchExtractHandler;
    }
}

package com.dcits.modelbank.service.impl;

import com.dcits.modelbank.service.PatchFileService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 2017-12-07 00:36.
 *
 * @author kevin
 */
public class PatchFileServiceImpl extends PatchFileService {

    public PatchFileServiceImpl(List<String> patchFileNames) {
        super(patchFileNames);
    }

    @Override
    public void patchFileExecute() {
        // TODO: 2017/12/7 进行增量文件的抽取
    }
}

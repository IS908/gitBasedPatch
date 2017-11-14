package com.dcits.modelbank.service;

import com.dcits.modelbank.model.FileModel;

import java.util.List;

/**
 * Created on 2017-11-07 19:41.
 *
 * @author kevin
 */
public interface GitService {
    void genChangesFileListToday();

    List<FileModel> getFileModelFromXml();
}

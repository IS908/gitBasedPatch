package com.dcits.modelbank.service;

/**
 * Created on 2017-11-07 19:41.
 *
 * @author kevin
 */
public interface GitService {
    /**
     * 获取当天的增量文件列表
     */
    void genChangesFileListToday();

//    /**
//     *
//     * @return
//     */
//    List<FileModel> getFileModelFromXml();

    /**
     *
     */
    void patchFileExecute();
}

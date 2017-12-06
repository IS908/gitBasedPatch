package com.dcits.modelbank.service;

import com.dcits.modelbank.jgit.helper.GitHelper;

/**
 * Created on 2017-11-07 19:41.
 *
 * @author kevin
 */
public abstract class GitService {
    protected GitHelper gitHelper;

    public GitService(GitHelper gitHelper) {
        this.gitHelper = gitHelper;
    }

    /**
     * 获取当天的增量文件列表
     */
    public abstract void genChangesFileListToday();

    /**
     * 获取两个Tag之间的增量文件列表
     *
     * @param tagStart 开始Tag
     * @param tagEnd   截止Tag
     */
    public abstract void genChangesFileListBetweenTag(String tagStart, String tagEnd);

//    /**
//     *
//     * @return
//     */
//    List<FileModel> getFileModelFromXml();

    /**
     * 进行增量文件的抽取
     */
    public abstract void patchFileExecute();

    /**
     * 获取Tag对应的版本的提交时间
     *
     * @param tagName
     */
    public abstract void getCommitTimeByTag(String tagName);
}

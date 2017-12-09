package com.dcits.modelbank.service;

import com.dcits.modelbank.jgit.GitHandler;
import com.dcits.modelbank.utils.XmlBulider;

/**
 * Created on 2017-11-07 19:41.
 *
 * @author kevin
 */
public interface GitService {
    GitHandler getGitHandler();
    XmlBulider getXmlBulider();

    /**
     * 获取当天的增量文件列表
     */
    void genChangesFileListToday();

    /**
     * 获取两个Tag之间的增量文件列表
     *
     * @param tagStart 开始Tag
     * @param tagEnd   截止Tag
     */
    void genChangesFileListBetweenTag(String tagStart, String tagEnd);

    /**
     * 获取Tag对应的版本的提交时间
     *
     * @param tagName
     */
    void getCommitTimeByTag(String tagName);
}

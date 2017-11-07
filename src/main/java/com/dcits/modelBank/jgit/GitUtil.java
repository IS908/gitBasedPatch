package com.dcits.modelBank.jgit;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

/**
 * Created on 2017-11-07 20:01.
 *
 * @author kevin
 */
public interface GitUtil {
    /**
     * 提交所有文件到本地，并支持推送到远程
     *
     * @param note     提交本地注释
     * @param pushFlag 是否推送远程标志
     * @return 本次提交的版本号
     */
    String commitAndPushAllChanges(String note, boolean pushFlag);

    /**
     * 推送选中的文件到本地，并支持推送到远程
     *
     * @param fileList 推送文件列表
     * @param note     提交本地注释
     * @param pushFlag 是否推送远程标志
     * @return 本次提交的版本号
     */
    String commitAndPush(List<String> fileList, String note, boolean pushFlag);

    /**
     * 列出两个分支间的差异文件
     *
     * @param fromBranch 基准分支名称
     * @param toBranch   差异分支名称
     * @return 差异文件信息列表
     */
    List<DiffEntry> showDiffFilesByBranches(String fromBranch, String toBranch);

    /**
     * 列出两个提交版本号之间的差异
     *
     * @param fromCommitId 基准版本号
     * @param toCommitId   差异版本号
     * @return 差异文件信息列表
     */
    List<DiffEntry> showDiffFilesByCommits(String fromCommitId, String toCommitId);

    /**
     * 回滚到指定版本的上一个版本
     *
     * @param diffEntries 需要回滚的文件
     * @param revision 版本号
     * @param note 备注
     * @return
     */
    boolean rollBackPreRevision(List<DiffEntry> diffEntries,
                                String revision, String note);
}

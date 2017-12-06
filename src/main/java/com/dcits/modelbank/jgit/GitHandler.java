package com.dcits.modelbank.jgit;

import com.dcits.modelbank.jgit.helper.PullEnum;
import com.dcits.modelbank.model.FileDiffEntry;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.FetchResult;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017-11-07 20:01.
 *
 * @author kevin
 */
public interface GitHandler {

    /**
     * 获取所有子模块
     *
     * @return
     */
    SubmoduleWalk getSubmodules() throws IOException;

    /**
     * 获取指定文件的blame信息
     *
     * @param commitID 对应的版本号
     * @param file     指定文件
     * @return
     */
    BlameResult fileBlame(String commitID, String file);

    /**
     * 显示当天的提交日志
     *
     * @return
     */
    Iterable<RevCommit> showLogToday();

    /**
     * 暂存当前修改
     *
     * @return
     */
    boolean stash();

    /**
     * 查看暂存列表
     *
     * @return
     */
    Collection<RevCommit> stashList();

    /**
     * 应用指定暂存
     *
     * @return
     */
    ObjectId unstash(int index);

    /**
     * fetch远程仓库
     *
     * @return
     */
    FetchResult fetch();

    /**
     * 默认merge方式进行拉取更新
     *
     * @return
     */
    PullResult pull();

    /**
     * 指定方式进行时拉取更新
     *
     * @param type 拉取更新方式
     * @return
     */
    PullResult pull(PullEnum type);

    /**
     * 推送远程仓库操作
     *
     * @return
     */
    boolean push();

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
    List<DiffEntry> showBranchDiff(String fromBranch, String toBranch);


    /**
     * 列出两个提交版本号之间的差异
     *
     * @param fromCommitId 基准版本号
     * @param toCommitId   差异版本号
     * @return 差异文件信息列表
     */
    List<DiffEntry> showCommitDiff(String fromCommitId, String toCommitId);

    /**
     * 回滚到指定版本
     *
     * @param diffEntries 需要回滚的文件
     * @param revision    版本号
     * @param note        备注
     * @return
     */
    boolean rollBackPreRevision(List<DiffEntry> diffEntries, String revision, String note);

    /**
     * 按照文件划分获取该文件的相应提交记录
     *
     * @return
     */
    Map<String, List<FileDiffEntry>> getCommitsLogByFile();

    /**
     * 获取指定两个Tag之间的增量文件列表
     * @param tagStart
     * @param tagEnd
     * @return
     */
    Map<String, List<FileDiffEntry>> getCommitsLogByFile(String tagStart, String tagEnd);

//    /**
//     * 判断是否存在该Tag
//     *
//     * @param tagName
//     * @return
//     */
//    boolean checkTagExists(String tagName);

    /**
     * 根据Tag获取Tag对应版本号的提交时间
     *
     * @param tag
     * @return
     */
    int commitTimeOfTag(String tag);
}

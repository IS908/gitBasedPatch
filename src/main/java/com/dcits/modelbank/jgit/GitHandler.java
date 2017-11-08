package com.dcits.modelbank.jgit;

import com.dcits.modelbank.jgit.helper.PullEnum;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;

import java.util.Collection;
import java.util.List;

/**
 * Created on 2017-11-07 20:01.
 *
 * @author kevin
 */
public interface GitHandler {
    /**
     * checkout到另一个现有分支
     * @param branch 新分支名
     * @return
     */
    boolean checkoutBranch(String branch);

    /**
     * 检出一个新的分支
     * @param branch 本地新分支名
     * @param origin 跟踪远程分支名(若为null，则只在本地创建新分支)
     * @return
     */
    boolean checkoutNewBranch(String branch, String origin);

    /**
     * 暂存当前修改
     * @return
     */
    boolean stash();

    /**
     * 查看暂存列表
     * @return
     */
    Collection<RevCommit> stashList();

    /**
     * 应用指定暂存
     * @return
     */
    ObjectId unstash(int index);

    /**
     * fetch远程仓库
     * @return
     */
    FetchResult fetch();

    /**
     * 默认merge方式进行拉取更新
     * @return
     */
    PullResult pull();

    /**
     * 指定方式进行时拉取更新
     * @param type 拉取更新方式
     * @return
     */
    PullResult pull(PullEnum type);

    /**
     * 推送远程仓库操作
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
     * @param revision 版本号
     * @param note 备注
     * @return
     */
    boolean rollBackPreRevision(List<DiffEntry> diffEntries, String revision, String note);
}

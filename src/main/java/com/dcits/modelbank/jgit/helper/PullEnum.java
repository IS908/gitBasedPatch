package com.dcits.modelbank.jgit.helper;

/**
 * Created on 2017-11-07 23:26.
 *
 * @author kevin
 */
public enum PullEnum {
    REBASE("rebase"), MERGE("merge");

    private String pullType;

    PullEnum(String pullType) {
        this.pullType = pullType;
    }

    @Override
    public String toString() {
        return this.pullType;
    }

    public static void main(String[] args) {
        System.out.println(PullEnum.MERGE);
    }
}

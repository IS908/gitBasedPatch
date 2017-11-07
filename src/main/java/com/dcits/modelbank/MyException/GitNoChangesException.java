package com.dcits.modelbank.MyException;

/**
 * Git 工作空间没有待提交的修改异常
 * Created on 2017-11-07 20:44.
 *
 * @author kevin
 */
public class GitNoChangesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GitNoChangesException(String message) {
        super(message);
    }

    public GitNoChangesException(String message, Throwable cause) {
        super(message, cause);
    }
}

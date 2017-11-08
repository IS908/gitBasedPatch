package com.dcits.modelbank.MyException;

/**
 * Created on 2017-11-08 15:49.
 *
 * @author kevin
 */
public class GitNoSuchBranchException extends RuntimeException {
    public GitNoSuchBranchException(String message) {
        super(message);
    }

    public GitNoSuchBranchException(String message, Throwable cause) {
        super(message, cause);
    }
}

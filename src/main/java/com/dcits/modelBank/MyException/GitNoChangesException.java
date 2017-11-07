package com.dcits.modelBank.MyException;

/**
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

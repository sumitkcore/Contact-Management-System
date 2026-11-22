package com.cms.exception;

/**
 * Unchecked runtime exception wrapping SQL and JDBC database connectivity issues.
 */
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.cms.exception;

/**
 * Unchecked runtime exception wrapping file I/O and object serialization issues.
 */
public class StorageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

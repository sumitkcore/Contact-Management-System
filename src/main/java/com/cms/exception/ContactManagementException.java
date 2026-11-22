package com.cms.exception;

/**
 * Base Checked Exception for the Contact Management System.
 * Demonstrates Java Exception Handling hierarchy and inheritance.
 */
public class ContactManagementException extends Exception {
    private static final long serialVersionUID = 1L;

    public ContactManagementException(String message) {
        super(message);
    }

    public ContactManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

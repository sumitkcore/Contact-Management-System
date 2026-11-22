package com.cms.exception;

/**
 * Checked exception thrown when an attempt is made to insert or update a contact
 * with a duplicate unique identifier (e.g. phone number or email address).
 */
public class DuplicateContactException extends ContactManagementException {
    private static final long serialVersionUID = 1L;

    private final String conflictField;
    private final String conflictValue;

    public DuplicateContactException(String conflictField, String conflictValue) {
        super("A contact with this " + conflictField + " (" + conflictValue + ") already exists in the system.");
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    public String getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }
}

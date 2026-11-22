package com.cms.model;

/**
 * Enumeration representing auditable system actions for security and tracking.
 */
public enum AuditAction {
    CREATE("Contact Created"),
    UPDATE("Contact Updated"),
    DELETE("Contact Moved to Trash"),
    RESTORE("Contact Restored from Trash"),
    PERMANENT_DELETE("Contact Permanently Deleted"),
    CALL_LOGGED("Call Activity Logged"),
    IMPORT("Contacts Imported"),
    EXPORT("Contacts Exported");

    private final String description;

    AuditAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

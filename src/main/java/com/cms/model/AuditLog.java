package com.cms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Audit log record to track operations performed in the Contact Management System.
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final LocalDateTime timestamp;
    private final AuditAction action;
    private final String contactId;
    private final String contactName;
    private final String details;

    public AuditLog(AuditAction action, String contactId, String contactName, String details) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.contactId = contactId != null ? contactId : "N/A";
        this.contactName = contactName != null ? contactName : "N/A";
        this.details = details != null ? details : "";
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %-15s | Contact: %-20s | %s",
                timestamp.format(dtf), action.name(), contactName, details);
    }
}

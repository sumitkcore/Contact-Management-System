package com.cms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Entity representing an interaction / phone call log with a contact.
 */
public class CallLog implements Serializable, Comparable<CallLog> {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String contactId;
    private final CallType callType;
    private final LocalDateTime timestamp;
    private final int durationSeconds;
    private final String notes;

    public CallLog(String id, String contactId, CallType callType, LocalDateTime timestamp, int durationSeconds, String notes) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.contactId = contactId;
        this.callType = callType != null ? callType : CallType.OUTGOING;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.durationSeconds = Math.max(0, durationSeconds);
        this.notes = notes != null ? notes.trim() : "";
    }

    public CallLog(String contactId, CallType callType, int durationSeconds, String notes) {
        this(UUID.randomUUID().toString(), contactId, callType, LocalDateTime.now(), durationSeconds, notes);
    }

    public String getId() {
        return id;
    }

    public String getContactId() {
        return contactId;
    }

    public CallType getCallType() {
        return callType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getNotes() {
        return notes;
    }

    public String getFormattedDuration() {
        if (callType == CallType.MISSED) return "Missed";
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        if (minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        return timestamp.format(formatter);
    }

    @Override
    public int compareTo(CallLog o) {
        // Most recent calls first
        return o.timestamp.compareTo(this.timestamp);
    }
}

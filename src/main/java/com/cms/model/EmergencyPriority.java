package com.cms.model;

/**
 * Enumeration representing emergency contact priority levels.
 */
public enum EmergencyPriority {
    PRIMARY(1, "Primary Responder (First Call)"),
    SECONDARY(2, "Secondary Responder"),
    TERTIARY(3, "Tertiary Responder");

    private final int level;
    private final String description;

    EmergencyPriority(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}

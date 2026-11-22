package com.cms.model;

/**
 * Enumeration representing the type of a phone call.
 */
public enum CallType {
    INCOMING("Incoming Call", "📥"),
    OUTGOING("Outgoing Call", "📤"),
    MISSED("Missed Call", "⚠️");

    private final String label;
    private final String icon;

    CallType(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return icon + " " + label;
    }
}

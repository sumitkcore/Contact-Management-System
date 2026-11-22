
package com.cms.model;

/**
 * Enumeration representing the primary categories of contacts.
 * Provides display names, icons, and colors for the UI.
 */
public enum ContactType {

    PERSONAL("Personal", "👤", "#3B82F6"),
    BUSINESS("Business", "💼", "#8B5CF6"),
    EMERGENCY("Emergency", "🚨", "#EF4444");

    private final String displayName;
    private final String icon;
    private final String color;

    /**
     * Constructor for ContactType.
     */
    ContactType(String displayName, String icon, String color) {
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
    }

    /**
     * Returns the display name of the contact type.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the icon associated with the contact type.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the color associated with the contact type.
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the hexadecimal color code.
     */
    public String getColorHex() {
        return color;
    }

    /**
     * Finds a ContactType using its enum name or display name.
     */
    public static ContactType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        for (ContactType type : ContactType.values()) {
            if (type.name().equalsIgnoreCase(value.trim())
                    || type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        return null;
    }

    /**
     * Returns the display name for UI components.
     */
    @Override
    public String toString() {
        return displayName;
    }
}

package com.cms.model;

/**
 * Enumeration of predefined contact groups for organizational tagging.
 */
public enum ContactGroup {
    FAMILY("Family"),
    FRIENDS("Friends"),
    COLLEAGUES("Colleagues"),
    CLIENTS("Clients"),
    VIP("VIP"),
    SERVICES("Services"),
    EMERGENCY("Emergency Services"),
    OTHER("Other");

    private final String displayName;

    ContactGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ContactGroup fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return OTHER;
        }
        for (ContactGroup g : ContactGroup.values()) {
            if (g.name().equalsIgnoreCase(text.trim()) || g.displayName.equalsIgnoreCase(text.trim())) {
                return g;
            }
        }
        return OTHER;
    }
}

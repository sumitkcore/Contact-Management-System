package com.cms.model;

/**
 * Enumeration representing medical blood groups for emergency contacts.
 */
public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    UNKNOWN("Unknown");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static BloodGroup fromString(String text) {
        if (text == null) return UNKNOWN;
        for (BloodGroup bg : BloodGroup.values()) {
            if (bg.name().equalsIgnoreCase(text) || bg.label.equalsIgnoreCase(text)) {
                return bg;
            }
        }
        return UNKNOWN;
    }
}

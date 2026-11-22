package com.cms.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object representing a contact's phone number with type classification.
 * Demonstrates Encapsulation, Immutability / Value Object semantics, and Serializable.
 */
public class PhoneNumber implements Serializable, Comparable<PhoneNumber> {
    private static final long serialVersionUID = 1L;

    public enum Type {
        MOBILE("Mobile"),
        HOME("Home"),
        WORK("Work"),
        OTHER("Other");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final Type type;
    private final String number;
    private final boolean isPrimary;

    public PhoneNumber(Type type, String number, boolean isPrimary) {
        this.type = type != null ? type : Type.MOBILE;
        this.number = number != null ? number.trim() : "";
        this.isPrimary = isPrimary;
    }

    public PhoneNumber(String number) {
        this(Type.MOBILE, number, true);
    }

    public Type getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Formats phone number for display (handles standard 10-digit Indian/US patterns).
     */
    public String getFormattedNumber() {
        if (number == null || number.isEmpty()) return "";
        String cleaned = number.replaceAll("[^0-9+]", "");
        if (cleaned.length() == 10) {
            return cleaned.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "($1) $2-$3");
        } else if (cleaned.length() == 12 && cleaned.startsWith("91")) {
            return "+91 " + cleaned.substring(2, 7) + " " + cleaned.substring(7);
        }
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(number.replaceAll("[^0-9]", ""), that.number.replaceAll("[^0-9]", ""));
    }

    @Override
    public int hashCode() {
        return Objects.hash(number.replaceAll("[^0-9]", ""));
    }

    @Override
    public int compareTo(PhoneNumber o) {
        if (this.isPrimary && !o.isPrimary) return -1;
        if (!this.isPrimary && o.isPrimary) return 1;
        return this.number.compareTo(o.number);
    }

    @Override
    public String toString() {
        return (isPrimary ? "★ " : "") + "[" + type + "] " + getFormattedNumber();
    }
}

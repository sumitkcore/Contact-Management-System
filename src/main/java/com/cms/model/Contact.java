package com.cms.model;

import com.cms.validation.Validatable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Base Abstract Class representing a Contact.
 * 
 * Demonstrates OOP Concepts for CSE2003:
 * - Abstraction: Abstract methods getContactType(), getSummary(), getSpecificDetailsMap()
 * - Encapsulation: Private/Protected members with controlled getters and setters
 * - Inheritance: Extended by PersonalContact, BusinessContact, and EmergencyContact
 * - Polymorphism: Dynamic method dispatch in toVCard(), getSummary(), validate()
 * - Interfaces: Implements Serializable, Comparable<Contact>, Cloneable, Validatable
 */
public abstract class Contact implements Serializable, Comparable<Contact>, Cloneable, Validatable {
    private static final long serialVersionUID = 1L;

    // Core Identity & Identification
    protected String id;
    protected String firstName;
    protected String lastName;

    // Contact Communication Channels (Concrete serializable collections)
    protected ArrayList<PhoneNumber> phoneNumbers;
    protected String email;
    protected String secondaryEmail;

    // Location & Organization
    protected Address address;
    protected ContactGroup group;
    protected TreeSet<String> tags;
    protected String notes;

    // State, Flags & Metadata
    protected boolean isFavorite;
    protected boolean isDeleted; // Soft-delete support (Recycle Bin)
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected String avatarColorHex;

    // Interaction History
    protected ArrayList<CallLog> callLogs;

    /**
     * Protected Constructor for Abstract Base Class
     */
    protected Contact(String id, String firstName, String lastName) {
        this.id = (id != null && !id.trim().isEmpty()) ? id : UUID.randomUUID().toString();
        this.firstName = (firstName != null) ? firstName.trim() : "";
        this.lastName = (lastName != null) ? lastName.trim() : "";
        this.phoneNumbers = new ArrayList<>();
        this.email = "";
        this.secondaryEmail = "";
        this.address = Address.empty();
        this.group = ContactGroup.OTHER;
        this.tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.notes = "";
        this.isFavorite = false;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.avatarColorHex = "";
        this.callLogs = new ArrayList<>();
    }

    // ==========================================
    // ABSTRACT METHODS (Polymorphism & Abstraction)
    // ==========================================

    public abstract ContactType getContactType();

    public abstract String getSummary();

    public abstract Map<String, String> getSpecificDetailsMap();

    // ==========================================
    // CONCRETE METHODS & ENCAPSULATION
    // ==========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        String fn = firstName != null ? firstName.trim() : "";
        String ln = lastName != null ? lastName.trim() : "";
        if (fn.isEmpty() && ln.isEmpty()) return "Unnamed Contact";
        if (fn.isEmpty()) return ln;
        if (ln.isEmpty()) return fn;
        return fn + " " + ln;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return Collections.unmodifiableList(phoneNumbers);
    }

    public void setPhoneNumbers(List<PhoneNumber> numbers) {
        this.phoneNumbers = new ArrayList<>(numbers != null ? numbers : Collections.emptyList());
        this.updatedAt = LocalDateTime.now();
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber != null && !phoneNumber.getNumber().isEmpty()) {
            if (this.phoneNumbers.isEmpty()) {
                this.phoneNumbers.add(new PhoneNumber(phoneNumber.getType(), phoneNumber.getNumber(), true));
            } else {
                this.phoneNumbers.add(phoneNumber);
            }
            this.updatedAt = LocalDateTime.now();
        }
    }

    public String getPrimaryPhoneNumber() {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) return "";
        for (PhoneNumber p : phoneNumbers) {
            if (p.isPrimary()) return p.getNumber();
        }
        return phoneNumbers.get(0).getNumber();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail != null ? secondaryEmail.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public Address getAddress() {
        return address != null ? address : Address.empty();
    }

    public void setAddress(Address address) {
        this.address = address != null ? address : Address.empty();
        this.updatedAt = LocalDateTime.now();
    }

    public ContactGroup getGroup() {
        return group != null ? group : ContactGroup.OTHER;
    }

    public void setGroup(ContactGroup group) {
        this.group = group != null ? group : ContactGroup.OTHER;
        this.updatedAt = LocalDateTime.now();
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag.trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatarColorHex() {
        return avatarColorHex;
    }

    public void setAvatarColorHex(String avatarColorHex) {
        this.avatarColorHex = avatarColorHex;
    }

    public List<CallLog> getCallLogs() {
        return Collections.unmodifiableList(callLogs);
    }

    public void addCallLog(CallLog log) {
        if (log != null) {
            this.callLogs.add(0, log); // Add to head
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setCallLogs(List<CallLog> callLogs) {
        this.callLogs = new ArrayList<>(callLogs != null ? callLogs : Collections.emptyList());
    }

    public String getInitials() {
        String fn = firstName != null ? firstName.trim() : "";
        String ln = lastName != null ? lastName.trim() : "";
        if (!fn.isEmpty() && !ln.isEmpty()) {
            return ("" + fn.charAt(0) + ln.charAt(0)).toUpperCase();
        } else if (!fn.isEmpty()) {
            return fn.substring(0, Math.min(2, fn.length())).toUpperCase();
        } else if (!ln.isEmpty()) {
            return ln.substring(0, Math.min(2, ln.length())).toUpperCase();
        }
        return "?";
    }

    public boolean matchesSearch(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String q = query.trim().toLowerCase();

        if (getFullName().toLowerCase().contains(q)) return true;
        if (email != null && email.toLowerCase().contains(q)) return true;
        if (secondaryEmail != null && secondaryEmail.toLowerCase().contains(q)) return true;

        String qDigits = q.replaceAll("[^0-9]", "");
        if (!qDigits.isEmpty()) {
            for (PhoneNumber p : phoneNumbers) {
                if (p.getNumber().replaceAll("[^0-9]", "").contains(qDigits)) return true;
            }
        }

        if (group != null && group.getDisplayName().toLowerCase().contains(q)) return true;

        for (String tag : tags) {
            if (tag.toLowerCase().contains(q)) return true;
        }

        if (address != null && address.getFormatted().toLowerCase().contains(q)) return true;
        if (notes != null && notes.toLowerCase().contains(q)) return true;

        for (String val : getSpecificDetailsMap().values()) {
            if (val != null && val.toLowerCase().contains(q)) return true;
        }

        return false;
    }

    public String toVCard() {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCARD\r\n");
        sb.append("VERSION:3.0\r\n");
        sb.append("N:").append(lastName).append(";").append(firstName).append(";;;\r\n");
        sb.append("FN:").append(getFullName()).append("\r\n");

        for (PhoneNumber p : phoneNumbers) {
            String typeStr = p.getType().name();
            if (p.isPrimary()) typeStr += ",PREF";
            sb.append("TEL;TYPE=").append(typeStr).append(":").append(p.getNumber()).append("\r\n");
        }

        if (!email.isEmpty()) {
            sb.append("EMAIL;TYPE=INTERNET,PREF:").append(email).append("\r\n");
        }
        if (!secondaryEmail.isEmpty()) {
            sb.append("EMAIL;TYPE=INTERNET:").append(secondaryEmail).append("\r\n");
        }

        if (address != null && !address.isEmpty()) {
            sb.append("ADR;TYPE=HOME:;;").append(address.getStreet()).append(";")
                    .append(address.getCity()).append(";")
                    .append(address.getState()).append(";")
                    .append(address.getPostalCode()).append(";")
                    .append(address.getCountry()).append("\r\n");
        }

        if (!tags.isEmpty()) {
            sb.append("CATEGORIES:").append(String.join(",", tags)).append("\r\n");
        }

        if (!notes.isEmpty()) {
            sb.append("NOTE:").append(notes.replace("\n", "\\n")).append("\r\n");
        }

        sb.append("X-CMS-TYPE:").append(getContactType().name()).append("\r\n");
        sb.append("X-CMS-GROUP:").append(getGroup().name()).append("\r\n");

        appendSpecificVCardDetails(sb);

        sb.append("REV:").append(updatedAt).append("\r\n");
        sb.append("END:VCARD\r\n");
        return sb.toString();
    }

    protected void appendSpecificVCardDetails(StringBuilder sb) {
        // Overridden by subclasses
    }

    @Override
    public int compareTo(Contact other) {
        if (other == null) return 1;
        int fn = this.firstName.compareToIgnoreCase(other.firstName);
        if (fn != 0) return fn;
        return this.lastName.compareToIgnoreCase(other.lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + getContactType().getDisplayName() + ")";
    }

    @Override
    public Contact clone() {
        try {
            Contact copy = (Contact) super.clone();
            copy.phoneNumbers = new ArrayList<>(this.phoneNumbers);
            copy.tags = new TreeSet<>(this.tags);
            copy.callLogs = new ArrayList<>(this.callLogs);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }
}

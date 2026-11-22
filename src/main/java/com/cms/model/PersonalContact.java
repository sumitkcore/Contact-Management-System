package com.cms.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete Subclass representing a Personal Contact (Family, Friends, Acquaintances).
 * 
 * Demonstrates:
 * - Inheritance (extends Contact)
 * - Polymorphism (overrides getContactType(), getSummary(), getSpecificDetailsMap(), validate())
 * - Encapsulation (private fields with getters/setters)
 */
public class PersonalContact extends Contact {
    private static final long serialVersionUID = 1L;

    private String relationship; // e.g. "Brother", "Mother", "Best Friend", "Colleague"
    private LocalDate birthday;
    private LocalDate anniversary;
    private String nickname;

    public PersonalContact(String id, String firstName, String lastName) {
        super(id, firstName, lastName);
        this.relationship = "";
        this.nickname = "";
        this.group = ContactGroup.FRIENDS;
    }

    public PersonalContact(String firstName, String lastName) {
        this(null, firstName, lastName);
    }

    @Override
    public ContactType getContactType() {
        return ContactType.PERSONAL;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship != null ? relationship.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public LocalDate getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(LocalDate anniversary) {
        this.anniversary = anniversary;
        this.updatedAt = java.time.LocalDateTime.now();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname != null ? nickname.trim() : "";
        this.updatedAt = java.time.LocalDateTime.now();
    }

    /**
     * Calculates the age based on birthday.
     */
    public int getAge() {
        if (birthday == null) return -1;
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        if (nickname != null && !nickname.isEmpty()) {
            sb.append("\"").append(nickname).append("\" • ");
        }
        if (relationship != null && !relationship.isEmpty()) {
            sb.append(relationship);
        } else {
            sb.append("Personal Connection");
        }
        if (birthday != null) {
            sb.append(" • Age ").append(getAge());
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getSpecificDetailsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        if (nickname != null && !nickname.isEmpty()) {
            map.put("Nickname", nickname);
        }
        if (relationship != null && !relationship.isEmpty()) {
            map.put("Relationship", relationship);
        }
        if (birthday != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            map.put("Birthday", birthday.format(dtf) + " (" + getAge() + " years old)");
        }
        if (anniversary != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            map.put("Anniversary", anniversary.format(dtf));
        }
        return map;
    }

    @Override
    protected void appendSpecificVCardDetails(StringBuilder sb) {
        if (nickname != null && !nickname.isEmpty()) {
            sb.append("NICKNAME:").append(nickname).append("\r\n");
        }
        if (birthday != null) {
            sb.append("BDAY:").append(birthday).append("\r\n");
        }
        if (relationship != null && !relationship.isEmpty()) {
            sb.append("X-CMS-RELATIONSHIP:").append(relationship).append("\r\n");
        }
    }

    @Override
    public ValidationResult validate() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (firstName.isEmpty() && lastName.isEmpty()) {
            errors.put("Name", "At least first name or last name must be provided.");
        }
        if (phoneNumbers.isEmpty() && email.isEmpty()) {
            errors.put("Contact Method", "Either a phone number or email is required.");
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            errors.put("Birthday", "Birthday cannot be in the future.");
        }
        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }
}

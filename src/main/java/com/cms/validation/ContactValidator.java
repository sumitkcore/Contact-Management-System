package com.cms.validation;

import com.cms.model.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validation engine for Contact entities.
 * Demonstrates Regular Expressions, Defensive Programming, and Input Sanitation.
 */
public class ContactValidator {

    // RFC 5322 compliant simplified email regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // URL pattern for LinkedIn / Websites
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?[\\w-]+(\\.[\\w-]+)+(/[^\\s]*)?$", Pattern.CASE_INSENSITIVE
    );

    /**
     * Validates a generic contact entity.
     */
    public static ValidationResult validate(Contact contact) {
        if (contact == null) {
            return ValidationResult.singleError("Contact", "Contact object cannot be null.");
        }

        Map<String, String> errors = new LinkedHashMap<>();

        // 1. Identity validation
        String fn = contact.getFirstName();
        String ln = contact.getLastName();
        if ((fn == null || fn.trim().isEmpty()) && (ln == null || ln.trim().isEmpty())) {
            if (!(contact instanceof BusinessContact && !((BusinessContact) contact).getCompanyName().isEmpty())) {
                errors.put("Name", "Please provide at least a First Name or Last Name.");
            }
        }

        // 2. Phone Numbers validation
        if (contact.getPhoneNumbers() == null || contact.getPhoneNumbers().isEmpty()) {
            if (contact.getEmail().isEmpty()) {
                errors.put("Communication", "Contact must have at least one phone number or email address.");
            }
        } else {
            for (PhoneNumber p : contact.getPhoneNumbers()) {
                if (p.getNumber() == null || p.getNumber().trim().isEmpty()) {
                    errors.put("Phone", "Phone number entry cannot be empty.");
                } else if (!isValidPhoneNumber(p.getNumber(), contact instanceof EmergencyContact)) {
                    errors.put("Phone", "Invalid phone number format: '" + p.getNumber() + "'.");
                }
            }
        }

        // 3. Email validation
        if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
            if (!isValidEmail(contact.getEmail())) {
                errors.put("Email", "Invalid primary email format: '" + contact.getEmail() + "'.");
            }
        }
        if (contact.getSecondaryEmail() != null && !contact.getSecondaryEmail().trim().isEmpty()) {
            if (!isValidEmail(contact.getSecondaryEmail())) {
                errors.put("Secondary Email", "Invalid secondary email format: '" + contact.getSecondaryEmail() + "'.");
            }
        }

        // 4. Subclass specific validation
        if (contact instanceof PersonalContact) {
            PersonalContact pc = (PersonalContact) contact;
            if (pc.getBirthday() != null && pc.getBirthday().isAfter(LocalDate.now())) {
                errors.put("Birthday", "Birthday cannot be in the future.");
            }
            if (pc.getAnniversary() != null && pc.getAnniversary().isAfter(LocalDate.now())) {
                errors.put("Anniversary", "Anniversary cannot be in the future.");
            }
        } else if (contact instanceof BusinessContact) {
            BusinessContact bc = (BusinessContact) contact;
            if (bc.getWorkEmail() != null && !bc.getWorkEmail().trim().isEmpty()) {
                if (!isValidEmail(bc.getWorkEmail())) {
                    errors.put("Work Email", "Invalid work email format: '" + bc.getWorkEmail() + "'.");
                }
            }
            if (bc.getLinkedinUrl() != null && !bc.getLinkedinUrl().trim().isEmpty()) {
                if (!isValidUrl(bc.getLinkedinUrl())) {
                    errors.put("LinkedIn", "Invalid website or LinkedIn URL format.");
                }
            }
        } else if (contact instanceof EmergencyContact) {
            EmergencyContact ec = (EmergencyContact) contact;
            if (ec.getPhoneNumbers().isEmpty() && (ec.getAlternateEmergencyPhone() == null || ec.getAlternateEmergencyPhone().trim().isEmpty())) {
                errors.put("Emergency Phone", "Emergency contact must have at least one valid direct phone line.");
            }
            if (ec.getAlternateEmergencyPhone() != null && !ec.getAlternateEmergencyPhone().trim().isEmpty()) {
                if (!isValidPhoneNumber(ec.getAlternateEmergencyPhone(), true)) {
                    errors.put("Alternate Phone", "Invalid alternate emergency phone format.");
                }
            }
        }

        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return isValidPhoneNumber(phone, false);
    }

    public static boolean isValidPhoneNumber(String phone, boolean isEmergency) {
        if (phone == null) return false;
        String clean = phone.trim();
        String digitsOnly = clean.replaceAll("[^0-9]", "");
        int minDigits = isEmergency ? 3 : 7;
        if (digitsOnly.length() < minDigits || digitsOnly.length() > 15) {
            return false;
        }
        return clean.matches("^[+]?[0-9\\s\\-\\(\\)\\.]{" + minDigits + ",25}$");
    }

    public static boolean isValidUrl(String url) {
        if (url == null) return false;
        return URL_PATTERN.matcher(url.trim()).matches();
    }
}

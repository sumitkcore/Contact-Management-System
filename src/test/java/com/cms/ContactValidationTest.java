package com.cms;

import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;
import com.cms.validation.ContactValidator;

import java.time.LocalDate;

/**
 * Unit Test for Contact Validation logic and Regex checks.
 */
public class ContactValidationTest {

    public static void runTests() {
        System.out.println("Running ContactValidationTest...");

        testValidContact();
        testInvalidEmail();
        testInvalidPhone();
        testFutureBirthday();
        testEmergencyContactMandatoryPhone();

        System.out.println("✓ ContactValidationTest: ALL TESTS PASSED");
    }

    private static void testValidContact() {
        Contact valid = ContactBuilder.personal()
                .name("Sita", "Ram")
                .phone("+91 98765 43210")
                .email("sita.ram@example.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ValidationResult res = ContactValidator.validate(valid);
        assert res.isValid() : "Valid contact failed validation: " + res.getFormattedErrorMessage();
    }

    private static void testInvalidEmail() {
        Contact invalid = ContactBuilder.personal()
                .name("Alex", "Mercer")
                .phone("9876543210")
                .email("not-an-email-format")
                .build();

        ValidationResult res = ContactValidator.validate(invalid);
        assert !res.isValid() : "Invalid email should have failed validation";
        assert res.getErrors().containsKey("Email") : "Missing Email error key";
    }

    private static void testInvalidPhone() {
        Contact invalid = ContactBuilder.personal()
                .name("Bob", "Marley")
                .phone("12") // Too short
                .email("bob@music.com")
                .build();

        ValidationResult res = ContactValidator.validate(invalid);
        assert !res.isValid() : "Too short phone number should fail";
        assert res.getErrors().containsKey("Phone") : "Missing Phone error key";
    }

    private static void testFutureBirthday() {
        Contact invalid = ContactBuilder.personal()
                .name("Future", "Boy")
                .phone("+91 98123 45678")
                .birthday(LocalDate.now().plusDays(10))
                .build();

        ValidationResult res = ContactValidator.validate(invalid);
        assert !res.isValid() : "Future birthday should fail";
        assert res.getErrors().containsKey("Birthday") : "Missing Birthday error key";
    }

    private static void testEmergencyContactMandatoryPhone() {
        EmergencyContact invalid = (EmergencyContact) ContactBuilder.emergency()
                .name("Emergency", "Unit")
                .build(); // No phones provided

        ValidationResult res = ContactValidator.validate(invalid);
        assert !res.isValid() : "Emergency contact without phone must fail";
    }
}

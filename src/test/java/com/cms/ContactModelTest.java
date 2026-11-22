package com.cms;

import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.time.LocalDate;

/**
 * Unit Test for Model classes demonstrating OOP principles:
 * - Inheritance
 * - Polymorphism
 * - Encapsulation
 * - Abstraction
 */
public class ContactModelTest {

    public static void runTests() {
        System.out.println("Running ContactModelTest...");

        testPersonalContact();
        testBusinessContact();
        testEmergencyContact();
        testPolymorphism();
        testDefensiveCopyingAndEncapsulation();

        System.out.println("✓ ContactModelTest: ALL TESTS PASSED");
    }

    private static void testPersonalContact() {
        PersonalContact pc = (PersonalContact) ContactBuilder.personal()
                .name("Aarav", "Sharma")
                .nickname("Aaru")
                .relationship("Friend")
                .birthday(LocalDate.of(2005, 5, 14))
                .phone("+91 98201 45678")
                .email("aarav@example.com")
                .build();

        assert "Aarav Sharma".equals(pc.getFullName()) : "PersonalContact fullName mismatch";
        assert pc.getContactType() == ContactType.PERSONAL : "ContactType mismatch";
        assert "Aaru".equals(pc.getNickname()) : "Nickname mismatch";
        assert pc.getAge() >= 18 : "Age calculation error";
        assert pc.getSummary().contains("Aaru") : "Summary missing nickname";
        assert pc.toVCard().contains("BEGIN:VCARD") : "vCard generation error";
    }

    private static void testBusinessContact() {
        BusinessContact bc = (BusinessContact) ContactBuilder.business()
                .name("Dr. Senthil", "Murugan")
                .company("University Department of CSE")
                .jobTitle("Associate Professor")
                .department("School of CS")
                .workEmail("dr.murugan@university.edu")
                .phone("+91 416 220 2000")
                .build();

        assert bc.getContactType() == ContactType.BUSINESS : "Business ContactType mismatch";
        assert "University Department of CSE".equals(bc.getCompanyName()) : "Company name mismatch";
        assert bc.getSummary().contains("Associate Professor") : "Summary missing job title";
        assert bc.getSpecificDetailsMap().containsKey("Job Title") : "Missing specific map entry";
    }

    private static void testEmergencyContact() {
        EmergencyContact ec = (EmergencyContact) ContactBuilder.emergency()
                .name("Dr. Ramesh", "Babu")
                .priority(EmergencyPriority.PRIMARY)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .phone("+91 98401 99999")
                .alternateEmergencyPhone("+91 44 2829 0200")
                .hospitalAffiliation("Apollo Hospital")
                .build();

        assert ec.getContactType() == ContactType.EMERGENCY : "Emergency ContactType mismatch";
        assert ec.getPriority() == EmergencyPriority.PRIMARY : "Priority mismatch";
        assert ec.getBloodGroup() == BloodGroup.O_POSITIVE : "Blood group mismatch";
        assert ec.getSummary().contains("PRIMARY RESPONDER") : "Summary missing priority";
    }

    private static void testPolymorphism() {
        // Polymorphic list
        Contact[] contacts = new Contact[]{
                ContactBuilder.personal().name("John", "Doe").relationship("Friend").build(),
                ContactBuilder.business().name("Alice", "Smith").company("Tech Corp").jobTitle("Architect").build(),
                ContactBuilder.emergency().name("Emergency", "HQ").priority(EmergencyPriority.PRIMARY).build()
        };

        for (Contact c : contacts) {
            // Dynamic method dispatch
            String summary = c.getSummary();
            assert summary != null && !summary.isEmpty() : "Polymorphic summary should not be empty";
            assert c.getContactType() != null : "ContactType should not be null";
        }
    }

    private static void testDefensiveCopyingAndEncapsulation() {
        PersonalContact pc = (PersonalContact) ContactBuilder.personal()
                .name("Charlie", "Brown")
                .phone("1234567890")
                .tag("Friend")
                .build();

        // Ensure tags and phone lists cannot be corrupted externally
        try {
            pc.getTags().add("MaliciousTag");
            assert false : "Tags set should be unmodifiable";
        } catch (UnsupportedOperationException expected) {}

        try {
            pc.getPhoneNumbers().add(new PhoneNumber("9999999999"));
            assert false : "Phone numbers list should be unmodifiable";
        } catch (UnsupportedOperationException expected) {}
    }
}

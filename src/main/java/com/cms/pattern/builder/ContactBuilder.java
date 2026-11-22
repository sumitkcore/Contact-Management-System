package com.cms.pattern.builder;

import com.cms.model.*;
import com.cms.pattern.factory.ContactFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * Builder Pattern implementation for flexible, fluent construction of Contacts.
 * Demonstrates the Builder Design Pattern and Method Chaining.
 */
public class ContactBuilder {
    private String id;
    private final ContactType contactType;
    private String firstName = "";
    private String lastName = "";
    private final List<PhoneNumber> phoneNumbers = new ArrayList<>();
    private String email = "";
    private String secondaryEmail = "";
    private Address address = Address.empty();
    private ContactGroup group = ContactGroup.OTHER;
    private final Set<String> tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private String notes = "";
    private boolean isFavorite = false;
    private String avatarColorHex = "";

    // Personal Contact specific
    private String relationship = "";
    private LocalDate birthday;
    private LocalDate anniversary;
    private String nickname = "";

    // Business Contact specific
    private String companyName = "";
    private String jobTitle = "";
    private String department = "";
    private String workExtension = "";
    private String workEmail = "";
    private String linkedinUrl = "";

    // Emergency Contact specific
    private EmergencyPriority priority = EmergencyPriority.PRIMARY;
    private BloodGroup bloodGroup = BloodGroup.UNKNOWN;
    private String medicalNotes = "";
    private String knownAllergies = "";
    private String alternateEmergencyPhone = "";
    private String hospitalAffiliation = "";

    public ContactBuilder(ContactType contactType) {
        this.contactType = contactType != null ? contactType : ContactType.PERSONAL;
    }

    public static ContactBuilder personal() {
        return new ContactBuilder(ContactType.PERSONAL);
    }

    public static ContactBuilder business() {
        return new ContactBuilder(ContactType.BUSINESS);
    }

    public static ContactBuilder emergency() {
        return new ContactBuilder(ContactType.EMERGENCY);
    }

    public ContactBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ContactBuilder name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }

    public ContactBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ContactBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public ContactBuilder phone(String number) {
        if (number != null && !number.trim().isEmpty()) {
            this.phoneNumbers.add(new PhoneNumber(PhoneNumber.Type.MOBILE, number.trim(), this.phoneNumbers.isEmpty()));
        }
        return this;
    }

    public ContactBuilder phone(PhoneNumber.Type type, String number, boolean isPrimary) {
        if (number != null && !number.trim().isEmpty()) {
            this.phoneNumbers.add(new PhoneNumber(type, number.trim(), isPrimary));
        }
        return this;
    }

    public ContactBuilder email(String email) {
        this.email = email != null ? email.trim() : "";
        return this;
    }

    public ContactBuilder secondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail != null ? secondaryEmail.trim() : "";
        return this;
    }

    public ContactBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public ContactBuilder address(String street, String city, String state, String postalCode, String country) {
        this.address = new Address(street, city, state, postalCode, country);
        return this;
    }

    public ContactBuilder group(ContactGroup group) {
        this.group = group;
        return this;
    }

    public ContactBuilder tag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim());
        }
        return this;
    }

    public ContactBuilder tags(String... tags) {
        if (tags != null) {
            for (String t : tags) {
                tag(t);
            }
        }
        return this;
    }

    public ContactBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    public ContactBuilder favorite(boolean favorite) {
        this.isFavorite = favorite;
        return this;
    }

    public ContactBuilder avatarColorHex(String colorHex) {
        this.avatarColorHex = colorHex;
        return this;
    }

    // Personal setters
    public ContactBuilder relationship(String relationship) {
        this.relationship = relationship;
        return this;
    }

    public ContactBuilder birthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public ContactBuilder anniversary(LocalDate anniversary) {
        this.anniversary = anniversary;
        return this;
    }

    public ContactBuilder nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    // Business setters
    public ContactBuilder company(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public ContactBuilder jobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public ContactBuilder department(String department) {
        this.department = department;
        return this;
    }

    public ContactBuilder workExtension(String workExtension) {
        this.workExtension = workExtension;
        return this;
    }

    public ContactBuilder workEmail(String workEmail) {
        this.workEmail = workEmail;
        return this;
    }

    public ContactBuilder linkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
        return this;
    }

    // Emergency setters
    public ContactBuilder priority(EmergencyPriority priority) {
        this.priority = priority;
        return this;
    }

    public ContactBuilder bloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
        return this;
    }

    public ContactBuilder medicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
        return this;
    }

    public ContactBuilder knownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public ContactBuilder alternateEmergencyPhone(String alternateEmergencyPhone) {
        this.alternateEmergencyPhone = alternateEmergencyPhone;
        return this;
    }

    public ContactBuilder hospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation;
        return this;
    }

    /**
     * Builds and returns the constructed Contact instance.
     */
    public Contact build() {
        Contact contact = ContactFactory.createContact(contactType, id, firstName, lastName);
        contact.setPhoneNumbers(phoneNumbers);
        contact.setEmail(email);
        contact.setSecondaryEmail(secondaryEmail);
        contact.setAddress(address);
        contact.setGroup(group);
        contact.setTags(tags);
        contact.setNotes(notes);
        contact.setFavorite(isFavorite);
        contact.setAvatarColorHex(avatarColorHex);

        if (contact instanceof PersonalContact) {
            PersonalContact pc = (PersonalContact) contact;
            pc.setRelationship(relationship);
            pc.setBirthday(birthday);
            pc.setAnniversary(anniversary);
            pc.setNickname(nickname);
        } else if (contact instanceof BusinessContact) {
            BusinessContact bc = (BusinessContact) contact;
            bc.setCompanyName(companyName);
            bc.setJobTitle(jobTitle);
            bc.setDepartment(department);
            bc.setWorkExtension(workExtension);
            bc.setWorkEmail(workEmail);
            bc.setLinkedinUrl(linkedinUrl);
        } else if (contact instanceof EmergencyContact) {
            EmergencyContact ec = (EmergencyContact) contact;
            ec.setPriority(priority);
            ec.setBloodGroup(bloodGroup);
            ec.setMedicalNotes(medicalNotes);
            ec.setKnownAllergies(knownAllergies);
            ec.setAlternateEmergencyPhone(alternateEmergencyPhone);
            ec.setHospitalAffiliation(hospitalAffiliation);
        }

        return contact;
    }
}

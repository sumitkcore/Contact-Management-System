package com.cms.pattern.factory;

import com.cms.model.*;

import java.util.Map;

/**
 * Factory Pattern implementation for creating specialized Contact subclasses.
 * Demonstrates the Factory Method Design Pattern.
 */
public class ContactFactory {

    /**
     * Creates a Contact instance of the specified ContactType.
     */
    public static Contact createContact(ContactType type, String firstName, String lastName) {
        if (type == null) {
            type = ContactType.PERSONAL;
        }

        switch (type) {
            case BUSINESS:
                return new BusinessContact(firstName, lastName);
            case EMERGENCY:
                return new EmergencyContact(firstName, lastName);
            case PERSONAL:
            default:
                return new PersonalContact(firstName, lastName);
        }
    }

    /**
     * Overloaded factory method accepting an ID.
     */
    public static Contact createContact(ContactType type, String id, String firstName, String lastName) {
        if (type == null) {
            type = ContactType.PERSONAL;
        }

        switch (type) {
            case BUSINESS:
                return new BusinessContact(id, firstName, lastName);
            case EMERGENCY:
                return new EmergencyContact(id, firstName, lastName);
            case PERSONAL:
            default:
                return new PersonalContact(id, firstName, lastName);
        }
    }
}

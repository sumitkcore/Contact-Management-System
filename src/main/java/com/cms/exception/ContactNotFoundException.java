package com.cms.exception;

/**
 * Checked exception thrown when a contact search or lookup by ID fails.
 */
public class ContactNotFoundException extends ContactManagementException {
    private static final long serialVersionUID = 1L;

    private final String contactId;

    public ContactNotFoundException(String contactId) {
        super("Contact with identifier [" + contactId + "] was not found.");
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }
}

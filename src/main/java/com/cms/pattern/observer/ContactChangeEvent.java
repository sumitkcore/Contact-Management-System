package com.cms.pattern.observer;

import com.cms.model.Contact;

/**
 * Event object passed to ContactChangeListeners.
 */
public class ContactChangeEvent {
    private final ContactEventType eventType;
    private final Contact contact;
    private final String message;

    public ContactChangeEvent(ContactEventType eventType, Contact contact, String message) {
        this.eventType = eventType;
        this.contact = contact;
        this.message = message;
    }

    public ContactChangeEvent(ContactEventType eventType, Contact contact) {
        this(eventType, contact, "");
    }

    public ContactEventType getEventType() {
        return eventType;
    }

    public Contact getContact() {
        return contact;
    }

    public String getMessage() {
        return message;
    }
}

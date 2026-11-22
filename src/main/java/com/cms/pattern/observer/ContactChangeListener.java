package com.cms.pattern.observer;

/**
 * Observer interface for components that listen to Contact events.
 * Demonstrates the Observer Design Pattern.
 */
public interface ContactChangeListener {
    /**
     * Triggered when a contact is added, modified, deleted, or restored.
     */
    void onContactChanged(ContactChangeEvent event);
}

package com.cms.pattern.strategy;

import com.cms.model.Contact;
import java.util.Comparator;

/**
 * Strategy interface defining sorting algorithms for contacts.
 * Demonstrates the Strategy Design Pattern.
 */
public interface ContactSortingStrategy {
    /**
     * Returns a Comparator matching this sorting strategy.
     */
    Comparator<Contact> getComparator();

    /**
     * Human-readable label for UI selection dropdowns.
     */
    String getDisplayName();
}

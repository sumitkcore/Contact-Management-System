package com.cms.dao;

import com.cms.model.Contact;
import com.cms.model.ContactGroup;
import com.cms.model.ContactType;

import java.util.List;
import java.util.Map;

/**
 * Specialized Contact DAO Interface extending GenericDAO.
 * Defines comprehensive search, filtering, and aggregation queries.
 */
public interface ContactDAO extends GenericDAO<Contact, String> {

    /**
     * Searches contacts matching query across names, phones, emails, and tags.
     */
    List<Contact> search(String query);

    /**
     * Finds contacts belonging to a specific category (Personal, Business, Emergency).
     */
    List<Contact> findByType(ContactType type);

    /**
     * Finds contacts belonging to a specific group (Family, Friends, Colleagues, etc.).
     */
    List<Contact> findByGroup(ContactGroup group);

    /**
     * Finds contacts associated with a specific tag.
     */
    List<Contact> findByTag(String tag);

    /**
     * Retrieves all contacts marked as favorite.
     */
    List<Contact> findFavorites();

    /**
     * Retrieves soft-deleted contacts currently in the Recycle Bin.
     */
    List<Contact> findDeleted();

    /**
     * Restores a soft-deleted contact from the Recycle Bin.
     */
    boolean restore(String id);

    /**
     * Permanently purges a contact from storage.
     */
    boolean permanentDelete(String id);

    /**
     * Empties the entire Recycle Bin.
     */
    int emptyTrash();

    /**
     * Returns count of active (non-deleted) contacts.
     */
    long countActive();

    /**
     * Returns count of favorite contacts.
     */
    long countFavorites();

    /**
     * Returns a distribution map of contacts by ContactType.
     */
    Map<ContactType, Integer> getCategoryDistribution();

    /**
     * Returns all unique tags across all contacts.
     */
    List<String> getAllTags();

    /**
     * Checks if a phone number already exists (excluding a specific contact ID for updates).
     */
    boolean existsByPhone(String phone, String excludeId);

    /**
     * Checks if an email already exists (excluding a specific contact ID for updates).
     */
    boolean existsByEmail(String email, String excludeId);

    /**
     * Flushes any cached changes to persistent disk storage.
     */
    void flush();
}

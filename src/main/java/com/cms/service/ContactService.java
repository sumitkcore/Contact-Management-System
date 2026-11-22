package com.cms.service;

import com.cms.exception.*;
import com.cms.model.*;
import com.cms.pattern.strategy.ContactSortingStrategy;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Layer Interface defining high-level business operations.
 */
public interface ContactService {

    /**
     * Creates and validates a new contact.
     * @throws InvalidContactDataException if validation fails.
     * @throws DuplicateContactException if duplicate phone/email detected.
     */
    Contact createContact(Contact contact) throws InvalidContactDataException, DuplicateContactException;

    /**
     * Updates an existing contact.
     * @throws InvalidContactDataException if validation fails.
     * @throws DuplicateContactException if duplicate phone/email detected.
     * @throws ContactNotFoundException if contact does not exist.
     */
    Contact updateContact(Contact contact) throws InvalidContactDataException, DuplicateContactException, ContactNotFoundException;

    /**
     * Moves a contact to the Recycle Bin (Soft delete).
     */
    boolean deleteContact(String id) throws ContactNotFoundException;

    /**
     * Restores a contact from the Recycle Bin.
     */
    boolean restoreContact(String id) throws ContactNotFoundException;

    /**
     * Permanently deletes a contact from the system.
     */
    boolean permanentlyDeleteContact(String id) throws ContactNotFoundException;

    /**
     * Empties the entire Recycle Bin.
     */
    int emptyTrash();

    /**
     * Toggles favorite status of a contact.
     */
    boolean toggleFavorite(String id) throws ContactNotFoundException;

    /**
     * Adds an interactive call record to a contact.
     */
    void logCall(String contactId, CallType type, int durationSeconds, String notes) throws ContactNotFoundException;

    /**
     * Retrieves contact by unique ID.
     */
    Optional<Contact> getContactById(String id);

    /**
     * Retrieves all active contacts sorted by the given strategy.
     */
    List<Contact> getAllContacts(ContactSortingStrategy sortingStrategy);

    /**
     * Searches active contacts by query string.
     */
    List<Contact> searchContacts(String query, ContactSortingStrategy sortingStrategy);

    /**
     * Filters contacts by category.
     */
    List<Contact> getContactsByType(ContactType type, ContactSortingStrategy sortingStrategy);

    /**
     * Filters contacts by predefined group.
     */
    List<Contact> getContactsByGroup(ContactGroup group, ContactSortingStrategy sortingStrategy);

    /**
     * Filters contacts by tag.
     */
    List<Contact> getContactsByTag(String tag, ContactSortingStrategy sortingStrategy);

    /**
     * Retrieves favorite contacts.
     */
    List<Contact> getFavoriteContacts(ContactSortingStrategy sortingStrategy);

    /**
     * Retrieves contacts currently in the Recycle Bin.
     */
    List<Contact> getDeletedContacts();

    /**
     * Bulk imports contacts from external file with duplicate check.
     */
    int bulkImport(List<Contact> importedContacts);

    /**
     * Returns total active contacts count.
     */
    long getTotalActiveCount();

    /**
     * Returns total favorite contacts count.
     */
    long getTotalFavoriteCount();

    /**
     * Returns distribution counts across Personal, Business, and Emergency.
     */
    Map<ContactType, Integer> getCategoryDistribution();

    /**
     * Returns list of all unique tags in use.
     */
    List<String> getAllTags();

    /**
     * Triggers manual flush of dirty state to persistent storage.
     */
    void flushStorage();
}

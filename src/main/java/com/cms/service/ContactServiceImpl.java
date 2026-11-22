
package com.cms.service;

import com.cms.dao.ContactDAO;
import com.cms.dao.DAOFactory;
import com.cms.exception.*;
import com.cms.io.AuditLogger;
import com.cms.model.*;
import com.cms.pattern.observer.ContactChangeEvent;
import com.cms.pattern.observer.ContactEventManager;
import com.cms.pattern.observer.ContactEventType;
import com.cms.pattern.strategy.ContactSortingStrategy;
import com.cms.pattern.strategy.NameAscendingStrategy;
import com.cms.validation.ContactValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Contact Service Implementation.
 *
 * Responsibilities:
 * 1. Business logic and validation
 * 2. Contact CRUD operations
 * 3. Category, favorites and tag filtering
 * 4. Search and sorting
 * 5. Observer Pattern integration
 * 6. Audit logging
 * 7. Recycle Bin operations
 * 8. Bulk import
 */
public class ContactServiceImpl implements ContactService {

    private ContactDAO contactDAO;

    private final ContactEventManager eventManager;

    private final AuditLogger auditLogger;

    // ---------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------

    public ContactServiceImpl() {
        this(DAOFactory.getContactDAO());
    }

    public ContactServiceImpl(ContactDAO contactDAO) {

        if (contactDAO == null) {
            throw new IllegalArgumentException(
                    "ContactDAO cannot be null."
            );
        }

        this.contactDAO = contactDAO;

        this.eventManager = ContactEventManager.getInstance();

        this.auditLogger = AuditLogger.getInstance();
    }

    // ---------------------------------------------------------
    // DAO REFRESH
    // ---------------------------------------------------------

    public void refreshDAO() {

        ContactDAO newDAO = DAOFactory.getContactDAO();

        if (newDAO == null) {
            throw new IllegalStateException(
                    "Unable to initialize ContactDAO."
            );
        }

        this.contactDAO = newDAO;
    }

    // ---------------------------------------------------------
    // CREATE CONTACT
    // ---------------------------------------------------------

    @Override
    public Contact createContact(Contact contact)
            throws InvalidContactDataException,
                   DuplicateContactException {

        if (contact == null) {
            throw new IllegalArgumentException(
                    "Contact cannot be null."
            );
        }

        ValidationResult result =
                ContactValidator.validate(contact);

        if (!result.isValid()) {
            throw new InvalidContactDataException(result);
        }

        // Check duplicate phone number.
        String phone = contact.getPrimaryPhoneNumber();

        if (phone != null
                && !phone.trim().isEmpty()
                && contactDAO.existsByPhone(phone, null)) {

            throw new DuplicateContactException(
                    "Phone Number",
                    phone
            );
        }

        // Check duplicate email.
        String email = contact.getEmail();

        if (email != null
                && !email.trim().isEmpty()
                && contactDAO.existsByEmail(email, null)) {

            throw new DuplicateContactException(
                    "Email Address",
                    email
            );
        }

        contactDAO.save(contact);

        auditLogger.log(
                new AuditLog(
                        AuditAction.CREATE,
                        contact.getId(),
                        contact.getFullName(),
                        "Created new "
                                + contact.getContactType().getDisplayName()
                                + " contact."
                )
        );

        eventManager.fireEvent(
                new ContactChangeEvent(
                        ContactEventType.ADDED,
                        contact
                )
        );

        return contact;
    }

    // ---------------------------------------------------------
    // UPDATE CONTACT
    // ---------------------------------------------------------

    @Override
    public Contact updateContact(Contact contact)
            throws InvalidContactDataException,
                   DuplicateContactException,
                   ContactNotFoundException {

        if (contact == null || contact.getId() == null) {
            throw new IllegalArgumentException(
                    "Invalid contact parameter."
            );
        }

        Optional<Contact> existing =
                contactDAO.findById(contact.getId());

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(contact.getId());
        }

        ValidationResult result =
                ContactValidator.validate(contact);

        if (!result.isValid()) {
            throw new InvalidContactDataException(result);
        }

        String phone = contact.getPrimaryPhoneNumber();

        if (phone != null
                && !phone.trim().isEmpty()
                && contactDAO.existsByPhone(
                        phone,
                        contact.getId()
                )) {

            throw new DuplicateContactException(
                    "Phone Number",
                    phone
            );
        }

        String email = contact.getEmail();

        if (email != null
                && !email.trim().isEmpty()
                && contactDAO.existsByEmail(
                        email,
                        contact.getId()
                )) {

            throw new DuplicateContactException(
                    "Email Address",
                    email
            );
        }

        contactDAO.update(contact);

        auditLogger.log(
                new AuditLog(
                        AuditAction.UPDATE,
                        contact.getId(),
                        contact.getFullName(),
                        "Updated details for "
                                + contact.getFullName() + "."
                )
        );

        eventManager.fireEvent(
                new ContactChangeEvent(
                        ContactEventType.UPDATED,
                        contact
                )
        );

        return contact;
    }

    // ---------------------------------------------------------
    // DELETE CONTACT
    // ---------------------------------------------------------

    @Override
    public boolean deleteContact(String id)
            throws ContactNotFoundException {

        Optional<Contact> existing =
                contactDAO.findById(id);

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(id);
        }

        Contact contact = existing.get();

        boolean deleted = contactDAO.delete(id);

        if (deleted) {

            auditLogger.log(
                    new AuditLog(
                            AuditAction.DELETE,
                            id,
                            contact.getFullName(),
                            "Moved contact to Recycle Bin."
                    )
            );

            eventManager.fireEvent(
                    new ContactChangeEvent(
                            ContactEventType.DELETED,
                            contact
                    )
            );
        }

        return deleted;
    }

    // ---------------------------------------------------------
    // RESTORE CONTACT
    // ---------------------------------------------------------

    @Override
    public boolean restoreContact(String id)
            throws ContactNotFoundException {

        Optional<Contact> existing =
                contactDAO.findById(id);

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(id);
        }

        Contact contact = existing.get();

        boolean restored = contactDAO.restore(id);

        if (restored) {

            auditLogger.log(
                    new AuditLog(
                            AuditAction.RESTORE,
                            id,
                            contact.getFullName(),
                            "Restored contact from Recycle Bin."
                    )
            );

            eventManager.fireEvent(
                    new ContactChangeEvent(
                            ContactEventType.RESTORED,
                            contact
                    )
            );
        }

        return restored;
    }

    // ---------------------------------------------------------
    // PERMANENT DELETE
    // ---------------------------------------------------------

    @Override
    public boolean permanentlyDeleteContact(String id)
            throws ContactNotFoundException {

        Optional<Contact> existing =
                contactDAO.findById(id);

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(id);
        }

        Contact contact = existing.get();

        boolean deleted = contactDAO.permanentDelete(id);

        if (deleted) {

            auditLogger.log(
                    new AuditLog(
                            AuditAction.PERMANENT_DELETE,
                            id,
                            contact.getFullName(),
                            "Permanently removed contact."
                    )
            );

            eventManager.fireEvent(
                    new ContactChangeEvent(
                            ContactEventType.PERMANENTLY_DELETED,
                            contact
                    )
            );
        }

        return deleted;
    }

    // ---------------------------------------------------------
    // EMPTY RECYCLE BIN
    // ---------------------------------------------------------

    @Override
    public int emptyTrash() {

        int count = contactDAO.emptyTrash();

        if (count > 0) {

            auditLogger.log(
                    new AuditLog(
                            AuditAction.PERMANENT_DELETE,
                            "ALL",
                            "Recycle Bin",
                            "Emptied " + count + " items from trash."
                    )
            );

            eventManager.fireEvent(
                    new ContactChangeEvent(
                            ContactEventType.PERMANENTLY_DELETED,
                            null,
                            "Trash emptied"
                    )
            );
        }

        return count;
    }

    // ---------------------------------------------------------
    // FAVORITES
    // ---------------------------------------------------------

    @Override
    public boolean toggleFavorite(String id)
            throws ContactNotFoundException {

        Optional<Contact> existing =
                contactDAO.findById(id);

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(id);
        }

        Contact contact = existing.get();

        contact.setFavorite(!contact.isFavorite());

        contactDAO.update(contact);

        eventManager.fireEvent(
                new ContactChangeEvent(
                        ContactEventType.UPDATED,
                        contact
                )
        );

        return contact.isFavorite();
    }

    // ---------------------------------------------------------
    // CALL LOGGING
    // ---------------------------------------------------------

    @Override
    public void logCall(
            String contactId,
            CallType type,
            int durationSeconds,
            String notes
    ) throws ContactNotFoundException {

        Optional<Contact> existing =
                contactDAO.findById(contactId);

        if (!existing.isPresent()) {
            throw new ContactNotFoundException(contactId);
        }

        Contact contact = existing.get();

        CallLog log = new CallLog(
                contactId,
                type,
                durationSeconds,
                notes
        );

        contact.addCallLog(log);

        contactDAO.update(contact);

        auditLogger.log(
                new AuditLog(
                        AuditAction.CALL_LOGGED,
                        contactId,
                        contact.getFullName(),
                        "Logged "
                                + type.getLabel()
                                + " ("
                                + log.getFormattedDuration()
                                + ")."
                )
        );

        eventManager.fireEvent(
                new ContactChangeEvent(
                        ContactEventType.UPDATED,
                        contact
                )
        );
    }

    // ---------------------------------------------------------
    // GET CONTACT BY ID
    // ---------------------------------------------------------

    @Override
    public Optional<Contact> getContactById(String id) {

        return contactDAO.findById(id);
    }

    // ---------------------------------------------------------
    // GET ALL ACTIVE CONTACTS
    // ---------------------------------------------------------

    @Override
    public List<Contact> getAllContacts(
            ContactSortingStrategy sortingStrategy
    ) {

        List<Contact> contacts = contactDAO.findAll();

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // SEARCH CONTACTS
    // ---------------------------------------------------------

    @Override
    public List<Contact> searchContacts(
            String query,
            ContactSortingStrategy sortingStrategy
    ) {

        if (query == null || query.trim().isEmpty()) {

            return getAllContacts(sortingStrategy);
        }

        List<Contact> contacts =
                contactDAO.search(query.trim());

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // FILTER BY CATEGORY
    // ---------------------------------------------------------

    @Override
    public List<Contact> getContactsByType(
            ContactType type,
            ContactSortingStrategy sortingStrategy
    ) {

        if (type == null) {
            return new ArrayList<>();
        }

        List<Contact> contacts =
                contactDAO.findByType(type);

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // FILTER BY GROUP
    // ---------------------------------------------------------

    @Override
    public List<Contact> getContactsByGroup(
            ContactGroup group,
            ContactSortingStrategy sortingStrategy
    ) {

        if (group == null) {
            return new ArrayList<>();
        }

        List<Contact> contacts =
                contactDAO.findByGroup(group);

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // FILTER BY TAG
    // ---------------------------------------------------------

    @Override
    public List<Contact> getContactsByTag(
            String tag,
            ContactSortingStrategy sortingStrategy
    ) {

        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Contact> contacts =
                contactDAO.findByTag(tag.trim());

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // GET FAVORITE CONTACTS
    // ---------------------------------------------------------

    @Override
    public List<Contact> getFavoriteContacts(
            ContactSortingStrategy sortingStrategy
    ) {

        List<Contact> contacts =
                contactDAO.findFavorites();

        return sortContacts(contacts, sortingStrategy);
    }

    // ---------------------------------------------------------
    // GET DELETED CONTACTS
    // ---------------------------------------------------------

    @Override
    public List<Contact> getDeletedContacts() {

        return contactDAO.findDeleted();
    }

    // ---------------------------------------------------------
    // BULK IMPORT
    // ---------------------------------------------------------

    @Override
    public int bulkImport(List<Contact> importedContacts) {

        if (importedContacts == null || importedContacts.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (Contact contact : importedContacts) {

            if (contact == null) {
                continue;
            }

            try {

                String id = contact.getId();

                if (id == null
                        || id.trim().isEmpty()
                        || contactDAO.findById(id).isPresent()) {

                    contact.setId(UUID.randomUUID().toString());
                }

                contactDAO.save(contact);

                count++;

            } catch (Exception e) {

                System.err.println(
                        "Warning: Skipped importing contact "
                                + contact.getFullName()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        if (count > 0) {

            auditLogger.log(
                    new AuditLog(
                            AuditAction.IMPORT,
                            "BULK",
                            "Batch Import",
                            "Imported " + count + " contacts successfully."
                    )
            );

            eventManager.fireEvent(
                    new ContactChangeEvent(
                            ContactEventType.BULK_IMPORTED,
                            null,
                            "Imported " + count + " contacts."
                    )
            );
        }

        return count;
    }

    // ---------------------------------------------------------
    // CONTACT COUNTS
    // ---------------------------------------------------------

    @Override
    public long getTotalActiveCount() {

        return contactDAO.countActive();
    }

    @Override
    public long getTotalFavoriteCount() {

        return contactDAO.countFavorites();
    }

    // ---------------------------------------------------------
    // CATEGORY DISTRIBUTION
    // ---------------------------------------------------------

    @Override
    public Map<ContactType, Integer> getCategoryDistribution() {

        return contactDAO.getCategoryDistribution();
    }

    // ---------------------------------------------------------
    // GET ALL TAGS
    // ---------------------------------------------------------

    @Override
    public List<String> getAllTags() {

        return contactDAO.getAllTags();
    }

    // ---------------------------------------------------------
    // FLUSH STORAGE
    // ---------------------------------------------------------

    @Override
    public void flushStorage() {

        contactDAO.flush();
    }

    // ---------------------------------------------------------
    // SORTING HELPER
    // ---------------------------------------------------------

    private List<Contact> sortContacts(
            List<Contact> contacts,
            ContactSortingStrategy strategy
    ) {

        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        if (strategy != null) {

            contacts.sort(strategy.getComparator());

        } else {

            contacts.sort(
                    new NameAscendingStrategy().getComparator()
            );
        }

        return contacts;
    }
}

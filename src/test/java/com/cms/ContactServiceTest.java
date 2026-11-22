package com.cms;

import com.cms.dao.FileContactDAOImpl;
import com.cms.exception.DuplicateContactException;
import com.cms.exception.InvalidContactDataException;
import com.cms.model.Contact;
import com.cms.pattern.builder.ContactBuilder;
import com.cms.pattern.strategy.NameAscendingStrategy;
import com.cms.service.ContactService;
import com.cms.service.ContactServiceImpl;

import java.io.File;
import java.util.List;

/**
 * Unit Test for Service Layer Business Logic:
 * - CRUD operations
 * - Duplicate Detection
 * - Search & Filter
 * - Recycle Bin & Soft Delete
 */
public class ContactServiceTest {

    public static void runTests() throws Exception {
        System.out.println("Running ContactServiceTest...");

        File tempStorage = File.createTempFile("test_cms_", ".dat");
        tempStorage.deleteOnExit();

        ContactService service = new ContactServiceImpl(new FileContactDAOImpl(tempStorage));

        testCreateContact(service);
        testDuplicatePhoneRejection(service);
        testDuplicateEmailRejection(service);
        testSoftDeleteAndRestore(service);
        testSearchFilter(service);

        System.out.println("✓ ContactServiceTest: ALL TESTS PASSED");
    }

    private static void testCreateContact(ContactService service) throws Exception {
        Contact c = ContactBuilder.personal()
                .name("Aman", "Gupta")
                .phone("+91 99112 23344")
                .email("aman.gupta@boat.com")
                .tag("Shark")
                .build();

        Contact created = service.createContact(c);
        assert created.getId() != null : "Created contact ID cannot be null";
        assert service.getContactById(created.getId()).isPresent() : "Contact should be found in service";
    }

    private static void testDuplicatePhoneRejection(ContactService service) throws Exception {
        Contact duplicate = ContactBuilder.personal()
                .name("Another", "Gupta")
                .phone("+91 99112 23344") // Duplicate of Aman Gupta's phone
                .email("another.gupta@example.com")
                .build();

        try {
            service.createContact(duplicate);
            assert false : "Duplicate phone number should throw DuplicateContactException";
        } catch (DuplicateContactException expected) {
            assert expected.getConflictField().contains("Phone") : "Conflict field should mention Phone";
        }
    }

    private static void testDuplicateEmailRejection(ContactService service) throws Exception {
        Contact duplicate = ContactBuilder.personal()
                .name("Fake", "Account")
                .phone("+91 88888 77777")
                .email("aman.gupta@boat.com") // Duplicate of Aman Gupta's email
                .build();

        try {
            service.createContact(duplicate);
            assert false : "Duplicate email should throw DuplicateContactException";
        } catch (DuplicateContactException expected) {
            assert expected.getConflictField().contains("Email") : "Conflict field should mention Email";
        }
    }

    private static void testSoftDeleteAndRestore(ContactService service) throws Exception {
        Contact c = ContactBuilder.personal()
                .name("Temp", "DeleteUser")
                .phone("+91 99999 11111")
                .email("temp.delete@example.com")
                .build();

        service.createContact(c);
        assert service.getContactById(c.getId()).isPresent();

        // Delete (Soft)
        service.deleteContact(c.getId());
        List<Contact> active = service.getAllContacts(new NameAscendingStrategy());
        for (Contact a : active) {
            assert !a.getId().equals(c.getId()) : "Soft-deleted contact should not appear in active contacts";
        }

        List<Contact> trash = service.getDeletedContacts();
        boolean foundInTrash = trash.stream().anyMatch(t -> t.getId().equals(c.getId()));
        assert foundInTrash : "Deleted contact must be in Recycle Bin";

        // Restore
        service.restoreContact(c.getId());
        List<Contact> activeAfterRestore = service.getAllContacts(new NameAscendingStrategy());
        boolean restored = activeAfterRestore.stream().anyMatch(a -> a.getId().equals(c.getId()));
        assert restored : "Restored contact should appear in active contacts list";
    }

    private static void testSearchFilter(ContactService service) {
        List<Contact> results = service.searchContacts("Aman", new NameAscendingStrategy());
        assert !results.isEmpty() : "Search for 'Aman' should return matches";
        assert results.get(0).getFirstName().contains("Aman") : "Search result mismatch";
    }
}

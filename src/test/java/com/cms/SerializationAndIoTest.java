package com.cms;

import com.cms.io.CsvHandler;
import com.cms.io.JsonHandler;
import com.cms.io.ObjectSerializationHandler;
import com.cms.io.VCardHandler;
import com.cms.model.Contact;
import com.cms.pattern.builder.ContactBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit Test for File I/O, Object Serialization, CSV, and vCard processing.
 */
public class SerializationAndIoTest {

    public static void runTests() throws Exception {
        System.out.println("Running SerializationAndIoTest...");

        List<Contact> testContacts = new ArrayList<>();
        testContacts.add(ContactBuilder.personal().name("Aarav", "Sharma").phone("+91 98201 45678").email("aarav@test.com").tag("College").build());
        testContacts.add(ContactBuilder.business().name("Dr. Senthil", "Murugan").company("VIT").jobTitle("Professor").phone("+91 416 220 2000").build());

        testObjectSerialization(testContacts);
        testCsvExportAndImport(testContacts);
        testVCardExportAndImport(testContacts);
        testJsonExport(testContacts);

        System.out.println("✓ SerializationAndIoTest: ALL TESTS PASSED");
    }

    private static void testObjectSerialization(List<Contact> original) throws Exception {
        File temp = File.createTempFile("test_ser_", ".dat");
        temp.deleteOnExit();

        ObjectSerializationHandler.saveToFile(original, temp);
        assert temp.exists() && temp.length() > 0 : "Serialized file not created";

        List<Contact> loaded = ObjectSerializationHandler.loadFromFile(temp);
        assert loaded.size() == original.size() : "Loaded count mismatch";
        assert loaded.get(0).getFullName().equals(original.get(0).getFullName()) : "Contact data corrupted in serialization";
    }

    private static void testCsvExportAndImport(List<Contact> original) throws Exception {
        File temp = File.createTempFile("test_csv_", ".csv");
        temp.deleteOnExit();

        CsvHandler.exportToCsv(original, temp);
        assert temp.exists() && temp.length() > 0 : "CSV file empty";

        List<Contact> imported = CsvHandler.importFromCsv(temp);
        assert imported.size() == original.size() : "CSV imported size mismatch: expected " + original.size() + ", got " + imported.size();
        assert imported.get(0).getFirstName().equals("Aarav") : "CSV first name mismatch";
    }

    private static void testVCardExportAndImport(List<Contact> original) throws Exception {
        File temp = File.createTempFile("test_vcard_", ".vcf");
        temp.deleteOnExit();

        VCardHandler.exportToVCard(original, temp);
        assert temp.exists() && temp.length() > 0 : "vCard file empty";

        List<Contact> imported = VCardHandler.importFromVCard(temp);
        assert imported.size() == original.size() : "vCard imported count mismatch";
        assert imported.get(0).getFullName().contains("Aarav") : "vCard parsed name mismatch";
    }

    private static void testJsonExport(List<Contact> original) throws Exception {
        File temp = File.createTempFile("test_json_", ".json");
        temp.deleteOnExit();

        JsonHandler.exportToJson(original, temp);
        assert temp.exists() && temp.length() > 0 : "JSON file empty";
    }
}

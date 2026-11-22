package com.cms.io;

import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard vCard 3.0 (.vcf) Import / Export Handler.
 * Compatible with Android, iOS, macOS Contacts, Microsoft Outlook, and Google Contacts.
 */
public class VCardHandler {

    public static void exportToVCard(List<Contact> contacts, File destinationFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile), StandardCharsets.UTF_8))) {
            for (Contact contact : contacts) {
                if (!contact.isDeleted()) {
                    writer.write(contact.toVCard());
                    writer.newLine();
                }
            }
        }
    }

    public static List<Contact> importFromVCard(File file) throws IOException {
        List<Contact> contacts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            ContactBuilder currentBuilder = null;
            ContactType detectedType = ContactType.PERSONAL;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("BEGIN:VCARD")) {
                    detectedType = ContactType.PERSONAL;
                    currentBuilder = ContactBuilder.personal();
                } else if (line.equalsIgnoreCase("END:VCARD")) {
                    if (currentBuilder != null) {
                        try {
                            contacts.add(currentBuilder.build());
                        } catch (Exception ignored) {}
                    }
                    currentBuilder = null;
                } else if (currentBuilder != null) {
                    parseVCardLine(line, currentBuilder);
                }
            }
        }
        return contacts;
    }

    private static void parseVCardLine(String line, ContactBuilder b) {
        int colonIdx = line.indexOf(':');
        if (colonIdx == -1) return;

        String keyPart = line.substring(0, colonIdx).toUpperCase();
        String valPart = line.substring(colonIdx + 1).trim();

        if (keyPart.startsWith("FN")) {
            String[] parts = valPart.split("\\s+", 2);
            b.firstName(parts[0]);
            if (parts.length > 1) b.lastName(parts[1]);
        } else if (keyPart.startsWith("N")) {
            String[] nParts = valPart.split(";");
            if (nParts.length > 1 && !nParts[1].isEmpty()) b.firstName(nParts[1]);
            if (nParts.length > 0 && !nParts[0].isEmpty()) b.lastName(nParts[0]);
        } else if (keyPart.startsWith("TEL")) {
            b.phone(valPart);
        } else if (keyPart.startsWith("EMAIL")) {
            b.email(valPart);
        } else if (keyPart.startsWith("ORG")) {
            String[] orgParts = valPart.split(";");
            b.company(orgParts[0]);
            if (orgParts.length > 1) b.department(orgParts[1]);
        } else if (keyPart.startsWith("TITLE")) {
            b.jobTitle(valPart);
        } else if (keyPart.startsWith("ADR")) {
            String[] adr = valPart.split(";");
            String street = adr.length > 2 ? adr[2] : "";
            String city = adr.length > 3 ? adr[3] : "";
            String state = adr.length > 4 ? adr[4] : "";
            String zip = adr.length > 5 ? adr[5] : "";
            String country = adr.length > 6 ? adr[6] : "";
            b.address(street, city, state, zip, country);
        } else if (keyPart.startsWith("NOTE")) {
            b.notes(valPart.replace("\\n", "\n"));
        } else if (keyPart.startsWith("CATEGORIES")) {
            String[] cats = valPart.split(",");
            b.tags(cats);
        } else if (keyPart.startsWith("NICKNAME")) {
            b.nickname(valPart);
        } else if (keyPart.startsWith("BDAY")) {
            try {
                b.birthday(java.time.LocalDate.parse(valPart));
            } catch (Exception ignored) {}
        }
    }
}

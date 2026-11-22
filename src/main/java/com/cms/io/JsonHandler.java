package com.cms.io;

import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java JSON Serializer and Deserializer.
 * Requires ZERO external third-party libraries, guaranteeing 100% portability.
 */
public class JsonHandler {

    public static void exportToJson(List<Contact> contacts, File destinationFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            if (c.isDeleted()) continue;
            sb.append("  {\n");
            sb.append("    \"id\": \"").append(escapeJson(c.getId())).append("\",\n");
            sb.append("    \"type\": \"").append(c.getContactType().name()).append("\",\n");
            sb.append("    \"firstName\": \"").append(escapeJson(c.getFirstName())).append("\",\n");
            sb.append("    \"lastName\": \"").append(escapeJson(c.getLastName())).append("\",\n");
            sb.append("    \"email\": \"").append(escapeJson(c.getEmail())).append("\",\n");
            sb.append("    \"secondaryEmail\": \"").append(escapeJson(c.getSecondaryEmail())).append("\",\n");
            sb.append("    \"group\": \"").append(c.getGroup().name()).append("\",\n");
            sb.append("    \"favorite\": ").append(c.isFavorite()).append(",\n");
            sb.append("    \"notes\": \"").append(escapeJson(c.getNotes())).append("\",\n");

            // Address
            Address a = c.getAddress();
            sb.append("    \"address\": {\n");
            sb.append("      \"street\": \"").append(escapeJson(a.getStreet())).append("\",\n");
            sb.append("      \"city\": \"").append(escapeJson(a.getCity())).append("\",\n");
            sb.append("      \"state\": \"").append(escapeJson(a.getState())).append("\",\n");
            sb.append("      \"postalCode\": \"").append(escapeJson(a.getPostalCode())).append("\",\n");
            sb.append("      \"country\": \"").append(escapeJson(a.getCountry())).append("\"\n");
            sb.append("    },\n");

            // Phone numbers
            sb.append("    \"phoneNumbers\": [\n");
            List<PhoneNumber> phones = c.getPhoneNumbers();
            for (int p = 0; p < phones.size(); p++) {
                PhoneNumber pn = phones.get(p);
                sb.append("      {\"type\": \"").append(pn.getType().name()).append("\", \"number\": \"")
                        .append(escapeJson(pn.getNumber())).append("\", \"primary\": ").append(pn.isPrimary()).append("}");
                if (p < phones.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("    ],\n");

            // Tags
            sb.append("    \"tags\": [");
            int tIdx = 0;
            for (String tag : c.getTags()) {
                if (tIdx++ > 0) sb.append(", ");
                sb.append("\"").append(escapeJson(tag)).append("\"");
            }
            sb.append("],\n");

            // Subtype specific
            if (c instanceof PersonalContact) {
                PersonalContact pc = (PersonalContact) c;
                sb.append("    \"relationship\": \"").append(escapeJson(pc.getRelationship())).append("\",\n");
                sb.append("    \"nickname\": \"").append(escapeJson(pc.getNickname())).append("\",\n");
                sb.append("    \"birthday\": \"").append(pc.getBirthday() != null ? pc.getBirthday().toString() : "").append("\"\n");
            } else if (c instanceof BusinessContact) {
                BusinessContact bc = (BusinessContact) c;
                sb.append("    \"company\": \"").append(escapeJson(bc.getCompanyName())).append("\",\n");
                sb.append("    \"jobTitle\": \"").append(escapeJson(bc.getJobTitle())).append("\",\n");
                sb.append("    \"department\": \"").append(escapeJson(bc.getDepartment())).append("\",\n");
                sb.append("    \"workEmail\": \"").append(escapeJson(bc.getWorkEmail())).append("\",\n");
                sb.append("    \"linkedinUrl\": \"").append(escapeJson(bc.getLinkedinUrl())).append("\"\n");
            } else if (c instanceof EmergencyContact) {
                EmergencyContact ec = (EmergencyContact) c;
                sb.append("    \"priority\": \"").append(ec.getPriority().name()).append("\",\n");
                sb.append("    \"bloodGroup\": \"").append(ec.getBloodGroup().name()).append("\",\n");
                sb.append("    \"alternatePhone\": \"").append(escapeJson(ec.getAlternateEmergencyPhone())).append("\",\n");
                sb.append("    \"medicalNotes\": \"").append(escapeJson(ec.getMedicalNotes())).append("\",\n");
                sb.append("    \"knownAllergies\": \"").append(escapeJson(ec.getKnownAllergies())).append("\"\n");
            } else {
                sb.append("    \"custom\": true\n");
            }

            sb.append("  }");
            if (i < contacts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(destinationFile), StandardCharsets.UTF_8)) {
            writer.write(sb.toString());
        }
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

package com.cms.io;

import com.cms.exception.StorageException;
import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV Import / Export Handler following RFC-4180 standard.
 * Handles commas inside quotes, line breaks, and multi-value fields.
 */
public class CsvHandler {

    private static final String HEADER = "ID,Type,FirstName,LastName,PrimaryPhone,SecondaryPhone,Email,SecondaryEmail,Street,City,State,PostalCode,Country,Group,Tags,Favorite,Notes,Specific1,Specific2,Specific3";

    public static void exportToCsv(List<Contact> contacts, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println(HEADER);
            for (Contact c : contacts) {
                if (c.isDeleted()) continue;
                writer.println(contactToCsvLine(c));
            }
        }
    }

    private static String contactToCsvLine(Contact c) {
        List<String> values = new ArrayList<>();
        values.add(c.getId());
        values.add(c.getContactType().name());
        values.add(c.getFirstName());
        values.add(c.getLastName());

        List<PhoneNumber> phones = c.getPhoneNumbers();
        values.add(phones.size() > 0 ? phones.get(0).getNumber() : "");
        values.add(phones.size() > 1 ? phones.get(1).getNumber() : "");

        values.add(c.getEmail());
        values.add(c.getSecondaryEmail());

        Address addr = c.getAddress();
        values.add(addr.getStreet());
        values.add(addr.getCity());
        values.add(addr.getState());
        values.add(addr.getPostalCode());
        values.add(addr.getCountry());

        values.add(c.getGroup().name());
        values.add(String.join(";", c.getTags()));
        values.add(String.valueOf(c.isFavorite()));
        values.add(c.getNotes().replace("\n", " "));

        // Subclass specifics in standard slots
        if (c instanceof PersonalContact) {
            PersonalContact pc = (PersonalContact) c;
            values.add(pc.getRelationship());
            values.add(pc.getBirthday() != null ? pc.getBirthday().toString() : "");
            values.add(pc.getNickname());
        } else if (c instanceof BusinessContact) {
            BusinessContact bc = (BusinessContact) c;
            values.add(bc.getCompanyName());
            values.add(bc.getJobTitle());
            values.add(bc.getDepartment());
        } else if (c instanceof EmergencyContact) {
            EmergencyContact ec = (EmergencyContact) c;
            values.add(ec.getPriority().name());
            values.add(ec.getBloodGroup().name());
            values.add(ec.getAlternateEmergencyPhone());
        } else {
            values.add("");
            values.add("");
            values.add("");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeSpecialCharacters(values.get(i)));
        }
        return sb.toString();
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public static List<Contact> importFromCsv(File file) throws IOException, StorageException {
        List<Contact> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // Header
            if (line == null) return list;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 4) {
                    try {
                        String id = tokens.size() > 0 ? tokens.get(0) : "";
                        String typeStr = tokens.size() > 1 ? tokens.get(1) : "PERSONAL";
                        ContactType type = ContactType.valueOf(typeStr.toUpperCase());
                        String fn = tokens.size() > 2 ? tokens.get(2) : "";
                        String ln = tokens.size() > 3 ? tokens.get(3) : "";

                        ContactBuilder builder = new ContactBuilder(type)
                                .id(id.isEmpty() ? null : id)
                                .firstName(fn)
                                .lastName(ln);

                        if (tokens.size() > 4 && !tokens.get(4).isEmpty()) {
                            builder.phone(tokens.get(4));
                        }
                        if (tokens.size() > 5 && !tokens.get(5).isEmpty()) {
                            builder.phone(PhoneNumber.Type.WORK, tokens.get(5), false);
                        }
                        if (tokens.size() > 6) builder.email(tokens.get(6));
                        if (tokens.size() > 7) builder.secondaryEmail(tokens.get(7));

                        String street = tokens.size() > 8 ? tokens.get(8) : "";
                        String city = tokens.size() > 9 ? tokens.get(9) : "";
                        String state = tokens.size() > 10 ? tokens.get(10) : "";
                        String zip = tokens.size() > 11 ? tokens.get(11) : "";
                        String country = tokens.size() > 12 ? tokens.get(12) : "";
                        builder.address(street, city, state, zip, country);

                        if (tokens.size() > 13) builder.group(ContactGroup.fromString(tokens.get(13)));
                        if (tokens.size() > 14 && !tokens.get(14).isEmpty()) {
                            String[] tagArr = tokens.get(14).split(";");
                            builder.tags(tagArr);
                        }
                        if (tokens.size() > 15) builder.favorite(Boolean.parseBoolean(tokens.get(15)));
                        if (tokens.size() > 16) builder.notes(tokens.get(16));

                        // Subclass specific fields
                        if (type == ContactType.PERSONAL) {
                            if (tokens.size() > 17) builder.relationship(tokens.get(17));
                            if (tokens.size() > 18 && !tokens.get(18).isEmpty()) {
                                try {
                                    builder.birthday(java.time.LocalDate.parse(tokens.get(18)));
                                } catch (Exception ignored) {}
                            }
                            if (tokens.size() > 19) builder.nickname(tokens.get(19));
                        } else if (type == ContactType.BUSINESS) {
                            if (tokens.size() > 17) builder.company(tokens.get(17));
                            if (tokens.size() > 18) builder.jobTitle(tokens.get(18));
                            if (tokens.size() > 19) builder.department(tokens.get(19));
                        } else if (type == ContactType.EMERGENCY) {
                            if (tokens.size() > 17 && !tokens.get(17).isEmpty()) {
                                try {
                                    builder.priority(EmergencyPriority.valueOf(tokens.get(17).toUpperCase()));
                                } catch (Exception ignored) {}
                            }
                            if (tokens.size() > 18) builder.bloodGroup(BloodGroup.fromString(tokens.get(18)));
                            if (tokens.size() > 19) builder.alternateEmergencyPhone(tokens.get(19));
                        }

                        list.add(builder.build());
                    } catch (Exception e) {
                        System.err.println("Warning: Skipped malformed CSV line: " + e.getMessage());
                    }
                }
            }
        }
        return list;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(cur.toString().trim());
                cur = new StringBuilder();
            } else {
                cur.append(ch);
            }
        }
        result.add(cur.toString().trim());
        return result;
    }
}

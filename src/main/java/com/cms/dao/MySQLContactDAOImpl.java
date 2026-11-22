package com.cms.dao;

import com.cms.exception.DatabaseException;
import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Complete MySQL JDBC Implementation of ContactDAO.
 * 
 * Demonstrates:
 * - PreparedStatement to prevent SQL Injection
 * - Transaction Management (setAutoCommit(false), commit(), rollback())
 * - Batch Statements
 * - Relational Mapping for normalized tables (contacts, phone_numbers, contact_tags, call_logs)
 */
public class MySQLContactDAOImpl implements ContactDAO {

    private final DatabaseConnectionManager connectionManager;

    public MySQLContactDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
        initializeSchema();
    }

    private void initializeSchema() {
        String createContacts = "CREATE TABLE IF NOT EXISTS cms_contacts (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "contact_type VARCHAR(20) NOT NULL, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "last_name VARCHAR(100), " +
                "email VARCHAR(150), " +
                "secondary_email VARCHAR(150), " +
                "street VARCHAR(200), " +
                "city VARCHAR(100), " +
                "state VARCHAR(100), " +
                "postal_code VARCHAR(20), " +
                "country VARCHAR(100), " +
                "contact_group VARCHAR(50), " +
                "is_favorite BOOLEAN DEFAULT FALSE, " +
                "is_deleted BOOLEAN DEFAULT FALSE, " +
                "avatar_color_hex VARCHAR(10), " +
                "notes TEXT, " +
                "specific_data1 VARCHAR(255), " +
                "specific_data2 VARCHAR(255), " +
                "specific_data3 VARCHAR(255), " +
                "created_at DATETIME, " +
                "updated_at DATETIME);";

        String createPhones = "CREATE TABLE IF NOT EXISTS cms_phone_numbers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "contact_id VARCHAR(50) NOT NULL, " +
                "phone_type VARCHAR(20), " +
                "phone_number VARCHAR(30) NOT NULL, " +
                "is_primary BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (contact_id) REFERENCES cms_contacts(id) ON DELETE CASCADE);";

        String createTags = "CREATE TABLE IF NOT EXISTS cms_contact_tags (" +
                "contact_id VARCHAR(50) NOT NULL, " +
                "tag VARCHAR(50) NOT NULL, " +
                "PRIMARY KEY (contact_id, tag), " +
                "FOREIGN KEY (contact_id) REFERENCES cms_contacts(id) ON DELETE CASCADE);";

        String createCalls = "CREATE TABLE IF NOT EXISTS cms_call_logs (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "contact_id VARCHAR(50) NOT NULL, " +
                "call_type VARCHAR(20) NOT NULL, " +
                "call_time DATETIME NOT NULL, " +
                "duration_sec INT DEFAULT 0, " +
                "notes TEXT, " +
                "FOREIGN KEY (contact_id) REFERENCES cms_contacts(id) ON DELETE CASCADE);";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createContacts);
            stmt.execute(createPhones);
            stmt.execute(createTags);
            stmt.execute(createCalls);
        } catch (Exception e) {
            System.err.println("Notice: Schema auto-init deferred or MySQL unavailable: " + e.getMessage());
        }
    }

    @Override
    public boolean save(Contact c) {
        String sql = "INSERT INTO cms_contacts (id, contact_type, first_name, last_name, email, secondary_email, " +
                "street, city, state, postal_code, country, contact_group, is_favorite, is_deleted, avatar_color_hex, " +
                "notes, specific_data1, specific_data2, specific_data3, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                mapContactToPreparedStatement(ps, c);
                ps.executeUpdate();
            }

            // Insert Phone Numbers
            insertPhoneNumbers(conn, c);

            // Insert Tags
            insertTags(conn, c);

            // Insert Call Logs
            insertCallLogs(conn, c);

            conn.commit(); // Commit Transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            throw new DatabaseException("Failed to save contact via MySQL: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean update(Contact c) {
        String sql = "UPDATE cms_contacts SET contact_type=?, first_name=?, last_name=?, email=?, secondary_email=?, " +
                "street=?, city=?, state=?, postal_code=?, country=?, contact_group=?, is_favorite=?, is_deleted=?, " +
                "avatar_color_hex=?, notes=?, specific_data1=?, specific_data2=?, specific_data3=?, updated_at=? " +
                "WHERE id=?";

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getContactType().name());
                ps.setString(2, c.getFirstName());
                ps.setString(3, c.getLastName());
                ps.setString(4, c.getEmail());
                ps.setString(5, c.getSecondaryEmail());
                ps.setString(6, c.getAddress().getStreet());
                ps.setString(7, c.getAddress().getCity());
                ps.setString(8, c.getAddress().getState());
                ps.setString(9, c.getAddress().getPostalCode());
                ps.setString(10, c.getAddress().getCountry());
                ps.setString(11, c.getGroup().name());
                ps.setBoolean(12, c.isFavorite());
                ps.setBoolean(13, c.isDeleted());
                ps.setString(14, c.getAvatarColorHex());
                ps.setString(15, c.getNotes());

                setSpecificFields(ps, c, 16);

                ps.setTimestamp(19, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(20, c.getId());
                ps.executeUpdate();
            }

            // Replace child relations
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM cms_phone_numbers WHERE contact_id=?")) {
                ps.setString(1, c.getId());
                ps.executeUpdate();
            }
            insertPhoneNumbers(conn, c);

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM cms_contact_tags WHERE contact_id=?")) {
                ps.setString(1, c.getId());
                ps.executeUpdate();
            }
            insertTags(conn, c);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            throw new DatabaseException("Failed to update contact via MySQL: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "UPDATE cms_contacts SET is_deleted=TRUE, updated_at=? WHERE id=?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to soft-delete contact: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean restore(String id) {
        String sql = "UPDATE cms_contacts SET is_deleted=FALSE, updated_at=? WHERE id=?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to restore contact: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean permanentDelete(String id) {
        String sql = "DELETE FROM cms_contacts WHERE id=?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to permanently delete contact: " + e.getMessage(), e);
        }
    }

    @Override
    public int emptyTrash() {
        String sql = "DELETE FROM cms_contacts WHERE is_deleted=TRUE";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to empty trash: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Contact> findById(String id) {
        String sql = "SELECT * FROM cms_contacts WHERE id=?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contact c = mapResultSetToContact(rs);
                    loadChildRelations(conn, c);
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching contact by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Contact> findAll() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM cms_contacts WHERE is_deleted=FALSE ORDER BY first_name ASC, last_name ASC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contact c = mapResultSetToContact(rs);
                loadChildRelations(conn, c);
                list.add(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all contacts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> search(String query) {
        if (query == null || query.trim().isEmpty()) return findAll();
        String param = "%" + query.trim() + "%";
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM cms_contacts c " +
                "LEFT JOIN cms_phone_numbers p ON c.id = p.contact_id " +
                "LEFT JOIN cms_contact_tags t ON c.id = t.contact_id " +
                "WHERE c.is_deleted=FALSE AND (" +
                "c.first_name LIKE ? OR c.last_name LIKE ? OR c.email LIKE ? OR c.notes LIKE ? " +
                "OR p.phone_number LIKE ? OR t.tag LIKE ? OR c.specific_data1 LIKE ? OR c.specific_data2 LIKE ?) " +
                "ORDER BY c.first_name ASC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 8; i++) {
                ps.setString(i, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact c = mapResultSetToContact(rs);
                    loadChildRelations(conn, c);
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing contact search: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> findByType(ContactType type) {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM cms_contacts WHERE is_deleted=FALSE AND contact_type=? ORDER BY first_name ASC";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact c = mapResultSetToContact(rs);
                    loadChildRelations(conn, c);
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching contacts by type: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> findByGroup(ContactGroup group) {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM cms_contacts WHERE is_deleted=FALSE AND contact_group=? ORDER BY first_name ASC";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, group.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact c = mapResultSetToContact(rs);
                    loadChildRelations(conn, c);
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching contacts by group: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> findByTag(String tag) {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT c.* FROM cms_contacts c JOIN cms_contact_tags t ON c.id=t.contact_id " +
                "WHERE c.is_deleted=FALSE AND t.tag=? ORDER BY c.first_name ASC";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tag);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact c = mapResultSetToContact(rs);
                    loadChildRelations(conn, c);
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching contacts by tag: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> findFavorites() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM cms_contacts WHERE is_deleted=FALSE AND is_favorite=TRUE ORDER BY first_name ASC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contact c = mapResultSetToContact(rs);
                loadChildRelations(conn, c);
                list.add(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching favorites: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Contact> findDeleted() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM cms_contacts WHERE is_deleted=TRUE ORDER BY updated_at DESC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contact c = mapResultSetToContact(rs);
                loadChildRelations(conn, c);
                list.add(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching deleted contacts: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public long count() {
        return countActive();
    }

    @Override
    public long countActive() {
        String sql = "SELECT COUNT(*) FROM cms_contacts WHERE is_deleted=FALSE";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Error counting active contacts: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public long countFavorites() {
        String sql = "SELECT COUNT(*) FROM cms_contacts WHERE is_deleted=FALSE AND is_favorite=TRUE";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Error counting favorite contacts: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Map<ContactType, Integer> getCategoryDistribution() {
        Map<ContactType, Integer> map = new LinkedHashMap<>();
        for (ContactType ct : ContactType.values()) {
            map.put(ct, 0);
        }
        String sql = "SELECT contact_type, COUNT(*) FROM cms_contacts WHERE is_deleted=FALSE GROUP BY contact_type";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String typeStr = rs.getString(1);
                int cnt = rs.getInt(2);
                try {
                    map.put(ContactType.valueOf(typeStr), cnt);
                } catch (Exception ignored) {}
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting category counts: " + e.getMessage(), e);
        }
        return map;
    }

    @Override
    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT DISTINCT tag FROM cms_contact_tags ORDER BY tag ASC";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tags.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching tags: " + e.getMessage(), e);
        }
        return tags;
    }

    @Override
    public boolean existsByPhone(String phone, String excludeId) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String digits = phone.replaceAll("[^0-9]", "");
        String sql = "SELECT COUNT(*) FROM cms_phone_numbers p JOIN cms_contacts c ON p.contact_id=c.id " +
                "WHERE c.is_deleted=FALSE AND REPLACE(REPLACE(REPLACE(p.phone_number, ' ', ''), '-', ''), '+', '') LIKE ? " +
                (excludeId != null ? "AND c.id != ?" : "");
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + digits + "%");
            if (excludeId != null) ps.setString(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email, String excludeId) {
        if (email == null || email.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM cms_contacts WHERE is_deleted=FALSE AND LOWER(email)=LOWER(?) " +
                (excludeId != null ? "AND id != ?" : "");
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeId != null) ps.setString(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @Override
    public void flush() {
        // Direct MySQL writes are committed per transaction.
    }

    // ==========================================
    // HELPER MAPPING METHODS
    // ==========================================

    private void mapContactToPreparedStatement(PreparedStatement ps, Contact c) throws SQLException {
        ps.setString(1, c.getId());
        ps.setString(2, c.getContactType().name());
        ps.setString(3, c.getFirstName());
        ps.setString(4, c.getLastName());
        ps.setString(5, c.getEmail());
        ps.setString(6, c.getSecondaryEmail());
        ps.setString(7, c.getAddress().getStreet());
        ps.setString(8, c.getAddress().getCity());
        ps.setString(9, c.getAddress().getState());
        ps.setString(10, c.getAddress().getPostalCode());
        ps.setString(11, c.getAddress().getCountry());
        ps.setString(12, c.getGroup().name());
        ps.setBoolean(13, c.isFavorite());
        ps.setBoolean(14, c.isDeleted());
        ps.setString(15, c.getAvatarColorHex());
        ps.setString(16, c.getNotes());

        setSpecificFields(ps, c, 17);

        ps.setTimestamp(20, Timestamp.valueOf(c.getCreatedAt() != null ? c.getCreatedAt() : LocalDateTime.now()));
        ps.setTimestamp(21, Timestamp.valueOf(LocalDateTime.now()));
    }

    private void setSpecificFields(PreparedStatement ps, Contact c, int startIdx) throws SQLException {
        if (c instanceof PersonalContact) {
            PersonalContact pc = (PersonalContact) c;
            ps.setString(startIdx, pc.getRelationship());
            ps.setString(startIdx + 1, pc.getBirthday() != null ? pc.getBirthday().toString() : "");
            ps.setString(startIdx + 2, pc.getNickname());
        } else if (c instanceof BusinessContact) {
            BusinessContact bc = (BusinessContact) c;
            ps.setString(startIdx, bc.getCompanyName());
            ps.setString(startIdx + 1, bc.getJobTitle());
            ps.setString(startIdx + 2, bc.getDepartment());
        } else if (c instanceof EmergencyContact) {
            EmergencyContact ec = (EmergencyContact) c;
            ps.setString(startIdx, ec.getPriority().name());
            ps.setString(startIdx + 1, ec.getBloodGroup().name());
            ps.setString(startIdx + 2, ec.getAlternateEmergencyPhone());
        } else {
            ps.setString(startIdx, "");
            ps.setString(startIdx + 1, "");
            ps.setString(startIdx + 2, "");
        }
    }

    private Contact mapResultSetToContact(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String typeStr = rs.getString("contact_type");
        ContactType type = ContactType.valueOf(typeStr);
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");

        ContactBuilder builder = new ContactBuilder(type)
                .id(id)
                .name(fn, ln)
                .email(rs.getString("email"))
                .secondaryEmail(rs.getString("secondary_email"))
                .address(rs.getString("street"), rs.getString("city"), rs.getString("state"), rs.getString("postal_code"), rs.getString("country"))
                .group(ContactGroup.fromString(rs.getString("contact_group")))
                .favorite(rs.getBoolean("is_favorite"))
                .notes(rs.getString("notes"))
                .avatarColorHex(rs.getString("avatar_color_hex"));

        String spec1 = rs.getString("specific_data1");
        String spec2 = rs.getString("specific_data2");
        String spec3 = rs.getString("specific_data3");

        if (type == ContactType.PERSONAL) {
            builder.relationship(spec1);
            if (spec2 != null && !spec2.isEmpty()) {
                try { builder.birthday(LocalDate.parse(spec2)); } catch (Exception ignored) {}
            }
            builder.nickname(spec3);
        } else if (type == ContactType.BUSINESS) {
            builder.company(spec1);
            builder.jobTitle(spec2);
            builder.department(spec3);
        } else if (type == ContactType.EMERGENCY) {
            try { builder.priority(EmergencyPriority.valueOf(spec1)); } catch (Exception ignored) {}
            builder.bloodGroup(BloodGroup.fromString(spec2));
            builder.alternateEmergencyPhone(spec3);
        }

        Contact contact = builder.build();
        contact.setDeleted(rs.getBoolean("is_deleted"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) contact.setCreatedAt(cat.toLocalDateTime());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) contact.setUpdatedAt(uat.toLocalDateTime());

        return contact;
    }

    private void loadChildRelations(Connection conn, Contact c) throws SQLException {
        // Load Phone Numbers
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM cms_phone_numbers WHERE contact_id=?")) {
            ps.setString(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<PhoneNumber> phones = new ArrayList<>();
                while (rs.next()) {
                    PhoneNumber.Type type = PhoneNumber.Type.valueOf(rs.getString("phone_type"));
                    String num = rs.getString("phone_number");
                    boolean isPrimary = rs.getBoolean("is_primary");
                    phones.add(new PhoneNumber(type, num, isPrimary));
                }
                c.setPhoneNumbers(phones);
            }
        }

        // Load Tags
        try (PreparedStatement ps = conn.prepareStatement("SELECT tag FROM cms_contact_tags WHERE contact_id=?")) {
            ps.setString(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                Set<String> tags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                while (rs.next()) {
                    tags.add(rs.getString("tag"));
                }
                c.setTags(tags);
            }
        }

        // Load Call Logs
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM cms_call_logs WHERE contact_id=? ORDER BY call_time DESC")) {
            ps.setString(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<CallLog> calls = new ArrayList<>();
                while (rs.next()) {
                    String logId = rs.getString("id");
                    CallType type = CallType.valueOf(rs.getString("call_type"));
                    LocalDateTime time = rs.getTimestamp("call_time").toLocalDateTime();
                    int dur = rs.getInt("duration_sec");
                    String notes = rs.getString("notes");
                    calls.add(new CallLog(logId, c.getId(), type, time, dur, notes));
                }
                c.setCallLogs(calls);
            }
        }
    }

    private void insertPhoneNumbers(Connection conn, Contact c) throws SQLException {
        String sql = "INSERT INTO cms_phone_numbers (contact_id, phone_type, phone_number, is_primary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (PhoneNumber p : c.getPhoneNumbers()) {
                ps.setString(1, c.getId());
                ps.setString(2, p.getType().name());
                ps.setString(3, p.getNumber());
                ps.setBoolean(4, p.isPrimary());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertTags(Connection conn, Contact c) throws SQLException {
        String sql = "INSERT INTO cms_contact_tags (contact_id, tag) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String tag : c.getTags()) {
                ps.setString(1, c.getId());
                ps.setString(2, tag);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertCallLogs(Connection conn, Contact c) throws SQLException {
        if (c.getCallLogs() == null || c.getCallLogs().isEmpty()) return;
        String sql = "INSERT INTO cms_call_logs (id, contact_id, call_type, call_time, duration_sec, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CallLog log : c.getCallLogs()) {
                ps.setString(1, log.getId());
                ps.setString(2, c.getId());
                ps.setString(3, log.getCallType().name());
                ps.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
                ps.setInt(5, log.getDurationSeconds());
                ps.setString(6, log.getNotes());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}

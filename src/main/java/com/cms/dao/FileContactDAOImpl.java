package com.cms.dao;

import com.cms.io.ObjectSerializationHandler;
import com.cms.model.*;
import com.cms.util.DummyDataGenerator;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * File-based Persistent Contact DAO Implementation.
 * 
 * Demonstrates:
 * - Java Collections Framework (Map, List, Set, Streams)
 * - Concurrency control using ReentrantReadWriteLock
 * - Object Serialization & File Persistence
 * - Automatic pre-seeding with dummy data on first launch
 */
public final class FileContactDAOImpl implements ContactDAO {

    private static final String DATA_FILE_PATH = "contacts_data.dat";
    private final File storageFile;
    private final Map<String, Contact> contactMap = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public FileContactDAOImpl() {
        this(new File(DATA_FILE_PATH));
    }

    public FileContactDAOImpl(File storageFile) {
        this.storageFile = storageFile;
        initStorage();
    }

    private void initStorage() {
        rwLock.writeLock().lock();
        try {
            if (storageFile.exists() && storageFile.length() > 0) {
                try {
                    List<Contact> loaded = ObjectSerializationHandler.loadFromFile(storageFile);
                    contactMap.clear();
                    for (Contact c : loaded) {
                        contactMap.put(c.getId(), c);
                    }
                    System.out.println("Loaded " + contactMap.size() + " contacts from local persistent file.");
                } catch (Exception e) {
                    System.err.println("Notice: Could not parse existing data file, seeding samples: " + e.getMessage());
                    seedInitialDataInternal();
                }
            } else {
                seedInitialDataInternal();
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void seedInitialDataInternal() {
        contactMap.clear();
        List<Contact> samples = DummyDataGenerator.generateSampleContacts();
        for (Contact c : samples) {
            contactMap.put(c.getId(), c);
        }
        flushInternal();
    }

    private void flushInternal() {
        try {
            ObjectSerializationHandler.saveToFile(new ArrayList<>(contactMap.values()), storageFile);
        } catch (Exception e) {
            System.err.println("Warning: Storage write error: " + e.getMessage());
        }
    }

    @Override
    public boolean save(Contact entity) {
        if (entity == null) return false;
        rwLock.writeLock().lock();
        try {
            contactMap.put(entity.getId(), entity);
            flushInternal();
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean update(Contact entity) {
        if (entity == null || !contactMap.containsKey(entity.getId())) return false;
        rwLock.writeLock().lock();
        try {
            entity.setUpdatedAt(LocalDateTime.now());
            contactMap.put(entity.getId(), entity);
            flushInternal();
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean delete(String id) {
        rwLock.writeLock().lock();
        try {
            Contact c = contactMap.get(id);
            if (c != null) {
                c.setDeleted(true);
                c.setUpdatedAt(LocalDateTime.now());
                flushInternal();
                return true;
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean restore(String id) {
        rwLock.writeLock().lock();
        try {
            Contact c = contactMap.get(id);
            if (c != null) {
                c.setDeleted(false);
                c.setUpdatedAt(LocalDateTime.now());
                flushInternal();
                return true;
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public boolean permanentDelete(String id) {
        rwLock.writeLock().lock();
        try {
            boolean removed = contactMap.remove(id) != null;
            if (removed) {
                flushInternal();
            }
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public int emptyTrash() {
        rwLock.writeLock().lock();
        try {
            List<String> toRemove = contactMap.values().stream()
                    .filter(Contact::isDeleted)
                    .map(Contact::getId)
                    .collect(Collectors.toList());
            for (String id : toRemove) {
                contactMap.remove(id);
            }
            if (!toRemove.isEmpty()) {
                flushInternal();
            }
            return toRemove.size();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Contact> findById(String id) {
        rwLock.readLock().lock();
        try {
            Contact c = contactMap.get(id);
            return Optional.ofNullable(c != null ? c.clone() : null);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findAll() {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted())
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> search(String query) {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted())
                    .filter(c -> c.matchesSearch(query))
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findByType(ContactType type) {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted() && c.getContactType() == type)
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findByGroup(ContactGroup group) {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted() && c.getGroup() == group)
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findByTag(String tag) {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted() && c.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(tag)))
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findFavorites() {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(c -> !c.isDeleted() && c.isFavorite())
                    .map(Contact::clone)
                    .sorted()
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Contact> findDeleted() {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream()
                    .filter(Contact::isDeleted)
                    .map(Contact::clone)
                    .sorted((c1, c2) -> c2.getUpdatedAt().compareTo(c1.getUpdatedAt()))
                    .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public long count() {
        return countActive();
    }

    @Override
    public long countActive() {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream().filter(c -> !c.isDeleted()).count();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public long countFavorites() {
        rwLock.readLock().lock();
        try {
            return contactMap.values().stream().filter(c -> !c.isDeleted() && c.isFavorite()).count();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Map<ContactType, Integer> getCategoryDistribution() {
        rwLock.readLock().lock();
        try {
            Map<ContactType, Integer> map = new LinkedHashMap<>();
            for (ContactType ct : ContactType.values()) {
                map.put(ct, 0);
            }
            for (Contact c : contactMap.values()) {
                if (!c.isDeleted()) {
                    map.put(c.getContactType(), map.get(c.getContactType()) + 1);
                }
            }
            return map;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<String> getAllTags() {
        rwLock.readLock().lock();
        try {
            Set<String> allTags = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Contact c : contactMap.values()) {
                if (!c.isDeleted()) {
                    allTags.addAll(c.getTags());
                }
            }
            return new ArrayList<>(allTags);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public boolean existsByPhone(String phone, String excludeId) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return false;

        rwLock.readLock().lock();
        try {
            for (Contact c : contactMap.values()) {
                if (c.isDeleted()) continue;
                if (excludeId != null && c.getId().equals(excludeId)) continue;
                for (PhoneNumber p : c.getPhoneNumbers()) {
                    if (p.getNumber().replaceAll("[^0-9]", "").equals(digits)) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public boolean existsByEmail(String email, String excludeId) {
        if (email == null || email.trim().isEmpty()) return false;
        String clean = email.trim().toLowerCase();

        rwLock.readLock().lock();
        try {
            for (Contact c : contactMap.values()) {
                if (c.isDeleted()) continue;
                if (excludeId != null && c.getId().equals(excludeId)) continue;
                if (c.getEmail().equalsIgnoreCase(clean) || c.getSecondaryEmail().equalsIgnoreCase(clean)) {
                    return true;
                }
            }
            return false;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void flush() {
        rwLock.readLock().lock();
        try {
            flushInternal();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}

package com.cms.io;

import com.cms.exception.StorageException;
import com.cms.model.Contact;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles binary Object Serialization and Deserialization using Java Standard Library.
 * Demonstrates:
 * - Serializable interface
 * - ObjectOutputStream / ObjectInputStream
 * - Atomic file write with safety backup
 */
public class ObjectSerializationHandler {

    /**
     * Serializes a list of Contact objects to a binary file.
     */
    public static synchronized void saveToFile(List<Contact> contacts, File targetFile) throws StorageException {
        File tempFile = new File(targetFile.getAbsolutePath() + ".tmp");
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)))) {
            oos.writeObject(new ArrayList<>(contacts));
            oos.flush();
        } catch (IOException e) {
            if (tempFile.exists()) tempFile.delete();
            throw new StorageException("Failed to serialize contacts to binary storage: " + e.getMessage(), e);
        }

        // Atomic swap
        if (targetFile.exists()) {
            targetFile.delete();
        }
        if (!tempFile.renameTo(targetFile)) {
            throw new StorageException("Failed to commit binary contact storage file.");
        }
    }

    /**
     * Deserializes a list of Contact objects from a binary file.
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<Contact> loadFromFile(File sourceFile) throws StorageException {
        if (!sourceFile.exists() || sourceFile.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<Contact>) obj;
            }
            throw new StorageException("Invalid object data found in binary contact storage.");
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Failed to deserialize contacts from binary storage: " + e.getMessage(), e);
        }
    }
}

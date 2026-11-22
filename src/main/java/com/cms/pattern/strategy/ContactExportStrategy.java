package com.cms.pattern.strategy;

import com.cms.exception.StorageException;
import com.cms.model.Contact;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Strategy interface for exporting contacts in multiple external formats.
 */
public interface ContactExportStrategy {
    void exportContacts(List<Contact> contacts, File destinationFile) throws IOException, StorageException;
    String getFormatName();
    String getFileExtension();
}

package com.cms.pattern.strategy;

import com.cms.exception.StorageException;
import com.cms.io.JsonHandler;
import com.cms.model.Contact;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonExportStrategy implements ContactExportStrategy {
    @Override
    public void exportContacts(List<Contact> contacts, File destinationFile) throws IOException, StorageException {
        JsonHandler.exportToJson(contacts, destinationFile);
    }

    @Override
    public String getFormatName() {
        return "JSON Document (.json)";
    }

    @Override
    public String getFileExtension() {
        return "json";
    }

    @Override
    public String toString() {
        return getFormatName();
    }
}

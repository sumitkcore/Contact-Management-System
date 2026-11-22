package com.cms.pattern.strategy;

import com.cms.exception.StorageException;
import com.cms.io.CsvHandler;
import com.cms.model.Contact;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CsvExportStrategy implements ContactExportStrategy {
    @Override
    public void exportContacts(List<Contact> contacts, File destinationFile) throws IOException, StorageException {
        CsvHandler.exportToCsv(contacts, destinationFile);
    }

    @Override
    public String getFormatName() {
        return "Comma Separated Values (.csv)";
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }

    @Override
    public String toString() {
        return getFormatName();
    }
}

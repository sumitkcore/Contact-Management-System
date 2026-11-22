package com.cms.pattern.strategy;

import com.cms.exception.StorageException;
import com.cms.io.VCardHandler;
import com.cms.model.Contact;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VCardExportStrategy implements ContactExportStrategy {
    @Override
    public void exportContacts(List<Contact> contacts, File destinationFile) throws IOException, StorageException {
        VCardHandler.exportToVCard(contacts, destinationFile);
    }

    @Override
    public String getFormatName() {
        return "vCard 3.0 Electronic Business Card (.vcf)";
    }

    @Override
    public String getFileExtension() {
        return "vcf";
    }

    @Override
    public String toString() {
        return getFormatName();
    }
}

package com.cms;

import com.cms.dao.DAOFactory;
import com.cms.model.Contact;
import com.cms.model.ContactType;
import com.cms.service.ContactService;
import com.cms.service.ContactServiceImpl;
import com.cms.ui.MainFrame;
import com.cms.ui.ModernTheme;
import com.cms.ui.dialogs.*;
import com.cms.ui.view.ContactDetailPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * UI Integration & Component Lifecycle Test.
 * Verifies that all UI frames, dialogs, custom components, and renderers
 * initialize, layout, and dispose without throwing any runtime exceptions.
 */
public class UIIntegrationTest {

    public static void main(String[] args) {
        System.out.println("Running UIIntegrationTest...");

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("ℹ Environment is headless; skipping display-bound UI rendering checks.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Test Theme switching
                ModernTheme.setCurrentMode(ModernTheme.ThemeMode.DARK);
                assert ModernTheme.getBackground() != null : "Dark background null";
                ModernTheme.setCurrentMode(ModernTheme.ThemeMode.LIGHT);
                assert ModernTheme.getBackground() != null : "Light background null";

                // 2. Service
                ContactService service = new ContactServiceImpl();
                List<Contact> all = service.getAllContacts();
                assert !all.isEmpty() : "Pre-seeded contacts missing";

                // 3. Test MainFrame instantiation
                MainFrame frame = new MainFrame();
                frame.pack();
                assert frame.getTitle().contains("Group 17") : "Frame title incorrect";

                // 4. Test Detail Panel
                ContactDetailPanel detailPanel = new ContactDetailPanel(service, (v) -> {});
                detailPanel.displayContact(all.get(0));

                // 5. Test Dialogs
                AboutDialog aboutDialog = new AboutDialog(frame);
                aboutDialog.pack();
                aboutDialog.dispose();

                RecycleBinDialog recycleDialog = new RecycleBinDialog(frame, service, () -> {});
                recycleDialog.pack();
                recycleDialog.dispose();

                SettingsDialog settingsDialog = new SettingsDialog(frame, service, () -> {});
                settingsDialog.pack();
                settingsDialog.dispose();

                AnalyticsDialog analyticsDialog = new AnalyticsDialog(frame, service);
                analyticsDialog.pack();
                analyticsDialog.dispose();

                ContactEditorDialog editorDialog = new ContactEditorDialog(frame, all.get(0), service);
                editorDialog.pack();
                editorDialog.dispose();

                CallSimulatorDialog callDialog = new CallSimulatorDialog(frame, all.get(0), service, () -> {});
                callDialog.pack();
                callDialog.dispose();

                ImportExportDialog ioDialog = new ImportExportDialog(frame, service, () -> {});
                ioDialog.pack();
                ioDialog.dispose();

                frame.dispose();

                System.out.println("✓ UIIntegrationTest: ALL UI COMPONENTS INITIALIZED & DISPOSED CLEANLY (100% SUCCESS)");
            } catch (Throwable t) {
                System.err.println("✕ UIIntegrationTest failed: " + t.getMessage());
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}

package com.cms.ui.dialogs;

import com.cms.io.CsvHandler;
import com.cms.io.JsonHandler;
import com.cms.io.VCardHandler;
import com.cms.model.Contact;
import com.cms.pattern.strategy.*;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Modern Import & Export Dialog supporting CSV, vCard (.vcf), and JSON formats.
 * Demonstrates Multithreading (SwingWorker) for non-blocking I/O operations.
 */
public final class ImportExportDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final transient Runnable onDataImported;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public ImportExportDialog(Window owner, ContactService service, Runnable onDataImported) {
        super(owner, "Import & Export Contacts", ModalityType.APPLICATION_MODAL);
        this.contactService = service;
        this.onDataImported = onDataImported;

        initDialog();
    }

    private void initDialog() {
        setSize(560, 440);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ModernTheme.getSurface());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel title = new JLabel("📤 Import / Export Data Manager");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.getTextPrimary());
        header.add(title, BorderLayout.NORTH);

        JLabel sub = new JLabel("Universal format interoperability: CSV (RFC-4180), vCard 3.0 (.vcf), JSON");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.getTextSecondary());
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ModernTheme.FONT_BODY_BOLD);
        tabs.addTab("Export Contacts 📤", buildExportTab());
        tabs.addTab("Import Contacts 📥", buildImportTab());
        add(tabs, BorderLayout.CENTER);

        // Bottom Progress Bar
        JPanel bottom = new JPanel(new BorderLayout(8, 4));
        bottom.setOpaque(true);
        bottom.setBackground(ModernTheme.getSurface());
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(ModernTheme.FONT_SMALL);
        statusLabel.setForeground(ModernTheme.getTextSecondary());
        bottom.add(statusLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        bottom.add(progressBar, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildExportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));

        p.add(createFormatRow("CSV (Comma Separated Values)", "Standard tabular format for Excel / Google Sheets", () -> runExport(new CsvExportStrategy())));
        p.add(Box.createVerticalStrut(12));
        p.add(createFormatRow("vCard 3.0 (.vcf)", "Industry standard for Apple Contacts, Android & Outlook", () -> runExport(new VCardExportStrategy())));
        p.add(Box.createVerticalStrut(12));
        p.add(createFormatRow("JSON Document (.json)", "Structured modern web backup format", () -> runExport(new JsonExportStrategy())));

        return p;
    }

    private JPanel buildImportTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));

        p.add(createFormatRow("Import from CSV File", "Reads RFC-4180 CSV files with auto header mapping", () -> runImport("csv")));
        p.add(Box.createVerticalStrut(12));
        p.add(createFormatRow("Import from vCard (.vcf)", "Imports single or multi-contact .vcf address books", () -> runImport("vcf")));

        return p;
    }

    private JPanel createFormatRow(String title, String desc, Runnable action) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(true);
        row.setBackground(ModernTheme.getSurface());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(ModernTheme.FONT_BODY_BOLD);
        t.setForeground(ModernTheme.getTextPrimary());
        text.add(t);

        JLabel d = new JLabel(desc);
        d.setFont(ModernTheme.FONT_SMALL);
        d.setForeground(ModernTheme.getTextSecondary());
        text.add(d);

        row.add(text, BorderLayout.CENTER);

        ModernButton btn = new ModernButton("Select & Run", ModernButton.ButtonStyle.PRIMARY);
        btn.addActionListener(e -> action.run());
        row.add(btn, BorderLayout.EAST);

        return row;
    }

    private void runExport(ContactExportStrategy strategy) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("contacts_export." + strategy.getFileExtension()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            progressBar.setVisible(true);
            statusLabel.setText("Exporting to " + strategy.getFormatName() + "...");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    List<Contact> all = contactService.getAllContacts(new NameAscendingStrategy());
                    strategy.exportContacts(all, dest);
                    return null;
                }

                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    try {
                        get();
                        statusLabel.setText("Export completed successfully: " + dest.getName());
                        ToastNotification.show(getOwner(), "Exported to " + dest.getName(), ToastNotification.Type.SUCCESS);
                    } catch (Exception ex) {
                        statusLabel.setText("Export failed: " + ex.getMessage());
                        JOptionPane.showMessageDialog(ImportExportDialog.this, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void runImport(String ext) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File source = chooser.getSelectedFile();
            progressBar.setVisible(true);
            statusLabel.setText("Parsing and importing contacts from " + source.getName() + "...");

            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    List<Contact> imported;
                    if ("csv".equalsIgnoreCase(ext)) {
                        imported = CsvHandler.importFromCsv(source);
                    } else {
                        imported = VCardHandler.importFromVCard(source);
                    }
                    return contactService.bulkImport(imported);
                }

                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    try {
                        int count = get();
                        statusLabel.setText("Import finished: " + count + " contacts added.");
                        ToastNotification.show(getOwner(), "Imported " + count + " contacts!", ToastNotification.Type.SUCCESS);
                        if (onDataImported != null) onDataImported.run();
                    } catch (Exception ex) {
                        statusLabel.setText("Import error: " + ex.getMessage());
                        JOptionPane.showMessageDialog(ImportExportDialog.this, ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}

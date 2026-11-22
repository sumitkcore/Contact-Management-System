package com.cms.ui.dialogs;

import com.cms.dao.DAOFactory;
import com.cms.dao.DatabaseConnectionManager;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ModernTextField;
import com.cms.ui.components.ToastNotification;
import com.cms.util.DummyDataGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings & Configuration Modal.
 * Switch storage engines (MySQL vs Local File Persistent), toggle Dark/Light Mode, and test DB connectivity.
 * Demonstrates runtime DAO switching (DAO Pattern), JDBC configuration, and theme management.
 */
public final class SettingsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final transient Runnable onThemeOrStorageSwitched;

    private final JComboBox<ModernTheme.ThemeMode> themeCombo;
    private final JComboBox<DAOFactory.StorageType> storageCombo;
    private final ModernTextField dbUrlField;
    private final ModernTextField dbUserField;
    private final JPasswordField dbPassField;
    private final JLabel statusLabel;

    public SettingsDialog(Window owner, ContactService service, Runnable onThemeOrStorageSwitched) {
        super(owner, "System Settings & Database Architecture", ModalityType.APPLICATION_MODAL);
        this.contactService = service;
        this.onThemeOrStorageSwitched = onThemeOrStorageSwitched;

        setSize(640, 620);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ModernTheme.getSurface());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JLabel title = new JLabel("⚙ System Settings & Architecture");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.getTextPrimary());
        header.add(title, BorderLayout.NORTH);

        JLabel sub = new JLabel("Configure runtime storage persistence engine, JDBC DataSource, and UI appearance.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.getTextSecondary());
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Body Panel (Scrollable Card Layout)
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 22, 16, 22));

        // --- Card 1: Appearance & UI ---
        JPanel themeCard = createCardPanel("🎨 Visual Appearance & UI Theme");
        JPanel themeRow = new JPanel(new BorderLayout(12, 0));
        themeRow.setOpaque(false);

        JLabel thmLbl = new JLabel("Theme Mode:");
        thmLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        thmLbl.setForeground(ModernTheme.getTextPrimary());
        themeRow.add(thmLbl, BorderLayout.WEST);

        themeCombo = new JComboBox<>(ModernTheme.ThemeMode.values());
        themeCombo.setSelectedItem(ModernTheme.getCurrentMode());
        themeCombo.setFont(ModernTheme.FONT_BODY);
        themeRow.add(themeCombo, BorderLayout.CENTER);
        themeCard.add(themeRow);

        JLabel themeHelp = new JLabel("Switches between modern dark mode and crisp high-contrast light mode.");
        themeHelp.setFont(ModernTheme.FONT_SMALL);
        themeHelp.setForeground(ModernTheme.getTextMuted());
        themeHelp.setBorder(new EmptyBorder(6, 0, 0, 0));
        themeCard.add(themeHelp);
        body.add(themeCard);
        body.add(Box.createVerticalStrut(14));

        // --- Card 2: Storage Architecture (DAO Pattern) ---
        JPanel storageCard = createCardPanel("💾 Persistence Architecture (DAO Pattern)");
        JPanel storageRow = new JPanel(new BorderLayout(12, 0));
        storageRow.setOpaque(false);

        JLabel strLbl = new JLabel("Active Storage DAO:");
        strLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        strLbl.setForeground(ModernTheme.getTextPrimary());
        storageRow.add(strLbl, BorderLayout.WEST);

        storageCombo = new JComboBox<>(DAOFactory.StorageType.values());
        storageCombo.setSelectedItem(DAOFactory.getCurrentStorageType());
        storageCombo.setFont(ModernTheme.FONT_BODY);
        storageRow.add(storageCombo, BorderLayout.CENTER);
        storageCard.add(storageRow);

        JLabel strHelp = new JLabel("Demonstrates the Abstract DAO Factory pattern. Switches data source dynamically at runtime.");
        strHelp.setFont(ModernTheme.FONT_SMALL);
        strHelp.setForeground(ModernTheme.getTextMuted());
        strHelp.setBorder(new EmptyBorder(6, 0, 0, 0));
        storageCard.add(strHelp);
        body.add(storageCard);
        body.add(Box.createVerticalStrut(14));

        // --- Card 3: MySQL JDBC Connection Configuration ---
        JPanel dbCard = createCardPanel("🗄 MySQL 8.0 JDBC DataSource Configuration");

        DatabaseConnectionManager dcm = DatabaseConnectionManager.getInstance();

        dbUrlField = new ModernTextField(dcm.getUrl());
        dbCard.add(createLabeledField("JDBC Connection URL", dbUrlField));
        dbCard.add(Box.createVerticalStrut(10));

        JPanel credRow = new JPanel(new GridLayout(1, 2, 12, 0));
        credRow.setOpaque(false);

        dbUserField = new ModernTextField(dcm.getUsername());
        dbPassField = new JPasswordField("MyNewPrivatePassword_2026!");
        dbPassField.setFont(ModernTheme.FONT_BODY);
        dbPassField.setBackground(ModernTheme.getSurface());
        dbPassField.setForeground(ModernTheme.getTextPrimary());
        dbPassField.setCaretColor(ModernTheme.getTextPrimary());
        dbPassField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        credRow.add(createLabeledField("Database Username", dbUserField));
        credRow.add(createLabeledField("Database Password", dbPassField));
        dbCard.add(credRow);
        dbCard.add(Box.createVerticalStrut(12));

        // DB Actions Row
        JPanel dbActionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dbActionsRow.setOpaque(false);

        ModernButton testBtn = new ModernButton("Test Connection 🔌", ModernButton.ButtonStyle.SECONDARY);
        testBtn.addActionListener(e -> testMySQL());
        dbActionsRow.add(testBtn);

        ModernButton initDbBtn = new ModernButton("Initialize Schema 🛠", ModernButton.ButtonStyle.SECONDARY);
        initDbBtn.addActionListener(e -> initializeDatabaseSchema());
        dbActionsRow.add(initDbBtn);

        dbCard.add(dbActionsRow);
        dbCard.add(Box.createVerticalStrut(8));

        statusLabel = new JLabel("Status: Ready to test");
        statusLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        statusLabel.setForeground(ModernTheme.getTextSecondary());
        dbCard.add(statusLabel);

        body.add(dbCard);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(null);
        bodyScroll.setOpaque(false);
        bodyScroll.getViewport().setOpaque(false);
        add(bodyScroll, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));

        ModernButton cancelBtn = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        footer.add(cancelBtn);

        ModernButton applyBtn = new ModernButton("Apply Settings ✓", ModernButton.ButtonStyle.PRIMARY);
        applyBtn.addActionListener(e -> applySettings());
        footer.add(applyBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private void testMySQL() {
        DatabaseConnectionManager dcm = DatabaseConnectionManager.getInstance();
        dcm.setConnectionConfig(dbUrlField.getText().trim(), dbUserField.getText().trim(), new String(dbPassField.getPassword()));
        statusLabel.setText("Connecting to MySQL...");
        statusLabel.setForeground(ModernTheme.getTextSecondary());

        boolean ok = dcm.testConnection();
        if (ok) {
            statusLabel.setText("✓ Connection Successful! MySQL 8.0 server reachable.");
            statusLabel.setForeground(ModernTheme.ACCENT_SUCCESS);
        } else {
            statusLabel.setText("✕ Connection Failed (Verify MySQL server status & credentials)");
            statusLabel.setForeground(ModernTheme.ACCENT_DANGER);
        }
    }

    private void initializeDatabaseSchema() {
        DatabaseConnectionManager dcm = DatabaseConnectionManager.getInstance();
        dcm.setConnectionConfig(dbUrlField.getText().trim(), dbUserField.getText().trim(), new String(dbPassField.getPassword()));
        statusLabel.setText("Creating database tables...");

        boolean ok = dcm.ensureDatabaseExists();
        if (ok) {
            statusLabel.setText("✓ Database 'contact_db' and tables verified successfully.");
            statusLabel.setForeground(ModernTheme.ACCENT_SUCCESS);
            ToastNotification.show(this, "Database schema verified!", ToastNotification.Type.SUCCESS);
        } else {
            statusLabel.setText("✕ Schema initialization failed. Check server logs.");
            statusLabel.setForeground(ModernTheme.ACCENT_DANGER);
            ToastNotification.show(this, "Database initialization failed.", ToastNotification.Type.ERROR);
        }
    }

    private void applySettings() {
        ModernTheme.ThemeMode selectedMode = (ModernTheme.ThemeMode) themeCombo.getSelectedItem();
        boolean themeChanged = selectedMode != null && selectedMode != ModernTheme.getCurrentMode();
        if (themeChanged) {
            ModernTheme.setCurrentMode(selectedMode);
        }

        DAOFactory.StorageType selectedStorage = (DAOFactory.StorageType) storageCombo.getSelectedItem();
        boolean storageChanged = selectedStorage != null && selectedStorage != DAOFactory.getCurrentStorageType();
        if (storageChanged) {
            DAOFactory.setStorageType(selectedStorage);
        }

        DatabaseConnectionManager dcm = DatabaseConnectionManager.getInstance();
        dcm.setConnectionConfig(dbUrlField.getText().trim(), dbUserField.getText().trim(), new String(dbPassField.getPassword()));

        ToastNotification.show(getOwner(), "Settings applied successfully!", ToastNotification.Type.SUCCESS);
        if (onThemeOrStorageSwitched != null) {
            onThemeOrStorageSwitched.run();
        }
        dispose();
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(ModernTheme.getSurface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ModernTheme.FONT_SUBHEADER);
        titleLbl.setForeground(ModernTheme.ACCENT_PRIMARY);
        titleLbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(titleLbl);
        return card;
    }

    private JPanel createLabeledField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(ModernTheme.FONT_SMALL_BOLD);
        l.setForeground(ModernTheme.getTextSecondary());
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
}

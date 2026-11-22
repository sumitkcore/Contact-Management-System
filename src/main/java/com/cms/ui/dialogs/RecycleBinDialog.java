package com.cms.ui.dialogs;

import com.cms.model.Contact;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Recycle Bin (Soft Delete) Management Modal.
 * View deleted contacts, restore them, or permanently purge them.
 * Demonstrates state management, soft vs hard deletion, and transactional safety.
 */
public final class RecycleBinDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final transient Runnable onDataChanged;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel countBadgeLbl;
    private final CardLayout cardLayout;
    private final JPanel centerPanel;
    private transient List<Contact> deletedContacts = new ArrayList<>();

    public RecycleBinDialog(Window owner, ContactService service, Runnable onDataChanged) {
        super(owner, "Recycle Bin — Deleted Contacts", ModalityType.APPLICATION_MODAL);
        this.contactService = service;
        this.onDataChanged = onDataChanged;

        setSize(720, 480);
        setMinimumSize(new Dimension(600, 380));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ModernTheme.getSurface());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);

        JLabel title = new JLabel("🗑 Recycle Bin / Soft-Deleted Contacts");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.getTextPrimary());
        topRow.add(title);

        countBadgeLbl = new JLabel("0 items");
        countBadgeLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        countBadgeLbl.setForeground(ModernTheme.ACCENT_DANGER);
        countBadgeLbl.setOpaque(true);
        countBadgeLbl.setBackground(ModernTheme.getSurfaceVariant());
        countBadgeLbl.setBorder(new EmptyBorder(2, 8, 2, 8));
        topRow.add(countBadgeLbl);

        titlePanel.add(topRow);

        JLabel sub = new JLabel("Soft-deleted contacts are kept here safely. You can restore them anytime or purge permanently.");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.getTextSecondary());
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));
        titlePanel.add(sub);

        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Center Panel (CardLayout: Table vs Empty State)
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(false);

        // Table Card
        String[] columns = {"Name", "Category", "Primary Phone", "Email", "Deleted On"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(ModernTheme.FONT_BODY);
        table.setRowHeight(34);
        table.setBackground(ModernTheme.getSurface());
        table.setForeground(ModernTheme.getTextPrimary());
        table.setSelectionBackground(ModernTheme.getSelectionBackground());
        table.setSelectionForeground(ModernTheme.getSelectionForeground());
        table.setGridColor(ModernTheme.getBorder());
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Customize Table Header
        JTableHeader th = table.getTableHeader();
        th.setFont(ModernTheme.FONT_SMALL_BOLD);
        th.setBackground(ModernTheme.getSurfaceVariant());
        th.setForeground(ModernTheme.getTextSecondary());
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 32));

        // Center align category & date
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRender);

        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        centerPanel.add(scrollPane, "TABLE");

        // Empty State Card
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.add(Box.createVerticalGlue());

        JLabel emptyIcon = new JLabel("✨");
        emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyIcon);

        JLabel emptyTitle = new JLabel("Recycle Bin is Empty");
        emptyTitle.setFont(ModernTheme.FONT_HEADER);
        emptyTitle.setForeground(ModernTheme.getTextPrimary());
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyTitle);

        JLabel emptySub = new JLabel("Deleted contacts will appear here until restored or cleared.");
        emptySub.setFont(ModernTheme.FONT_SMALL);
        emptySub.setForeground(ModernTheme.getTextSecondary());
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptySub);
        emptyPanel.add(Box.createVerticalGlue());

        centerPanel.add(emptyPanel, "EMPTY");
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Controls Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        ModernButton emptyTrashBtn = new ModernButton("Empty Entire Trash 🗑", ModernButton.ButtonStyle.DANGER);
        emptyTrashBtn.addActionListener(e -> emptyEntireTrash());
        footer.add(emptyTrashBtn, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        ModernButton restoreBtn = new ModernButton("Restore Selected ↺", ModernButton.ButtonStyle.SUCCESS);
        restoreBtn.addActionListener(e -> restoreSelected());
        rightBtns.add(restoreBtn);

        ModernButton closeBtn = new ModernButton("Close", ModernButton.ButtonStyle.SECONDARY);
        closeBtn.addActionListener(e -> dispose());
        rightBtns.add(closeBtn);

        footer.add(rightBtns, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        loadDeletedContacts();
    }

    private void loadDeletedContacts() {
        tableModel.setRowCount(0);
        deletedContacts = contactService.getDeletedContacts();

        countBadgeLbl.setText(deletedContacts.size() + " " + (deletedContacts.size() == 1 ? "item" : "items"));

        if (deletedContacts.isEmpty()) {
            cardLayout.show(centerPanel, "EMPTY");
        } else {
            cardLayout.show(centerPanel, "TABLE");
            for (Contact c : deletedContacts) {
                tableModel.addRow(new Object[]{
                        c.getFullName(),
                        c.getContactType().getDisplayName(),
                        c.getPrimaryPhoneNumber(),
                        c.getEmail(),
                        c.getUpdatedAt() != null ? c.getUpdatedAt().toLocalDate().toString() : "N/A"
                });
            }
        }
    }

    private void restoreSelected() {
        int row = table.getSelectedRow();
        if (row == -1 || row >= deletedContacts.size()) {
            JOptionPane.showMessageDialog(this, "Please select a contact from the table to restore.", "Select Contact", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Contact target = deletedContacts.get(row);
        try {
            contactService.restoreContact(target.getId());
            ToastNotification.show(this, "Restored " + target.getFullName(), ToastNotification.Type.SUCCESS);
            loadDeletedContacts();
            if (onDataChanged != null) onDataChanged.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void emptyEntireTrash() {
        if (deletedContacts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recycle Bin is already empty.", "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to PERMANENTLY delete all " + deletedContacts.size() + " items from the trash?\nThis action CANNOT be undone.",
                "Confirm Permanent Purge", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            int cleared = contactService.emptyTrash();
            ToastNotification.show(this, "Purged " + cleared + " items permanently.", ToastNotification.Type.INFO);
            loadDeletedContacts();
            if (onDataChanged != null) onDataChanged.run();
        }
    }
}


package com.cms.ui.view;

import com.cms.model.*;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.AvatarPanel;
import com.cms.ui.components.CategoryBadge;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ToastNotification;
import com.cms.ui.dialogs.CallSimulatorDialog;
import com.cms.ui.dialogs.ContactEditorDialog;
import com.cms.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Modern Dark Contact Profile Dashboard.
 * Displays contact information, communication history,
 * notes, metadata and profile actions.
 */
public final class ContactDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final transient Consumer<Void> onRefreshNeeded;

    private Contact currentContact;

    // Header Components
    private final AvatarPanel avatarPanel;
    private final JLabel nameLabel;
    private final JLabel subtitleLabel;
    private final JPanel badgeContainer;

    private final ModernButton favoriteBtn;
    private final ModernButton editBtn;
    private final ModernButton deleteBtn;
    private final ModernButton callBtn;
    private final ModernButton vcardBtn;

    // Tab Components
    private final JTabbedPane tabbedPane;

    // Overview
    private final JPanel specificFieldsPanel;
    private final JPanel phonePanel;
    private final JLabel emailLabel;
    private final JLabel addressLabel;

    // Call History
    private final DefaultTableModel callLogTableModel;
    private final JTable callLogTable;

    // Notes
    private final JPanel tagsPanel;
    private final JTextArea notesArea;
    private final JLabel metadataLabel;

    private final JPanel emptyStatePanel;
    private final JPanel profileRoot;

    public ContactDetailPanel(
            ContactService contactService,
            Consumer<Void> onRefreshNeeded) {

        this.contactService = contactService;
        this.onRefreshNeeded = onRefreshNeeded;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ModernTheme.getBackground());

        // ================= EMPTY STATE =================

        emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setOpaque(true);
        emptyStatePanel.setBackground(ModernTheme.getBackground());

        JLabel emptyIcon = new JLabel("◎");
        emptyIcon.setFont(new Font("Segoe UI", Font.BOLD, 58));
        emptyIcon.setForeground(ModernTheme.ACCENT_PRIMARY);

        JLabel emptyMsg = new JLabel(
                "Select a contact to view profile details"
        );

        emptyMsg.setFont(ModernTheme.FONT_SUBHEADER);
        emptyMsg.setForeground(ModernTheme.getTextPrimary());

        JLabel emptySub = new JLabel(
                "Use + Add Contact to create a new record"
        );

        emptySub.setFont(ModernTheme.FONT_SMALL);
        emptySub.setForeground(ModernTheme.getTextMuted());

        JPanel emptyInner = new JPanel();
        emptyInner.setLayout(
                new BoxLayout(emptyInner, BoxLayout.Y_AXIS)
        );

        emptyInner.setOpaque(false);

        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyInner.add(emptyIcon);
        emptyInner.add(Box.createVerticalStrut(16));
        emptyInner.add(emptyMsg);
        emptyInner.add(Box.createVerticalStrut(8));
        emptyInner.add(emptySub);

        emptyStatePanel.add(emptyInner);

        // ================= PROFILE ROOT =================

        profileRoot = new JPanel(new BorderLayout());
        profileRoot.setOpaque(true);
        profileRoot.setBackground(ModernTheme.getBackground());

        // ================= PROFILE HEADER =================

        JPanel headerCard = new JPanel(new BorderLayout(18, 14));

        headerCard.setOpaque(true);
        headerCard.setBackground(ModernTheme.getSurface());

        headerCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                ModernTheme.getBorder()
                        ),
                        BorderFactory.createEmptyBorder(
                                22, 24, 20, 24
                        )
                )
        );

        avatarPanel = new AvatarPanel(
                "?",
                "#6366F1",
                68
        );

        headerCard.add(avatarPanel, BorderLayout.WEST);

        // Contact name and subtitle

        JPanel headerInfo = new JPanel();

        headerInfo.setLayout(
                new BoxLayout(headerInfo, BoxLayout.Y_AXIS)
        );

        headerInfo.setOpaque(false);

        JPanel titleRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );

        titleRow.setOpaque(false);

        nameLabel = new JLabel("Contact Name");
        nameLabel.setFont(ModernTheme.FONT_TITLE);
        nameLabel.setForeground(ModernTheme.getTextPrimary());

        titleRow.add(nameLabel);

        badgeContainer = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 5, 0)
        );

        badgeContainer.setOpaque(false);

        titleRow.add(badgeContainer);

        headerInfo.add(titleRow);

        subtitleLabel = new JLabel("Contact Summary");
        subtitleLabel.setFont(ModernTheme.FONT_BODY);
        subtitleLabel.setForeground(ModernTheme.getTextSecondary());

        headerInfo.add(Box.createVerticalStrut(7));
        headerInfo.add(subtitleLabel);

        headerCard.add(headerInfo, BorderLayout.CENTER);

        // ================= ACTION BUTTONS =================

        JPanel actionsRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        actionsRow.setOpaque(false);

        favoriteBtn = new ModernButton(
                "☆ Favorite",
                ModernButton.ButtonStyle.SECONDARY
        );

        callBtn = new ModernButton(
                "Call Console",
                ModernButton.ButtonStyle.SUCCESS
        );

        editBtn = new ModernButton(
                "Edit Details",
                ModernButton.ButtonStyle.PRIMARY
        );

        vcardBtn = new ModernButton(
                "Export vCard",
                ModernButton.ButtonStyle.SECONDARY
        );

        deleteBtn = new ModernButton(
                "Delete",
                ModernButton.ButtonStyle.DANGER
        );

        actionsRow.add(favoriteBtn);
        actionsRow.add(callBtn);
        actionsRow.add(editBtn);
        actionsRow.add(vcardBtn);
        actionsRow.add(deleteBtn);

        headerCard.add(actionsRow, BorderLayout.SOUTH);

        profileRoot.add(headerCard, BorderLayout.NORTH);

        // ================= TABBED CONTENT =================

        tabbedPane = new JTabbedPane();

        tabbedPane.setFont(ModernTheme.FONT_BODY_BOLD);
        tabbedPane.setOpaque(true);

        tabbedPane.setBackground(ModernTheme.getBackground());
        tabbedPane.setForeground(ModernTheme.getTextPrimary());

        tabbedPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        // Set colors used by the Swing tabbed pane UI.

        UIManager.put(
                "TabbedPane.contentAreaColor",
                ModernTheme.getBackground()
        );

        UIManager.put(
                "TabbedPane.background",
                ModernTheme.getSurface()
        );

        UIManager.put(
                "TabbedPane.foreground",
                ModernTheme.getTextPrimary()
        );

        UIManager.put(
                "TabbedPane.selected",
                ModernTheme.getSelectionBackground()
        );

        tabbedPane.updateUI();

        // ================= 1. OVERVIEW TAB =================

        JPanel overviewTab = new JPanel();

        overviewTab.setLayout(
                new BoxLayout(overviewTab, BoxLayout.Y_AXIS)
        );

        overviewTab.setOpaque(true);
        overviewTab.setBackground(ModernTheme.getBackground());

        overviewTab.setBorder(
                new EmptyBorder(18, 20, 18, 20)
        );

        // Category attributes card

        specificFieldsPanel = createSectionCard(
                "Category Specific Attributes"
        );

        overviewTab.add(specificFieldsPanel);

        overviewTab.add(Box.createVerticalStrut(16));

        // Communication card

        JPanel commCard = createSectionCard(
                "Communication & Location"
        );

        phonePanel = new JPanel();

        phonePanel.setLayout(
                new BoxLayout(phonePanel, BoxLayout.Y_AXIS)
        );

        phonePanel.setOpaque(false);

        commCard.add(phonePanel);
        commCard.add(Box.createVerticalStrut(10));

        emailLabel = new JLabel();
        emailLabel.setFont(ModernTheme.FONT_BODY);
        emailLabel.setForeground(ModernTheme.getTextPrimary());

        commCard.add(emailLabel);
        commCard.add(Box.createVerticalStrut(10));

        addressLabel = new JLabel();
        addressLabel.setFont(ModernTheme.FONT_BODY);
        addressLabel.setForeground(ModernTheme.getTextPrimary());

        commCard.add(addressLabel);

        overviewTab.add(commCard);

        JScrollPane overviewScroll = new JScrollPane(overviewTab);

        styleScrollPane(overviewScroll);

        tabbedPane.addTab(
                "Overview",
                overviewScroll
        );

        // ================= 2. CALL HISTORY TAB =================

        JPanel callTab = new JPanel(
                new BorderLayout(0, 14)
        );

        callTab.setOpaque(true);
        callTab.setBackground(ModernTheme.getBackground());

        callTab.setBorder(
                new EmptyBorder(18, 20, 18, 20)
        );

        JPanel callTopBar = new JPanel(new BorderLayout());

        callTopBar.setOpaque(false);

        JLabel callTitle = new JLabel(
                "Activity & Call Interaction Log"
        );

        callTitle.setFont(ModernTheme.FONT_SUBHEADER);
        callTitle.setForeground(ModernTheme.getTextPrimary());

        callTopBar.add(callTitle, BorderLayout.WEST);

        ModernButton addCallBtn = new ModernButton(
                "+ Log Interactive Call",
                ModernButton.ButtonStyle.SECONDARY
        );

        addCallBtn.addActionListener(
                e -> openCallSimulator()
        );

        callTopBar.add(addCallBtn, BorderLayout.EAST);

        callTab.add(callTopBar, BorderLayout.NORTH);

        String[] cols = {
                "Type",
                "Date & Time",
                "Duration",
                "Discussion Notes"
        };

        callLogTableModel = new DefaultTableModel(cols, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        callLogTable = new JTable(callLogTableModel);

        callLogTable.setFont(ModernTheme.FONT_BODY);
        callLogTable.setRowHeight(38);

        callLogTable.setBackground(ModernTheme.getSurface());
        callLogTable.setForeground(ModernTheme.getTextPrimary());

        callLogTable.setSelectionBackground(
                ModernTheme.getSelectionBackground()
        );

        callLogTable.setSelectionForeground(
                ModernTheme.getSelectionForeground()
        );

        callLogTable.setShowGrid(false);
        callLogTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        callLogTable.setFillsViewportHeight(true);

        callLogTable.setAutoCreateRowSorter(true);

        callLogTable.getTableHeader().setFont(
                ModernTheme.FONT_SMALL_BOLD
        );

        callLogTable.getTableHeader().setBackground(
                ModernTheme.getSurfaceVariant()
        );

        callLogTable.getTableHeader().setForeground(
                ModernTheme.getTextPrimary()
        );

        callLogTable.getTableHeader().setOpaque(true);

        callLogTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean selected,
                            boolean focused,
                            int row,
                            int column) {

                        Component c = super.getTableCellRendererComponent(
                                table,
                                value,
                                selected,
                                focused,
                                row,
                                column
                        );

                        c.setFont(ModernTheme.FONT_BODY);

                        if (selected) {
                            c.setBackground(
                                    ModernTheme.getSelectionBackground()
                            );

                            c.setForeground(
                                    ModernTheme.getSelectionForeground()
                            );

                        } else {
                            c.setBackground(
                                    ModernTheme.getSurface()
                            );

                            c.setForeground(
                                    ModernTheme.getTextPrimary()
                            );
                        }

                        if (c instanceof JLabel) {
                            ((JLabel) c).setBorder(
                                    new EmptyBorder(0, 10, 0, 10)
                            );
                        }

                        return c;
                    }
                }
        );

        JScrollPane callScroll = new JScrollPane(callLogTable);

        callScroll.setBorder(
                BorderFactory.createLineBorder(
                        ModernTheme.getBorder()
                )
        );

        callScroll.setOpaque(true);
        callScroll.setBackground(ModernTheme.getBackground());

        callScroll.getViewport().setOpaque(true);
        callScroll.getViewport().setBackground(
                ModernTheme.getSurface()
        );

        callTab.add(callScroll, BorderLayout.CENTER);

        tabbedPane.addTab(
                "Call History",
                callTab
        );

        // ================= 3. NOTES TAB =================

        JPanel notesTab = new JPanel();

        notesTab.setLayout(
                new BoxLayout(notesTab, BoxLayout.Y_AXIS)
        );

        notesTab.setOpaque(true);
        notesTab.setBackground(ModernTheme.getBackground());

        notesTab.setBorder(
                new EmptyBorder(18, 20, 18, 20)
        );

        // Tags card

        JPanel tagsCard = createSectionCard(
                "Assigned Tags & Classifications"
        );

        tagsPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 5)
        );

        tagsPanel.setOpaque(false);

        tagsCard.add(tagsPanel);

        notesTab.add(tagsCard);

        notesTab.add(Box.createVerticalStrut(16));

        // Notes card

        JPanel notesCard = createSectionCard(
                "Personal Notes"
        );

        notesArea = new JTextArea(5, 20);

        notesArea.setFont(ModernTheme.FONT_BODY);
        notesArea.setEditable(false);

        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        notesArea.setBackground(
                ModernTheme.getSurfaceVariant()
        );

        notesArea.setForeground(
                ModernTheme.getTextPrimary()
        );

        notesArea.setCaretColor(
                ModernTheme.getTextPrimary()
        );

        notesArea.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                ModernTheme.getBorder()
                        ),
                        new EmptyBorder(12, 14, 12, 14)
                )
        );

        notesCard.add(notesArea);

        notesTab.add(notesCard);

        notesTab.add(Box.createVerticalStrut(18));

        metadataLabel = new JLabel();

        metadataLabel.setFont(ModernTheme.FONT_SMALL);
        metadataLabel.setForeground(ModernTheme.getTextMuted());

        notesTab.add(metadataLabel);

        JScrollPane notesScroll = new JScrollPane(notesTab);

        styleScrollPane(notesScroll);

        tabbedPane.addTab(
                "Notes & Metadata",
                notesScroll
        );

        profileRoot.add(
                tabbedPane,
                BorderLayout.CENTER
        );

        // ================= ACTION LISTENERS =================

        favoriteBtn.addActionListener(
                e -> toggleFavorite()
        );

        callBtn.addActionListener(
                e -> openCallSimulator()
        );

        editBtn.addActionListener(
                e -> openEditor()
        );

        vcardBtn.addActionListener(
                e -> exportSingleVCard()
        );

        deleteBtn.addActionListener(
                e -> deleteCurrentContact()
        );

        add(emptyStatePanel, BorderLayout.CENTER);
    }

    // ================= SCROLL PANE STYLING =================

    private void styleScrollPane(JScrollPane scrollPane) {

        scrollPane.setOpaque(true);

        scrollPane.setBackground(
                ModernTheme.getBackground()
        );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.getViewport().setOpaque(true);

        scrollPane.getViewport().setBackground(
                ModernTheme.getBackground()
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
    }

    // ================= SECTION CARD =================

    private JPanel createSectionCard(String title) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setOpaque(true);

        card.setBackground(
                ModernTheme.getSurface()
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                ModernTheme.getBorder()
                        ),
                        BorderFactory.createEmptyBorder(
                                16, 18, 16, 18
                        )
                )
        );

        return card;
    }

    // ================= DISPLAY CONTACT =================

    public void displayContact(Contact contact) {

        this.currentContact = contact;

        if (contact == null) {

            removeAll();

            add(
                    emptyStatePanel,
                    BorderLayout.CENTER
            );

            setBackground(ModernTheme.getBackground());

            revalidate();
            repaint();

            return;
        }

        removeAll();

        // Header information

        avatarPanel.update(
                contact.getInitials(),
                contact.getAvatarColorHex()
        );

        nameLabel.setText(
                contact.getFullName()
        );

        subtitleLabel.setText(
                contact.getSummary()
        );

        // Category and group badges

        badgeContainer.removeAll();

        badgeContainer.add(
                new CategoryBadge(contact.getContactType())
        );

        badgeContainer.add(
                Box.createHorizontalStrut(4)
        );

        JLabel grpBadge = new JLabel(
                "[" + contact.getGroup().getDisplayName() + "]"
        );

        grpBadge.setFont(ModernTheme.FONT_SMALL_BOLD);

        grpBadge.setForeground(
                ModernTheme.getTextMuted()
        );

        badgeContainer.add(grpBadge);

        // Favorite button

        favoriteBtn.setText(
                contact.isFavorite()
                        ? "★ Favorited"
                        : "☆ Favorite"
        );

        favoriteBtn.setForeground(
                contact.isFavorite()
                        ? ModernTheme.ACCENT_FAVORITE
                        : ModernTheme.getTextPrimary()
        );

        // ================= CATEGORY ATTRIBUTES =================

        specificFieldsPanel.removeAll();

        JLabel specTitle = new JLabel(
                "✦ "
                        + contact.getContactType()
                        .getDisplayName()
                        .toUpperCase()
                        + " ATTRIBUTES"
        );

        specTitle.setFont(
                ModernTheme.FONT_SMALL_BOLD
        );

        specTitle.setForeground(
                ModernTheme.ACCENT_PRIMARY
        );

        specificFieldsPanel.add(specTitle);

        specificFieldsPanel.add(
                Box.createVerticalStrut(14)
        );

        Map<String, String> specifics =
                contact.getSpecificDetailsMap();

        if (specifics.isEmpty()) {

            JLabel emptySpec = new JLabel(
                    "No extra category attributes specified."
            );

            emptySpec.setFont(ModernTheme.FONT_SMALL);

            emptySpec.setForeground(
                    ModernTheme.getTextMuted()
            );

            specificFieldsPanel.add(emptySpec);

        } else {

            for (Map.Entry<String, String> entry : specifics.entrySet()) {

                JPanel row = new JPanel(
                        new BorderLayout(12, 0)
                );

                row.setOpaque(false);

                row.setBorder(
                        new EmptyBorder(5, 0, 5, 0)
                );

                JLabel key = new JLabel(
                        entry.getKey() + ":"
                );

                key.setFont(
                        ModernTheme.FONT_BODY_BOLD
                );

                key.setForeground(
                        ModernTheme.getTextSecondary()
                );

                JLabel val = new JLabel(
                        entry.getValue()
                );

                val.setFont(
                        ModernTheme.FONT_BODY
                );

                val.setForeground(
                        ModernTheme.getTextPrimary()
                );

                row.add(key, BorderLayout.WEST);
                row.add(val, BorderLayout.CENTER);

                specificFieldsPanel.add(row);
            }
        }

        // ================= PHONE NUMBERS =================

        phonePanel.removeAll();

        for (PhoneNumber p : contact.getPhoneNumbers()) {

            JPanel pRow = new JPanel(
                    new FlowLayout(
                            FlowLayout.LEFT,
                            8,
                            4
                    )
            );

            pRow.setOpaque(false);

            JLabel numLbl = new JLabel(
                    "☎  " + p.toString()
            );

            numLbl.setFont(
                    ModernTheme.FONT_BODY
            );

            numLbl.setForeground(
                    ModernTheme.getTextPrimary()
            );

            pRow.add(numLbl);

            phonePanel.add(pRow);
        }

        if (contact.getPhoneNumbers().isEmpty()) {

            JLabel noPhone = new JLabel(
                    "No phone numbers recorded."
            );

            noPhone.setFont(
                    ModernTheme.FONT_BODY
            );

            noPhone.setForeground(
                    ModernTheme.getTextMuted()
            );

            phonePanel.add(noPhone);
        }

        // ================= EMAIL =================

        String em = contact.getEmail().isEmpty()
                ? "No email provided"
                : contact.getEmail();

        if (!contact.getSecondaryEmail().isEmpty()) {

            em += "  •  " + contact.getSecondaryEmail();
        }

        emailLabel.setText(
                "✉  Email: " + em
        );

        emailLabel.setForeground(
                ModernTheme.getTextPrimary()
        );

        // ================= ADDRESS =================

        addressLabel.setText(
                "⌖  Address: "
                        + contact.getAddress().getFormatted()
        );

        addressLabel.setForeground(
                ModernTheme.getTextPrimary()
        );

        // ================= CALL HISTORY =================

        callLogTableModel.setRowCount(0);

        for (CallLog log : contact.getCallLogs()) {

            callLogTableModel.addRow(
                    new Object[]{
                            log.getCallType().getLabel(),
                            log.getFormattedTime(),
                            log.getFormattedDuration(),
                            log.getNotes()
                    }
            );
        }

        // ================= TAGS =================

        tagsPanel.removeAll();

        for (String tag : contact.getTags()) {

            JLabel tagBadge = new JLabel(
                    "#" + tag
            );

            tagBadge.setFont(
                    ModernTheme.FONT_SMALL_BOLD
            );

            tagBadge.setOpaque(true);

            tagBadge.setBackground(
                    ModernTheme.getSurfaceVariant()
            );

            tagBadge.setForeground(
                    ModernTheme.ACCENT_PRIMARY
            );

            tagBadge.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    ModernTheme.getBorder()
                            ),
                            new EmptyBorder(
                                    5, 11, 5, 11
                            )
                    )
            );

            tagsPanel.add(tagBadge);
        }

        if (contact.getTags().isEmpty()) {

            JLabel noTags = new JLabel(
                    "No tags assigned."
            );

            noTags.setFont(
                    ModernTheme.FONT_SMALL
            );

            noTags.setForeground(
                    ModernTheme.getTextMuted()
            );

            tagsPanel.add(noTags);
        }

        // ================= NOTES =================

        notesArea.setText(
                contact.getNotes().isEmpty()
                        ? "No personal notes entered."
                        : contact.getNotes()
        );

        // ================= METADATA =================

        metadataLabel.setText(
                "<html>System UUID: "
                        + contact.getId()
                        + "<br><br>Created: "
                        + DateTimeUtils.formatDateTime(
                                contact.getCreatedAt()
                        )
                        + "<br><br>Last Modified: "
                        + DateTimeUtils.formatDateTime(
                                contact.getUpdatedAt()
                        )
                        + "</html>"
        );

        // ================= FINAL DISPLAY =================

        add(
                profileRoot,
                BorderLayout.CENTER
        );

        revalidate();
        repaint();
    }

    // ================= FAVORITE =================

    private void toggleFavorite() {

        if (currentContact == null) {
            return;
        }

        try {

            boolean fav = contactService.toggleFavorite(
                    currentContact.getId()
            );

            currentContact.setFavorite(fav);

            favoriteBtn.setText(
                    fav ? "★ Favorited" : "☆ Favorite"
            );

            favoriteBtn.setForeground(
                    fav
                            ? ModernTheme.ACCENT_FAVORITE
                            : ModernTheme.getTextPrimary()
            );

            ToastNotification.show(
                    this,
                    fav
                            ? "Added to Favorites ★"
                            : "Removed from Favorites",
                    ToastNotification.Type.SUCCESS
            );

            onRefreshNeeded.accept(null);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= CALL SIMULATOR =================

    private void openCallSimulator() {

        if (currentContact == null) {
            return;
        }

        CallSimulatorDialog dialog =
                new CallSimulatorDialog(
                        SwingUtilities.getWindowAncestor(this),
                        currentContact,
                        contactService
                );

        dialog.setVisible(true);

        displayContact(currentContact);
    }

    // ================= EDIT CONTACT =================

    private void openEditor() {

        if (currentContact == null) {
            return;
        }

        ContactEditorDialog dialog =
                new ContactEditorDialog(
                        SwingUtilities.getWindowAncestor(this),
                        currentContact,
                        contactService
                );

        dialog.setVisible(true);

        if (dialog.isSaved()) {

            displayContact(
                    dialog.getSavedContact()
            );

            onRefreshNeeded.accept(null);
        }
    }

    // ================= EXPORT VCARD =================

    private void exportSingleVCard() {

        if (currentContact == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(
                new File(
                        currentContact.getFullName()
                                .replace(" ", "_")
                                + ".vcf"
                )
        );

        if (chooser.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            try (FileWriter fw = new FileWriter(
                    chooser.getSelectedFile()
            )) {

                fw.write(
                        currentContact.toVCard()
                );

                ToastNotification.show(
                        this,
                        "Exported vCard for "
                                + currentContact.getFullName(),
                        ToastNotification.Type.SUCCESS
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Failed to export: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // ================= DELETE CONTACT =================

    private void deleteCurrentContact() {

        if (currentContact == null) {
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Move "
                        + currentContact.getFullName()
                        + " to Recycle Bin?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opt == JOptionPane.YES_OPTION) {

            try {

                contactService.deleteContact(
                        currentContact.getId()
                );

                ToastNotification.show(
                        this,
                        "Moved to Recycle Bin",
                        ToastNotification.Type.INFO
                );

                displayContact(null);

                onRefreshNeeded.accept(null);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}

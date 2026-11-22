
package com.cms.ui.dialogs;

import com.cms.exception.DuplicateContactException;
import com.cms.exception.InvalidContactDataException;
import com.cms.model.*;
import com.cms.pattern.builder.ContactBuilder;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ModernTextField;
import com.cms.util.ColorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modern modal dialog for Adding and Editing Contacts.
 * Features dynamic subtype-specific card panels,
 * phone number manager, and live validation.
 */
public final class ContactEditorDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final Contact existingContact;

    private Contact savedContact;
    private boolean isSaved = false;

    // Type Selector
    private JComboBox<ContactType> typeCombo;

    // Core Fields
    private ModernTextField firstNameField;
    private ModernTextField lastNameField;
    private ModernTextField primaryPhoneField;
    private ModernTextField secondaryPhoneField;
    private ModernTextField emailField;
    private ModernTextField secondaryEmailField;
    private ModernTextField streetField;
    private ModernTextField cityField;
    private ModernTextField stateField;
    private ModernTextField zipField;
    private ModernTextField countryField;

    private JComboBox<ContactGroup> groupCombo;

    private ModernTextField tagsField;
    private JTextArea notesArea;
    private JCheckBox favoriteCheck;

    // Dynamic Subtype Panel
    private JPanel dynamicCardPanel;
    private CardLayout cardLayout;

    // Personal Fields
    private ModernTextField relationshipField;
    private ModernTextField birthdayField;
    private ModernTextField nicknameField;

    // Business Fields
    private ModernTextField companyField;
    private ModernTextField jobTitleField;
    private ModernTextField departmentField;
    private ModernTextField workEmailField;
    private ModernTextField linkedinField;

    // Emergency Fields
    private JComboBox<EmergencyPriority> priorityCombo;
    private JComboBox<BloodGroup> bloodGroupCombo;
    private ModernTextField altEmergencyPhoneField;
    private ModernTextField hospitalField;
    private JTextArea medicalNotesArea;

    public ContactEditorDialog(
            Window owner,
            Contact existingContact,
            ContactService service) {

        super(
                owner,
                existingContact != null
                        ? "Edit Contact — " + existingContact.getFullName()
                        : "Add New Contact",
                ModalityType.APPLICATION_MODAL
        );

        this.existingContact = existingContact;
        this.contactService = service;

        initDialog();

        if (existingContact != null) {
            populateFields(existingContact);
        }
    }

    private void initDialog() {

        // Dialog size and positioning
        setSize(850, 650);
        setMinimumSize(new Dimension(750, 550));
        setResizable(true);
        setLocationRelativeTo(getOwner());

        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // ================= TOP BANNER =================

        JPanel banner = new JPanel(new BorderLayout(16, 0));
        banner.setOpaque(true);
        banner.setBackground(ModernTheme.getSurface());

        banner.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                ModernTheme.getBorder()
                        ),
                        BorderFactory.createEmptyBorder(
                                18, 24, 18, 24
                        )
                )
        );

        JPanel titleBox = new JPanel();
        titleBox.setLayout(
                new BoxLayout(titleBox, BoxLayout.Y_AXIS)
        );
        titleBox.setOpaque(false);

        JLabel titleLbl = new JLabel(
                existingContact != null
                        ? "✏ Edit Contact Details"
                        : "👤 Create New Contact Record"
        );

        titleLbl.setFont(ModernTheme.FONT_TITLE);
        titleLbl.setForeground(ModernTheme.getTextPrimary());

        titleBox.add(titleLbl);

        JLabel subLbl = new JLabel(
                "Enter contact personal, business, or emergency information"
        );

        subLbl.setFont(ModernTheme.FONT_SMALL);
        subLbl.setForeground(ModernTheme.getTextSecondary());

        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subLbl);

        banner.add(titleBox, BorderLayout.WEST);

        JPanel typePanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        typePanel.setOpaque(false);

        JLabel typeLbl = new JLabel("Category:");
        typeLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        typeLbl.setForeground(ModernTheme.getTextSecondary());

        typePanel.add(typeLbl);

        typeCombo = new JComboBox<>(ContactType.values());
        typeCombo.setFont(ModernTheme.FONT_BODY);

        typeCombo.setEnabled(existingContact == null);

        typeCombo.addActionListener(e -> {
            ContactType ct =
                    (ContactType) typeCombo.getSelectedItem();

            if (ct != null && cardLayout != null
                    && dynamicCardPanel != null) {
                cardLayout.show(dynamicCardPanel, ct.name());
            }
        });

        typePanel.add(typeCombo);
        banner.add(typePanel, BorderLayout.EAST);

        add(banner, BorderLayout.NORTH);

        // ================= FORM BODY =================

        JPanel formBody = new JPanel();

        formBody.setLayout(
                new BoxLayout(formBody, BoxLayout.Y_AXIS)
        );

        formBody.setOpaque(false);
        formBody.setBorder(
                new EmptyBorder(16, 24, 16, 24)
        );

        // 1. Basic Identification

        formBody.add(
                createSectionHeader("1. Basic Identification")
        );

        JPanel nameRow = new JPanel(
                new GridLayout(1, 2, 14, 0)
        );
        nameRow.setOpaque(false);

        firstNameField = new ModernTextField();
        firstNameField.setPlaceholder("First Name (Required)");

        lastNameField = new ModernTextField();
        lastNameField.setPlaceholder("Last Name");

        nameRow.add(
                createLabeledField("First Name *", firstNameField)
        );

        nameRow.add(
                createLabeledField("Last Name", lastNameField)
        );

        formBody.add(nameRow);
        formBody.add(Box.createVerticalStrut(12));

        // 2. Phone Numbers

        formBody.add(
                createSectionHeader("2. Phone Numbers")
        );

        JPanel phoneRow = new JPanel(
                new GridLayout(1, 2, 14, 0)
        );
        phoneRow.setOpaque(false);

        primaryPhoneField = new ModernTextField();
        primaryPhoneField.setPlaceholder("+91 98765 43210");

        secondaryPhoneField = new ModernTextField();
        secondaryPhoneField.setPlaceholder(
                "Alternate / Work Phone (Optional)"
        );

        phoneRow.add(
                createLabeledField(
                        "Primary Mobile *",
                        primaryPhoneField
                )
        );

        phoneRow.add(
                createLabeledField(
                        "Secondary Phone",
                        secondaryPhoneField
                )
        );

        formBody.add(phoneRow);
        formBody.add(Box.createVerticalStrut(12));

        // 3. Email Communication

        formBody.add(
                createSectionHeader("3. Email Communication")
        );

        JPanel emailRow = new JPanel(
                new GridLayout(1, 2, 14, 0)
        );
        emailRow.setOpaque(false);

        emailField = new ModernTextField();
        emailField.setPlaceholder(
                "primary.email@domain.com"
        );

        secondaryEmailField = new ModernTextField();
        secondaryEmailField.setPlaceholder(
                "alternate@domain.com"
        );

        emailRow.add(
                createLabeledField(
                        "Primary Email",
                        emailField
                )
        );

        emailRow.add(
                createLabeledField(
                        "Secondary Email",
                        secondaryEmailField
                )
        );

        formBody.add(emailRow);
        formBody.add(Box.createVerticalStrut(12));

        // 4. Category-Specific Fields

        formBody.add(
                createSectionHeader(
                        "4. Category-Specific Fields"
                )
        );

        cardLayout = new CardLayout();
        dynamicCardPanel = new JPanel(cardLayout);
        dynamicCardPanel.setOpaque(false);

        dynamicCardPanel.add(
                buildPersonalPanel(),
                ContactType.PERSONAL.name()
        );

        dynamicCardPanel.add(
                buildBusinessPanel(),
                ContactType.BUSINESS.name()
        );

        dynamicCardPanel.add(
                buildEmergencyPanel(),
                ContactType.EMERGENCY.name()
        );

        formBody.add(dynamicCardPanel);
        formBody.add(Box.createVerticalStrut(12));

        // 5. Physical Location

        formBody.add(
                createSectionHeader("5. Physical Location")
        );

        streetField = new ModernTextField();
        streetField.setPlaceholder(
                "Street Address, Landmark"
        );

        formBody.add(
                createLabeledField(
                        "Street Address",
                        streetField
                )
        );

        formBody.add(Box.createVerticalStrut(8));

        JPanel addrRow = new JPanel(
                new GridLayout(1, 4, 8, 0)
        );
        addrRow.setOpaque(false);

        cityField = new ModernTextField();
        stateField = new ModernTextField();
        zipField = new ModernTextField();

        countryField = new ModernTextField("India");

        addrRow.add(createLabeledField("City", cityField));
        addrRow.add(createLabeledField("State", stateField));
        addrRow.add(createLabeledField("Postal Code", zipField));
        addrRow.add(createLabeledField("Country", countryField));

        formBody.add(addrRow);
        formBody.add(Box.createVerticalStrut(12));

        // 6. Organization, Tags and Notes

        formBody.add(
                createSectionHeader(
                        "6. Organization, Tags & Notes"
                )
        );

        JPanel orgRow = new JPanel(
                new GridLayout(1, 2, 14, 0)
        );
        orgRow.setOpaque(false);

        groupCombo = new JComboBox<>(ContactGroup.values());
        groupCombo.setFont(ModernTheme.FONT_BODY);

        tagsField = new ModernTextField();
        tagsField.setPlaceholder(
                "college, friend, vip (comma separated)"
        );

        orgRow.add(
                createLabeledField(
                        "Group Directory",
                        groupCombo
                )
        );

        orgRow.add(
                createLabeledField(
                        "Tags",
                        tagsField
                )
        );

        formBody.add(orgRow);
        formBody.add(Box.createVerticalStrut(10));

        favoriteCheck = new JCheckBox(
                "Mark as Starred Favorite ★"
        );

        favoriteCheck.setFont(ModernTheme.FONT_BODY_BOLD);
        favoriteCheck.setForeground(
                ModernTheme.ACCENT_FAVORITE
        );
        favoriteCheck.setOpaque(false);

        formBody.add(favoriteCheck);
        formBody.add(Box.createVerticalStrut(10));

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(ModernTheme.FONT_BODY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        notesArea.setBackground(ModernTheme.getSurface());
        notesArea.setForeground(
                ModernTheme.getTextPrimary()
        );

        notesArea.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                ModernTheme.getBorder()
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 10, 8, 10
                        )
                )
        );

        formBody.add(
                createLabeledField(
                        "Personal Notes / Remarks",
                        notesArea
                )
        );

        // Scrollable form
        JScrollPane scrollPane = new JScrollPane(formBody);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);

        scrollPane.getViewport().setOpaque(false);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);

        // ================= BOTTOM ACTION FOOTER =================

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 12)
        );

        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());

        footer.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0,
                        ModernTheme.getBorder()
                )
        );

        // Cancel Button
        ModernButton cancelBtn = new ModernButton(
                "Cancel",
                ModernButton.ButtonStyle.SECONDARY
        );

        cancelBtn.addActionListener(e -> dispose());

        footer.add(cancelBtn);

        // Save Button
        ModernButton saveBtn = new ModernButton(
                "Save Contact Record ✓",
                ModernButton.ButtonStyle.PRIMARY
        );

        saveBtn.setToolTipText(
                "Click to save this contact"
        );

        saveBtn.addActionListener(e -> handleSave());

        footer.add(saveBtn);

        // Footer always stays at the bottom
        add(footer, BorderLayout.SOUTH);

        // Default category
        if (existingContact == null) {
            cardLayout.show(
                    dynamicCardPanel,
                    ContactType.PERSONAL.name()
            );
        }
    }

    // ================= PERSONAL PANEL =================

    private JPanel buildPersonalPanel() {

        JPanel p = new JPanel(
                new GridLayout(1, 3, 12, 0)
        );

        p.setOpaque(false);

        relationshipField = new ModernTextField();
        relationshipField.setPlaceholder(
                "e.g. Brother, Best Friend"
        );

        birthdayField = new ModernTextField();
        birthdayField.setPlaceholder(
                "YYYY-MM-DD (e.g. 2005-08-15)"
        );

        nicknameField = new ModernTextField();
        nicknameField.setPlaceholder(
                "e.g. Peehu, Aaru"
        );

        p.add(
                createLabeledField(
                        "Relationship",
                        relationshipField
                )
        );

        p.add(
                createLabeledField(
                        "Birthday",
                        birthdayField
                )
        );

        p.add(
                createLabeledField(
                        "Nickname",
                        nicknameField
                )
        );

        return p;
    }

    // ================= BUSINESS PANEL =================

    private JPanel buildBusinessPanel() {

        JPanel root = new JPanel();

        root.setLayout(
                new BoxLayout(root, BoxLayout.Y_AXIS)
        );

        root.setOpaque(false);

        JPanel row1 = new JPanel(
                new GridLayout(1, 3, 12, 0)
        );

        row1.setOpaque(false);

        companyField = new ModernTextField();
        companyField.setPlaceholder(
                "Company / Institution"
        );

        jobTitleField = new ModernTextField();
        jobTitleField.setPlaceholder("Job Title");

        departmentField = new ModernTextField();
        departmentField.setPlaceholder("Department");

        row1.add(
                createLabeledField("Company", companyField)
        );

        row1.add(
                createLabeledField("Job Title", jobTitleField)
        );

        row1.add(
                createLabeledField(
                        "Department",
                        departmentField
                )
        );

        root.add(row1);
        root.add(Box.createVerticalStrut(8));

        JPanel row2 = new JPanel(
                new GridLayout(1, 2, 12, 0)
        );

        row2.setOpaque(false);

        workEmailField = new ModernTextField();
        workEmailField.setPlaceholder(
                "work.email@company.com"
        );

        linkedinField = new ModernTextField();
        linkedinField.setPlaceholder(
                "https://linkedin.com/in/username"
        );

        row2.add(
                createLabeledField(
                        "Work Email",
                        workEmailField
                )
        );

        row2.add(
                createLabeledField(
                        "LinkedIn Profile URL",
                        linkedinField
                )
        );

        root.add(row2);

        return root;
    }

    // ================= EMERGENCY PANEL =================

    private JPanel buildEmergencyPanel() {

        JPanel root = new JPanel();

        root.setLayout(
                new BoxLayout(root, BoxLayout.Y_AXIS)
        );

        root.setOpaque(false);

        JPanel row1 = new JPanel(
                new GridLayout(1, 4, 10, 0)
        );

        row1.setOpaque(false);

        priorityCombo = new JComboBox<>(
                EmergencyPriority.values()
        );

        priorityCombo.setFont(ModernTheme.FONT_BODY);

        bloodGroupCombo = new JComboBox<>(
                BloodGroup.values()
        );

        bloodGroupCombo.setFont(ModernTheme.FONT_BODY);

        altEmergencyPhoneField = new ModernTextField();
        altEmergencyPhoneField.setPlaceholder(
                "Alternate Emergency Phone"
        );

        hospitalField = new ModernTextField();
        hospitalField.setPlaceholder(
                "Hospital / Clinic Affiliation"
        );

        row1.add(
                createLabeledField(
                        "Priority Level",
                        priorityCombo
                )
        );

        row1.add(
                createLabeledField(
                        "Blood Group",
                        bloodGroupCombo
                )
        );

        row1.add(
                createLabeledField(
                        "Alternate SOS Phone",
                        altEmergencyPhoneField
                )
        );

        row1.add(
                createLabeledField(
                        "Hospital",
                        hospitalField
                )
        );

        root.add(row1);
        root.add(Box.createVerticalStrut(8));

        medicalNotesArea = new JTextArea(2, 20);

        medicalNotesArea.setFont(ModernTheme.FONT_BODY);
        medicalNotesArea.setLineWrap(true);
        medicalNotesArea.setWrapStyleWord(true);

        medicalNotesArea.setBackground(
                ModernTheme.getSurface()
        );

        medicalNotesArea.setForeground(
                ModernTheme.getTextPrimary()
        );

        medicalNotesArea.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                ModernTheme.getBorder()
                        ),
                        BorderFactory.createEmptyBorder(
                                6, 8, 6, 8
                        )
                )
        );

        root.add(
                createLabeledField(
                        "Critical Medical Notes & Known Allergies",
                        medicalNotesArea
                )
        );

        return root;
    }

    // ================= POPULATE EXISTING CONTACT =================

    private void populateFields(Contact c) {

        typeCombo.setSelectedItem(c.getContactType());

        cardLayout.show(
                dynamicCardPanel,
                c.getContactType().name()
        );

        firstNameField.setText(c.getFirstName());
        lastNameField.setText(c.getLastName());

        List<PhoneNumber> phones = c.getPhoneNumbers();

        if (phones.size() > 0) {
            primaryPhoneField.setText(
                    phones.get(0).getNumber()
            );
        }

        if (phones.size() > 1) {
            secondaryPhoneField.setText(
                    phones.get(1).getNumber()
            );
        }

        emailField.setText(c.getEmail());
        secondaryEmailField.setText(c.getSecondaryEmail());

        Address a = c.getAddress();

        streetField.setText(a.getStreet());
        cityField.setText(a.getCity());
        stateField.setText(a.getState());
        zipField.setText(a.getPostalCode());
        countryField.setText(a.getCountry());

        groupCombo.setSelectedItem(c.getGroup());

        tagsField.setText(
                String.join(", ", c.getTags())
        );

        notesArea.setText(c.getNotes());

        favoriteCheck.setSelected(c.isFavorite());

        if (c instanceof PersonalContact) {

            PersonalContact pc = (PersonalContact) c;

            relationshipField.setText(
                    pc.getRelationship()
            );

            if (pc.getBirthday() != null) {
                birthdayField.setText(
                        pc.getBirthday().toString()
                );
            }

            nicknameField.setText(pc.getNickname());

        } else if (c instanceof BusinessContact) {

            BusinessContact bc = (BusinessContact) c;

            companyField.setText(bc.getCompanyName());
            jobTitleField.setText(bc.getJobTitle());
            departmentField.setText(bc.getDepartment());
            workEmailField.setText(bc.getWorkEmail());
            linkedinField.setText(bc.getLinkedinUrl());

        } else if (c instanceof EmergencyContact) {

            EmergencyContact ec = (EmergencyContact) c;

            priorityCombo.setSelectedItem(ec.getPriority());
            bloodGroupCombo.setSelectedItem(ec.getBloodGroup());

            altEmergencyPhoneField.setText(
                    ec.getAlternateEmergencyPhone()
            );

            hospitalField.setText(
                    ec.getHospitalAffiliation()
            );

            medicalNotesArea.setText(
                    ec.getMedicalNotes()
                            + (ec.getKnownAllergies().isEmpty()
                            ? ""
                            : " | Allergies: "
                            + ec.getKnownAllergies())
            );
        }
    }

    // ================= SAVE CONTACT =================

    private void handleSave() {

        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();

        ContactType type =
                (ContactType) typeCombo.getSelectedItem();

        if (type == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a contact category.",
                    "Missing Category",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (fn.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "First name is required.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            firstNameField.requestFocusInWindow();
            return;
        }

        if (primaryPhoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a primary mobile number.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            primaryPhoneField.requestFocusInWindow();
            return;
        }

        ContactBuilder builder = new ContactBuilder(type)
                .id(
                        existingContact != null
                                ? existingContact.getId()
                                : null
                )
                .name(fn, ln)
                .email(emailField.getText().trim())
                .secondaryEmail(
                        secondaryEmailField.getText().trim()
                )
                .address(
                        streetField.getText().trim(),
                        cityField.getText().trim(),
                        stateField.getText().trim(),
                        zipField.getText().trim(),
                        countryField.getText().trim()
                )
                .group(
                        (ContactGroup) groupCombo.getSelectedItem()
                )
                .notes(notesArea.getText().trim())
                .favorite(favoriteCheck.isSelected())
                .avatarColorHex(
                        ColorUtils.getHexForName(fn + " " + ln)
                );

        // Phone Numbers

        String p1 = primaryPhoneField.getText().trim();

        if (!p1.isEmpty()) {
            builder.phone(
                    PhoneNumber.Type.MOBILE,
                    p1,
                    true
            );
        }

        String p2 = secondaryPhoneField.getText().trim();

        if (!p2.isEmpty()) {
            builder.phone(
                    PhoneNumber.Type.WORK,
                    p2,
                    false
            );
        }

        // Tags

        String tagsRaw = tagsField.getText().trim();

        if (!tagsRaw.isEmpty()) {

            for (String t : tagsRaw.split(",")) {

                if (!t.trim().isEmpty()) {
                    builder.tag(t.trim());
                }
            }
        }

        // Personal Contact

        if (type == ContactType.PERSONAL) {

            builder.relationship(
                    relationshipField.getText().trim()
            );

            builder.nickname(
                    nicknameField.getText().trim()
            );

            String bday = birthdayField.getText().trim();

            if (!bday.isEmpty()) {

                try {

                    builder.birthday(
                            LocalDate.parse(bday)
                    );

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Birthday must be in format YYYY-MM-DD",
                            "Invalid Date",
                            JOptionPane.WARNING_MESSAGE
                    );

                    birthdayField.requestFocusInWindow();
                    return;
                }
            }

        // Business Contact

        } else if (type == ContactType.BUSINESS) {

            builder.company(
                    companyField.getText().trim()
            );

            builder.jobTitle(
                    jobTitleField.getText().trim()
            );

            builder.department(
                    departmentField.getText().trim()
            );

            builder.workEmail(
                    workEmailField.getText().trim()
            );

            builder.linkedinUrl(
                    linkedinField.getText().trim()
            );

        // Emergency Contact

        } else if (type == ContactType.EMERGENCY) {

            builder.priority(
                    (EmergencyPriority) priorityCombo.getSelectedItem()
            );

            builder.bloodGroup(
                    (BloodGroup) bloodGroupCombo.getSelectedItem()
            );

            builder.alternateEmergencyPhone(
                    altEmergencyPhoneField.getText().trim()
            );

            builder.hospitalAffiliation(
                    hospitalField.getText().trim()
            );

            builder.medicalNotes(
                    medicalNotesArea.getText().trim()
            );
        }

        try {

            Contact assembled = builder.build();

            if (existingContact == null) {

                savedContact =
                        contactService.createContact(assembled);

            } else {

                assembled.setCallLogs(
                        existingContact.getCallLogs()
                );

                assembled.setCreatedAt(
                        existingContact.getCreatedAt()
                );

                savedContact =
                        contactService.updateContact(assembled);
            }

            isSaved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Contact saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (InvalidContactDataException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (DuplicateContactException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Duplicate Detected",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Save error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= REUSABLE UI COMPONENTS =================

    private JPanel createLabeledField(
            String label,
            JComponent comp) {

        JPanel p = new JPanel(
                new BorderLayout(0, 4)
        );

        p.setOpaque(false);

        JLabel l = new JLabel(label);

        l.setFont(ModernTheme.FONT_SMALL_BOLD);

        l.setForeground(
                ModernTheme.getTextSecondary()
        );

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);

        return p;
    }

    private JPanel createSectionHeader(String title) {

        JPanel p = new JPanel(
                new BorderLayout()
        );

        p.setOpaque(false);

        p.setBorder(
                new EmptyBorder(10, 0, 6, 0)
        );

        JLabel l = new JLabel(title);

        l.setFont(ModernTheme.FONT_SUBHEADER);

        l.setForeground(
                ModernTheme.ACCENT_PRIMARY
        );

        p.add(l, BorderLayout.WEST);

        return p;
    }

    // ================= PUBLIC METHODS =================

    public boolean isSaved() {
        return isSaved;
    }

    public Contact getSavedContact() {
        return savedContact;
    }
}

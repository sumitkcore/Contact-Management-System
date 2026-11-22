package com.cms.ui.dialogs;

import com.cms.model.CallType;
import com.cms.model.Contact;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;
import com.cms.ui.components.ModernTextField;
import com.cms.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interactive Phone Call Simulator & Logger.
 * Allows simulating an active call with stopwatch timer and logging notes directly.
 */
public final class CallSimulatorDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final Contact contact;
    private final transient ContactService contactService;

    private JComboBox<CallType> callTypeCombo;
    private JLabel timerLabel;
    private ModernButton startEndBtn;
    private ModernTextField notesField;
    private Timer callTimer;
    private int elapsedSeconds = 0;
    private boolean isCallActive = false;

    public CallSimulatorDialog(Window owner, Contact contact, ContactService service) {
        super(owner, "Call Simulator — " + contact.getFullName(), ModalityType.APPLICATION_MODAL);
        this.contact = contact;
        this.contactService = service;

        initDialog();
    }

    private void initDialog() {
        setSize(480, 380);
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

        JLabel title = new JLabel("📞 Interactive Call Console");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.getTextPrimary());
        header.add(title, BorderLayout.NORTH);

        JLabel sub = new JLabel("Contact: " + contact.getFullName() + " (" + contact.getPrimaryPhoneNumber() + ")");
        sub.setFont(ModernTheme.FONT_BODY);
        sub.setForeground(ModernTheme.getTextSecondary());
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typeRow.setOpaque(false);
        JLabel typeLbl = new JLabel("Call Direction:");
        typeLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        typeLbl.setForeground(ModernTheme.getTextPrimary());
        typeRow.add(typeLbl);

        callTypeCombo = new JComboBox<>(CallType.values());
        callTypeCombo.setFont(ModernTheme.FONT_BODY);
        typeRow.add(callTypeCombo);
        center.add(typeRow);
        center.add(Box.createVerticalStrut(16));

        // Timer Display
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 38));
        timerLabel.setForeground(ModernTheme.ACCENT_PRIMARY);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(timerLabel);
        center.add(Box.createVerticalStrut(14));

        startEndBtn = new ModernButton("Start Live Call 📞", ModernButton.ButtonStyle.SUCCESS);
        startEndBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startEndBtn.addActionListener(e -> toggleCall());
        center.add(startEndBtn);
        center.add(Box.createVerticalStrut(16));

        notesField = new ModernTextField();
        notesField.setPlaceholder("Enter call summary or discussion notes...");
        JPanel notesPanel = new JPanel(new BorderLayout(0, 4));
        notesPanel.setOpaque(false);
        JLabel nLbl = new JLabel("Call Notes / Discussion Summary:");
        nLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        nLbl.setForeground(ModernTheme.getTextSecondary());
        notesPanel.add(nLbl, BorderLayout.NORTH);
        notesPanel.add(notesField, BorderLayout.CENTER);
        center.add(notesPanel);

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()));

        ModernButton cancelBtn = new ModernButton("Close", ModernButton.ButtonStyle.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        footer.add(cancelBtn);

        ModernButton saveLogBtn = new ModernButton("Save Call Log ✓", ModernButton.ButtonStyle.PRIMARY);
        saveLogBtn.addActionListener(e -> saveCallRecord());
        footer.add(saveLogBtn);

        add(footer, BorderLayout.SOUTH);

        setupTimer();
    }

    private void setupTimer() {
        callTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                int m = elapsedSeconds / 60;
                int s = elapsedSeconds % 60;
                timerLabel.setText(String.format("%02d:%02d", m, s));
            }
        });
    }

    private void toggleCall() {
        if (!isCallActive) {
            isCallActive = true;
            elapsedSeconds = 0;
            callTimer.start();
            startEndBtn.setText("End Call 🛑");
            startEndBtn.setBackground(ModernTheme.ACCENT_DANGER);
            timerLabel.setForeground(ModernTheme.ACCENT_SUCCESS);
        } else {
            isCallActive = false;
            callTimer.stop();
            startEndBtn.setText("Restart Call 📞");
            startEndBtn.setBackground(ModernTheme.ACCENT_PRIMARY);
            timerLabel.setForeground(ModernTheme.ACCENT_PRIMARY);
        }
    }

    private void saveCallRecord() {
        if (isCallActive) {
            toggleCall();
        }
        CallType type = (CallType) callTypeCombo.getSelectedItem();
        int duration = (type == CallType.MISSED) ? 0 : Math.max(elapsedSeconds, 15);
        String notes = notesField.getText().trim();
        if (notes.isEmpty()) {
            notes = "Standard " + type.getLabel() + " recorded.";
        }

        try {
            contactService.logCall(contact.getId(), type, duration, notes);
            ToastNotification.show(getOwner(), "Call logged successfully!", ToastNotification.Type.SUCCESS);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

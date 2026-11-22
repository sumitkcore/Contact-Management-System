package com.cms.ui.components;

import com.cms.model.Contact;
import com.cms.ui.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Card-style ListCellRenderer for Contacts.
 * Displays high-DPI Avatar, Full Name, Category Badge, Primary Phone, and Favorite Star.
 */
public final class ContactListRenderer extends JPanel implements ListCellRenderer<Contact> {
    private static final long serialVersionUID = 1L;

    private final AvatarPanel avatarPanel;
    private final JLabel nameLabel;
    private final JLabel subtitleLabel;
    private final JLabel phoneLabel;
    private final JLabel favoriteLabel;
    private final JPanel badgeContainer;
    private final JPanel centerPanel;
    private final JPanel rightPanel;

    public ContactListRenderer() {
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        setOpaque(true);

        avatarPanel = new AvatarPanel("?", "#3B82F6", 44);
        add(avatarPanel, BorderLayout.WEST);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        nameLabel = new JLabel();
        nameLabel.setFont(ModernTheme.FONT_BODY_BOLD);
        centerPanel.add(nameLabel);

        centerPanel.add(Box.createVerticalStrut(2));
        subtitleLabel = new JLabel();
        subtitleLabel.setFont(ModernTheme.FONT_SMALL);
        centerPanel.add(subtitleLabel);

        add(centerPanel, BorderLayout.CENTER);

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        badgeContainer.setOpaque(false);

        favoriteLabel = new JLabel("★");
        favoriteLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        favoriteLabel.setForeground(ModernTheme.ACCENT_FAVORITE);
        badgeContainer.add(favoriteLabel);

        rightPanel.add(badgeContainer);

        phoneLabel = new JLabel();
        phoneLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        phoneLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(3));
        rightPanel.add(phoneLabel);

        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Contact> list, Contact contact, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (contact == null) {
            return this;
        }

        nameLabel.setText(contact.getFullName());
        subtitleLabel.setText(contact.getSummary());

        String phone = contact.getPrimaryPhoneNumber();
        phoneLabel.setText(phone.isEmpty() ? "No phone" : phone);

        avatarPanel.update(contact.getInitials(), contact.getAvatarColorHex());
        favoriteLabel.setVisible(contact.isFavorite());

        badgeContainer.removeAll();
        if (contact.isFavorite()) {
            badgeContainer.add(favoriteLabel);
        }
        badgeContainer.add(new CategoryBadge(contact.getContactType()));

        if (isSelected) {
            setBackground(ModernTheme.getSelectionBackground());
            nameLabel.setForeground(ModernTheme.getSelectionForeground());
            subtitleLabel.setForeground(ModernTheme.getSelectionForeground());
            phoneLabel.setForeground(ModernTheme.getSelectionForeground());
        } else {
            setBackground(index % 2 == 0 ? ModernTheme.getSurface() : ModernTheme.getSurfaceVariant());
            nameLabel.setForeground(ModernTheme.getTextPrimary());
            subtitleLabel.setForeground(ModernTheme.getTextSecondary());
            phoneLabel.setForeground(ModernTheme.getTextMuted());
        }

        return this;
    }
}

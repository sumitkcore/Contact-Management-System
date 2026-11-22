package com.cms.ui.components;

import com.cms.ui.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Metric Summary Card showing icons, numbers, and descriptive labels.
 */
public final class StatCard extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel numberLabel;
    private final JLabel titleLabel;
    private final JLabel iconLabel;
    private final Color accentColor;

    public StatCard(String title, String initialValue, String icon, Color accentColor) {
        this.accentColor = accentColor != null ? accentColor : ModernTheme.ACCENT_PRIMARY;
        setLayout(new BorderLayout(10, 4));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        setOpaque(false);

        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        numberLabel = new JLabel(initialValue);
        numberLabel.setFont(ModernTheme.FONT_TITLE);
        numberLabel.setForeground(this.accentColor);
        textPanel.add(numberLabel);

        titleLabel = new JLabel(title);
        titleLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(ModernTheme.getTextSecondary());
        textPanel.add(titleLabel);

        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        numberLabel.setText(value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(ModernTheme.getSurface());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);

        // Accent indicator bar on left
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 4, h, 4, 4);

        // Border
        g2.setColor(ModernTheme.getBorder());
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }
}

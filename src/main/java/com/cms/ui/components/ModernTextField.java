package com.cms.ui.components;

import com.cms.ui.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Modern Text Field with rounded border and focus glow.
 */
public final class ModernTextField extends JTextField {
    private static final long serialVersionUID = 1L;

    private boolean isFocused = false;
    private String placeholder = "";

    public ModernTextField(int columns) {
        super(columns);
        init();
    }

    public ModernTextField() {
        super();
        init();
    }

    public ModernTextField(String text) {
        super(text);
        init();
    }

    private void init() {
        setFont(ModernTheme.FONT_BODY);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setOpaque(false);

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        // Draw background
        g2.setColor(ModernTheme.getSurface());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);

        // Draw border
        if (isFocused) {
            g2.setColor(ModernTheme.ACCENT_PRIMARY);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(ModernTheme.getBorder());
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

        // Paint text
        setCaretColor(ModernTheme.getTextPrimary());
        setForeground(ModernTheme.getTextPrimary());
        super.paintComponent(g);

        // Draw placeholder if empty
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(ModernTheme.getTextMuted());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int py = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 12, py);
        }

        g2.dispose();
    }
}

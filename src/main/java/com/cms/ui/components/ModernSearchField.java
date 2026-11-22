package com.cms.ui.components;

import com.cms.ui.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Modern Search Field with magnifying glass icon, clear button, and search debounce.
 */
public final class ModernSearchField extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField textField;
    private final JLabel clearLabel;
    private transient Consumer<String> onSearchCallback;
    private final Timer debounceTimer;

    public ModernSearchField(String placeholder) {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setForeground(ModernTheme.getTextMuted());
        add(iconLabel, BorderLayout.WEST);

        textField = new JTextField();
        textField.setFont(ModernTheme.FONT_BODY);
        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setForeground(ModernTheme.getTextPrimary());
        textField.setCaretColor(ModernTheme.getTextPrimary());
        add(textField, BorderLayout.CENTER);

        // 180ms debounce timer for responsive real-time search without lagging
        debounceTimer = new Timer(180, e -> {
            if (onSearchCallback != null) {
                onSearchCallback.accept(textField.getText().trim());
            }
        });
        debounceTimer.setRepeats(false);

        clearLabel = new JLabel("✕");
        clearLabel.setFont(ModernTheme.FONT_SMALL_BOLD);
        clearLabel.setForeground(ModernTheme.getTextMuted());
        clearLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearLabel.setVisible(false);
        clearLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText("");
                textField.requestFocusInWindow();
            }
        });
        add(clearLabel, BorderLayout.EAST);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            private void trigger() {
                String text = textField.getText().trim();
                clearLabel.setVisible(!text.isEmpty());
                debounceTimer.restart();
            }
            @Override
            public void insertUpdate(DocumentEvent e) { trigger(); }
            @Override
            public void removeUpdate(DocumentEvent e) { trigger(); }
            @Override
            public void changedUpdate(DocumentEvent e) { trigger(); }
        });
    }

    public void setOnSearchCallback(Consumer<String> callback) {
        this.onSearchCallback = callback;
    }

    public String getText() {
        return textField.getText().trim();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    @Override
    public boolean requestFocusInWindow() {
        return textField.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(ModernTheme.getSurface());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);

        g2.setColor(textField.hasFocus() ? ModernTheme.ACCENT_PRIMARY : ModernTheme.getBorder());
        g2.setStroke(new BasicStroke(textField.hasFocus() ? 1.5f : 1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

        g2.dispose();
        super.paintComponent(g);
    }
}

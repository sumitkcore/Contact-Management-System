package com.cms.ui.components;

import com.cms.ui.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Animated Floating Toast Notification popup for user actions.
 */
public final class ToastNotification extends JPanel {
    private static final long serialVersionUID = 1L;

    public enum Type {
        SUCCESS("✓", ModernTheme.ACCENT_SUCCESS),
        INFO("ℹ", ModernTheme.ACCENT_PRIMARY),
        WARNING("⚠", ModernTheme.ACCENT_WARNING),
        ERROR("✕", ModernTheme.ACCENT_DANGER);

        private final String icon;
        private final Color color;

        Type(String icon, Color color) {
            this.icon = icon;
            this.color = color;
        }
    }

    private static final class ToastCardPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Type type;

        ToastCardPanel(Type type) {
            super(new FlowLayout(FlowLayout.LEFT, 10, 8));
            this.type = type;
            setOpaque(false);
            setBorder(new EmptyBorder(6, 12, 6, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            ModernTheme.enableAntiAliasing(g2);
            g2.setColor(ModernTheme.getSurface());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.setColor(type.color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void show(Component parent, String message, Type type) {
        if (parent == null || message == null) return;

        Window owner = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        if (owner == null) return;

        JWindow window = new JWindow(owner);
        window.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new ToastCardPanel(type);

        JLabel iconLbl = new JLabel(type.icon);
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        iconLbl.setForeground(type.color);
        panel.add(iconLbl);

        JLabel msgLbl = new JLabel(message);
        msgLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        msgLbl.setForeground(ModernTheme.getTextPrimary());
        panel.add(msgLbl);

        window.setContentPane(panel);
        window.pack();

        try {
            Point parentLocation = parent.getLocationOnScreen();
            int x = parentLocation.x + (parent.getWidth() - window.getWidth()) / 2;
            int y = parentLocation.y + parent.getHeight() - window.getHeight() - 40;
            window.setLocation(Math.max(10, x), Math.max(10, y));
            window.setAlwaysOnTop(true);
            window.setVisible(true);

            Timer timer = new Timer(2800, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    window.setVisible(false);
                    window.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } catch (IllegalComponentStateException ignored) {}
    }
}

package com.cms.ui.components;

import com.cms.ui.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern rounded button with hover effect, custom colors, and soft shadows.
 */
public final class ModernButton extends JButton {
    private static final long serialVersionUID = 1L;

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS,
        GHOST
    }

    private ButtonStyle style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 10;

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style != null ? style : ButtonStyle.PRIMARY;
        setFont(ModernTheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setStyle(ButtonStyle style) {
        this.style = style != null ? style : ButtonStyle.PRIMARY;
        repaint();
    }

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    public Insets getInsets() {
        return new Insets(8, 16, 8, 16);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        Color bgColor;
        Color textColor;
        Color borderColor = null;

        switch (style) {
            case PRIMARY:
                bgColor = isPressed ? ModernTheme.ACCENT_PRIMARY.darker() :
                        isHovered ? ModernTheme.ACCENT_PRIMARY_HOVER : ModernTheme.ACCENT_PRIMARY;
                textColor = Color.WHITE;
                break;
            case DANGER:
                bgColor = isPressed ? ModernTheme.ACCENT_DANGER.darker() :
                        isHovered ? new Color(220, 38, 38) : ModernTheme.ACCENT_DANGER;
                textColor = Color.WHITE;
                break;
            case SUCCESS:
                bgColor = isPressed ? ModernTheme.ACCENT_SUCCESS.darker() :
                        isHovered ? new Color(5, 150, 105) : ModernTheme.ACCENT_SUCCESS;
                textColor = Color.WHITE;
                break;
            case SECONDARY:
                bgColor = isPressed ? ModernTheme.getSurfaceHover().darker() :
                        isHovered ? ModernTheme.getSurfaceHover() : ModernTheme.getSurface();
                textColor = ModernTheme.getTextPrimary();
                borderColor = ModernTheme.getBorder();
                break;
            case GHOST:
            default:
                bgColor = isHovered ? ModernTheme.getSurfaceHover() : new Color(0, 0, 0, 0);
                textColor = ModernTheme.getTextPrimary();
                break;
        }

        // Fill background
        if (bgColor.getAlpha() > 0) {
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // Draw border if secondary
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        // Draw text and icon
        FontMetrics fm = g2.getFontMetrics(getFont());
        g2.setFont(getFont());
        g2.setColor(textColor);
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }
}

package com.cms.ui.components;

import com.cms.ui.ModernTheme;
import com.cms.util.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Circular Avatar Component with smooth anti-aliased initials and subtle gradient.
 */
public final class AvatarPanel extends JComponent {
    private static final long serialVersionUID = 1L;

    private String initials;
    private Color avatarColor;
    private final boolean isLarge;

    public AvatarPanel(String initials, String colorHex, int diameter) {
        this.initials = (initials != null && !initials.isEmpty()) ? initials : "?";
        this.avatarColor = (colorHex != null && !colorHex.isEmpty()) ? ColorUtils.hexToColor(colorHex) : ColorUtils.getColorForName(initials);
        this.isLarge = diameter > 45;
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    public void update(String initials, String colorHex) {
        this.initials = (initials != null && !initials.isEmpty()) ? initials : "?";
        this.avatarColor = (colorHex != null && !colorHex.isEmpty()) ? ColorUtils.hexToColor(colorHex) : ColorUtils.getColorForName(initials);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int size = Math.min(getWidth(), getHeight()) - 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Draw soft outer shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new Ellipse2D.Float(x, y + 1, size, size));

        // Draw circle with subtle gradient
        Color lighter = new Color(
                Math.min(255, avatarColor.getRed() + 30),
                Math.min(255, avatarColor.getGreen() + 30),
                Math.min(255, avatarColor.getBlue() + 30)
        );
        GradientPaint gp = new GradientPaint(x, y, lighter, x + size, y + size, avatarColor);
        g2.setPaint(gp);
        g2.fill(new Ellipse2D.Float(x, y, size, size));

        // Draw outer translucent border
        g2.setColor(new Color(255, 255, 255, 75));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new Ellipse2D.Float(x, y, size, size));

        // Draw Initials
        g2.setColor(Color.WHITE);
        g2.setFont(isLarge ? ModernTheme.FONT_AVATAR_LARGE : ModernTheme.FONT_AVATAR);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (size - fm.stringWidth(initials)) / 2;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initials, textX, textY);

        g2.dispose();
    }
}

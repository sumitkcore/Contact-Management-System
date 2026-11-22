package com.cms.ui.components;

import com.cms.model.ContactType;
import com.cms.ui.ModernTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Rounded pill badge component displaying contact category.
 */
public final class CategoryBadge extends JComponent {
    private static final long serialVersionUID = 1L;

    private final ContactType contactType;

    public CategoryBadge(ContactType type) {
        this.contactType = type != null ? type : ContactType.PERSONAL;
        setOpaque(false);
        setFont(ModernTheme.FONT_SMALL_BOLD);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        String text = contactType.getIcon() + " " + contactType.getDisplayName();
        int w = fm.stringWidth(text) + 16;
        int h = fm.getHeight() + 8;
        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        ModernTheme.enableAntiAliasing(g2);

        int w = getWidth();
        int h = getHeight();

        Color baseColor = Color.decode(contactType.getColorHex());
        Color bgTint = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 35);

        // Draw pill background
        g2.setColor(bgTint);
        g2.fillRoundRect(0, 1, w, h - 2, h - 2, h - 2);

        // Draw subtle pill border
        g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 1, w - 1, h - 3, h - 2, h - 2);

        // Draw label text
        g2.setColor(baseColor);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = contactType.getIcon() + " " + contactType.getDisplayName();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}

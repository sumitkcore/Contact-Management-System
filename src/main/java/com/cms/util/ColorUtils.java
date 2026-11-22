package com.cms.util;

import java.awt.Color;

/**
 * Utility for generating attractive and distinct pastel avatar colors based on contact name hashes.
 */
public class ColorUtils {

    private static final Color[] PALETTE = {
            new Color(59, 130, 246),  // Blue
            new Color(139, 92, 246),  // Purple
            new Color(236, 72, 153),  // Pink
            new Color(16, 185, 129),  // Emerald Green
            new Color(245, 158, 11),  // Amber / Orange
            new Color(14, 165, 233),  // Sky Blue
            new Color(168, 85, 247),  // Violet
            new Color(239, 68, 68),   // Red
            new Color(20, 184, 166),  // Teal
            new Color(99, 102, 241),  // Indigo
            new Color(249, 115, 22),  // Deep Orange
            new Color(132, 204, 22)   // Lime Green
    };

    public static Color getColorForName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return PALETTE[0];
        }
        int hash = Math.abs(name.trim().toLowerCase().hashCode());
        return PALETTE[hash % PALETTE.length];
    }

    public static String getHexForName(String name) {
        Color c = getColorForName(name);
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Color hexToColor(String hex) {
        if (hex == null || !hex.startsWith("#") || hex.length() != 7) {
            return PALETTE[0];
        }
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return PALETTE[0];
        }
    }
}

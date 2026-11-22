package com.cms.ui;

import java.awt.*;

/**
 * Modern Design System and Color Palette for Contact Management System.
 * Supports dynamic Dark Mode and Light Mode switching with high-DPI antialiased rendering.
 */
public final class ModernTheme {

    public enum ThemeMode {
        DARK("Dark Slate (Modern)"),
        LIGHT("Clean Minimal Light");

        private final String label;
        ThemeMode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private static ThemeMode currentMode = ThemeMode.DARK;

    // Static typography
    public static final Font FONT_APP_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONT_AVATAR = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_AVATAR_LARGE = new Font("Segoe UI", Font.BOLD, 26);

    // Accent Colors
    public static final Color ACCENT_PRIMARY = new Color(99, 102, 241);     // Indigo #6366F1
    public static final Color ACCENT_PRIMARY_HOVER = new Color(79, 70, 229); // #4F46E5
    public static final Color ACCENT_SUCCESS = new Color(16, 185, 129);      // Emerald #10B981
    public static final Color ACCENT_WARNING = new Color(245, 158, 11);      // Amber #F59E0B
    public static final Color ACCENT_DANGER = new Color(239, 68, 68);        // Rose #EF4444
    public static final Color ACCENT_FAVORITE = new Color(251, 191, 36);     // Gold #FBBF24
    public static final Color ACCENT_INFO = new Color(14, 165, 233);         // Sky Blue #0EA5E9

    private ModernTheme() {}

    public static ThemeMode getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(ThemeMode mode) {
        currentMode = mode != null ? mode : ThemeMode.DARK;
    }

    public static void toggleMode() {
        currentMode = (currentMode == ThemeMode.DARK) ? ThemeMode.LIGHT : ThemeMode.DARK;
    }

    // Dynamic Palette based on Active Theme
    public static Color getBackground() {
        return currentMode == ThemeMode.DARK ? new Color(11, 15, 25) : new Color(248, 250, 252);
    }

    public static Color getSidebarBackground() {
        return currentMode == ThemeMode.DARK ? new Color(17, 24, 39) : new Color(255, 255, 255);
    }

    public static Color getSurface() {
        return currentMode == ThemeMode.DARK ? new Color(26, 36, 56) : new Color(255, 255, 255);
    }

    public static Color getSurfaceHover() {
        return currentMode == ThemeMode.DARK ? new Color(38, 51, 77) : new Color(241, 245, 249);
    }

    public static Color getSurfaceVariant() {
        return currentMode == ThemeMode.DARK ? new Color(19, 27, 43) : new Color(241, 245, 249);
    }

    public static Color getBorder() {
        return currentMode == ThemeMode.DARK ? new Color(45, 59, 85) : new Color(226, 232, 240);
    }

    public static Color getTextPrimary() {
        return currentMode == ThemeMode.DARK ? new Color(248, 250, 252) : new Color(15, 23, 42);
    }

    public static Color getTextSecondary() {
        return currentMode == ThemeMode.DARK ? new Color(156, 163, 175) : new Color(71, 85, 105);
    }

    public static Color getTextMuted() {
        return currentMode == ThemeMode.DARK ? new Color(107, 114, 128) : new Color(148, 163, 184);
    }

    public static Color getSelectionBackground() {
        return currentMode == ThemeMode.DARK ? new Color(49, 46, 129) : new Color(224, 231, 255);
    }

    public static Color getSelectionForeground() {
        return currentMode == ThemeMode.DARK ? new Color(224, 231, 255) : new Color(49, 46, 129);
    }

    /**
     * Enables anti-aliasing and quality rendering hints on Graphics2D instances.
     */
    public static void enableAntiAliasing(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
    }
}

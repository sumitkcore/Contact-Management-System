package com.cms;

import com.cms.ui.MainFrame;

import javax.swing.*;

/**
 * Main Application Entry Point.
 * 
 * Group 17 | Object Oriented Programming in Java (CSE2003) Mini Project
 * 
 * Initiates the modern Swing UI on the Event Dispatch Thread (EDT) with system anti-aliasing
 * and high DPI display support.
 */
public class Main {

    public static void main(String[] args) {
        // High DPI and text anti-aliasing flags for crisp rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel as baseline
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Default to standard Swing L&F
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

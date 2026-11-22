package com.cms.ui.dialogs;

import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * About & Academic Documentation Modal.
 * Showcases Project Group details, CSE2003 OOP Mini Project requirements,
 * Course Syllabus mapping, and Runtime Environment Diagnostics.
 */
public final class AboutDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public AboutDialog(Window owner) {
        super(owner, "About Contact Management System", ModalityType.APPLICATION_MODAL);

        setSize(680, 640);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ModernTheme.getSurface());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("📱 Contact Management System");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.ACCENT_PRIMARY);
        titlePanel.add(title);

        JLabel sub = new JLabel("Group 17 | Object Oriented Programming in Java (CSE2003) Mini Project");
        sub.setFont(ModernTheme.FONT_BODY_BOLD);
        sub.setForeground(ModernTheme.getTextPrimary());
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));
        titlePanel.add(sub);

        JLabel semLbl = new JLabel("B.Tech Computer Science & Engineering (Year 1)");
        semLbl.setFont(ModernTheme.FONT_SMALL);
        semLbl.setForeground(ModernTheme.getTextSecondary());
        titlePanel.add(semLbl);

        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Body Panel
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 22, 16, 22));

        // Academic Scope Card
        JPanel metaCard = createCard("🎓 Project & Course Specification");
        metaCard.add(createBadgeRow("Academic Course", "CSE2003 — Object Oriented Programming with Java"));
        metaCard.add(Box.createVerticalStrut(4));
        metaCard.add(createBadgeRow("Project Scope", "Full-Featured Enterprise Desktop CMS Application"));
        metaCard.add(Box.createVerticalStrut(4));
        metaCard.add(createBadgeRow("Architecture", "Clean N-Tier: Model → DAO → Service → Presentation/UI"));
        metaCard.add(Box.createVerticalStrut(4));
        metaCard.add(createBadgeRow("Project Group", "Group 17"));
        body.add(metaCard);
        body.add(Box.createVerticalStrut(12));

        // Syllabus OOP Mapping Card
        JPanel oopCard = createCard("🧩 OOP Concepts & Syllabus Compliance");
        String[] concepts = {
                "✓ Encapsulation: Private members, getters/setters, defensive clones, data validation",
                "✓ Inheritance: Contact abstract class extended by Personal, Business & Emergency contacts",
                "✓ Polymorphism: Dynamic method dispatch in getSummary(), toVCard(), validate()",
                "✓ Abstraction & Interfaces: GenericDAO<T,ID>, Validatable, ContactSortingStrategy",
                "✓ Custom Exceptions: Hierarchy: InvalidContactData, DuplicateContact, ContactNotFound",
                "✓ Java Collections: ConcurrentHashMap, ArrayList, TreeSet with custom Comparators",
                "✓ Multithreading: Background daemon AutoSaveManager (45s), SwingWorker async I/O",
                "✓ File I/O & Serialization: RFC-4180 CSV, vCard 3.0 (.vcf), JSON & Object Serialization",
                "✓ Database & JDBC: MySQL 8.0 PreparedStatements, Transactions & Connection Pooling",
                "✓ 6 Design Patterns: Singleton, DAO, Factory, Builder, Observer & Strategy"
        };
        for (String c : concepts) {
            JLabel cLbl = new JLabel(c);
            cLbl.setFont(ModernTheme.FONT_SMALL);
            cLbl.setForeground(ModernTheme.getTextSecondary());
            cLbl.setBorder(new EmptyBorder(2, 4, 2, 4));
            oopCard.add(cLbl);
        }
        body.add(oopCard);
        body.add(Box.createVerticalStrut(12));

        // System Diagnostics Card
        JPanel envCard = createCard("💻 Runtime System Diagnostics");
        envCard.add(createBadgeRow("Java Version", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")"));
        envCard.add(Box.createVerticalStrut(4));
        envCard.add(createBadgeRow("Operating System", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")"));
        envCard.add(Box.createVerticalStrut(4));
        long memoryMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        envCard.add(createBadgeRow("Heap Memory Usage", memoryMb + " MB / " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB"));
        body.add(envCard);

        JScrollPane scrollPane = new JScrollPane(body);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));

        ModernButton closeBtn = new ModernButton("Close", ModernButton.ButtonStyle.PRIMARY);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(ModernTheme.getSurface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ModernTheme.FONT_SUBHEADER);
        titleLbl.setForeground(ModernTheme.ACCENT_PRIMARY);
        titleLbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(titleLbl);
        return card;
    }

    private JPanel createBadgeRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label + ":");
        l.setFont(ModernTheme.FONT_BODY_BOLD);
        l.setForeground(ModernTheme.getTextSecondary());
        JLabel v = new JLabel(value);
        v.setFont(ModernTheme.FONT_BODY);
        v.setForeground(ModernTheme.getTextPrimary());
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}

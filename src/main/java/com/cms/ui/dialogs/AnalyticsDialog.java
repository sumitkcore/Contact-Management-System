package com.cms.ui.dialogs;

import com.cms.model.ContactType;
import com.cms.service.ContactService;
import com.cms.ui.ModernTheme;
import com.cms.ui.components.ModernButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Map;

/**
 * Analytics Dashboard with Custom Java2D Charts.
 * Renders a dynamic Donut Chart for Category Distribution and Metrics Cards.
 */
public final class AnalyticsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;

    public AnalyticsDialog(Window owner, ContactService service) {
        super(owner, "System Analytics & Visual Metrics", ModalityType.APPLICATION_MODAL);
        this.contactService = service;

        initDialog();
    }

    private void initDialog() {
        setSize(700, 580);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(ModernTheme.getBackground());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ModernTheme.getSurface());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel title = new JLabel("📊 Contact Analytics & Intelligence Dashboard");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.getTextPrimary());
        header.add(title, BorderLayout.NORTH);

        JLabel sub = new JLabel("Category breakdown, favorite engagement ratio, and organizational metrics");
        sub.setFont(ModernTheme.FONT_SMALL);
        sub.setForeground(ModernTheme.getTextSecondary());
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Left: Custom Java2D Pie Chart
        JPanel chartCard = new JPanel(new BorderLayout());
        chartCard.setOpaque(true);
        chartCard.setBackground(ModernTheme.getSurface());
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel chartTitle = new JLabel("Distribution by Contact Type");
        chartTitle.setFont(ModernTheme.FONT_SUBHEADER);
        chartTitle.setForeground(ModernTheme.getTextPrimary());
        chartCard.add(chartTitle, BorderLayout.NORTH);

        Map<ContactType, Integer> dist = contactService.getCategoryDistribution();
        chartCard.add(new CategoryPieChartPanel(dist), BorderLayout.CENTER);
        body.add(chartCard);

        // Right: Key Metrics & Tag Cloud
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        long total = contactService.getTotalActiveCount();
        long favs = contactService.getTotalFavoriteCount();
        int pers = dist.getOrDefault(ContactType.PERSONAL, 0);
        int biz = dist.getOrDefault(ContactType.BUSINESS, 0);
        int emg = dist.getOrDefault(ContactType.EMERGENCY, 0);

        rightCol.add(createMetricRow("Total Active Contacts", String.valueOf(total), ModernTheme.ACCENT_PRIMARY));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createMetricRow("Starred Favorites ★", favs + " (" + (total > 0 ? (favs * 100 / total) : 0) + "%)", ModernTheme.ACCENT_FAVORITE));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createMetricRow("Personal Connections", String.valueOf(pers), Color.decode(ContactType.PERSONAL.getColorHex())));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createMetricRow("Business / Work Leads", String.valueOf(biz), Color.decode(ContactType.BUSINESS.getColorHex())));
        rightCol.add(Box.createVerticalStrut(10));
        rightCol.add(createMetricRow("Emergency Responders", String.valueOf(emg), Color.decode(ContactType.EMERGENCY.getColorHex())));
        rightCol.add(Box.createVerticalStrut(14));

        // Tags Section
        JPanel tagsCard = new JPanel(new BorderLayout());
        tagsCard.setOpaque(true);
        tagsCard.setBackground(ModernTheme.getSurface());
        tagsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel tagsTitle = new JLabel("Active Tag Taxonomy");
        tagsTitle.setFont(ModernTheme.FONT_SMALL_BOLD);
        tagsTitle.setForeground(ModernTheme.getTextSecondary());
        tagsCard.add(tagsTitle, BorderLayout.NORTH);

        JPanel tagFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        tagFlow.setOpaque(false);
        List<String> tags = contactService.getAllTags();
        for (String t : tags) {
            JLabel lbl = new JLabel("#" + t);
            lbl.setFont(ModernTheme.FONT_SMALL);
            lbl.setOpaque(true);
            lbl.setBackground(ModernTheme.getSurfaceVariant());
            lbl.setForeground(ModernTheme.ACCENT_PRIMARY);
            lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
            tagFlow.add(lbl);
        }
        if (tags.isEmpty()) {
            JLabel none = new JLabel("No tags defined yet.");
            none.setFont(ModernTheme.FONT_SMALL);
            none.setForeground(ModernTheme.getTextMuted());
            tagFlow.add(none);
        }
        tagsCard.add(tagFlow, BorderLayout.CENTER);
        rightCol.add(tagsCard);

        body.add(rightCol);
        add(body, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setOpaque(true);
        footer.setBackground(ModernTheme.getSurface());
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.getBorder()));
        ModernButton closeBtn = new ModernButton("Close Dashboard", ModernButton.ButtonStyle.PRIMARY);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createMetricRow(String title, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(true);
        p.setBackground(ModernTheme.getSurface());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel t = new JLabel(title);
        t.setFont(ModernTheme.FONT_BODY_BOLD);
        t.setForeground(ModernTheme.getTextPrimary());
        p.add(t, BorderLayout.WEST);

        JLabel v = new JLabel(value);
        v.setFont(ModernTheme.FONT_BODY_BOLD);
        v.setForeground(accent);
        p.add(v, BorderLayout.EAST);

        return p;
    }

    private static class CategoryPieChartPanel extends JComponent {
        private static final long serialVersionUID = 1L;
        private final transient Map<ContactType, Integer> data;

        public CategoryPieChartPanel(Map<ContactType, Integer> data) {
            this.data = data;
            setPreferredSize(new Dimension(240, 240));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            ModernTheme.enableAntiAliasing(g2);

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            int w = getWidth();
            int h = getHeight() - 40;
            int size = Math.min(w, h) - 20;
            int x = (w - size) / 2;
            int y = 10;

            if (total == 0) {
                g2.setColor(ModernTheme.getTextMuted());
                g2.setFont(ModernTheme.FONT_BODY);
                g2.drawString("No contacts recorded yet.", x + 20, y + size / 2);
                g2.dispose();
                return;
            }

            double curAngle = 90.0;
            for (Map.Entry<ContactType, Integer> entry : data.entrySet()) {
                double arcAngle = (entry.getValue() / (double) total) * 360.0;
                Color col = Color.decode(entry.getKey().getColorHex());

                g2.setColor(col);
                g2.fill(new Arc2D.Double(x, y, size, size, curAngle, arcAngle, Arc2D.PIE));

                g2.setColor(ModernTheme.getSurface());
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(new Arc2D.Double(x, y, size, size, curAngle, arcAngle, Arc2D.PIE));

                curAngle += arcAngle;
            }

            // Donut hole
            int holeSize = size / 2;
            int hx = x + (size - holeSize) / 2;
            int hy = y + (size - holeSize) / 2;
            g2.setColor(ModernTheme.getSurface());
            g2.fillOval(hx, hy, holeSize, holeSize);

            // Donut center text
            g2.setColor(ModernTheme.getTextPrimary());
            g2.setFont(ModernTheme.FONT_HEADER);
            FontMetrics fm = g2.getFontMetrics();
            String totalStr = String.valueOf(total);
            g2.drawString(totalStr, hx + (holeSize - fm.stringWidth(totalStr)) / 2, hy + (holeSize / 2) + 2);

            g2.setFont(ModernTheme.FONT_SMALL);
            String lbl = "Contacts";
            FontMetrics fms = g2.getFontMetrics();
            g2.drawString(lbl, hx + (holeSize - fms.stringWidth(lbl)) / 2, hy + (holeSize / 2) + 16);

            // Legend at bottom
            int legY = getHeight() - 25;
            int legX = 10;
            for (Map.Entry<ContactType, Integer> entry : data.entrySet()) {
                Color col = Color.decode(entry.getKey().getColorHex());
                g2.setColor(col);
                g2.fillRect(legX, legY, 10, 10);
                g2.setColor(ModernTheme.getTextSecondary());
                g2.setFont(ModernTheme.FONT_SMALL);
                String lText = entry.getKey().getDisplayName() + " (" + entry.getValue() + ")";
                g2.drawString(lText, legX + 14, legY + 9);
                legX += g2.getFontMetrics().stringWidth(lText) + 24;
            }

            g2.dispose();
        }
    }
}


package com.cms.ui;

import com.cms.dao.DAOFactory;
import com.cms.model.Contact;
import com.cms.model.ContactType;
import com.cms.pattern.observer.ContactChangeEvent;
import com.cms.pattern.observer.ContactChangeListener;
import com.cms.pattern.observer.ContactEventManager;
import com.cms.pattern.strategy.*;
import com.cms.service.AutoSaveManager;
import com.cms.service.ContactService;
import com.cms.service.ContactServiceImpl;
import com.cms.ui.components.*;
import com.cms.ui.dialogs.*;
import com.cms.ui.view.ContactDetailPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MainFrame extends JFrame implements ContactChangeListener {

    private static final long serialVersionUID = 1L;

    private final transient ContactService contactService;
    private final transient AutoSaveManager autoSaveManager;

    private enum ViewMode {
        CARDS, TABLE
    }

    private ViewMode currentViewMode = ViewMode.CARDS;

    private JPanel masterContainer;
    private CardLayout masterCardLayout;

    private ModernSearchField searchField;
    private JComboBox<ContactSortingStrategy> sortCombo;
    private JLabel resultsCountLbl;
    private JLabel storageIndicatorPill;

    private DefaultListModel<Contact> contactListModel;
    private JList<Contact> contactList;

    private DefaultTableModel tableModel;
    private JTable contactTable;

    private transient List<Contact> displayedContacts = new ArrayList<>();

    private ContactDetailPanel detailPanel;

    private JLabel totalBadgeLbl;
    private JLabel favBadgeLbl;
    private JLabel persBadgeLbl;
    private JLabel bizBadgeLbl;
    private JLabel emgBadgeLbl;
    private JLabel trashBadgeLbl;

    private JPanel tagPillsContainer;

    private StatCard totalStatCard;
    private StatCard favStatCard;

    private JLabel statusStorageLbl;
    private JLabel statusAutoSaveLbl;
    private JLabel statusCountLbl;

    private String currentFilterMode = "ALL";

    private transient ContactSortingStrategy currentSortingStrategy =
            new NameAscendingStrategy();

    // Prevent duplicate keyboard event dispatchers.
    private boolean keyboardShortcutsInstalled = false;

    // Preserve the search query when the interface is rebuilt.
    private String currentSearchQuery = "";

    public MainFrame() {

        super("Contact Management System — Group 17 | CSE2003 OOPJ Mini Project");

        this.contactService = new ContactServiceImpl();
        this.autoSaveManager = AutoSaveManager.getInstance();

        setSize(1280, 840);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initThemeAndUI();

        ContactEventManager.getInstance().addListener(this);

        autoSaveManager.start(contactService, 45);

        refreshContactList();
        updateSidebarBadges();
        updateStorageIndicator();
    }

    // ---------------------------------------------------------
    // MAIN UI INITIALIZATION
    // ---------------------------------------------------------

    private void initThemeAndUI() {

        getContentPane().removeAll();

        getContentPane().setBackground(ModernTheme.getBackground());

        setLayout(new BorderLayout());

        add(buildTopAppBar(), BorderLayout.NORTH);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setOpaque(false);

        workspace.add(buildSidebar(), BorderLayout.WEST);
        workspace.add(buildMasterDetailPane(), BorderLayout.CENTER);

        add(workspace, BorderLayout.CENTER);

        add(buildStatusBar(), BorderLayout.SOUTH);

        setupKeyboardShortcuts();

        revalidate();
        repaint();
    }

    // ---------------------------------------------------------
    // TOP APPLICATION BAR
    // ---------------------------------------------------------

    private JPanel buildTopAppBar() {

        JPanel bar = new JPanel(new BorderLayout(16, 0));

        bar.setOpaque(true);
        bar.setBackground(ModernTheme.getSurface());

        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JPanel brandPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 0)
        );

        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel("📱");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        brandPanel.add(logoIcon);

        JPanel titleBlock = new JPanel();

        titleBlock.setLayout(
                new BoxLayout(titleBlock, BoxLayout.Y_AXIS)
        );

        titleBlock.setOpaque(false);

        JLabel logoLbl = new JLabel("Contact Management System");

        logoLbl.setFont(ModernTheme.FONT_TITLE);
        logoLbl.setForeground(ModernTheme.getTextPrimary());

        titleBlock.add(logoLbl);

        JLabel subLbl = new JLabel(
                "Group 17 • CSE2003 Java OOP Mini Project"
        );

        subLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        subLbl.setForeground(ModernTheme.ACCENT_PRIMARY);

        titleBlock.add(subLbl);

        brandPanel.add(titleBlock);

        storageIndicatorPill = new JLabel("● Storage");

        storageIndicatorPill.setFont(ModernTheme.FONT_SMALL_BOLD);
        storageIndicatorPill.setOpaque(true);
        storageIndicatorPill.setBackground(ModernTheme.getSurfaceVariant());
        storageIndicatorPill.setForeground(ModernTheme.ACCENT_SUCCESS);
        storageIndicatorPill.setBorder(new EmptyBorder(4, 10, 4, 10));

        storageIndicatorPill.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        storageIndicatorPill.setToolTipText(
                "Click to configure Database & Storage settings"
        );

        storageIndicatorPill.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        openSettingsDialog();
                    }
                }
        );

        brandPanel.add(storageIndicatorPill);

        bar.add(brandPanel, BorderLayout.WEST);

        searchField = new ModernSearchField(
                "Search contacts by name, phone, email, tags (Ctrl+F)..."
        );

        searchField.setPreferredSize(new Dimension(380, 38));

        searchField.setOnSearchCallback(this::filterContacts);

        bar.add(searchField, BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        actions.setOpaque(false);

        ModernButton newBtn = new ModernButton(
                "+ Add Contact",
                ModernButton.ButtonStyle.PRIMARY
        );

        newBtn.setToolTipText("Create a new contact (Ctrl+N)");
        newBtn.addActionListener(e -> openNewContactDialog());

        actions.add(newBtn);

        ModernButton ioBtn = new ModernButton(
                "Import/Export ⇄",
                ModernButton.ButtonStyle.SECONDARY
        );

        ioBtn.setToolTipText(
                "Import or export contacts in CSV, vCard or JSON format"
        );

        ioBtn.addActionListener(e ->
                new ImportExportDialog(
                        this,
                        contactService,
                        this::refreshContactList
                ).setVisible(true)
        );

        actions.add(ioBtn);

        ModernButton analyticsBtn = new ModernButton(
                "Analytics 📊",
                ModernButton.ButtonStyle.SECONDARY
        );

        analyticsBtn.setToolTipText(
                "View contact demographics and breakdown chart"
        );

        analyticsBtn.addActionListener(e ->
                new AnalyticsDialog(this, contactService).setVisible(true)
        );

        actions.add(analyticsBtn);

        ModernButton themeBtn = new ModernButton(
                ModernTheme.getCurrentMode() == ModernTheme.ThemeMode.DARK
                        ? "☀️ Light"
                        : "🌙 Dark",
                ModernButton.ButtonStyle.SECONDARY
        );

        themeBtn.setToolTipText("Toggle Visual Theme (Ctrl+T)");

        themeBtn.addActionListener(e -> toggleTheme());

        actions.add(themeBtn);

        ModernButton settingsBtn = new ModernButton(
                "⚙",
                ModernButton.ButtonStyle.SECONDARY
        );

        settingsBtn.setToolTipText("Settings & Database Connection");
        settingsBtn.addActionListener(e -> openSettingsDialog());

        actions.add(settingsBtn);

        ModernButton aboutBtn = new ModernButton(
                "ℹ",
                ModernButton.ButtonStyle.SECONDARY
        );

        aboutBtn.setToolTipText("About Project & Course Syllabus");
        aboutBtn.addActionListener(e ->
                new AboutDialog(this).setVisible(true)
        );

        actions.add(aboutBtn);

        bar.add(actions, BorderLayout.EAST);

        return bar;
    }

    // ---------------------------------------------------------
    // SIDEBAR
    // ---------------------------------------------------------

    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setOpaque(true);
        sidebar.setBackground(ModernTheme.getSurface());

        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(16, 14, 16, 14)
        ));

        JLabel navTitle = new JLabel("DIRECTORY");

        navTitle.setFont(ModernTheme.FONT_SMALL_BOLD);
        navTitle.setForeground(ModernTheme.getTextMuted());

        sidebar.add(navTitle);
        sidebar.add(Box.createVerticalStrut(10));

        totalBadgeLbl = new JLabel("0");
        favBadgeLbl = new JLabel("0");
        persBadgeLbl = new JLabel("0");
        bizBadgeLbl = new JLabel("0");
        emgBadgeLbl = new JLabel("0");
        trashBadgeLbl = new JLabel("0");

        sidebar.add(createNavItem(
                "👥 All Contacts", "ALL", totalBadgeLbl));

        sidebar.add(Box.createVerticalStrut(4));

        sidebar.add(createNavItem(
                "★ Favorites", "FAVORITES", favBadgeLbl));

        sidebar.add(Box.createVerticalStrut(4));

        sidebar.add(createNavItem(
                "👤 Personal", "PERSONAL", persBadgeLbl));

        sidebar.add(Box.createVerticalStrut(4));

        sidebar.add(createNavItem(
                "💼 Business", "BUSINESS", bizBadgeLbl));

        sidebar.add(Box.createVerticalStrut(4));

        sidebar.add(createNavItem(
                "🚨 Emergency", "EMERGENCY", emgBadgeLbl));

        sidebar.add(Box.createVerticalStrut(4));

        JPanel trashRow = new JPanel(new BorderLayout(8, 0));

        trashRow.setOpaque(true);
        trashRow.setBackground(ModernTheme.getSurface());
        trashRow.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        trashRow.setBorder(new EmptyBorder(7, 10, 7, 10));

        JLabel trashLbl = new JLabel("🗑 Recycle Bin");

        trashLbl.setFont(ModernTheme.FONT_BODY_BOLD);
        trashLbl.setForeground(ModernTheme.ACCENT_DANGER);

        trashRow.add(trashLbl, BorderLayout.WEST);

        trashBadgeLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        trashBadgeLbl.setForeground(ModernTheme.ACCENT_DANGER);

        trashRow.add(trashBadgeLbl, BorderLayout.EAST);

        trashRow.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                        new RecycleBinDialog(
                                MainFrame.this,
                                contactService,
                                () -> {
                                    refreshContactList();
                                    updateSidebarBadges();
                                }
                        ).setVisible(true);
                    }
                }
        );

        sidebar.add(trashRow);

        sidebar.add(Box.createVerticalStrut(18));

        JPanel tagHeader = new JPanel(new BorderLayout());

        tagHeader.setOpaque(false);

        JLabel tagTitle = new JLabel("FILTER BY TAG");

        tagTitle.setFont(ModernTheme.FONT_SMALL_BOLD);
        tagTitle.setForeground(ModernTheme.getTextMuted());

        tagHeader.add(tagTitle, BorderLayout.WEST);

        if (currentFilterMode.startsWith("TAG:")) {

            JLabel clearTag = new JLabel("Clear ✕");

            clearTag.setFont(ModernTheme.FONT_SMALL);
            clearTag.setForeground(ModernTheme.ACCENT_PRIMARY);

            clearTag.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );

            clearTag.addMouseListener(
                    new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {

                            currentFilterMode = "ALL";
                            currentSearchQuery = "";

                            rebuildAndRefresh();
                        }
                    }
            );

            tagHeader.add(clearTag, BorderLayout.EAST);
        }

        sidebar.add(tagHeader);
        sidebar.add(Box.createVerticalStrut(8));

        tagPillsContainer = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 4, 4)
        );

        tagPillsContainer.setOpaque(false);

        sidebar.add(tagPillsContainer);
        sidebar.add(Box.createVerticalGlue());

        totalStatCard = new StatCard(
                "Active Directory",
                "0",
                "👥",
                ModernTheme.ACCENT_PRIMARY
        );

        favStatCard = new StatCard(
                "Top Favorites",
                "0",
                "★",
                ModernTheme.ACCENT_FAVORITE
        );

        sidebar.add(totalStatCard);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(favStatCard);

        return sidebar;
    }

    // ---------------------------------------------------------
    // NAVIGATION ITEM
    // ---------------------------------------------------------

    private JPanel createNavItem(
            String label,
            String mode,
            JLabel badgeLbl
    ) {

        JPanel item = new JPanel(new BorderLayout(8, 0));

        item.setOpaque(true);

        boolean isActive = currentFilterMode.equals(mode);

        item.setBackground(
                isActive
                        ? ModernTheme.getSurfaceHover()
                        : ModernTheme.getSurface()
        );

        item.setBorder(BorderFactory.createCompoundBorder(
                isActive
                        ? BorderFactory.createMatteBorder(
                                0, 3, 0, 0, ModernTheme.ACCENT_PRIMARY)
                        : BorderFactory.createEmptyBorder(0, 3, 0, 0),
                new EmptyBorder(7, 10, 7, 10)
        ));

        item.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        JLabel text = new JLabel(label);

        text.setFont(ModernTheme.FONT_BODY_BOLD);

        text.setForeground(
                isActive
                        ? ModernTheme.ACCENT_PRIMARY
                        : ModernTheme.getTextPrimary()
        );

        item.add(text, BorderLayout.WEST);

        badgeLbl.setFont(ModernTheme.FONT_SMALL_BOLD);

        badgeLbl.setForeground(
                isActive
                        ? ModernTheme.ACCENT_PRIMARY
                        : ModernTheme.getTextMuted()
        );

        item.add(badgeLbl, BorderLayout.EAST);

        item.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                        if (!currentFilterMode.equals(mode)) {

                            currentFilterMode = mode;
                            currentSearchQuery = "";

                            // FIX: Rebuild first, then load the contacts.
                            rebuildAndRefresh();
                        }
                    }
                }
        );

        return item;
    }

    // ---------------------------------------------------------
    // MASTER DETAIL PANE
    // ---------------------------------------------------------

    private JSplitPane buildMasterDetailPane() {

        JPanel masterPanel = new JPanel(new BorderLayout());

        masterPanel.setOpaque(false);

        JPanel listToolbar = new JPanel(new BorderLayout(10, 0));

        listToolbar.setOpaque(true);
        listToolbar.setBackground(ModernTheme.getSurface());

        listToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, ModernTheme.getBorder()),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JPanel viewTogglePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 4, 0)
        );

        viewTogglePanel.setOpaque(false);

        ModernButton cardsViewBtn = new ModernButton(
                "≡ Cards",
                currentViewMode == ViewMode.CARDS
                        ? ModernButton.ButtonStyle.PRIMARY
                        : ModernButton.ButtonStyle.SECONDARY
        );

        ModernButton tableViewBtn = new ModernButton(
                "☷ Table",
                currentViewMode == ViewMode.TABLE
                        ? ModernButton.ButtonStyle.PRIMARY
                        : ModernButton.ButtonStyle.SECONDARY
        );

        cardsViewBtn.addActionListener(e -> {

            currentViewMode = ViewMode.CARDS;

            cardsViewBtn.setStyle(ModernButton.ButtonStyle.PRIMARY);
            tableViewBtn.setStyle(ModernButton.ButtonStyle.SECONDARY);

            masterCardLayout.show(masterContainer, "CARDS");

            syncSelectionToCards();
        });

        tableViewBtn.addActionListener(e -> {

            currentViewMode = ViewMode.TABLE;

            tableViewBtn.setStyle(ModernButton.ButtonStyle.PRIMARY);
            cardsViewBtn.setStyle(ModernButton.ButtonStyle.SECONDARY);

            masterCardLayout.show(masterContainer, "TABLE");

            syncSelectionToTable();
        });

        viewTogglePanel.add(cardsViewBtn);
        viewTogglePanel.add(tableViewBtn);

        listToolbar.add(viewTogglePanel, BorderLayout.WEST);

        JPanel sortPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 6, 0)
        );

        sortPanel.setOpaque(false);

        JLabel sortLbl = new JLabel("Sort:");

        sortLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        sortLbl.setForeground(ModernTheme.getTextSecondary());

        sortPanel.add(sortLbl);

        ContactSortingStrategy[] strategies = {
                new NameAscendingStrategy(),
                new NameDescendingStrategy(),
                new DateAddedNewestStrategy(),
                new FavoritesFirstStrategy(),
                new CategoryStrategy()
        };

        sortCombo = new JComboBox<>(strategies);

        sortCombo.setFont(ModernTheme.FONT_SMALL);
        sortCombo.setSelectedItem(currentSortingStrategy);

        sortCombo.addActionListener(e -> {

            ContactSortingStrategy selected =
                    (ContactSortingStrategy) sortCombo.getSelectedItem();

            if (selected != null) {

                currentSortingStrategy = selected;

                refreshContactList();
            }
        });

        sortPanel.add(sortCombo);

        listToolbar.add(sortPanel, BorderLayout.CENTER);

        resultsCountLbl = new JLabel("0 contacts");

        resultsCountLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        resultsCountLbl.setForeground(ModernTheme.getTextSecondary());

        listToolbar.add(resultsCountLbl, BorderLayout.EAST);

        masterPanel.add(listToolbar, BorderLayout.NORTH);

        masterCardLayout = new CardLayout();

        masterContainer = new JPanel(masterCardLayout);

        masterContainer.setOpaque(false);

        // CARD VIEW

        contactListModel = new DefaultListModel<>();

        contactList = new JList<>(contactListModel);

        contactList.setCellRenderer(new ContactListRenderer());

        contactList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        contactList.setBackground(ModernTheme.getBackground());

        contactList.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && detailPanel != null) {

                Contact selected = contactList.getSelectedValue();

                detailPanel.displayContact(selected);
            }
        });

        JScrollPane listScroll = new JScrollPane(contactList);

        listScroll.setBorder(null);

        listScroll.getVerticalScrollBar().setUnitIncrement(14);

        masterContainer.add(listScroll, "CARDS");

        // TABLE VIEW

        String[] tableCols = {
                "★",
                "Name",
                "Category",
                "Primary Phone",
                "Email",
                "Company / Affiliation",
                "Tags"
        };

        tableModel = new DefaultTableModel(tableCols, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        contactTable = new JTable(tableModel);

        contactTable.setFont(ModernTheme.FONT_BODY);
        contactTable.setRowHeight(32);
        contactTable.setBackground(ModernTheme.getSurface());
        contactTable.setForeground(ModernTheme.getTextPrimary());

        contactTable.setSelectionBackground(
                ModernTheme.getSelectionBackground()
        );

        contactTable.setSelectionForeground(
                ModernTheme.getSelectionForeground()
        );

        contactTable.setGridColor(ModernTheme.getBorder());

        contactTable.setShowVerticalLines(false);
        contactTable.setShowHorizontalLines(true);

        contactTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JTableHeader th = contactTable.getTableHeader();

        th.setFont(ModernTheme.FONT_SMALL_BOLD);
        th.setBackground(ModernTheme.getSurfaceVariant());
        th.setForeground(ModernTheme.getTextSecondary());
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 30));

        DefaultTableCellRenderer centerRender =
                new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        contactTable.getColumnModel().getColumn(0)
                .setPreferredWidth(32);

        contactTable.getColumnModel().getColumn(0)
                .setCellRenderer(centerRender);

        contactTable.getColumnModel().getColumn(1)
                .setPreferredWidth(140);

        contactTable.getColumnModel().getColumn(2)
                .setPreferredWidth(90);

        contactTable.getColumnModel().getColumn(2)
                .setCellRenderer(centerRender);

        contactTable.getColumnModel().getColumn(3)
                .setPreferredWidth(120);

        contactTable.getColumnModel().getColumn(4)
                .setPreferredWidth(160);

        contactTable.getColumnModel().getColumn(5)
                .setPreferredWidth(130);

        contactTable.getColumnModel().getColumn(6)
                .setPreferredWidth(100);

        contactTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = contactTable.getSelectedRow();

                if (row >= 0 && row < displayedContacts.size()) {

                    Contact selected = displayedContacts.get(row);

                    if (detailPanel != null) {
                        detailPanel.displayContact(selected);
                    }

                    if (contactList.getSelectedValue() != selected) {
                        contactList.setSelectedValue(selected, false);
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(contactTable);

        tableScroll.setBorder(null);

        tableScroll.getVerticalScrollBar().setUnitIncrement(14);

        masterContainer.add(tableScroll, "TABLE");

        masterPanel.add(masterContainer, BorderLayout.CENTER);

        // DETAIL PANEL

        detailPanel = new ContactDetailPanel(
                contactService,
                (v) -> {
                    refreshContactList();
                    updateSidebarBadges();
                }
        );

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                masterPanel,
                detailPanel
        );

        splitPane.setDividerLocation(420);
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        return splitPane;
    }

    // ---------------------------------------------------------
    // STATUS BAR
    // ---------------------------------------------------------

    private JPanel buildStatusBar() {

        JPanel bar = new JPanel(new BorderLayout(16, 0));

        bar.setOpaque(true);
        bar.setBackground(ModernTheme.getSurface());

        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0, ModernTheme.getBorder()),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        statusStorageLbl = new JLabel(
                "Engine: " + DAOFactory.getCurrentStorageType().getDescription()
        );

        statusStorageLbl.setFont(ModernTheme.FONT_SMALL);
        statusStorageLbl.setForeground(ModernTheme.getTextSecondary());

        bar.add(statusStorageLbl, BorderLayout.WEST);

        JLabel shortcutsLbl = new JLabel(
                "Shortcuts: Ctrl+N New  •  Ctrl+F Search  •  Ctrl+S Quick Save  •  Ctrl+T Theme"
        );

        shortcutsLbl.setFont(ModernTheme.FONT_SMALL);
        shortcutsLbl.setForeground(ModernTheme.getTextMuted());
        shortcutsLbl.setHorizontalAlignment(SwingConstants.CENTER);

        bar.add(shortcutsLbl, BorderLayout.CENTER);

        JPanel rightStatus = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 14, 0)
        );

        rightStatus.setOpaque(false);

        statusAutoSaveLbl = new JLabel("● Daemon: Active (45s)");

        statusAutoSaveLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        statusAutoSaveLbl.setForeground(ModernTheme.ACCENT_SUCCESS);

        rightStatus.add(statusAutoSaveLbl);

        statusCountLbl = new JLabel("Total: 0");

        statusCountLbl.setFont(ModernTheme.FONT_SMALL_BOLD);
        statusCountLbl.setForeground(ModernTheme.getTextPrimary());

        rightStatus.add(statusCountLbl);

        bar.add(rightStatus, BorderLayout.EAST);

        return bar;
    }

    // ---------------------------------------------------------
    // KEYBOARD SHORTCUTS
    // ---------------------------------------------------------

    private void setupKeyboardShortcuts() {

        // Register only once, not on every UI rebuild.
        if (keyboardShortcutsInstalled) {
            return;
        }

        keyboardShortcutsInstalled = true;

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {

                    if (e.getID() == KeyEvent.KEY_PRESSED) {

                        if (e.isControlDown()
                                && e.getKeyCode() == KeyEvent.VK_N) {

                            openNewContactDialog();
                            return true;
                        }

                        if (e.isControlDown()
                                && e.getKeyCode() == KeyEvent.VK_F) {

                            if (searchField != null) {
                                searchField.requestFocusInWindow();
                            }

                            return true;
                        }

                        if (e.isControlDown()
                                && e.getKeyCode() == KeyEvent.VK_S) {

                            contactService.flushStorage();

                            ToastNotification.show(
                                    getRootPane(),
                                    "Contacts safely saved to disk ✓",
                                    ToastNotification.Type.SUCCESS
                            );

                            return true;
                        }

                        if (e.isControlDown()
                                && e.getKeyCode() == KeyEvent.VK_T) {

                            toggleTheme();
                            return true;
                        }
                    }

                    return false;
                });
    }

    // ---------------------------------------------------------
    // THEME
    // ---------------------------------------------------------

    private void toggleTheme() {

        ModernTheme.toggleMode();

        rebuildAndRefresh();
    }

    // ---------------------------------------------------------
    // REBUILD AND REFRESH - IMPORTANT FIX
    // ---------------------------------------------------------

    private void rebuildAndRefresh() {

        // First create fresh UI components and models.
        initThemeAndUI();

        // Then populate the newly created models.
        refreshContactList();

        // Update the sidebar counters and tag buttons.
        updateSidebarBadges();

        updateStorageIndicator();

        revalidate();
        repaint();
    }

    // ---------------------------------------------------------
    // CONTACT DIALOGS
    // ---------------------------------------------------------

    private void openNewContactDialog() {

        ContactEditorDialog dialog =
                new ContactEditorDialog(this, null, contactService);

        dialog.setVisible(true);

        if (dialog.isSaved()) {

            currentFilterMode = "ALL";
            currentSearchQuery = "";

            rebuildAndRefresh();

            selectContactInView(dialog.getSavedContact());
        }
    }

    private void openSettingsDialog() {

        new SettingsDialog(
                this,
                contactService,
                () -> {
                    rebuildAndRefresh();
                }
        ).setVisible(true);
    }

    // ---------------------------------------------------------
    // STORAGE INDICATOR
    // ---------------------------------------------------------

    private void updateStorageIndicator() {

        DAOFactory.StorageType type =
                DAOFactory.getCurrentStorageType();

        if (storageIndicatorPill != null) {

            if (type == DAOFactory.StorageType.MYSQL_DATABASE) {

                storageIndicatorPill.setText("● MySQL 8.0 Engine");

                storageIndicatorPill.setForeground(
                        ModernTheme.ACCENT_PRIMARY
                );

            } else {

                storageIndicatorPill.setText("● Local Object Engine");

                storageIndicatorPill.setForeground(
                        ModernTheme.ACCENT_SUCCESS
                );
            }
        }

        if (statusStorageLbl != null) {

            statusStorageLbl.setText(
                    "Engine: " + type.getDescription()
            );
        }
    }

    // ---------------------------------------------------------
    // CONTACT FILTERING - FIXED
    // ---------------------------------------------------------

    private void refreshContactList() {

        List<Contact> list;

        switch (currentFilterMode) {

            case "FAVORITES":

                list = contactService.getFavoriteContacts(
                        currentSortingStrategy
                );

                break;

            case "PERSONAL":

                list = contactService.getContactsByType(
                        ContactType.PERSONAL,
                        currentSortingStrategy
                );

                break;

            case "BUSINESS":

                list = contactService.getContactsByType(
                        ContactType.BUSINESS,
                        currentSortingStrategy
                );

                break;

            case "EMERGENCY":

                list = contactService.getContactsByType(
                        ContactType.EMERGENCY,
                        currentSortingStrategy
                );

                break;

            default:

                if (currentFilterMode.startsWith("TAG:")) {

                    String tag = currentFilterMode.substring(4);

                    list = contactService.getContactsByTag(
                            tag,
                            currentSortingStrategy
                    );

                } else {

                    list = contactService.getAllContacts(
                            currentSortingStrategy
                    );
                }

                break;
        }

        if (list == null) {
            list = new ArrayList<>();
        }

        // Apply search within the currently selected category.
        if (currentSearchQuery != null
                && !currentSearchQuery.trim().isEmpty()) {

            String query = currentSearchQuery.trim().toLowerCase();

            list.removeIf(contact -> !matchesSearch(contact, query));
        }

        updateDisplayWithContacts(list);
    }

    // ---------------------------------------------------------
    // SEARCH
    // ---------------------------------------------------------

    private void filterContacts(String query) {

        currentSearchQuery = query == null ? "" : query.trim();

        refreshContactList();
    }

    private boolean matchesSearch(Contact contact, String query) {

        if (contact == null) {
            return false;
        }

        if (contains(contact.getFullName(), query)
                || contains(contact.getEmail(), query)
                || contains(contact.getPrimaryPhoneNumber(), query)) {

            return true;
        }

        if (contact.getTags() != null) {

            for (String tag : contact.getTags()) {

                if (contains(tag, query)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean contains(String value, String query) {

        return value != null
                && value.toLowerCase().contains(query);
    }

    // ---------------------------------------------------------
    // UPDATE CONTACT DISPLAY
    // ---------------------------------------------------------

    private void updateDisplayWithContacts(List<Contact> list) {

        if (list == null) {
            list = new ArrayList<>();
        }

        displayedContacts = new ArrayList<>(list);

        // Clear previous selections before changing the models.
        contactList.clearSelection();
        contactTable.clearSelection();

        // Update Card List.
        contactListModel.clear();

        for (Contact c : displayedContacts) {
            contactListModel.addElement(c);
        }

        // Update Table.
        tableModel.setRowCount(0);

        for (Contact c : displayedContacts) {

            String star = c.isFavorite() ? "★" : "";

            String company = "—";

            if (c instanceof com.cms.model.BusinessContact) {

                company = ((com.cms.model.BusinessContact) c)
                        .getCompanyName();
            }

            String tags = c.getTags() == null
                    ? ""
                    : String.join(", ", c.getTags());

            tableModel.addRow(new Object[]{
                    star,
                    c.getFullName(),
                    c.getContactType().getDisplayName(),
                    c.getPrimaryPhoneNumber(),
                    c.getEmail(),
                    company,
                    tags
            });
        }

        // Update result counters.
        if (resultsCountLbl != null) {

            resultsCountLbl.setText(
                    displayedContacts.size()
                            + (displayedContacts.size() == 1
                            ? " contact"
                            : " contacts")
            );
        }

        if (statusCountLbl != null) {

            statusCountLbl.setText(
                    "Displaying: " + displayedContacts.size()
            );
        }

        // Display first contact automatically.
        if (!displayedContacts.isEmpty()) {

            if (currentViewMode == ViewMode.CARDS) {

                contactList.setSelectedIndex(0);

            } else {

                contactTable.setRowSelectionInterval(0, 0);
            }

        } else {

            if (detailPanel != null) {
                detailPanel.displayContact(null);
            }
        }

        revalidate();
        repaint();
    }

    // ---------------------------------------------------------
    // VIEW SELECTION SYNCHRONIZATION
    // ---------------------------------------------------------

    private void syncSelectionToCards() {

        int row = contactTable.getSelectedRow();

        if (row >= 0 && row < displayedContacts.size()) {

            contactList.setSelectedValue(
                    displayedContacts.get(row),
                    true
            );
        }
    }

    private void syncSelectionToTable() {

        Contact selected = contactList.getSelectedValue();

        if (selected != null) {

            int index = displayedContacts.indexOf(selected);

            if (index >= 0) {

                contactTable.setRowSelectionInterval(index, index);
            }
        }
    }

    private void selectContactInView(Contact contact) {

        if (contact == null) {
            return;
        }

        int index = displayedContacts.indexOf(contact);

        if (index >= 0) {

            contactList.setSelectedIndex(index);

            contactTable.setRowSelectionInterval(index, index);

            if (detailPanel != null) {
                detailPanel.displayContact(contact);
            }
        }
    }

    // ---------------------------------------------------------
    // SIDEBAR BADGES AND TAGS
    // ---------------------------------------------------------

    private void updateSidebarBadges() {

        long total = contactService.getTotalActiveCount();

        long favorites = contactService.getTotalFavoriteCount();

        long trash = contactService.getDeletedContacts().size();

        Map<ContactType, Integer> distribution =
                contactService.getCategoryDistribution();

        if (totalBadgeLbl != null) {
            totalBadgeLbl.setText(String.valueOf(total));
        }

        if (favBadgeLbl != null) {
            favBadgeLbl.setText(String.valueOf(favorites));
        }

        if (persBadgeLbl != null) {

            persBadgeLbl.setText(
                    String.valueOf(
                            distribution.getOrDefault(
                                    ContactType.PERSONAL, 0
                            )
                    )
            );
        }

        if (bizBadgeLbl != null) {

            bizBadgeLbl.setText(
                    String.valueOf(
                            distribution.getOrDefault(
                                    ContactType.BUSINESS, 0
                            )
                    )
            );
        }

        if (emgBadgeLbl != null) {

            emgBadgeLbl.setText(
                    String.valueOf(
                            distribution.getOrDefault(
                                    ContactType.EMERGENCY, 0
                            )
                    )
            );
        }

        if (trashBadgeLbl != null) {
            trashBadgeLbl.setText(String.valueOf(trash));
        }

        if (totalStatCard != null) {
            totalStatCard.setValue(String.valueOf(total));
        }

        if (favStatCard != null) {
            favStatCard.setValue(String.valueOf(favorites));
        }

        // Refresh tag buttons.
        if (tagPillsContainer != null) {

            tagPillsContainer.removeAll();

            List<String> tags = contactService.getAllTags();

            if (tags != null) {

                for (String tag : tags) {

                    JLabel pill = new JLabel("#" + tag);

                    pill.setFont(ModernTheme.FONT_SMALL_BOLD);
                    pill.setOpaque(true);

                    boolean selected =
                            currentFilterMode.equals("TAG:" + tag);

                    pill.setBackground(
                            selected
                                    ? ModernTheme.ACCENT_PRIMARY
                                    : ModernTheme.getSurfaceVariant()
                    );

                    pill.setForeground(
                            selected
                                    ? Color.WHITE
                                    : ModernTheme.getTextPrimary()
                    );

                    pill.setBorder(
                            new EmptyBorder(3, 8, 3, 8)
                    );

                    pill.setCursor(
                            Cursor.getPredefinedCursor(
                                    Cursor.HAND_CURSOR
                            )
                    );

                    pill.addMouseListener(
                            new java.awt.event.MouseAdapter() {
                                @Override
                                public void mouseClicked(
                                        java.awt.event.MouseEvent e
                                ) {

                                    currentFilterMode = "TAG:" + tag;
                                    currentSearchQuery = "";

                                    // FIX: Rebuild before refreshing.
                                    rebuildAndRefresh();
                                }
                            }
                    );

                    tagPillsContainer.add(pill);
                }
            }

            tagPillsContainer.revalidate();
            tagPillsContainer.repaint();
        }
    }

    // ---------------------------------------------------------
    // OBSERVER PATTERN
    // ---------------------------------------------------------

    @Override
    public void onContactChanged(ContactChangeEvent event) {

        // Update current category instead of switching to ALL.
        refreshContactList();

        updateSidebarBadges();

        updateStorageIndicator();
    }
}

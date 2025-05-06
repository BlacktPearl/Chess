import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardGUI extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private List<Tournament> availableTournaments;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel dashboardPanel, tournamentsPanel, profilePanel, adminPanel, refereePanel;
    
    // Colors for UI
    private static final Color PRIMARY_COLOR = new Color(10, 88, 202); // Rich blue
    private static final Color DARKER_PRIMARY = new Color(8, 71, 163); // Darker rich blue
    private static final Color SECONDARY_COLOR = new Color(45, 55, 72); // Dark slate
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Nearly white
    private static final Color SIDEBAR_COLOR = new Color(31, 41, 55); // Dark gray for sidebar
    private static final Color PANEL_BACKGROUND = new Color(255, 255, 255); // Pure white
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // Dark gray
    private static final Color LIGHT_TEXT = new Color(240, 240, 245); // Light text for dark backgrounds
    private static final Color MUTED_TEXT = new Color(107, 114, 128); // Medium gray
    private static final Color FIELD_BACKGROUND = new Color(243, 244, 246); // Light gray
    private static final Color FIELD_BORDER = new Color(229, 231, 235); // Lighter gray
    private static final Color ACCENT_COLOR = new Color(99, 102, 241); // Vibrant indigo
    
    // Fonts
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font SIDEBAR_FONT = new Font("SansSerif", Font.BOLD, 15);
    private static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    // Sidebar buttons
    private JButton dashboardBtn, tournamentsBtn, profileBtn, adminBtn, refereeBtn, trainingBtn;
    private JButton[] sidebarButtons;
    
    public DashboardGUI(User user) {
        this.currentUser = user;
        
        setTitle("Chess Tournament Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data
        initializeData();
        
        // Set content pane with background color
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BACKGROUND_COLOR);
        setContentPane(contentPane);
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Create main panel with sidebar and content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create sidebar
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create content panel with card layout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        
        // Create content panels
        dashboardPanel = createDashboardPanel();
        tournamentsPanel = createTournamentsPanel();
        profilePanel = createProfilePanel();
        
        // Add panels to card layout
        mainContentPanel.add(dashboardPanel, "dashboard");
        mainContentPanel.add(tournamentsPanel, "tournaments");
        mainContentPanel.add(profilePanel, "profile");
        
        // Add role-specific panels
        if (currentUser.getRole() == 2) { // Admin
            adminPanel = createAdminPanel();
            mainContentPanel.add(adminPanel, "admin");
        } else if (currentUser.getRole() == 1) { // Referee
            refereePanel = createRefereePanel();
            mainContentPanel.add(refereePanel, "referee");
        }
        
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Show dashboard panel initially
        cardLayout.show(mainContentPanel, "dashboard");
        updateSidebarSelection("dashboard");
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int w = getWidth();
                int h = getHeight();
                
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, h, DARKER_PRIMARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // App title
        JLabel titleLabel = new JLabel("Chess Tournament System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(HEADER_FONT);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        // User label with role
        String roleText = getRoleString(currentUser.getRole());
        JLabel userLabel = new JLabel(currentUser.getName() + " (" + roleText + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(REGULAR_FONT);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(REGULAR_FONT);
        logoutButton.setForeground(PRIMARY_COLOR);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());
        
        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        userPanel.add(logoutButton);
        
        panel.add(userPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(200, 0));
        
        // Add logo or brand name
        JLabel logoLabel = new JLabel("♟️ Chess TMS");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 0));
        sidebar.add(logoLabel);
        
        // Create navigation buttons
        dashboardBtn = createSidebarButton("Dashboard", "dashboard");
        tournamentsBtn = createSidebarButton("Tournaments", "tournaments");
        profileBtn = createSidebarButton("Profile", "profile");
        trainingBtn = createSidebarButton("Training Board", "");
        
        // Add training board button action
        trainingBtn.addActionListener(e -> openTrainingBoard());
        
        // Store buttons in array for styling
        if (currentUser.getRole() == 2) { // Admin
            adminBtn = createSidebarButton("Admin Panel", "admin");
            sidebarButtons = new JButton[] { dashboardBtn, tournamentsBtn, profileBtn, adminBtn, trainingBtn };
        } else if (currentUser.getRole() == 1) { // Referee
            refereeBtn = createSidebarButton("Referee Panel", "referee");
            sidebarButtons = new JButton[] { dashboardBtn, tournamentsBtn, profileBtn, refereeBtn, trainingBtn };
        } else { // Player or guest
            sidebarButtons = new JButton[] { dashboardBtn, tournamentsBtn, profileBtn, trainingBtn };
        }
        
        // Add buttons to sidebar
        for (JButton button : sidebarButtons) {
            sidebar.add(button);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Add spacer
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createSidebarButton(String text, String cardName, String icon) {
        // Create a button without an icon prefix (we'll use proper icons later)
        JButton button = new JButton(text);
        button.setFont(SIDEBAR_FONT);
        button.setForeground(LIGHT_TEXT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setContentAreaFilled(true);
                    button.setBackground(new Color(50, 60, 80));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(PRIMARY_COLOR)) {
                    button.setContentAreaFilled(false);
                }
            }
        });
        
        // Add action to show corresponding panel
        button.addActionListener(e -> {
            cardLayout.show(mainContentPanel, cardName);
            updateSidebarSelection(cardName);
        });
        
        return button;
    }
    
    private JButton createSidebarButton(String text, String cardName) {
        // Overload to support existing method calls without icons
        return createSidebarButton(text, cardName, null);
    }
    
    private void updateSidebarSelection(String selectedCard) {
        // Reset all buttons
        for (JButton button : sidebarButtons) {
            button.setContentAreaFilled(false);
            button.setForeground(LIGHT_TEXT);
        }
        
        // Set selected button
        JButton selectedButton = null;
        switch (selectedCard) {
            case "dashboard": selectedButton = dashboardBtn; break;
            case "tournaments": selectedButton = tournamentsBtn; break;
            case "profile": selectedButton = profileBtn; break;
            case "admin": selectedButton = adminBtn; break;
            case "referee": selectedButton = refereeBtn; break;
        }
        
        if (selectedButton != null) {
            selectedButton.setContentAreaFilled(true);
            selectedButton.setBackground(PRIMARY_COLOR);
            selectedButton.setForeground(Color.WHITE);
        }
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(TEXT_COLOR);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main dashboard content with stats and quick actions
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        statsPanel.setOpaque(false);
        
        // Top row stats
        JPanel topStatsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        topStatsPanel.setOpaque(false);
        topStatsPanel.add(createInfoPanel("Active Tournaments", ""+availableTournaments.size(), "🏆"));
        topStatsPanel.add(createInfoPanel("Your Role", getRoleString(currentUser.getRole()), "👑"));
        
        // Bottom row stats
        JPanel bottomStatsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomStatsPanel.setOpaque(false);
        
        if (currentUser instanceof Player) {
            Player player = (Player) currentUser;
            bottomStatsPanel.add(createInfoPanel("Your Rating", ""+player.getRating(), "📊"));
            bottomStatsPanel.add(createInfoPanel("Total Games", ""+(player.getWins() + player.getLosses() + player.getDraws()), "🎮"));
        } else {
            bottomStatsPanel.add(createInfoPanel("System Status", "Online", "✅"));
            bottomStatsPanel.add(createInfoPanel("Active Users", "24", "👥"));
        }
        
        statsPanel.add(topStatsPanel);
        statsPanel.add(bottomStatsPanel);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(PANEL_BACKGROUND);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(HEADER_FONT);
        actionsTitle.setForeground(TEXT_COLOR);
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create action buttons
        JButton playButton = createActionButton("Play Game", e -> startNewGame());
        JButton browseButton = createActionButton("Browse Tournaments", e -> {
            cardLayout.show(mainContentPanel, "tournaments");
            updateSidebarSelection("tournaments");
        });
        
        JButton profileButton = createActionButton("Edit Profile", e -> {
            cardLayout.show(mainContentPanel, "profile");
            updateSidebarSelection("profile");
        });
        
        JButton trainingButton = createActionButton("Training Board", e -> openTrainingBoard());
        
        actionsPanel.add(actionsTitle);
        actionsPanel.add(Box.createVerticalStrut(20));
        actionsPanel.add(playButton);
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(browseButton);
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(trainingButton);
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(profileButton);
        
        contentPanel.add(statsPanel);
        contentPanel.add(actionsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel(String title, String value, String icon) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Icon and title in a panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Value
        JLabel valueLabel = new JLabel(value, JLabel.LEFT);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_COLOR);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(FIELD_BACKGROUND);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        button.addActionListener(action);
        return button;
    }
    
    private JPanel createTournamentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Available Tournaments");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Only show create tournament button for admins
        if (currentUser instanceof Admin) {
            JButton createButton = createPrimaryButton("Create Tournament");
            createButton.addActionListener(e -> createNewTournament());
            headerPanel.add(createButton, BorderLayout.EAST);
        }
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tournaments table with modern styling
        String[] columnNames = {"Name", "Location", "Date", "Status", "Players"};
        
        // Sample data - Add more tournaments with Chinese cities
        Object[][] data = {
            {"City Chess Championship", "London", "2023-11-05", "upcoming", "1"},
            {"Shanghai Masters", "上海 (Shanghai)", "2023-12-15", "open", "8"},
            {"Beijing Open", "北京 (Beijing)", "2024-01-20", "open", "12"},
            {"Guangzhou Invitational", "广州 (Guangzhou)", "2024-02-10", "upcoming", "6"},
            {"Hangzhou Cup", "杭州 (Hangzhou)", "2024-03-05", "upcoming", "4"},
            {"Chengdu Chess Festival", "成都 (Chengdu)", "2024-04-12", "upcoming", "0"},
            {"Nanjing Tournament", "南京 (Nanjing)", "2024-05-18", "upcoming", "2"},
            {"Shenzhen Championship", "深圳 (Shenzhen)", "2024-06-25", "upcoming", "3"},
            {"Xi'an Ancient City Cup", "西安 (Xi'an)", "2024-07-30", "upcoming", "0"},
            {"Suzhou Masters", "苏州 (Suzhou)", "2024-08-15", "upcoming", "5"},
        };
        
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Modern table styling
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(PRIMARY_COLOR);
        
        // Custom cell renderer for better appearance
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Alternating row colors
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                    c.setForeground(TEXT_COLOR);
                }
                
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        
        // Configure column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Name
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Location
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Date
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Players
        
        // Set the table header appearance
        JTableHeader header = table.getTableHeader();
        header.setBackground(FIELD_BACKGROUND);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, REGULAR_FONT.getSize()));
        header.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        
        // Apply the renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        // Wrap in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setOpaque(false);
        
        JButton viewButton = createSecondaryButton("View Details");
        viewButton.setEnabled(false); // Initially disabled
        
        JButton joinButton = createPrimaryButton("Join Tournament");
        joinButton.setEnabled(false); // Initially disabled
        
        actionsPanel.add(viewButton);
        actionsPanel.add(joinButton);
        
        // Add help text
        JLabel helpLabel = new JLabel("Select a tournament to view details or join");
        helpLabel.setFont(REGULAR_FONT);
        helpLabel.setForeground(MUTED_TEXT);
        helpLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(actionsPanel, BorderLayout.WEST);
        bottomPanel.add(helpLabel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Enable buttons when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() != -1;
            viewButton.setEnabled(rowSelected);
            joinButton.setEnabled(rowSelected);
            
            if (rowSelected) {
                // Update help label text with selected tournament
                String tournamentName = (String) table.getValueAt(table.getSelectedRow(), 0);
                helpLabel.setText("Selected: " + tournamentName);
            } else {
                helpLabel.setText("Select a tournament to view details or join");
            }
        });
        
        // Add action listeners
        viewButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String tournamentName = (String) table.getValueAt(selectedRow, 0);
                String location = (String) table.getValueAt(selectedRow, 1);
                showTournamentDetails(tournamentName, location);
            }
        });
        
        joinButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String tournamentName = (String) table.getValueAt(selectedRow, 0);
                joinTournament(tournamentName);
            }
        });
        
        return panel;
    }
    
    private void showTournamentDetails(String tournamentName, String location) {
        // Find the tournament
        final Tournament targetTournament = findTournamentByName(tournamentName);
        
        if (targetTournament != null) {
            // For non-admin users, check role before allowing access to tournament details
            int userRole = currentUser.getRole();
            if (userRole != 0 && userRole != 2 && !currentUser.getName().toLowerCase().contains("guest")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Only players, guests, and administrators can view tournament details.\n\n" +
                    "You are currently logged in as a " + getRoleString(userRole) + ".",
                    "Access Restricted",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Open the tournament view
            SwingUtilities.invokeLater(() -> new TournamentGUI(targetTournament, currentUser).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Tournament not found!");
        }
    }
    
    private void joinTournament(String tournamentName) {
        // Check if the user is a player (role 0) or a guest user
        boolean isPlayer = currentUser.getRole() == 0 || currentUser.getName().toLowerCase().contains("guest");
        
        if (!isPlayer) {
            JOptionPane.showMessageDialog(
                this,
                "Only players can join tournaments.\n\n" +
                "You are currently logged in as a " + getRoleString(currentUser.getRole()) + ".",
                "Access Restricted",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Find the tournament - making it effectively final for lambda usage
        final Tournament targetTournament = findTournamentByName(tournamentName);
        
        if (targetTournament != null) {
            if (currentUser instanceof Player) {
                boolean joined = targetTournament.addPlayer((Player)currentUser);
                if (joined) {
                    JOptionPane.showMessageDialog(this, "Successfully joined tournament: " + tournamentName);
                    
                    // Open tournament details
                    SwingUtilities.invokeLater(() -> new TournamentGUI(targetTournament, currentUser).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Could not join tournament. It may be full, already started, or you're already registered.");
                }
            } else {
                // Create a temporary Player object for a guest user
                Player tempPlayer = new Player(currentUser.getName(), 1200, currentUser.getCountry());
                boolean joined = targetTournament.addPlayer(tempPlayer);
                if (joined) {
                    JOptionPane.showMessageDialog(this, "Successfully joined tournament: " + tournamentName);
                    
                    // Open tournament details
                    SwingUtilities.invokeLater(() -> new TournamentGUI(targetTournament, currentUser).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Could not join tournament. It may be full, already started, or you're already registered.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tournament not found.");
        }
    }
    
    // Helper method to find a tournament by name
    private Tournament findTournamentByName(String name) {
        for (Tournament t : availableTournaments) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
    
    /**
     * Opens the training board in a new window
     */
    private void openTrainingBoard() {
        // Check if the user is a player (role 0) or a guest user
        boolean isPlayer = currentUser.getRole() == 0 || currentUser.getName().toLowerCase().contains("guest");
        
        if (!isPlayer) {
            JOptionPane.showMessageDialog(
                this,
                "Only players can access training features.\n\n" +
                "You are currently logged in as a " + getRoleString(currentUser.getRole()) + ".",
                "Access Restricted",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        new TrainingBoardGUI().setVisible(true);
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Profile header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        // Create profile header with avatar and info
        JPanel profileHeader = new JPanel(new BorderLayout(20, 0));
        profileHeader.setOpaque(false);
        
        // Avatar circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular background
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw user initials
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
                String initials = getInitials(currentUser.getName());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getHeight();
                g2d.drawString(initials, (getWidth() - textWidth) / 2, 
                               (getHeight() + textHeight / 2) / 2 - fm.getDescent());
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        profileHeader.add(avatarPanel, BorderLayout.WEST);
        
        // User info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(TITLE_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel roleLabel = new JLabel(getRoleString(currentUser.getRole()) + " • " + currentUser.getCountry());
        roleLabel.setFont(REGULAR_FONT);
        roleLabel.setForeground(MUTED_TEXT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(roleLabel);
        
        profileHeader.add(userInfoPanel, BorderLayout.CENTER);
        
        // Edit profile button on the right
        JButton editButton = createSecondaryButton("Edit Profile");
        editButton.setPreferredSize(new Dimension(120, 40));
        editButton.addActionListener(e -> editProfile());
        profileHeader.add(editButton, BorderLayout.EAST);
        
        headerPanel.add(profileHeader, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content area with profile details and stats
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setOpaque(false);
        
        // Details card
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBackground(PANEL_BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        // Panel title
        JLabel detailsTitle = new JLabel("Account Details");
        detailsTitle.setFont(HEADER_FONT);
        detailsTitle.setForeground(TEXT_COLOR);
        JPanel detailsTitlePanel = new JPanel(new BorderLayout());
        detailsTitlePanel.setOpaque(false);
        detailsTitlePanel.add(detailsTitle, BorderLayout.WEST);
        detailsTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        detailsPanel.add(detailsTitlePanel, BorderLayout.NORTH);
        
        // Details list
        JPanel detailsListPanel = new JPanel(new GridLayout(0, 1, 0, 15));
        detailsListPanel.setOpaque(false);
        
        // Username field
        detailsListPanel.add(createProfileField("Username", currentUser.getUsername()));
        
        // Email field (simulated as we don't store emails)
        detailsListPanel.add(createProfileField("Email", 
                                               currentUser.getUsername().toLowerCase() + "@chess-tournament.com"));
        
        // Account created date (simulated)
        detailsListPanel.add(createProfileField("Account Created", "January 15, 2025"));
        
        // Change password button
        JButton passwordButton = createSecondaryButton("Change Password");
        passwordButton.addActionListener(e -> changePassword());
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordButton);
        detailsListPanel.add(passwordPanel);
        
        detailsPanel.add(detailsListPanel, BorderLayout.CENTER);
        
        // Player statistics card (only for players)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BorderLayout());
        statsPanel.setBackground(PANEL_BACKGROUND);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        if (currentUser instanceof Player) {
            Player player = (Player) currentUser;
            
            // Stats title
            JLabel statsTitle = new JLabel("Player Statistics");
            statsTitle.setFont(HEADER_FONT);
            statsTitle.setForeground(TEXT_COLOR);
            JPanel statsTitlePanel = new JPanel(new BorderLayout());
            statsTitlePanel.setOpaque(false);
            statsTitlePanel.add(statsTitle, BorderLayout.WEST);
            statsTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            statsPanel.add(statsTitlePanel, BorderLayout.NORTH);
            
            // Rating card
            JPanel ratingPanel = createStatsCard("Current Rating", 
                                               String.valueOf(player.getRating()), 
                                               PRIMARY_COLOR);
            
            // Record card
            String record = player.getWins() + "-" + player.getLosses() + "-" + player.getDraws();
            JPanel recordPanel = createStatsCard("Record (W-L-D)", record, SECONDARY_COLOR);
            
            // Stats grid
            JPanel statsGridPanel = new JPanel(new GridLayout(2, 1, 0, 15));
            statsGridPanel.setOpaque(false);
            statsGridPanel.add(ratingPanel);
            statsGridPanel.add(recordPanel);
            
            statsPanel.add(statsGridPanel, BorderLayout.CENTER);
            
            // Recent matches
            if (player.getWins() + player.getLosses() + player.getDraws() > 0) {
                JPanel matchesPanel = new JPanel(new BorderLayout());
                matchesPanel.setOpaque(false);
                matchesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                
                JLabel matchesTitle = new JLabel("Recent Matches");
                matchesTitle.setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, REGULAR_FONT.getSize()));
                matchesTitle.setForeground(TEXT_COLOR);
                matchesPanel.add(matchesTitle, BorderLayout.NORTH);
                
                // This would normally be populated from a database
                String[] columns = {"Date", "Opponent", "Result", "Rating Change"};
                Object[][] data = {
                    {"Jan 20, 2025", "John Doe", "Win", "+12"},
                    {"Jan 18, 2025", "Jane Smith", "Loss", "-8"},
                    {"Jan 15, 2025", "Bob Johnson", "Draw", "+2"}
                };
                
                JTable matchesTable = new JTable(data, columns);
                matchesTable.setRowHeight(35);
                matchesTable.setFont(REGULAR_FONT);
                matchesTable.setShowGrid(false);
                matchesTable.setBackground(PANEL_BACKGROUND);
                matchesTable.getTableHeader().setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, 
                                                             REGULAR_FONT.getSize() - 1));
                matchesTable.getTableHeader().setBackground(PANEL_BACKGROUND);
                matchesTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, 
                                                                                      new Color(220, 220, 220)));
                
                JScrollPane matchesScroll = new JScrollPane(matchesTable);
                matchesScroll.setBorder(BorderFactory.createEmptyBorder());
                matchesScroll.getViewport().setBackground(PANEL_BACKGROUND);
                
                matchesPanel.add(matchesScroll, BorderLayout.CENTER);
                statsPanel.add(matchesPanel, BorderLayout.SOUTH);
            }
        } else {
            // For non-players
            JLabel noStatsLabel = new JLabel("Statistics only available for players", JLabel.CENTER);
            noStatsLabel.setFont(REGULAR_FONT);
            noStatsLabel.setForeground(MUTED_TEXT);
            statsPanel.add(noStatsLabel, BorderLayout.CENTER);
        }
        
        mainPanel.add(detailsPanel);
        mainPanel.add(statsPanel);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background with gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    0, getHeight(), 
                    DARKER_PRIMARY
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                // Paint text
                g2.setColor(Color.WHITE);
                g2.setFont(BUTTON_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
                g2.dispose();
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background with border
                g2.setColor(FIELD_BACKGROUND);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(FIELD_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                
                // Paint text
                g2.setColor(TEXT_COLOR);
                g2.setFont(BUTTON_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
                g2.dispose();
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        
        return button;
    }
    
    private JPanel createProfileField(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, REGULAR_FONT.getSize()));
        labelText.setForeground(MUTED_TEXT);
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(REGULAR_FONT);
        valueText.setForeground(TEXT_COLOR);
        
        panel.add(labelText, BorderLayout.NORTH);
        panel.add(valueText, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsCard(String label, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueLabel.setForeground(color);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(REGULAR_FONT);
        labelText.setForeground(MUTED_TEXT);
        
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(labelText, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Helper method to get user initials for the avatar
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }
    
    private void startNewGame() {
        // Check if the user is a player (role 0) or a guest user
        boolean isPlayer = currentUser.getRole() == 0 || currentUser.getName().toLowerCase().contains("guest");
        
        if (!isPlayer) {
            JOptionPane.showMessageDialog(
                this,
                "Only players can start games.\n\n" +
                "You are currently logged in as a " + getRoleString(currentUser.getRole()) + ".",
                "Access Restricted",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        Player opponent = selectOpponent();
        if (opponent != null && currentUser instanceof Player) {
            Match match = new Match(1, (Player)currentUser, opponent, "10|0");
            match.startMatch();
            new MatchGUI(match).setVisible(true);
        } else if (opponent != null) {
            // Handle case where currentUser is not a Player instance
            // Create a temporary Player object based on the current user
            Player player = new Player(currentUser.getName(), 1200, currentUser.getCountry());
            Match match = new Match(1, player, opponent, "10|0");
            match.startMatch();
            new MatchGUI(match).setVisible(true);
        }
    }
    
    private Player selectOpponent() {
        // Options for opponent selection
        String[] options = {"Human Opponent", "Specific Player", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this, 
            "How would you like to find an opponent?", 
            "Find Opponent",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 0) {
            // Human opponent - user will play both sides
            Player opponent = new Player("Opponent", 1200, "Local");
            
            JOptionPane.showMessageDialog(
                this, 
                "You will be playing against a local human opponent.\n" +
                "Both players will use the same computer and take turns.\n\n" +
                "White: " + currentUser.getName() + "\n" +
                "Black: " + opponent.getName(),
                "Human Opponent",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            return opponent;
        } else if (choice == 1) {
            // Specific player - would show user selection dialog
            String name = JOptionPane.showInputDialog(this, "Enter player name:");
            if (name != null && !name.isEmpty()) {
                return new Player(name, 1200, "Unknown");
            }
        }
        
        return null;
    }
    
    private void createNewTournament() {
        if (currentUser.getRole() != 2) {
            JOptionPane.showMessageDialog(this, "Only administrators can create tournaments.");
            return;
        }
        
        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField dateField = new JTextField(10);
        JTextField timeControlField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Tournament Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Start Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Time Control:"));
        panel.add(timeControlField);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Create Tournament", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Create tournament and add to list
            Admin admin = (Admin)currentUser;
            Tournament tournament = admin.createTournament(
                nameField.getText(),
                locationField.getText(),
                dateField.getText(),
                timeControlField.getText()
            );
            
            if (tournament != null) {
                availableTournaments.add(tournament);
                JOptionPane.showMessageDialog(this, "Tournament created successfully!");
                
                // Refresh the tournaments panel
                mainContentPanel.remove(tournamentsPanel);
                tournamentsPanel = createTournamentsPanel();
                mainContentPanel.add(tournamentsPanel, "tournaments");
                cardLayout.show(mainContentPanel, "tournaments");
            }
        }
    }
    
    private void manageTournaments() {
        if (currentUser.getRole() != 2) {
            JOptionPane.showMessageDialog(this, "Only administrators can manage tournaments.");
            return;
        }
        
        // In a real system, would show tournament management interface
        JOptionPane.showMessageDialog(this, "Tournament management functionality would be implemented here.");
    }
    
    private void managePlayers() {
        if (currentUser.getRole() != 2) {
            JOptionPane.showMessageDialog(this, "Only administrators can manage players.");
            return;
        }
        
        // In a real system, would show player management interface
        JOptionPane.showMessageDialog(this, "Player management functionality would be implemented here.");
    }
    
    private void manageReferees() {
        if (currentUser.getRole() != 2) {
            JOptionPane.showMessageDialog(this, "Only administrators can manage referees.");
            return;
        }
        
        // In a real system, would show referee management interface
        JOptionPane.showMessageDialog(this, "Referee management functionality would be implemented here.");
    }
    
    private void startMatch(String matchId) {
        // In a real system, would start the match
        JOptionPane.showMessageDialog(this, "Starting match #" + matchId);
    }
    
    private void resolveDispute(String matchId) {
        // In a real system, would show dispute resolution interface
        String[] options = {"White Wins", "Black Wins", "Draw", "Cancel"};
        int decision = JOptionPane.showOptionDialog(
            this,
            "Resolve dispute for match #" + matchId,
            "Resolve Dispute",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[3]
        );
        
        if (decision < 3) {
            JOptionPane.showMessageDialog(this, "Dispute resolved: " + options[decision]);
        }
    }
    
    private void editProfile() {
        JTextField nameField = new JTextField(currentUser.getName(), 20);
        JTextField countryField = new JTextField(currentUser.getCountry(), 20);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Country:"));
        panel.add(countryField);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Edit Profile", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            currentUser.setName(nameField.getText());
            currentUser.setCountry(countryField.getText());
            
            // Refresh the profile panel
            mainContentPanel.remove(profilePanel);
            profilePanel = createProfilePanel();
            mainContentPanel.add(profilePanel, "profile");
            cardLayout.show(mainContentPanel, "profile");
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        }
    }
    
    private void changePassword() {
        JPasswordField currentPasswordField = new JPasswordField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Current Password:"));
        panel.add(currentPasswordField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Change Password", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // In a real system, would validate and update password
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        }
    }
    
    private void handleLogout() {
        currentUser.logout();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
    
    private void initializeData() {
        availableTournaments = new ArrayList<>();
        
        // Create sample tournaments
        Tournament t1 = new Tournament(1, "International Chess Open", "New York", "2023-10-15", "90+30", 5);
        Tournament t2 = new Tournament(2, "City Chess Championship", "London", "2023-11-05", "60+10", 7);
        Tournament t3 = new Tournament(3, "Weekend Rapid Tournament", "Paris", "2023-09-30", "15+10", 4);
        
        // Add some players to tournaments
        t1.addPlayer(new Player("Alice", 1600, "USA"));
        t1.addPlayer(new Player("Bob", 1550, "UK"));
        t2.addPlayer(new Player("Charlie", 1700, "France"));
        
        availableTournaments.add(t1);
        availableTournaments.add(t2);
        availableTournaments.add(t3);
    }
    
    private String getRoleString(int role) {
        switch (role) {
            case 0: return "Player";
            case 1: return "Referee";
            case 2: return "Administrator";
            default: return "Unknown";
        }
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Administration Tools");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Admin actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Action cards
        actionsPanel.add(createActionCard("Create Tournament", "Create and manage new chess tournaments", 
                                        e -> createNewTournament()));
        
        actionsPanel.add(createActionCard("Manage Tournaments", "Edit, delete, or view existing tournaments", 
                                        e -> manageTournaments()));
        
        actionsPanel.add(createActionCard("Manage Players", "View and edit player accounts and ratings", 
                                        e -> managePlayers()));
        
        actionsPanel.add(createActionCard("Manage Referees", "Assign and manage tournament referees", 
                                        e -> manageReferees()));
        
        panel.add(actionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRefereePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Referee Dashboard");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Assigned matches panel
        JPanel matchesContainer = new JPanel(new BorderLayout());
        matchesContainer.setBackground(PANEL_BACKGROUND);
        matchesContainer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        // Matches title
        JLabel matchesTitle = new JLabel("Assigned Matches");
        matchesTitle.setFont(HEADER_FONT);
        matchesTitle.setForeground(TEXT_COLOR);
        matchesContainer.add(matchesTitle, BorderLayout.NORTH);
        
        // Matches table
        String[] columns = {"Match ID", "Player 1", "Player 2", "Status", "Time Control"};
        Object[][] data = {
            {"1001", "Player A", "Player B", "Scheduled", "10+5"},
            {"1002", "Player C", "Player D", "In Progress", "15+10"}
        };
        
        // Create modern table
        JTable matchesTable = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Apply modern styling to cells
                if (comp instanceof JComponent) {
                    ((JComponent) comp).setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                    
                    // Alternating row colors for better readability
                    if (!isRowSelected(row)) {
                        comp.setBackground(row % 2 == 0 ? PANEL_BACKGROUND : new Color(248, 249, 250));
                    } else {
                        comp.setBackground(PRIMARY_COLOR.brighter());
                        comp.setForeground(Color.WHITE);
                    }
                }
                
                return comp;
            }
        };
        
        // Set table styling
        matchesTable.setRowHeight(45);
        matchesTable.setFont(REGULAR_FONT);
        matchesTable.setShowGrid(false);
        matchesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchesTable.setFillsViewportHeight(true);
        matchesTable.getTableHeader().setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, REGULAR_FONT.getSize()));
        matchesTable.getTableHeader().setBackground(new Color(248, 249, 250));
        matchesTable.getTableHeader().setForeground(TEXT_COLOR);
        matchesTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(matchesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        // Add to matches container
        matchesContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setOpaque(false);
        
        JButton startButton = createPrimaryButton("Start Match");
        startButton.setPreferredSize(new Dimension(140, 40));
        startButton.addActionListener(e -> {
            int row = matchesTable.getSelectedRow();
            if (row >= 0) {
                startMatch((String)data[row][0]);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a match.");
            }
        });
        
        JButton resolveButton = createSecondaryButton("Resolve Dispute");
        resolveButton.setPreferredSize(new Dimension(140, 40));
        resolveButton.addActionListener(e -> {
            int row = matchesTable.getSelectedRow();
            if (row >= 0) {
                resolveDispute((String)data[row][0]);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a match.");
            }
        });
        
        // First add a help text
        JLabel helpLabel = new JLabel("Select a match to manage");
        helpLabel.setFont(REGULAR_FONT);
        helpLabel.setForeground(MUTED_TEXT);
        buttonsPanel.add(helpLabel);
        
        // Add flexible space
        buttonsPanel.add(Box.createHorizontalGlue());
        
        // Add buttons
        buttonsPanel.add(resolveButton);
        buttonsPanel.add(startButton);
        
        matchesContainer.add(buttonsPanel, BorderLayout.SOUTH);
        
        panel.add(matchesContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionCard(String title, String description, ActionListener action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(REGULAR_FONT);
        descLabel.setForeground(MUTED_TEXT);
        
        // Button
        JButton actionButton = createPrimaryButton("Open");
        actionButton.setPreferredSize(new Dimension(100, 35));
        actionButton.addActionListener(action);
        
        // Layout
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(actionButton);
        
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY_COLOR, 1, true),
                    new EmptyBorder(20, 25, 20, 25)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(20, 25, 20, 25)
                ));
            }
        });
        
        return panel;
    }
    
    public static void main(String[] args) {
        // For testing
        SwingUtilities.invokeLater(() -> {
            Player testUser = new Player("Test User", 1500, "USA");
            new DashboardGUI(testUser);
        });
    }
} 
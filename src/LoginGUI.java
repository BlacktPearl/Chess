
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton, registerButton;
    private JPanel cards;
    private CardLayout cardLayout;
    
    // Registration fields
    private JTextField regUsernameField, regNameField, regCountryField;
    private JPasswordField regPasswordField, regConfirmPasswordField;
    private JComboBox<String> regRoleComboBox;
    
    // Colors for UI - 2025 modern color scheme
    private static final Color PRIMARY_COLOR = new Color(10, 88, 202); // Rich blue
    private static final Color SECONDARY_COLOR = new Color(45, 55, 72); // Dark slate
    private static final Color ACCENT_COLOR = new Color(99, 102, 241); // Vibrant indigo
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Nearly white
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255); // Pure white
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // Dark gray
    private static final Color MUTED_TEXT = new Color(107, 114, 128); // Medium gray
    private static final Color FIELD_BACKGROUND = new Color(243, 244, 246); // Light gray
    private static final Color FIELD_BORDER = new Color(229, 231, 235); // Lighter gray
    
    // Fonts - Modern typography for 2025
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    public LoginGUI() {
        setTitle("Chess Tournament Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800); // Increased height to fit all registration form fields
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing to help users see all content
        
        // Create card layout for login and registration
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setBackground(BACKGROUND_COLOR);
        
        // Create login panel
        JPanel loginPanel = createLoginPanel();
        
        // Create registration panel
        JPanel registrationPanel = createRegistrationPanel();
        
        // Add panels to card layout
        cards.add(loginPanel, "login");
        cards.add(registrationPanel, "register");
        
        add(cards, BorderLayout.CENTER);
        
        // Start with login panel
        cardLayout.show(cards, "login");
        
        // Ensure users.txt exists
        try {
            File usersFile = new File("users.txt");
            if (!usersFile.exists()) {
                usersFile.createNewFile();
                // Create admin user by default
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
                    // username,passwordHash,role,name,country,id
                    String adminHash = User.hashPassword("admin");
                    writer.write("admin," + adminHash + ",2,Administrator,Global,1");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error initializing user database: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }


    // Set default close operation}
        // Apply modern look and feel
        applyModernLookAndFeel();
    }
    
    private void applyModernLookAndFeel() {
        // Set modern UI properties
        UIManager.put("OptionPane.background", CARD_BACKGROUND);
        UIManager.put("Panel.background", CARD_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Label.font", REGULAR_FONT);
        UIManager.put("TextField.font", REGULAR_FONT);
        UIManager.put("PasswordField.font", REGULAR_FONT);
    }
    
    private JPanel createLoginPanel() {
        // Create a panel with subtle gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, BACKGROUND_COLOR, 
                    0, h, new Color(235, 238, 250)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 60, 30, 60));
        
        // Main content panel with white background and shadow effect
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw subtle shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        contentPanel.setLayout(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        
        // Header with logo and title
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        
        // Chess logo
        JLabel logoLabel = new JLabel(createChessLogo());
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(logoLabel, BorderLayout.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel("Chess Tournament System", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.SOUTH);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel - direct layout without container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome back", JLabel.LEFT);
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(welcomeLabel);
        
        JLabel subtitleLabel = new JLabel("Sign in to continue", JLabel.LEFT);
        subtitleLabel.setFont(REGULAR_FONT);
        subtitleLabel.setForeground(MUTED_TEXT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(REGULAR_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        usernameField = createModernTextField(20);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setText("");
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(REGULAR_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        passwordField = createModernPasswordField(20);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setText("");
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Login button
        loginButton = createPrimaryButton("Sign In");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // Register button - make it visible and prominent
        JButton registerBtn = createSecondaryButton("Register New Account");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> cardLayout.show(cards, "register"));
        registerBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        formPanel.add(registerBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Or text divider
        JPanel dividerPanel = new JPanel(new BorderLayout(10, 0));
        dividerPanel.setOpaque(false);
        dividerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JSeparator leftSep = new JSeparator();
        leftSep.setForeground(FIELD_BORDER);
        JSeparator rightSep = new JSeparator();
        rightSep.setForeground(FIELD_BORDER);
        
        JLabel orLabel = new JLabel("or", JLabel.CENTER);
        orLabel.setFont(REGULAR_FONT);
        orLabel.setForeground(MUTED_TEXT);
        
        dividerPanel.add(leftSep, BorderLayout.WEST);
        dividerPanel.add(orLabel, BorderLayout.CENTER);
        dividerPanel.add(rightSep, BorderLayout.EAST);
        
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(dividerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Guest login option
        JPanel guestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        guestPanel.setOpaque(false);
        guestPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel guestLabel = new JLabel("Continue as ");
        guestLabel.setFont(REGULAR_FONT);
        guestLabel.setForeground(MUTED_TEXT);
        guestPanel.add(guestLabel);
        
        JButton guestButton = new JButton("Guest");
        guestButton.setFont(new Font(REGULAR_FONT.getName(), Font.BOLD, REGULAR_FONT.getSize()));
        guestButton.setForeground(PRIMARY_COLOR);
        guestButton.setBackground(null);
        guestButton.setBorder(null);
        guestButton.setContentAreaFilled(false);
        guestButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guestButton.addActionListener(e -> handleGuestLogin());
        guestButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                guestButton.setText("<html><u>Guest</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                guestButton.setText("Guest");
            }
        });
        guestPanel.add(guestButton);
        
        formPanel.add(guestPanel);
        
        // Directly add form panel to content panel - no scroll pane needed
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Copyright footer
        JLabel copyrightLabel = new JLabel("© 2025 Chess Tournament Management System", JLabel.CENTER);
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        copyrightLabel.setForeground(MUTED_TEXT);
        
        contentPanel.add(copyrightLabel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private ImageIcon createChessLogo() {
        // Create a modern chess logo - smaller size
        int size = 54; // Reduced from 64
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circular background
        g2.setColor(PRIMARY_COLOR);
        g2.fillOval(0, 0, size, size);
        
        // Draw chess king silhouette in white
        g2.setColor(Color.WHITE);
        
        // Draw a more elegant chess rook shape
        int margin = size/6;
        int baseHeight = size/5;
        
        // Base
        g2.fillRect(margin, size-margin-baseHeight, size-2*margin, baseHeight);
        
        // Body
        int bodyWidth = size/2;
        int bodyLeft = (size-bodyWidth)/2;
        int bodyHeight = size/2;
        int bodyTop = size-margin-baseHeight-bodyHeight;
        
        g2.fillRect(bodyLeft, bodyTop, bodyWidth, bodyHeight);
        
        // Top
        int topWidth = size/3;
        int topLeft = (size-topWidth)/2;
        int topHeight = size/5;
        int topTop = bodyTop-topHeight;
        
        g2.fillRect(topLeft, topTop, topWidth, topHeight);
        
        // Draw small details for aesthetics
        g2.setColor(new Color(220, 230, 255));
        g2.drawLine(topLeft+topWidth/3, topTop, topLeft+topWidth/3, topTop+topHeight);
        g2.drawLine(topLeft+2*topWidth/3, topTop, topLeft+2*topWidth/3, topTop+topHeight);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(REGULAR_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Clear any default text and ensure proper rendering
        field.setText("");
        
        return field;
    }
    
    private JPasswordField createModernPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(REGULAR_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Clear any default text and ensure proper rendering
        field.setText("");
        // Set echo character to standard bullet
        field.setEchoChar('\u2022');
        
        return field;
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
                    PRIMARY_COLOR.darker()
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
            
            // Override the preferred size to ensure button is fully visible
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(d.width, 150), 50);
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
                g2.setColor(PRIMARY_COLOR);  // Use primary color for border to make it stand out
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                
                // Paint text
                g2.setColor(PRIMARY_COLOR);  // Text in primary color for better visibility
                g2.setFont(BUTTON_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2);
                g2.dispose();
            }
            
            // Override the preferred size to ensure button is fully visible
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(d.width, 120), 45);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(PRIMARY_COLOR);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createRegistrationPanel() {
        // Create panel with same background as login
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                    0, 0, BACKGROUND_COLOR, 
                    0, h, new Color(235, 238, 250)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Reduced padding significantly

        // Main content panel with white background and shadow effect
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw subtle shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        contentPanel.setLayout(new BorderLayout(0, 5)); // Reduced vertical spacing
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 25, 15, 25)); // Reduced padding

        // Header with logo - smaller logo
        JPanel headerPanel = new JPanel(new BorderLayout(0, 5)); // Reduced spacing
        headerPanel.setOpaque(false);
        
        // Logo - smaller logo
        JLabel logoLabel = new JLabel(createChessLogo());
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(logoLabel, BorderLayout.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.SOUTH);
        
        // Back link in top corner
        JButton backButton = new JButton("← Back to login");
        backButton.setFont(REGULAR_FONT);
        backButton.setForeground(PRIMARY_COLOR);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setHorizontalAlignment(SwingConstants.LEFT);
        backButton.addActionListener(e -> cardLayout.show(cards, "login"));
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setText("<html><u>← Back to login</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setText("← Back to login");
            }
        });
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel without scroll pane
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3); // Reduced padding between form elements
        gbc.weightx = 1.0;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(REGULAR_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        regUsernameField = createModernTextField(15);
        formPanel.add(regUsernameField, gbc);
        
        // Full Name
        gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(REGULAR_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridy = 3;
        regNameField = createModernTextField(15);
        formPanel.add(regNameField, gbc);
        
        // Country
        gbc.gridy = 4;
        JLabel countryLabel = new JLabel("Country");
        countryLabel.setFont(REGULAR_FONT);
        countryLabel.setForeground(TEXT_COLOR);
        formPanel.add(countryLabel, gbc);
        
        gbc.gridy = 5;
        regCountryField = createModernTextField(15);
        formPanel.add(regCountryField, gbc);
        
        // Password
        gbc.gridy = 6;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(REGULAR_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridy = 7;
        regPasswordField = createModernPasswordField(15);
        formPanel.add(regPasswordField, gbc);
        
        // Confirm Password
        gbc.gridy = 8;
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(REGULAR_FONT);
        confirmLabel.setForeground(TEXT_COLOR);
        formPanel.add(confirmLabel, gbc);
        
        gbc.gridy = 9;
        regConfirmPasswordField = createModernPasswordField(15);
        formPanel.add(regConfirmPasswordField, gbc);
        
        // Role
        gbc.gridy = 10;
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(REGULAR_FONT);
        roleLabel.setForeground(TEXT_COLOR);
        formPanel.add(roleLabel, gbc);
        
        gbc.gridy = 11;
        String[] roles = {"Player", "Referee", "Admin"};
        regRoleComboBox = new JComboBox<>(roles);
        regRoleComboBox.setFont(REGULAR_FONT);
        regRoleComboBox.setBackground(FIELD_BACKGROUND);
        regRoleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        regRoleComboBox.setSelectedIndex(0); // Default to Player
        formPanel.add(regRoleComboBox, gbc);
        
        // Buttons
        gbc.gridy = 12;
        gbc.insets = new Insets(10, 3, 3, 3); // Less space before buttons
        
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
        buttonPanel.setOpaque(false);
        
        // Register button - full width
        JButton registerButton = createPrimaryButton("Create Account");
        registerButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45)); // Reduced from 50
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        registerButton.addActionListener(e -> handleRegistration());
        
        // Cancel button below
        JButton cancelButton = createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> cardLayout.show(cards, "login"));
        
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelPanel.setOpaque(false);
        cancelPanel.add(cancelButton);
        
        buttonPanel.add(registerButton, BorderLayout.NORTH);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.CENTER); // Reduced from 10
        buttonPanel.add(cancelPanel, BorderLayout.SOUTH);
        
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        formPanel.add(buttonPanel, gbc);
        
        // Directly add form panel to content panel - no scroll pane needed
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username and password cannot be empty",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = authenticate(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, 
                    "Login successful!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Launch main dashboard and close login screen
                this.dispose();
                SwingUtilities.invokeLater(() -> new DashboardGUI(user));

            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error during login: " + e.getMessage(),
                "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegistration() {
        String username = regUsernameField.getText().trim();
        String name = regNameField.getText().trim();
        String country = regCountryField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(regConfirmPasswordField.getPassword());
        int role = regRoleComboBox.getSelectedIndex(); // 0: Player, 1: Referee, 2: Admin
        
        // Enhanced Validation
        StringBuilder errors = new StringBuilder();
        
        // Check for empty fields
        if (username.isEmpty() || name.isEmpty() || country.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            errors.append("• All fields are required\n");
        }
        
        // Username validation - allow letters, numbers, and underscores, 3-20 chars
        if (!username.isEmpty() && !username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            errors.append("• Username must be 3-20 characters and contain only letters, numbers, and underscores\n");
        }
        
        // Name validation - allow letters, spaces, and hyphens
        if (!name.isEmpty() && !name.matches("^[a-zA-Z\\s-]{2,50}$")) {
            errors.append("• Name must contain only letters, spaces, and hyphens (2-50 characters)\n");
        }
        
        // Country validation - allow letters, spaces, and hyphens
        if (!country.isEmpty() && !country.matches("^[a-zA-Z\\s-]{2,50}$")) {
            errors.append("• Country must contain only letters, spaces, and hyphens (2-50 characters)\n");
        }
        
        // Password strength validation
        if (!password.isEmpty()) {
            // Password must be at least 8 characters
            if (password.length() < 8) {
                errors.append("• Password must be at least 8 characters long\n");
            }
            
            // Password must contain at least one uppercase letter
            if (!password.matches(".*[A-Z].*")) {
                errors.append("• Password must contain at least one uppercase letter\n");
            }
            
            // Password must contain at least one lowercase letter
            if (!password.matches(".*[a-z].*")) {
                errors.append("• Password must contain at least one lowercase letter\n");
            }
            
            // Password must contain at least one digit
            if (!password.matches(".*[0-9].*")) {
                errors.append("• Password must contain at least one digit\n");
            }
            
            // Password must contain at least one special character
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                errors.append("• Password must contain at least one special character\n");
            }
        }
        
        // Check if passwords match
        if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            errors.append("• Passwords do not match\n");
        }
        
        // Show errors if any
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Please fix the following errors:\n" + errors.toString(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username exists
            if (userExists(username)) {
                JOptionPane.showMessageDialog(this, 
                    "Username already exists",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get next available ID
            int nextId = getNextUserId();
            
            // Hash the password
            String passwordHash = User.hashPassword(password);
            
            // Save user to file
            File usersFile = new File("users.txt");
            try (FileWriter fw = new FileWriter(usersFile, true);
                 BufferedWriter writer = new BufferedWriter(fw)) {
                // Format: username,passwordHash,role,name,country,id
                writer.write(username + "," + passwordHash + "," + role + "," + name + "," + country + "," + nextId);
                writer.newLine();
            }

            File statsFile = new File("playerStats.txt");
            try (FileWriter fw = new FileWriter(statsFile, true);
                 BufferedWriter writer = new BufferedWriter(fw)) {
                writer.write(username + ", 0, 0, 0, 1200");
                writer.newLine();
            }



            
            JOptionPane.showMessageDialog(this, 
                "Registration successful!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Switch back to login panel
            cardLayout.show(cards, "login");
            
            // Set the username in the login field for convenience
            usernameField.setText(username);
            passwordField.setText("");
            
            // Clear registration fields
            regUsernameField.setText("");
            regNameField.setText("");
            regCountryField.setText("");
            regPasswordField.setText("");
            regConfirmPasswordField.setText("");
            regRoleComboBox.setSelectedIndex(0);
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error during registration: " + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private User authenticate(String username, String password) throws IOException {
        File usersFile = new File("users.txt");
        if (!usersFile.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals(username)) {
                    String storedHash = parts[1];
                    int role = Integer.parseInt(parts[2]);
                    String name = parts[3];
                    String country = parts[4];
                    int id = Integer.parseInt(parts[5]);
                    
                    // Verify password
                    if (User.verifyPasswordHash(password, storedHash)) {
                        // Create appropriate user type based on role
                        switch (role) {
                            case 0: // Player
                                return new Player(name, 1200, country, username, storedHash, id); // Default rating 1200
                            case 1: // Referee
                                return new Referee(id, name, country, username, storedHash);
                            case 2: // Admin
                                return new Admin(id, name, country, username, storedHash, 2);
                            default:
                                return new User(id, name, country, username, storedHash, role);
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean userExists(String username) throws IOException {
        File usersFile = new File("users.txt");
        if (!usersFile.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private int getNextUserId() throws IOException {
        File usersFile = new File("users.txt");
        if (!usersFile.exists()) {
            return 1;
        }
        
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        int id = Integer.parseInt(parts[5]);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
        
        return maxId + 1;
    }
    
    private void handleGuestLogin() {
        // Create a guest user with limited access
        User guestUser = new User(0, "Guest User", "Global", "guest", "", 0);
        this.dispose();
        SwingUtilities.invokeLater(() -> new DashboardGUI(guestUser));
    }
    
    public static void main(String[] args) {
        try {
            // Set look and feel to system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
} 
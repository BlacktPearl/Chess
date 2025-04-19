import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TournamentGUI extends JFrame implements ActionListener {

    private Tournament tournament;
    private JTextArea detailsArea;
    private JButton startButton, advanceButton, viewLeaderboardButton, signInButton, signOutButton, createUserButton;
    private JTextField usernameField, passwordField;
    private User currentUser;

    public TournamentGUI(Tournament tournament) {
        this.tournament = tournament;

        setTitle("Chess Tournament");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start Tournament");
        advanceButton = new JButton("Advance Round");
        viewLeaderboardButton = new JButton("View Leaderboard");

        startButton.addActionListener(this);
        advanceButton.addActionListener(this);
        viewLeaderboardButton.addActionListener(this);

        buttonPanel.add(startButton);
        buttonPanel.add(advanceButton);
        buttonPanel.add(viewLeaderboardButton);

        JPanel signInPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        signInButton = new JButton("Sign In");
        signOutButton = new JButton("Sign Out");

        signInButton.addActionListener(this);
        signOutButton.addActionListener(this);

        signInPanel.add(new JLabel("Username:"));
        signInPanel.add(usernameField);
        signInPanel.add(new JLabel("Password:"));
        signInPanel.add(passwordField);
        signInPanel.add(signInButton);
        signInPanel.add(signOutButton);

        JButton createUserButton = new JButton("Create User");
        createUserButton.addActionListener(this);
        signInPanel.add(createUserButton);

        add(signInPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        updateDetails();
        setVisible(true);
        updateButtonVisibility();
    }

    private void updateDetails() {
        detailsArea.setText(tournament.getTournamentDetails());
    }

    private void updateButtonVisibility() {
        startButton.setEnabled(currentUser != null);
        advanceButton.setEnabled(currentUser != null);
        viewLeaderboardButton.setEnabled(currentUser != null);
        signOutButton.setEnabled(currentUser != null);
        signInButton.setEnabled(currentUser == null);
        usernameField.setEnabled(currentUser == null);
        passwordField.setEnabled(currentUser == null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            try {
                tournament.startTournament();
                updateDetails();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == advanceButton) {
            boolean advanced = tournament.advanceRound();
            if (advanced) {
                System.out.println("Advanced to next round");
            } else {
                System.out.println("Cannot advance round");
            }
            updateDetails();
        } else if (e.getSource() == viewLeaderboardButton) {
            // Display leaderboard (Placeholder)
            System.out.println("View Leaderboard button clicked");
            JOptionPane.showMessageDialog(this, "Leaderboard functionality not yet implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == signInButton) {
            String username = usernameField.getText();
            String password = new String(((JPasswordField) passwordField).getPassword());

            // Find user (Placeholder)
            User user = findUser(username);

            if (user != null && user.verifyPassword(password)) {
                currentUser = user;
                updateDetails();
                updateButtonVisibility();
                JOptionPane.showMessageDialog(this, "Sign in successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == signOutButton) {
            currentUser = null;
            updateDetails();
            updateButtonVisibility();
            JOptionPane.showMessageDialog(this, "Signed out successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }  else if (e.getSource() == createUserButton) {
            createUser();
        }
    }

    private void handleCreateUser() {
        createUser();
    }

    // Placeholder method to find user
    private User findUser(String username) {
        System.out.println("Finding user: " + username);
        // In real implementation, this would search a database or list of users
        if (username.equals("admin")) {
            System.out.println("Admin user found!");
            return new Admin(1, "Admin User", "USA");
        }
        System.out.println("User not found.");
        return null;
    }

    private void createUser() {
        String idStr = JOptionPane.showInputDialog(this, "Enter User ID:");
        String name = JOptionPane.showInputDialog(this, "Enter Name:");
        String country = JOptionPane.showInputDialog(this, "Enter Country:");
        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");

        try {
            int id = Integer.parseInt(idStr);
            User newUser = User.createUser(id, name, country, username, password);
            if (newUser != null) {
                JOptionPane.showMessageDialog(this, "User created successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid User ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Tournament tournament = new Tournament(1, "Chess Championship", "New York",
                "2025-05-01", "90+30", 5);
        tournament.addPlayer(new Player(101, "Alice", "USA", 1500));
        tournament.addPlayer(new Player(102, "Bob", "UK", 1600));
        tournament.addPlayer(new Player(103, "Charlie", "Germany", 1700));

        SwingUtilities.invokeLater(() -> new TournamentGUI(tournament));
    }
}

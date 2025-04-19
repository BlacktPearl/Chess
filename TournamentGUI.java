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

        createUserButton = new JButton("Create User");
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
            JOptionPane.showMessageDialog(this, tournament.getLeaderboard(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == signInButton) {
            String username = usernameField.getText();
            String password = new String(((JPasswordField) passwordField).getPassword());

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
        } else if (e.getSource() == createUserButton) {
            createUser();
        }
    }

    private User findUser(String username) {
        System.out.println("Finding user: " + username);
        if (username.equals("admin")) {
            return new Admin(1, "Admin User", "USA");
        }
        return null;
    }

    private void createUser() {
        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        String ratingStr = JOptionPane.showInputDialog(this, "Enter Rating:");
        String country = JOptionPane.showInputDialog(this, "Enter Country:");

        try {
            int rating = Integer.parseInt(ratingStr);
            Player newPlayer = new Player(username, rating, country);
            tournament.addPlayer(newPlayer);
            JOptionPane.showMessageDialog(this, "Player created successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid rating.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Tournament tournament = new Tournament(1, "Chess Championship", "New York",
                "2025-05-01", "90+30", 5);

        tournament.addPlayer(new Player("Alice", 1500, "USA"));
        tournament.addPlayer(new Player("Bob", 1600, "UK"));
        tournament.addPlayer(new Player("Charlie", 1700, "Germany"));

        SwingUtilities.invokeLater(() -> new TournamentGUI(tournament));
    }
}




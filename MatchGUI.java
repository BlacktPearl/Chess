import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Map;

public class MatchGUI extends JFrame {
    private Match match;
    private JTextArea moveHistoryArea;
    private JLabel statusLabel, winnerLabel;
    private JLabel whiteClock, blackClock;
    private ChessBoardPanel boardPanel;
    private JLabel selectedSquare;
    private String selectedPosition = null;
    private Timer clockTimer;

    public MatchGUI(Match match) {
        this.match = match;
        setTitle("Chess Match Viewer");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel playerPanel = new JPanel(new GridLayout(2, 2));
        playerPanel.add(new JLabel("Player 1 (White): " + match.getPlayer1().getUsername()));
        playerPanel.add(new JLabel("Player 2 (Black): " + match.getPlayer2().getUsername()));

        whiteClock = new JLabel("White Time: 00:10:00");
        blackClock = new JLabel("Black Time: 00:10:00");
        playerPanel.add(whiteClock);
        playerPanel.add(blackClock);

        add(playerPanel, BorderLayout.NORTH);

        boardPanel = new ChessBoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        moveHistoryArea = new JTextArea(10, 20);
        moveHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        add(scrollPane, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        statusLabel = new JLabel("Status: " + match.getStatus());
        winnerLabel = new JLabel("Winner: None");
        bottomPanel.add(statusLabel);
        bottomPanel.add(winnerLabel);

        JButton resignButton = new JButton("Resign");
        resignButton.addActionListener(e -> handleResign());
        bottomPanel.add(resignButton);

        add(bottomPanel, BorderLayout.SOUTH);

        startClockTimer();
    }

    private void handleResign() {
        Player resigner = match.getCurrentPlayer();
        Player winner = (resigner == match.getPlayer1()) ? match.getPlayer2() : match.getPlayer1();
        match.setWinner(winner);
        statusLabel.setText("Status: " + match.getStatus());
        winnerLabel.setText("Winner: " + winner.getUsername());
    }

    private void startClockTimer() {
        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                long w = match.getWhiteTimeLeft();
                long b = match.getBlackTimeLeft();
                whiteClock.setText("White Time: " + formatMillis(w));
                blackClock.setText("Black Time: " + formatMillis(b));
                statusLabel.setText("Status: " + match.getStatus());

                if (!match.getStatus().equals("Ongoing")) {
                    clockTimer.cancel();
                }
            }
        }, 0, 1000);
    }

    private String formatMillis(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long remSec = seconds % 60;
        return String.format("%02d:%02d", minutes, remSec);
    }

    private class ChessBoardPanel extends JPanel {
        private JLabel[][] squares = new JLabel[8][8];
        private Map<String, String> piecePositions = new HashMap<>();

        public ChessBoardPanel() {
            setLayout(new GridLayout(8, 8));
            initBoard();
            setupPieces();
        }

        private void initBoard() {
            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 8; col++) {
                    JLabel square = new JLabel("", SwingConstants.CENTER);
                    square.setFont(new Font("Serif", Font.BOLD, 32));
                    square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    square.setOpaque(true);
                    square.setBackground((row + col) % 2 == 0 ? Color.PINK : Color.BLACK);
                    square.setForeground((row + col) % 2 == 0 ? Color.BLACK : Color.PINK);
                    int finalRow = row;
                    int finalCol = col;
                    square.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            handleClick(finalRow, finalCol, square);
                        }
                    });
                    squares[row][col] = square;
                    add(square);
                }
            }
        }

        private void setupPieces() {
            String[] backRow = {"R", "N", "B", "Q", "K", "B", "N", "R"};
            for (int i = 0; i < 8; i++) {
                setPiece(i, 0, "w" + backRow[i]);
                setPiece(i, 1, "wP");
                setPiece(i, 6, "bP");
                setPiece(i, 7, "b" + backRow[i]);
            }
        }

        private void setPiece(int col, int row, String piece) {
            String symbol = getSymbol(piece);
            squares[row][col].setText(symbol);
            piecePositions.put(posKey(col, row), piece);
        }

        private String getSymbol(String piece) {
            switch (piece) {
                case "wP": return "♙";
                case "wR": return "♖";
                case "wN": return "♘";
                case "wB": return "♗";
                case "wQ": return "♕";
                case "wK": return "♔";
                case "bP": return "♟";
                case "bR": return "♜";
                case "bN": return "♞";
                case "bB": return "♝";
                case "bQ": return "♛";
                case "bK": return "♚";
                default: return "";
            }
        }

        private void handleClick(int row, int col, JLabel square) {
            String pos = "" + (char) ('a' + col) + (row + 1);
            if (selectedPosition == null) {
                String piece = piecePositions.get(pos);
                if (piece == null) return;

                boolean isWhite = piece.startsWith("w");
                if ((isWhite && !match.isWhiteTurn()) || (!isWhite && match.isWhiteTurn())) {
                    JOptionPane.showMessageDialog(MatchGUI.this, "It's not your turn!");
                    return;
                }

                selectedPosition = pos;
                square.setBackground(Color.YELLOW);
                selectedSquare = square;
            } else {
                String from = selectedPosition;
                String to = pos;
                String pieceCode = piecePositions.get(from);
                Player currentPlayer = match.getCurrentPlayer();

                if (Referee.isLegalMove(from, to, pieceCode, piecePositions)) {
                    Move move = new Move(from, to, pieceCode.substring(1), java.time.LocalDateTime.now().toString(), true, currentPlayer);
                    match.recordMove(move);
                    updateBoard(from, to, pieceCode);
                    moveHistoryArea.append(currentPlayer.getUsername() + ": " + move + "\n");
                    match.toggleTurn();
                } else {
                    Move illegalMove = new Move(from, to, pieceCode.substring(1), java.time.LocalDateTime.now().toString(), false, currentPlayer);
                    match.recordMove(illegalMove);
                    JOptionPane.showMessageDialog(MatchGUI.this, "Illegal move by " + currentPlayer.getUsername());
                    moveHistoryArea.append("[ILLEGAL] " + currentPlayer.getUsername() + ": " + illegalMove + "\n");
                }

                selectedSquare.setBackground(getOriginalColor(from));
                selectedPosition = null;
                selectedSquare = null;

                if (match.getWinner() != null) {
                    winnerLabel.setText("Winner: " + match.getWinner().getUsername());
                }
            }
        }

        private void updateBoard(String from, String to, String pieceCode) {
            int fromCol = from.charAt(0) - 'a';
            int fromRow = from.charAt(1) - '1';
            int toCol = to.charAt(0) - 'a';
            int toRow = to.charAt(1) - '1';
            squares[fromRow][fromCol].setText("");
            squares[toRow][toCol].setText(getSymbol(pieceCode));
            piecePositions.remove(from);
            piecePositions.put(to, pieceCode);
        }

        private String posKey(int col, int row) {
            return "" + (char) ('a' + col) + (row + 1);
        }

        private Color getOriginalColor(String pos) {
            int col = pos.charAt(0) - 'a';
            int row = pos.charAt(1) - '1';
            return (row + col) % 2 == 0 ? Color.PINK : Color.BLACK;
        }
    }

    public static User showRegistrationDialog() {
        JTextField name = new JTextField();
        JTextField country = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        String[] roles = {"Player", "Admin", "Referee"};
        JComboBox<String> roleList = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Country:"));
        panel.add(country);
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel("Role:"));
        panel.add(roleList);

        int result = JOptionPane.showConfirmDialog(null, panel, "Register User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selectedRole = (String) roleList.getSelectedItem();
            int roleId = 0;
            if (selectedRole.equals("Admin")) {
                roleId = 1;
            } else if (selectedRole.equals("Referee")) {
                roleId = 2;
            }
            // Generate a unique ID for the user
            int userId = (int) (Math.random() * 1000); // Replace with a better ID generation strategy

            User user = User.createUser(userId, name.getText(), country.getText(), username.getText(), new String(password.getPassword()), roleId);
            return user;
        }
        return null;
    }

    public static User showLoginDialog() {
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            User user = new User(0, "", "", username.getText(), "", 0); // Dummy user object
            if (user.login(username.getText(), new String(password.getPassword()))) {
                return user;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = showLoginDialog();
            if (user == null) {
                user = showRegistrationDialog();
                if (user == null) return;
            }

            String timeControl = JOptionPane.showInputDialog(null, "Enter time control (e.g., 10|0):", "10|0");
            if (timeControl == null || timeControl.trim().isEmpty()) {
                timeControl = "10|0"; // default fallback
            }

            // Create two Player objects for testing purposes
            Player player1 = new Player(user.getName(), 1200, user.getCountry());
            Player player2 = new Player("Opponent", 1200, "USA");

            Match match = new Match(1, player1, player2, timeControl);
            match.startMatch();

            int role = user.getRole();
            if (role == 0) {
                // Player
                // For now, show training board for all players
                SwingUtilities.invokeLater(() -> {
                    TrainingBoardGUI trainingBoardGUI = new TrainingBoardGUI();
                    trainingBoardGUI.setVisible(true);
                });
            } else if (role == 1) {
                // Admin
                showAdminScreen();
            } else if (role == 2) {
                // Referee
                showRefereeScreen();
            }
        });
    }

    private static void showAdminScreen() {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }

    private static void showRefereeScreen() {
        SwingUtilities.invokeLater(() -> {
            RefereeGUI refereeGUI = new RefereeGUI();
            refereeGUI.setVisible(true);
        });
    }
}

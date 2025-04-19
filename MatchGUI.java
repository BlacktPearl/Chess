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

    public static Player[] showRegistrationDialog() {
        JTextField name1 = new JTextField();
        JTextField country1 = new JTextField();
        JTextField rating1 = new JTextField();

        JTextField name2 = new JTextField();
        JTextField country2 = new JTextField();
        JTextField rating2 = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Player 1 (White) Username:"));
        panel.add(name1);
        panel.add(new JLabel("Country:"));
        panel.add(country1);
        panel.add(new JLabel("Rating:"));
        panel.add(rating1);

        panel.add(new JLabel("Player 2 (Black) Username:"));
        panel.add(name2);
        panel.add(new JLabel("Country:"));
        panel.add(country2);
        panel.add(new JLabel("Rating:"));
        panel.add(rating2);

        int result = JOptionPane.showConfirmDialog(null, panel, "Register Players",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Player p1 = new Player(name1.getText(), Integer.parseInt(rating1.getText()), country1.getText());
                Player p2 = new Player(name2.getText(), Integer.parseInt(rating2.getText()), country2.getText());
                return new Player[]{p1, p2};
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid rating input. Please use numbers.");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Player[] players = showRegistrationDialog();
            if (players == null) return;

            String timeControl = JOptionPane.showInputDialog(null, "Enter time control (e.g., 10|0):", "10|0");
            if (timeControl == null || timeControl.trim().isEmpty()) {
                timeControl = "10|0"; // default fallback
            }

            Match match = new Match(1, players[0], players[1], timeControl);
            match.startMatch();

            MatchGUI gui = new MatchGUI(match);
            gui.setVisible(true);
        });
    }
}



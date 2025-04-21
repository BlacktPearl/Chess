import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchGUI extends JFrame {
    private Match match;
    private JTextArea moveHistoryArea;
    private JLabel statusLabel, winnerLabel;
    private JLabel whiteClock, blackClock;
    private ChessBoardPanel boardPanel;
    private JLabel selectedSquare;
    private String selectedPosition = null;
    private Timer clockTimer;
    private JPanel sidePanel;
    private JPanel controlPanel;
    private JButton offerDrawButton;
    private JButton resignButton;
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private static final Color HEADER_COLOR = new Color(50, 50, 75);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color WHITE_PLAYER_COLOR = new Color(245, 245, 245);
    private static final Color BLACK_PLAYER_COLOR = new Color(220, 220, 220);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color RESIGN_BUTTON_COLOR = new Color(180, 70, 70);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font CLOCK_FONT = new Font("Monospaced", Font.BOLD, 18);

    public MatchGUI(Match match) {
        this.match = match;
        setTitle("Chess Match - " + match.getPlayer1().getUsername() + " vs " + match.getPlayer2().getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Don't exit app, just close match window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Set a better background for the main frame
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create panels with margins
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Add a header panel with title
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create player info panel
        createPlayerInfoPanel(mainPanel);
        
        // Create center panel with board
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Create board panel
        boardPanel = new ChessBoardPanel();
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Create side panel for move history
        createSidePanel(mainPanel);
        
        // Create bottom panel
        createBottomPanel(mainPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Apply custom styling to all components
        SwingUtilities.invokeLater(this::applyCustomStyling);
        
        // Start the clock timer
        startClockTimer();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel titleLabel = new JLabel("Chess Match", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private void createPlayerInfoPanel(JPanel mainPanel) {
        JPanel playerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        playerPanel.setOpaque(false);
        
        // White player panel
        JPanel whitePlayerPanel = createPlayerSubPanel(match.getPlayer1(), true);
        
        // Black player panel
        JPanel blackPlayerPanel = createPlayerSubPanel(match.getPlayer2(), false);
        
        playerPanel.add(whitePlayerPanel);
        playerPanel.add(blackPlayerPanel);
        
        mainPanel.add(playerPanel, BorderLayout.NORTH);
    }
    
    private JPanel createPlayerSubPanel(Player player, boolean isWhite) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Create a gradient background
        panel.setBackground(isWhite ? WHITE_PLAYER_COLOR : BLACK_PLAYER_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Player name and rating
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        namePanel.setOpaque(false);
        
        JLabel playerIcon = new JLabel(isWhite ? "♔ " : "♚ ");
        playerIcon.setFont(new Font("DejaVu Sans", Font.PLAIN, 20));
        playerIcon.setForeground(new Color(70, 70, 70));
        namePanel.add(playerIcon);
        
        JLabel nameLabel = new JLabel(player.getUsername() + " (" + player.getRating() + ")");
        nameLabel.setFont(TITLE_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        namePanel.add(nameLabel);
        
        panel.add(namePanel);
        
        // Add some spacing
        panel.add(Box.createVerticalStrut(5));
        
        // Country with flag emoji representation
        String flagEmoji = getFlagEmoji(player.getCountry());
        JLabel countryLabel = new JLabel(flagEmoji + " " + player.getCountry());
        countryLabel.setFont(REGULAR_FONT);
        countryLabel.setForeground(TEXT_COLOR);
        countryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(countryLabel);
        
        // Add some spacing
        panel.add(Box.createVerticalStrut(5));
        
        // Clock
        JLabel clockLabel;
        if (isWhite) {
            whiteClock = new JLabel("Time: 10:00");
            clockLabel = whiteClock;
        } else {
            blackClock = new JLabel("Time: 10:00");
            clockLabel = blackClock;
        }
        clockLabel.setFont(CLOCK_FONT);
        clockLabel.setForeground(new Color(50, 50, 50));
        clockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(clockLabel);
        
        // Add turn indicator
        if (isWhite && match.isWhiteTurn() || !isWhite && !match.isWhiteTurn()) {
            JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            turnPanel.setOpaque(false);
            
            JLabel turnIndicator = new JLabel("●");
            turnIndicator.setForeground(new Color(0, 150, 0));
            turnIndicator.setFont(new Font("Arial", Font.BOLD, 14));
            turnPanel.add(turnIndicator);
            
            JLabel turnLabel = new JLabel(" Your turn");
            turnLabel.setForeground(new Color(0, 150, 0));
            turnLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            turnPanel.add(turnLabel);
            
            panel.add(turnPanel);
        }
        
        return panel;
    }
    
    private String getFlagEmoji(String country) {
        // Map some common countries to flag emojis
        switch (country.toLowerCase()) {
            case "usa": case "united states": return "🇺🇸";
            case "uk": case "united kingdom": return "🇬🇧";
            case "france": return "🇫🇷";
            case "germany": return "🇩🇪";
            case "italy": return "🇮🇹";
            case "spain": return "🇪🇸";
            case "russia": return "🇷🇺";
            case "china": return "🇨🇳";
            case "japan": return "🇯🇵";
            case "brazil": return "🇧🇷";
            case "canada": return "🇨🇦";
            case "india": return "🇮🇳";
            case "australia": return "🇦🇺";
            default: return "🌐";
        }
    }
    
    private void createSidePanel(JPanel mainPanel) {
        sidePanel = new JPanel(new BorderLayout(0, 10));
        sidePanel.setPreferredSize(new Dimension(220, 0));
        sidePanel.setBackground(BACKGROUND_COLOR);
        
        // Move history title
        JPanel historyHeaderPanel = new JPanel();
        historyHeaderPanel.setBackground(HEADER_COLOR);
        historyHeaderPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        
        JLabel historyLabel = new JLabel("Move History", JLabel.CENTER);
        historyLabel.setFont(TITLE_FONT);
        historyLabel.setForeground(Color.WHITE);
        historyHeaderPanel.add(historyLabel);
        
        sidePanel.add(historyHeaderPanel, BorderLayout.NORTH);
        
        // Move history area
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moveHistoryArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        moveHistoryArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        sidePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel for game actions
        createControlPanel();
        sidePanel.add(controlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(sidePanel, BorderLayout.EAST);
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Offer draw button
        offerDrawButton = createStyledButton("Offer Draw", BUTTON_COLOR);
        offerDrawButton.addActionListener(e -> handleDrawOffer());
        controlPanel.add(offerDrawButton);
        
        // Resign button
        resignButton = createStyledButton("Resign", RESIGN_BUTTON_COLOR);
        resignButton.addActionListener(e -> handleResign());
        controlPanel.add(resignButton);
    }
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brighten(baseColor, 0.2f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        
        return button;
    }
    
    private Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
    }
    
    private void createBottomPanel(JPanel mainPanel) {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        statusPanel.setBackground(BACKGROUND_COLOR);
        
        statusLabel = new JLabel("Status: " + match.getStatus());
        statusLabel.setFont(REGULAR_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        
        winnerLabel = new JLabel("Winner: None");
        winnerLabel.setFont(REGULAR_FONT);
        winnerLabel.setForeground(TEXT_COLOR);
        
        statusPanel.add(statusLabel);
        statusPanel.add(winnerLabel);
        
        bottomPanel.add(statusPanel, BorderLayout.WEST);
        
        // Close button
        JButton closeButton = createStyledButton("Close Match", new Color(100, 100, 100));
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void applyCustomStyling() {
        // Apply consistent UI styling to all components
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", REGULAR_FONT);
    }

    private void handleDrawOffer() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Would you like to offer a draw to your opponent?",
            "Draw Offer",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            // In a real game, this would send the offer to the opponent
            // For demo purposes, just simulate opponent's response
            response = JOptionPane.showConfirmDialog(
                this,
                "Your opponent has been offered a draw. Accept?",
                "Draw Offer",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response == JOptionPane.YES_OPTION) {
                // Accept draw
                match.setStatus("Draw by agreement");
                statusLabel.setText("Status: Draw by agreement");
                winnerLabel.setText("Winner: None (Draw)");
                
                // Disable game controls
                offerDrawButton.setEnabled(false);
                resignButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Draw offer declined.");
            }
        }
    }

    private void handleResign() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to resign this match?",
            "Confirm Resignation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
        Player resigner = match.getCurrentPlayer();
        Player winner = (resigner == match.getPlayer1()) ? match.getPlayer2() : match.getPlayer1();
        match.setWinner(winner);
        statusLabel.setText("Status: " + match.getStatus());
            winnerLabel.setText("Winner: " + winner.getUsername() + " (by resignation)");
            
            // Disable game controls
            offerDrawButton.setEnabled(false);
            resignButton.setEnabled(false);
        }
    }

    private void startClockTimer() {
        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                long w = match.getWhiteTimeLeft();
                long b = match.getBlackTimeLeft();
                    whiteClock.setText("Time: " + formatMillis(w));
                    blackClock.setText("Time: " + formatMillis(b));
                statusLabel.setText("Status: " + match.getStatus());

                if (!match.getStatus().equals("Ongoing")) {
                    clockTimer.cancel();
                }
                });
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
        private static final int BOARD_SIZE = 480; // Adjust as needed for your UI
        private int squareSize;

        public ChessBoardPanel() {
            squareSize = BOARD_SIZE / 8;
            setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setLayout(new GridLayout(8, 8));
            initBoard();
            setupPieces();
        }

        private void initBoard() {
            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 8; col++) {
                    JLabel square = new JLabel("", SwingConstants.CENTER);
                    square.setFont(new Font("DejaVu Sans", Font.BOLD, 36));
                    square.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                    square.setOpaque(true);
                    
                    // Use the standard chess board colors
                    Color squareColor = (row + col) % 2 == 0 ? 
                        ChessBoard.LIGHT_SQUARE_COLOR : ChessBoard.DARK_SQUARE_COLOR;
                    square.setBackground(squareColor);
                    
                    // Add rank and file labels with better styling
                    if (col == 0) {
                        JLabel rankLabel = new JLabel("" + (row + 1), JLabel.LEFT);
                        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
                        rankLabel.setForeground((row + col) % 2 == 0 ? 
                            new Color(120, 80, 40) : new Color(240, 220, 180));
                        square.setLayout(new BorderLayout());
                        square.add(rankLabel, BorderLayout.WEST);
                    }
                    
                    if (row == 0) {
                        JLabel fileLabel = new JLabel("" + (char)('a' + col), JLabel.RIGHT);
                        fileLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
                        fileLabel.setForeground((row + col) % 2 == 0 ? 
                            new Color(120, 80, 40) : new Color(240, 220, 180));
                        if (square.getLayout() == null) {
                            square.setLayout(new BorderLayout());
                        }
                        square.add(fileLabel, BorderLayout.SOUTH);
                    }
                    
                    int finalRow = row;
                    int finalCol = col;
                    square.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            handleClick(finalRow, finalCol, square);
                        }
                        
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            // Highlight square on hover if it's not selected
                            if (square != selectedSquare) {
                                square.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 0, 150), 2));
                            }
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            // Remove highlight when mouse leaves
                            if (square != selectedSquare) {
                                square.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                            }
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
            String symbol = ChessBoard.getUnicodeSymbol(piece);
            squares[row][col].setText(symbol);
            piecePositions.put(posKey(col, row), piece);
        }

        private void handleClick(int row, int col, JLabel square) {
            String pos = "" + (char) ('a' + col) + (row + 1);
            
            // Check if game is still ongoing
            if (!match.getStatus().equals("Ongoing")) {
                JOptionPane.showMessageDialog(MatchGUI.this, "Game is over!");
                return;
            }
            
            if (selectedPosition == null) {
                String piece = piecePositions.get(pos);
                if (piece == null) return;

                boolean isWhite = piece.startsWith("w");
                if ((isWhite && !match.isWhiteTurn()) || (!isWhite && match.isWhiteTurn())) {
                    JOptionPane.showMessageDialog(MatchGUI.this, "It's not your turn!");
                    return;
                }

                selectedPosition = pos;
                square.setBackground(ChessBoard.HIGHLIGHT_COLOR);
                selectedSquare = square;
            } else {
                String from = selectedPosition;
                String to = pos;
                String pieceCode = piecePositions.get(from);
                
                // If clicking the same square, deselect
                if (from.equals(to)) {
                    selectedSquare.setBackground(ChessBoard.getOriginalColor(from));
                    selectedPosition = null;
                    selectedSquare = null;
                    return;
                }
                
                Player currentPlayer = match.getCurrentPlayer();

                if (Referee.isLegalMove(from, to, pieceCode, piecePositions)) {
                    Move move = new Move(from, to, pieceCode.substring(1), java.time.LocalDateTime.now().toString(), true, currentPlayer);
                    match.recordMove(move);
                    updateBoard(from, to, pieceCode);
                    
                    // Format move history with numbers and timestamps
                    int moveNumber = match.getMoveHistory().size();
                    String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    String moveText = String.format("%d. %s: %s → %s [%s]%n", 
                        moveNumber, 
                        currentPlayer.getUsername(),
                        from, to,
                        timestamp);
                    
                    moveHistoryArea.append(moveText);
                    match.toggleTurn();
                    
                    // Check for game end conditions
                    if (isCheckmate(match.isWhiteTurn())) {
                        Player winner = match.isWhiteTurn() ? match.getPlayer2() : match.getPlayer1();
                        match.setWinner(winner);
                        statusLabel.setText("Status: Checkmate");
                        winnerLabel.setText("Winner: " + winner.getUsername() + " (by checkmate)");
                        JOptionPane.showMessageDialog(MatchGUI.this, "Checkmate! " + winner.getUsername() + " wins!");
                        offerDrawButton.setEnabled(false);
                        resignButton.setEnabled(false);
                    } else if (isCheck(match.isWhiteTurn())) {
                        JOptionPane.showMessageDialog(MatchGUI.this, "Check!");
                    }
                } else {
                    Move illegalMove = new Move(from, to, pieceCode.substring(1), java.time.LocalDateTime.now().toString(), false, currentPlayer);
                    match.recordMove(illegalMove);
                    JOptionPane.showMessageDialog(MatchGUI.this, "Illegal move by " + currentPlayer.getUsername());
                    moveHistoryArea.append("[ILLEGAL] " + currentPlayer.getUsername() + ": " + illegalMove + "\n");
                }

                selectedSquare.setBackground(ChessBoard.getOriginalColor(from));
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
            
            // Check for captures
            String capturedPiece = piecePositions.get(to);
            
            // Check for pawn promotion
            if (pieceCode.endsWith("P")) {
                // White pawn reaching 8th rank or black pawn reaching 1st rank
                if ((pieceCode.startsWith("w") && toRow == 7) || 
                    (pieceCode.startsWith("b") && toRow == 0)) {
                    pieceCode = handlePawnPromotion(pieceCode);
                }
            }
            
            // Move the piece
            squares[fromRow][fromCol].setText("");
            squares[toRow][toCol].setText(ChessBoard.getUnicodeSymbol(pieceCode));
            
            // Update the piecePositions map
            piecePositions.remove(from);
            piecePositions.put(to, pieceCode);
            
            // Play a sound for the move (if in real app)
            // playSound(capturedPiece != null ? "capture.wav" : "move.wav");
        }
        
        private String handlePawnPromotion(String pieceCode) {
            String color = pieceCode.substring(0, 1); // "w" or "b"
            
            // Create custom promotion dialog with piece icons
            JDialog promotionDialog = new JDialog(MatchGUI.this, "Promote Pawn", true);
            promotionDialog.setLayout(new GridLayout(1, 4));
            
            // Create piece buttons with chess piece icons
            String[] pieces = {"Q", "R", "B", "N"};
            String[] pieceNames = {"Queen", "Rook", "Bishop", "Knight"};
            JButton[] buttons = new JButton[4];
            
            // Use array to store the selected piece (allows modification from lambda)
            final String[] selectedPiece = {pieceCode};
            
            for (int i = 0; i < pieces.length; i++) {
                final String piece = pieces[i];
                buttons[i] = new JButton(ChessBoard.getUnicodeSymbol(color + piece));
                buttons[i].setFont(new Font("DejaVu Sans", Font.PLAIN, 36));
                buttons[i].setToolTipText(pieceNames[i]);
                buttons[i].setFocusPainted(false);
                buttons[i].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                buttons[i].addActionListener(e -> {
                    selectedPiece[0] = color + piece;
                    promotionDialog.dispose();
                });
                
                promotionDialog.add(buttons[i]);
            }
            
            // Set dialog properties
            promotionDialog.pack();
            promotionDialog.setLocationRelativeTo(MatchGUI.this);
            promotionDialog.setResizable(false);
            promotionDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            promotionDialog.setVisible(true);
            
            return selectedPiece[0];
        }

        private String posKey(int col, int row) {
            return "" + (char)('a' + col) + (row + 1);
        }
        
        private boolean isCheck(boolean isWhiteTurn) {
            // Find the king
            String kingCode = isWhiteTurn ? "wK" : "bK";
            String kingPos = null;
            
            // Create a copy of the map for safe iteration
            Map<String, String> positionsCopy = new HashMap<>(piecePositions);
            
            for (Map.Entry<String, String> entry : positionsCopy.entrySet()) {
                if (entry.getValue().equals(kingCode)) {
                    kingPos = entry.getKey();
                    break;
                }
            }
            
            if (kingPos == null) return false;
            
            // Check if any opponent piece can capture the king
            char opponentColor = isWhiteTurn ? 'b' : 'w';
            
            for (Map.Entry<String, String> entry : positionsCopy.entrySet()) {
                String pieceCode = entry.getValue();
                String piecePos = entry.getKey();
                
                if (pieceCode.charAt(0) == opponentColor) {
                    if (Referee.isLegalMove(piecePos, kingPos, pieceCode, piecePositions)) {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        private boolean isCheckmate(boolean isWhiteTurn) {
            // If not in check, can't be checkmate
            if (!isCheck(isWhiteTurn)) return false;
            
            // Find all of the player's pieces
            char playerColor = isWhiteTurn ? 'w' : 'b';
            
            // Create a copy of the map for safe iteration
            Map<String, String> positionsCopy = new HashMap<>(piecePositions);
            
            // For each piece, try all possible moves
            for (Map.Entry<String, String> entry : positionsCopy.entrySet()) {
                String pieceCode = entry.getValue();
                String piecePos = entry.getKey();
                
                if (pieceCode.charAt(0) == playerColor) {
                    // Try moving to every square
                    for (int row = 0; row < 8; row++) {
                        for (int col = 0; col < 8; col++) {
                            String targetPos = "" + (char)('a' + col) + (row + 1);
                            
                            // Check if move is legal and would get out of check
                            if (Referee.isLegalMove(piecePos, targetPos, pieceCode, piecePositions)) {
                                // Make the move temporarily
                                String capturedPiece = piecePositions.remove(targetPos);
                                piecePositions.put(targetPos, pieceCode);
                                piecePositions.remove(piecePos);
                                
                                // Check if still in check
                                boolean stillInCheck = isCheck(isWhiteTurn);
                                
                                // Undo the move
                                piecePositions.remove(targetPos);
                                piecePositions.put(piecePos, pieceCode);
                                if (capturedPiece != null) {
                                    piecePositions.put(targetPos, capturedPiece);
                                }
                                
                                // If not still in check, it's not checkmate
                                if (!stillInCheck) return false;
                            }
                        }
                    }
                }
            }
            
            // If we've tried all moves and none escape check, it's checkmate
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Player player1 = new Player("White Player", 1500, "USA");
            Player player2 = new Player("Black Player", 1600, "UK");
            Match match = new Match(1, player1, player2, "10|0");
            match.startMatch();
            new MatchGUI(match).setVisible(true);
        });
    }
}


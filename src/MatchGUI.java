import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private JButton analyzeButton;
    private JButton hintButton;
    private JTextArea analysisTextArea;
    private StockfishEngine stockfishEngine;
    
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private static final Color HEADER_COLOR = new Color(50, 50, 75);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color WHITE_PLAYER_COLOR = new Color(245, 245, 245);
    private static final Color BLACK_PLAYER_COLOR = new Color(220, 220, 220);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color RESIGN_BUTTON_COLOR = new Color(180, 70, 70);
    private static final Color HINT_BUTTON_COLOR = new Color(70, 180, 70);
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
        
        // Initialize Stockfish
        initializeStockfish();
        
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
        
        // Add window listener to clean up resources
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (stockfishEngine != null) {
                    StockfishManager.getInstance().closeEngine("match");
                }
            }
        });
    }
    
    /**
     * Initialize the Stockfish engine
     */
    private void initializeStockfish() {
        StockfishManager manager = StockfishManager.getInstance();
        if (manager.isStockfishAvailable()) {
            stockfishEngine = manager.getEngine("match");
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Match title with turn indicator
        boolean isWhiteTurn = match.getCurrentTurn() == 0;
        String playerName = isWhiteTurn ? match.getPlayer1().getUsername() : match.getPlayer2().getUsername();
        String turnText = "Chess Match - " + playerName + "'s Turn (" + (isWhiteTurn ? "White" : "Black") + ")";
        
        JLabel titleLabel = new JLabel(turnText, JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Add an info button
        JButton infoButton = new JButton("?");
        infoButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoButton.setFocusPainted(false);
        infoButton.addActionListener(e -> showHelpDialog());
        
        panel.add(infoButton, BorderLayout.EAST);
        
        return panel;
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Human vs. Human Match\n\n" +
            "• Players take turns using the same computer\n" +
            "• White plays first, then Black\n" +
            "• Click on a piece to select it, then click on a destination square to move\n" +
            "• The title bar shows whose turn it is currently\n" +
            "• The clock shows the time remaining for each player\n\n" +
            "Use the buttons below the board to:\n" +
            "• Offer a draw\n" +
            "• Resign the game\n" +
            "• Analyze the position (uses Stockfish)\n" +
            "• Get a hint (uses Stockfish)",
            "Chess Match Help",
            JOptionPane.INFORMATION_MESSAGE
        );
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
        sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout(0, 10));
        sidePanel.setBackground(BACKGROUND_COLOR);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(REGULAR_FONT);
        tabbedPane.setForeground(TEXT_COLOR);
        
        // Move history panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(WHITE_PLAYER_COLOR);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel historyTitle = new JLabel("Move History");
        historyTitle.setFont(TITLE_FONT);
        historyTitle.setForeground(TEXT_COLOR);
        historyPanel.add(historyTitle, BorderLayout.NORTH);
        
        moveHistoryArea = new JTextArea(20, 15);
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        moveHistoryArea.setText("Game started\n");
        
        JScrollPane historyScrollPane = new JScrollPane(moveHistoryArea);
        historyScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        // Analysis panel
        JPanel analysisPanel = new JPanel(new BorderLayout());
        analysisPanel.setBackground(WHITE_PLAYER_COLOR);
        analysisPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel analysisTitle = new JLabel("Engine Analysis");
        analysisTitle.setFont(TITLE_FONT);
        analysisTitle.setForeground(TEXT_COLOR);
        analysisPanel.add(analysisTitle, BorderLayout.NORTH);
        
        analysisTextArea = new JTextArea(20, 15);
        analysisTextArea.setEditable(false);
        analysisTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        if (stockfishEngine != null) {
            analysisTextArea.setText("Stockfish ready for analysis.\n");
        } else {
            analysisTextArea.setText("Stockfish engine not available.\n");
        }
        
        JScrollPane analysisScrollPane = new JScrollPane(analysisTextArea);
        analysisScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);
        
        // Analysis controls
        JPanel analysisControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        analysisControls.setOpaque(false);
        
        analyzeButton = createStyledButton("Analyze", BUTTON_COLOR);
        analyzeButton.addActionListener(e -> analyzePosition());
        
        hintButton = createStyledButton("Hint", HINT_BUTTON_COLOR);
        hintButton.addActionListener(e -> getHint());
        
        analysisControls.add(analyzeButton);
        analysisControls.add(hintButton);
        analysisPanel.add(analysisControls, BorderLayout.SOUTH);
        
        // Add tabs
        tabbedPane.addTab("Moves", historyPanel);
        tabbedPane.addTab("Analysis", analysisPanel);
        
        sidePanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add control buttons
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
            // Get position key
            String position = posKey(col, row);
            
            // If no piece is selected yet
            if (selectedPosition == null) {
                String pieceCode = piecePositions.get(position);
                // Check if there is a piece on the square
                if (pieceCode != null) {
                    // Make sure player is moving their own pieces
                    boolean isWhitePiece = pieceCode.startsWith("w");
                    boolean isWhiteTurn = match.isWhiteTurn();
                    
                    if (isWhitePiece == isWhiteTurn) {
                        selectedPosition = position;
                selectedSquare = square;
                        square.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 3));
                    }
                }
            } else {
                // A piece is already selected, try to move it
                String fromPosition = selectedPosition;
                String toPosition = position;
                
                String pieceCode = piecePositions.get(fromPosition);
                
                // Reset the selection
                selectedSquare.setBorder(null);
                    selectedPosition = null;
                    selectedSquare = null;
                
                // Perform the move if valid
                if (pieceCode != null) {
                    // Check if valid move
                    if (isValidMove(fromPosition, toPosition, pieceCode)) {
                        // Update the board
                        updateBoard(fromPosition, toPosition, pieceCode);
                        moveHistoryArea.append(pieceCode + ": " + fromPosition + " -> " + toPosition + "\n");
                        
                        // Update match state
                    match.toggleTurn();
                        
                        // Update the title to show whose turn it is
                        updateTitle();
                    
                    // Check for game end conditions
                        checkGameState();
                    }
                }
            }
        }

        private boolean isValidMove(String fromPosition, String toPosition, String pieceCode) {
            // Convert move to UCI format for Stockfish validation
            String uciMove = fromPosition + toPosition;
            
            // Check if the move is valid using Stockfish
            if (stockfishEngine != null) {
                String fen = StockfishEngine.boardToFen(piecePositions, match.isWhiteTurn());
                return stockfishEngine.isValidMove(fen, uciMove);
            }
            
            // If no Stockfish, use a simple validation
            // Don't allow moving to a square with your own piece
            String destPiece = piecePositions.get(toPosition);
            if (destPiece != null) {
                boolean isWhiteSource = pieceCode.startsWith("w");
                boolean isWhiteDest = destPiece.startsWith("w");
                if (isWhiteSource == isWhiteDest) {
                    return false; // Can't capture your own pieces
                }
            }
            
            // Basic move validation for each piece type
            return true; // Simplified for this fix
        }

        private void updateTitle() {
            boolean isWhiteTurn = match.isWhiteTurn();
            String playerName = isWhiteTurn ? match.getPlayer1().getUsername() : match.getPlayer2().getUsername();
            String turnText = "Chess Match - " + playerName + "'s Turn (" + (isWhiteTurn ? "White" : "Black") + ")";
            
            // Find the title label in the header panel
            JPanel mainPanel = (JPanel) MatchGUI.this.getContentPane();
            Component[] components = mainPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JPanel) {
                        JPanel header = (JPanel) panel.getComponent(0);
                        if (header.getComponentCount() > 0 && header.getComponent(0) instanceof JLabel) {
                            JLabel titleLabel = (JLabel) header.getComponent(0);
                            titleLabel.setText(turnText);
                            break;
                        }
                    }
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

        public void highlightMove(String uciMove) {
            if (uciMove.length() < 4) return;
            
            // Extract source and destination coordinates
            int fromFile = uciMove.charAt(0) - 'a';
            int fromRank = uciMove.charAt(1) - '1';
            int toFile = uciMove.charAt(2) - 'a';
            int toRank = uciMove.charAt(3) - '1';
            
            // Highlight source square
            squares[fromRank][fromFile].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            
            // Highlight destination square
            squares[toRank][toFile].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            
            // Schedule removal of highlights
            javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
                squares[fromRank][fromFile].setBorder(null);
                squares[toRank][toFile].setBorder(null);
            });
            timer.setRepeats(false);
            timer.start();
        }
        
        private void checkGameState() {
            boolean isWhiteTurn = match.isWhiteTurn();
            
            if (isCheck(isWhiteTurn)) {
                if (isCheckmate(isWhiteTurn)) {
                    String winner = isWhiteTurn ? "Black" : "White";
                    moveHistoryArea.append("CHECKMATE! " + winner + " wins!\n");
                    
                    // Update player stats
                    updatePlayerStats(winner.equals("White"));
                    
                    // Show dialog
                    winnerLabel.setText(winner + " wins by checkmate!");
                    winnerLabel.setVisible(true);
                    
                    // Disable board
                    setEnabled(false);
                } else {
                    moveHistoryArea.append("CHECK!\n");
                }
            }
        }
        
        private void updatePlayerStats(boolean whiteWins) {
            Player winner = whiteWins ? match.getPlayer1() : match.getPlayer2();
            Player loser = whiteWins ? match.getPlayer2() : match.getPlayer1();
            
            // Update ratings
            int winnerOriginalRating = winner.getRating();
            int loserOriginalRating = loser.getRating();
            
            // Simple ELO calculation (K=32)
            int kFactor = 32;
            double winnerExpected = 1.0 / (1.0 + Math.pow(10, (loserOriginalRating - winnerOriginalRating) / 400.0));
            int ratingChange = (int) (kFactor * (1.0 - winnerExpected));
            
            winner.updateRating(ratingChange);
            loser.updateRating(-ratingChange);
            
            // Update wins/losses
            winner.recordWin();
            loser.recordLoss();
            
            // Update stats file
            winner.changePlayerStats(1, 0, 0, ratingChange);
            loser.changePlayerStats(0, 1, 0, -ratingChange);
            
            // Show rating changes
            moveHistoryArea.append(String.format(
                "%s rating: %d → %d (+%d)\n%s rating: %d → %d (-%d)\n",
                winner.getName(), winnerOriginalRating, winner.getRating(), ratingChange,
                loser.getName(), loserOriginalRating, loser.getRating(), ratingChange
            ));
        }
    }

    /**
     * Analyze the current board position
     */
    private void analyzePosition() {
        if (stockfishEngine == null) {
            analysisTextArea.setText("Stockfish engine is not available.\n");
            return;
        }
        
        analysisTextArea.setText("Analyzing position...\n");
        
        // Convert board to FEN
        String fen = StockfishEngine.boardToFen(boardPanel.piecePositions, match.isWhiteTurn());
        
        // Run analysis in background thread
        new Thread(() -> {
            Map<String, Object> analysis = stockfishEngine.analyzePosition(fen, 15);
            
            if (analysis != null) {
                // Format analysis results
                StringBuilder result = new StringBuilder();
                
                if (analysis.containsKey("score")) {
                    double score = (double) analysis.get("score");
                    result.append(String.format("Evaluation: %+.2f\n", score));
                }
                
                if (analysis.containsKey("mateIn")) {
                    int mateIn = (int) analysis.get("mateIn");
                    result.append("Mate in ").append(Math.abs(mateIn)).append(" for ");
                    result.append(mateIn > 0 ? "white" : "black").append("\n");
                }
                
                result.append("\nBest move: ");
                if (analysis.containsKey("bestMove")) {
                    String bestMove = (String) analysis.get("bestMove");
                    result.append(formatMove(bestMove)).append("\n");
                }
                
                result.append("\nTop lines:\n");
                List<String> pvMoves = (List<String>) analysis.getOrDefault("pvMoves", List.of());
                for (int i = 0; i < Math.min(pvMoves.size(), 3); i++) {
                    result.append(i + 1).append(". ").append(formatMoves(pvMoves.get(i))).append("\n");
                }
                
                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText(result.toString());
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText("Analysis failed.");
                });
            }
        }).start();
    }
    
    /**
     * Get a hint for the current position
     */
    private void getHint() {
        if (stockfishEngine == null) {
            analysisTextArea.setText("Stockfish engine is not available.\n");
            return;
        }
        
        String fen = StockfishEngine.boardToFen(boardPanel.piecePositions, match.isWhiteTurn());
        
        new Thread(() -> {
            String hint = stockfishEngine.getHint(fen);
            
            if (hint != null) {
                String formattedHint = formatMove(hint);
                
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText("Suggested move: " + formattedHint);
                    boardPanel.highlightMove(hint);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText("Could not get a hint.");
                });
            }
        }).start();
    }
    
    private String formatMove(String uciMove) {
        if (uciMove == null || uciMove.length() < 4) return "Invalid move";
        
        char fromFile = uciMove.charAt(0);
        char fromRank = uciMove.charAt(1);
        char toFile = uciMove.charAt(2);
        char toRank = uciMove.charAt(3);
        
        String promotion = "";
        if (uciMove.length() > 4) {
            char promotionPiece = uciMove.charAt(4);
            switch (promotionPiece) {
                case 'q': promotion = "=Q"; break;
                case 'r': promotion = "=R"; break;
                case 'b': promotion = "=B"; break;
                case 'n': promotion = "=N"; break;
            }
        }
        
        return String.format("%c%c-%c%c%s", fromFile, fromRank, toFile, toRank, promotion);
    }
    
    private String formatMoves(String uciMoves) {
        String[] moves = uciMoves.split("\\s+");
        StringBuilder formatted = new StringBuilder();
        
        for (String move : moves) {
            formatted.append(formatMove(move)).append(" ");
        }
        
        return formatted.toString().trim();
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


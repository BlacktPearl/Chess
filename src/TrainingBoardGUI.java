import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingBoardGUI extends JFrame {
    // Color scheme
    private static final Color DARK_SQUARE = new Color(118, 150, 86);
    private static final Color LIGHT_SQUARE = new Color(238, 238, 210);
    private static final Color BACKGROUND_COLOR = new Color(28, 32, 36);
    private static final Color PANEL_COLOR = new Color(38, 42, 46);
    private static final Color PRIMARY_COLOR = new Color(75, 115, 153);
    private static final Color SECONDARY_COLOR = new Color(179, 148, 95);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    private static final Color MUTED_TEXT = new Color(180, 180, 180);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 213, 105, 120);
    private static final Color MOVE_INDICATOR = new Color(99, 172, 229, 150);
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font PIECE_FONT = new Font("Segoe UI", Font.BOLD, 42);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font COORDS_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private JLabel[][] squares = new JLabel[8][8];
    private Map<String, String> piecePositions = new HashMap<>();
    private String selectedPiece = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    
    private StockfishEngine stockfishEngine;
    private JTextArea analysisTextArea;
    private static boolean stockfishWarningShown = false;

    public TrainingBoardGUI() {
        setTitle("Chess Training Board - 2025 Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        
        // Initialize Stockfish engine
        initializeStockfish();
        
        // Use border layout for the main frame
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBackground(BACKGROUND_COLOR);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create components
        createBoardPanel();
        createControlPanel();
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(PANEL_COLOR);
        statusPanel.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        statusLabel = new JLabel("Ready to start training. Select a piece to move.");
        statusLabel.setFont(DEFAULT_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Add components to frame
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Initialize the board with pieces
        setupPieces();
        
        // Add window listener to clean up resources
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (stockfishEngine != null) {
                    StockfishManager.getInstance().closeEngine("training");
                }
            }
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PANEL_COLOR, 0, h, PANEL_COLOR.darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Chess Training Board");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton helpButton = createIconButton("?", "Training Help", e -> showHelpDialog());
        JButton backButton = createButton("Back to Dashboard", e -> dispose());
        
        buttonPanel.add(helpButton);
        buttonPanel.add(backButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void createBoardPanel() {
        // Create a panel to hold the board and coordinates
        JPanel boardContainer = new JPanel(new BorderLayout(10, 10));
        boardContainer.setBackground(BACKGROUND_COLOR);
        
        // Create row coordinates panel (1-8)
        JPanel rowCoords = new JPanel(new GridLayout(8, 1));
        rowCoords.setBackground(BACKGROUND_COLOR);
        for (int row = 7; row >= 0; row--) {
            JLabel label = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            label.setFont(COORDS_FONT);
            label.setForeground(MUTED_TEXT);
            rowCoords.add(label);
        }
        
        // Create column coordinates panel (a-h)
        JPanel colCoords = new JPanel(new GridLayout(1, 8));
        colCoords.setBackground(BACKGROUND_COLOR);
        for (int col = 0; col < 8; col++) {
            JLabel label = new JLabel(String.valueOf((char)('a' + col)), SwingConstants.CENTER);
            label.setFont(COORDS_FONT);
            label.setForeground(MUTED_TEXT);
            colCoords.add(label);
        }
        
        // Create the actual board with rounded corners
        boardPanel = new JPanel(new GridLayout(8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle for the board
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(DARK_SQUARE.darker());
                g2d.fill(rect);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        boardPanel.setOpaque(false);
        boardPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                JLabel square = new JLabel("", SwingConstants.CENTER);
                square.setFont(PIECE_FONT);
                square.setOpaque(true);
                
                // Softer colors for the squares
                square.setBackground((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                square.setForeground(Color.BLACK);
                
                final int finalRow = row;
                final int finalCol = col;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(finalRow, finalCol);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (piecePositions.containsKey(posKey(finalCol, finalRow)) || selectedPiece != null) {
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            
                            // Add subtle hover effect
                            if ((finalRow + finalCol) % 2 == 0) {
                                square.setBackground(LIGHT_SQUARE.darker());
                            } else {
                                square.setBackground(DARK_SQUARE.brighter());
                            }
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                        // Reset to original color
                        square.setBackground((finalRow + finalCol) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                    }
                });
                
                squares[row][col] = square;
                boardPanel.add(square);
            }
        }
        
        // Add all components to the container
        boardContainer.add(rowCoords, BorderLayout.WEST);
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        boardContainer.add(colCoords, BorderLayout.SOUTH);
        
        // Replace the boardPanel reference
        boardPanel = boardContainer;
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PANEL_COLOR, 0, h, PANEL_COLOR.darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        controlPanel.setPreferredSize(new Dimension(280, 0));
        
        // Training controls section
        JLabel controlsTitle = createSectionTitle("Training Controls");
        
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        buttonPanel.add(createButton("Reset Board", e -> resetBoard()));
        buttonPanel.add(createButton("Practice Openings", e -> loadOpeningTraining()));
        buttonPanel.add(createButton("Practice Endgames", e -> loadEndgameTraining()));
        buttonPanel.add(createButton("Analyze Position", e -> analyzeCurrentPosition()));
        buttonPanel.add(createButton("Save Position", e -> saveCurrentPosition()));
        
        // Create tabbed interface for pieces and training options
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DEFAULT_FONT);
        tabbedPane.setBackground(PANEL_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Piece selector panel
        JPanel piecesPanel = new JPanel();
        piecesPanel.setLayout(new BoxLayout(piecesPanel, BoxLayout.Y_AXIS));
        piecesPanel.setOpaque(false);
        piecesPanel.setBorder(new EmptyBorder(15, 10, 10, 10));
        
        JLabel placePiecesTitle = createSectionTitle("Place Pieces");
        
        JPanel pieceGrid = new JPanel(new GridLayout(2, 6, 8, 8));
        pieceGrid.setOpaque(false);
        pieceGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] pieces = {"wP", "wR", "wN", "wB", "wQ", "wK", "bP", "bR", "bN", "bB", "bQ", "bK"};
        for (String piece : pieces) {
            JPanel pieceContainer = new JPanel(new BorderLayout());
            pieceContainer.setBackground(PANEL_COLOR.brighter());
            pieceContainer.setBorder(new LineBorder(Color.GRAY, 1, true));
            
            JLabel pieceLabel = new JLabel(getSymbol(piece), SwingConstants.CENTER);
            pieceLabel.setFont(new Font("Serif", Font.BOLD, 30));
            pieceLabel.setForeground(TEXT_COLOR);
            pieceLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            pieceContainer.add(pieceLabel, BorderLayout.CENTER);
            
            pieceContainer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectPieceToPlace(piece);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    pieceContainer.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
                    pieceContainer.setBackground(PANEL_COLOR.brighter().brighter());
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    pieceContainer.setBorder(new LineBorder(Color.GRAY, 1, true));
                    pieceContainer.setBackground(PANEL_COLOR.brighter());
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            
            pieceGrid.add(pieceContainer);
        }
        
        JLabel pieceInstructions = new JLabel("Select a piece, then click on the board to place it");
        pieceInstructions.setFont(DEFAULT_FONT);
        pieceInstructions.setForeground(MUTED_TEXT);
        pieceInstructions.setAlignmentX(Component.LEFT_ALIGNMENT);
        pieceInstructions.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        piecesPanel.add(placePiecesTitle);
        piecesPanel.add(pieceGrid);
        piecesPanel.add(pieceInstructions);
        
        // Training notes panel
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setOpaque(false);
        notesPanel.setBorder(new EmptyBorder(15, 10, 10, 10));
        
        JLabel notesTitle = createSectionTitle("Training Notes");
        
        JTextArea notesArea = new JTextArea();
        notesArea.setFont(DEFAULT_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(PANEL_COLOR.brighter());
        notesArea.setForeground(TEXT_COLOR);
        notesArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        notesArea.setRows(7);
        
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(new LineBorder(Color.GRAY, 1));
        
        JButton saveNotesButton = createButton("Save Notes", e -> {
            JOptionPane.showMessageDialog(this, "Notes saved", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        saveNotesButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveNotesButton.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        notesPanel.add(notesTitle);
        notesPanel.add(scrollPane);
        notesPanel.add(saveNotesButton);
        
        // Add analysis textarea to one of the tabs
        JPanel analysisPanel = new JPanel();
        analysisPanel.setLayout(new BorderLayout(0, 10));
        analysisPanel.setBackground(PANEL_COLOR);
        analysisPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel analysisTitle = createSectionTitle("Engine Analysis");
        analysisPanel.add(analysisTitle, BorderLayout.NORTH);
        
        analysisTextArea = new JTextArea(10, 20);
        analysisTextArea.setEditable(false);
        analysisTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        analysisTextArea.setForeground(TEXT_COLOR);
        analysisTextArea.setBackground(PANEL_COLOR.darker());
        analysisTextArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        analysisTextArea.setLineWrap(true);
        analysisTextArea.setWrapStyleWord(true);
        
        JScrollPane analysisScrollPane = new JScrollPane(analysisTextArea);
        analysisScrollPane.setBorder(new LineBorder(PRIMARY_COLOR, 1));
        analysisScrollPane.getVerticalScrollBar().setForeground(PRIMARY_COLOR);
        analysisScrollPane.getVerticalScrollBar().setBackground(PANEL_COLOR.darker());
        
        analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);
        
        JPanel analysisButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        analysisButtonPanel.setOpaque(false);
        
        JButton analyzeButton = createButton("Analyze Position", e -> analyzeCurrentPosition());
        JButton hintButton = createButton("Get Hint", e -> getHint());
        
        analysisButtonPanel.add(analyzeButton);
        analysisButtonPanel.add(hintButton);
        
        analysisPanel.add(analysisButtonPanel, BorderLayout.SOUTH);
        
        // Add to the tabbed pane
        tabbedPane.addTab("Analysis", null, analysisPanel, "Analyze the current position");
        
        // Add all sections to the control panel
        controlPanel.add(controlsTitle);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        controlPanel.add(tabbedPane);
        
        // Add tabs
        tabbedPane.addTab("Pieces", piecesPanel);
        tabbedPane.addTab("Notes", notesPanel);
    }

    private JLabel createSectionTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        return title;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Create gradient background
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, PRIMARY_COLOR.darker(), 0, height, PRIMARY_COLOR.darker().darker());
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, PRIMARY_COLOR.brighter(), 0, height, PRIMARY_COLOR);
                } else {
                    gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, height, PRIMARY_COLOR.darker());
                }
                
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, 10, 10));
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2d);
                
                int x = (width - (int) r.getWidth()) / 2;
                int y = (height - (int) r.getHeight()) / 2 + fm.getAscent();
                
                g2d.setColor(TEXT_COLOR);
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = 36;
                return d;
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        return button;
    }

    private JButton createIconButton(String text, String tooltip, ActionListener listener) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                
                if (getModel().isPressed()) {
                    g2d.setColor(SECONDARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(SECONDARY_COLOR.brighter());
                } else {
                    g2d.setColor(SECONDARY_COLOR);
                }
                
                g2d.fillOval(0, 0, size, size);
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2d);
                
                int x = (size - (int) r.getWidth()) / 2;
                int y = (size + (int) r.getHeight()) / 2 - 2;
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(BUTTON_FONT);
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }
        };
        
        button.setToolTipText(tooltip);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        return button;
    }
    
    private void showHelpDialog() {
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
        helpPanel.setBackground(PANEL_COLOR);
        helpPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Training Board Help");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea helpText = new JTextArea(
            "The Chess Training Board allows you to practice positions and learn chess strategies.\n\n" +
            "• Click on a piece and then a square to move it\n" +
            "• Use the Pieces tab to place new pieces on the board\n" +
            "• Practice openings or endgames from the training controls\n" +
            "• Save your notes in the Notes tab\n" +
            "• Analyze positions to get feedback on your moves\n\n" +
            "Right-click on any piece to remove it from the board."
        );
        helpText.setFont(DEFAULT_FONT);
        helpText.setForeground(TEXT_COLOR);
        helpText.setBackground(PANEL_COLOR);
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        helpPanel.add(titleLabel);
        helpPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        helpPanel.add(helpText);
        
        JOptionPane.showMessageDialog(
            this,
            helpPanel,
            "Training Board Help",
            JOptionPane.PLAIN_MESSAGE
        );
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
        squares[row][col].setForeground(piece.startsWith("w") ? Color.BLACK : Color.BLACK);
        squares[row][col].setFont(PIECE_FONT);
        piecePositions.put(posKey(col, row), piece);
    }

    private void removePiece(int col, int row) {
        squares[row][col].setText("");
        piecePositions.remove(posKey(col, row));
    }

    private String getSymbol(String piece) {
        // Use larger, more visible chess symbols
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

    private String posKey(int col, int row) {
        return "" + (char) ('a' + col) + (row + 1);
    }
    
    private void handleSquareClick(int row, int col) {
        if (selectedPiece != null) {
            // Place the selected piece
            setPiece(col, row, selectedPiece);
            selectedPiece = null;
            statusLabel.setText("Piece placed at " + (char)('a' + col) + (row + 1));
        } else if (selectedRow == -1 && selectedCol == -1) {
            // First selection - check if there's a piece
            String key = posKey(col, row);
            if (piecePositions.containsKey(key)) {
                selectedRow = row;
                selectedCol = col;
                
                // Highlight selected square
                Color originalColor = squares[row][col].getBackground();
                squares[row][col].setBackground(PRIMARY_COLOR);
                
                statusLabel.setText("Selected piece at " + (char)('a' + col) + (row + 1) + ". Click destination or another piece.");
            }
        } else {
            // Second selection - move the piece
            String piece = piecePositions.get(posKey(selectedCol, selectedRow));
            removePiece(selectedCol, selectedRow);
            
            // Remove highlight from selected square
            squares[selectedRow][selectedCol].setBackground(
                (selectedRow + selectedCol) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE
            );
            
            // If destination has a piece, it's captured
            if (piecePositions.containsKey(posKey(col, row))) {
                statusLabel.setText("Captured piece at " + (char)('a' + col) + (row + 1));
            } else {
                statusLabel.setText("Moved piece to " + (char)('a' + col) + (row + 1));
            }
            
            setPiece(col, row, piece);
            selectedRow = -1;
            selectedCol = -1;
        }
    }
    
    private void selectPieceToPlace(String piece) {
        selectedPiece = piece;
        selectedRow = -1;
        selectedCol = -1;
        statusLabel.setText("Selected " + piece + " to place. Click on board to place the piece.");
    }
    
    private void resetBoard() {
        // Clear all pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setText("");
                squares[row][col].setBackground(
                    (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE
                );
            }
        }
        
        piecePositions.clear();
        setupPieces();
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
        
        statusLabel.setText("Board reset to starting position.");
    }
    
    private void loadOpeningTraining() {
        // This would load a specific opening position
        // For now, we'll just reset to the initial position
        resetBoard();
        statusLabel.setText("Opening training mode activated. Practice your opening moves.");
    }
    
    private void loadEndgameTraining() {
        // Clear all pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setText("");
                squares[row][col].setBackground(
                    (row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE
                );
            }
        }
        
        piecePositions.clear();
        
        // Setup a basic king and pawn endgame
        setPiece(4, 0, "wK");
        setPiece(4, 2, "wP");
        setPiece(4, 7, "bK");
        
        selectedPiece = null;
        selectedRow = -1;
        selectedCol = -1;
        
        statusLabel.setText("Endgame training loaded. Practice king and pawn endgame.");
    }
    
    private void analyzeCurrentPosition() {
        if (stockfishEngine == null) {
            displayStockfishError();
            return;
        }
        
        // Convert the current board position to FEN
        String fen = StockfishEngine.boardToFen(piecePositions, true); // Assuming white to move
        
        statusLabel.setText("Analyzing position...");
        analysisTextArea.setText("Analyzing...");
        
        // Run analysis in a separate thread to avoid freezing the UI
        new Thread(() -> {
            Map<String, Object> analysis = stockfishEngine.analyzePosition(fen, 15);
            
            if (analysis != null) {
                // Format the analysis results
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
                for (int i = 0; i < pvMoves.size(); i++) {
                    result.append(i + 1).append(". ").append(formatMoves(pvMoves.get(i))).append("\n");
                }
                
                // Update UI on the EDT
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText(result.toString());
                    statusLabel.setText("Analysis complete.");
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    analysisTextArea.setText("Analysis failed.");
                    statusLabel.setText("Analysis failed.");
                });
            }
        }).start();
    }
    
    private void getHint() {
        if (stockfishEngine == null) {
            displayStockfishError();
            return;
        }
        
        String fen = StockfishEngine.boardToFen(piecePositions, true); // Assuming white to move
        
        statusLabel.setText("Getting hint...");
        
        new Thread(() -> {
            String hint = stockfishEngine.getHint(fen);
            
            if (hint != null) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Hint: " + formatMove(hint));
                    
                    // Highlight the hint move on the board
                    highlightMove(hint);
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Could not get a hint.");
                });
            }
        }).start();
    }
    
    private void highlightMove(String uciMove) {
        if (uciMove.length() < 4) return;
        
        // Extract source and destination coordinates
        int fromFile = uciMove.charAt(0) - 'a';
        int fromRank = uciMove.charAt(1) - '1';
        int toFile = uciMove.charAt(2) - 'a';
        int toRank = uciMove.charAt(3) - '1';
        
        // Highlight source square
        squares[fromRank][fromFile].setBackground(HIGHLIGHT_COLOR);
        
        // Highlight destination square
        squares[toRank][toFile].setBackground(MOVE_INDICATOR);
        
        // Schedule removal of highlights
        Timer timer = new Timer(2000, e -> {
            squares[fromRank][fromFile].setBackground((fromRank + fromFile) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
            squares[toRank][toFile].setBackground((toRank + toFile) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
        });
        timer.setRepeats(false);
        timer.start();
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
    
    private void saveCurrentPosition() {
        // This would save the current board position
        statusLabel.setText("Current position saved (demo functionality).");
    }

    /**
     * Initialize the Stockfish engine
     */
    private void initializeStockfish() {
        StockfishManager manager = StockfishManager.getInstance();
        if (manager.isStockfishAvailable()) {
            stockfishEngine = manager.getEngine("training");
            if (stockfishEngine == null) {
                displayStockfishError();
            }
        } else {
            displayStockfishError();
        }
    }

    private void displayStockfishError() {
        // Display warning in the analysis text area
        if (analysisTextArea != null) {
            analysisTextArea.setText(
                "Stockfish engine is not available.\n\n" +
                "To use analysis features, please download Stockfish from stockfishchess.org\n" +
                "and place it in the resources/stockfish directory.\n\n" +
                "For macOS: resources/stockfish/stockfish-mac\n" +
                "For Windows: resources/stockfish/stockfish.exe\n" +
                "For Linux: resources/stockfish/stockfish"
            );
        }
        
        // Show a notification (but only once per session)
        if (!stockfishWarningShown) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    this,
                    "Stockfish engine is not available. Please download Stockfish from stockfishchess.org and place it in the resources/stockfish directory.",
                    "Engine Not Found",
                    JOptionPane.WARNING_MESSAGE
                );
            });
            stockfishWarningShown = true;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            TrainingBoardGUI trainingBoardGUI = new TrainingBoardGUI();
            trainingBoardGUI.setupPieces();
            trainingBoardGUI.setVisible(true);
        });
    }
}

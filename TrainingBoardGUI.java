import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class TrainingBoardGUI extends JFrame {
    private JLabel[][] squares = new JLabel[8][8];
    private Map<String, String> piecePositions = new HashMap<>();

    public TrainingBoardGUI() {
        setTitle("Training Board");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private String posKey(int col, int row) {
        return "" + (char) ('a' + col) + (row + 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrainingBoardGUI trainingBoardGUI = new TrainingBoardGUI();
            trainingBoardGUI.setVisible(true);
        });
    }
}

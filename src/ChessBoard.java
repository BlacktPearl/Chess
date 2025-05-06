import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

public class ChessBoard {
    private Map<String, String> board;
    private StockfishEngine stockfishEngine;
    private boolean useStockfish;
    
    // Color definitions for UI representation
    public static final Color LIGHT_SQUARE_COLOR = new Color(240, 217, 181);
    public static final Color DARK_SQUARE_COLOR = new Color(181, 136, 99);
    public static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0, 128);
    
    public ChessBoard() {
        initializeBoard();
        initializeStockfish();
    }

    /**
     * Initialize the Stockfish engine if available
     */
    private void initializeStockfish() {
        StockfishManager manager = StockfishManager.getInstance();
        if (manager.isStockfishAvailable()) {
            stockfishEngine = manager.getEngine("board");
            useStockfish = stockfishEngine != null && stockfishEngine.isReady();
        } else {
            useStockfish = false;
        }
    }

    public void initializeBoard() {
        board = new HashMap<>();
        String[] pieces = {"R", "N", "B", "Q", "K", "B", "N", "R"};
        for (int i = 0; i < 8; i++) {
            board.put((char) ('a' + i) + "1", "w" + pieces[i]);
            board.put((char) ('a' + i) + "2", "wP");
            board.put((char) ('a' + i) + "7", "bP");
            board.put((char) ('a' + i) + "8", "b" + pieces[i]);
        }
    }

    /**
     * Check if a move is legal using either internal rules or Stockfish
     */
    public boolean isLegalMove(String from, String to, String piece, boolean whiteTurn) {
        if (useStockfish) {
            return isLegalMoveWithStockfish(from, to, whiteTurn);
        } else {
            return isLegalMoveWithRules(from, to, piece, whiteTurn);
        }
    }
    
    /**
     * Check if a move is legal using simple chess rules
     */
    private boolean isLegalMoveWithRules(String from, String to, String piece, boolean whiteTurn) {
        String movingPiece = board.get(from);
        if (movingPiece == null || !movingPiece.endsWith(piece.substring(0, 1))) return false;
        if ((whiteTurn && !movingPiece.startsWith("w")) || (!whiteTurn && !movingPiece.startsWith("b"))) return false;

        String target = board.get(to);
        if (target != null && target.charAt(0) == movingPiece.charAt(0)) return false;

        int fromCol = from.charAt(0) - 'a';
        int fromRow = from.charAt(1) - '1';
        int toCol = to.charAt(0) - 'a';
        int toRow = to.charAt(1) - '1';

        int dx = toCol - fromCol;
        int dy = toRow - fromRow;

        switch (piece) {
            case "P":
                int direction = movingPiece.startsWith("w") ? 1 : -1;
                boolean startRow = movingPiece.startsWith("w") ? fromRow == 1 : fromRow == 6;
                if (dx == 0 && target == null) {
                    if (dy == direction) return true;
                    if (dy == 2 * direction && startRow && board.get((char) (from.charAt(0)) + "" + (fromRow + direction + 1)) == null)
                        return true;
                }
                if (Math.abs(dx) == 1 && dy == direction && target != null) return true;
                return false;
            case "R": return dx == 0 || dy == 0;
            case "N": return (Math.abs(dx) == 1 && Math.abs(dy) == 2) || (Math.abs(dx) == 2 && Math.abs(dy) == 1);
            case "B": return Math.abs(dx) == Math.abs(dy);
            case "Q": return dx == 0 || dy == 0 || Math.abs(dx) == Math.abs(dy);
            case "K": return Math.abs(dx) <= 1 && Math.abs(dy) <= 1;
            default:
                return false;
        }
    }
    
    /**
     * Check if a move is legal using Stockfish
     */
    private boolean isLegalMoveWithStockfish(String from, String to, boolean whiteTurn) {
        String uciMove = from + to;
        String fen = boardToFen(whiteTurn);
        return stockfishEngine.isValidMove(fen, uciMove);
    }

    /**
     * Convert the current board to FEN notation
     */
    public String boardToFen(boolean isWhiteTurn) {
        return StockfishEngine.boardToFen(board, isWhiteTurn);
    }

    /**
     * Get move suggestions from Stockfish
     */
    public String getBestMove(boolean whiteTurn, int thinkTimeMs) {
        if (!useStockfish) return null;
        
        String fen = boardToFen(whiteTurn);
        return stockfishEngine.getBestMove(fen, thinkTimeMs);
    }
    
    /**
     * Analyze the current position with Stockfish
     */
    public Map<String, Object> analyzePosition(boolean whiteTurn, int depth) {
        if (!useStockfish) return null;
        
        String fen = boardToFen(whiteTurn);
        return stockfishEngine.analyzePosition(fen, depth);
    }

    public void performMove(String from, String to) {
        String piece = board.remove(from);
        board.put(to, piece);
    }

    public String getPieceAt(String pos) {
        return board.get(pos);
    }

    public Map<String, String> getBoard() {
        return new HashMap<>(board);
    }
    
    /**
     * Set the board from a Map representation
     */
    public void setBoard(Map<String, String> newBoard) {
        board.clear();
        board.putAll(newBoard);
    }
    
    // Gets Unicode symbol for chess piece
    public static String getUnicodeSymbol(String piece) {
        if (piece == null) return "";
        
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
    
    // Clear the board
    public void clearBoard() {
        board.clear();
    }
    
    // Check if a position is in check
    public boolean isInCheck(boolean isWhite) {
        if (useStockfish) {
            // Use Stockfish to determine check
            String fen = boardToFen(isWhite);
            Map<String, Object> analysis = stockfishEngine.analyzePosition(fen, 1);
            if (analysis != null && analysis.containsKey("inCheck")) {
                return (boolean) analysis.get("inCheck");
            }
        }
        
        // Fallback to internal check detection
        // Find the king position
        String kingCode = isWhite ? "wK" : "bK";
        String kingPos = null;
        
        for (Map.Entry<String, String> entry : board.entrySet()) {
            if (entry.getValue().equals(kingCode)) {
                kingPos = entry.getKey();
                break;
            }
        }
        
        if (kingPos == null) return false;
        
        // Check if any opponent piece can attack the king
        for (Map.Entry<String, String> entry : board.entrySet()) {
            String piecePos = entry.getKey();
            String pieceCode = entry.getValue();
            
            // If it's an opponent's piece
            if (pieceCode.charAt(0) != (isWhite ? 'w' : 'b')) {
                // Check if it can attack the king
                if (isLegalMoveWithRules(piecePos, kingPos, pieceCode.substring(1), !isWhite)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    // Get original color of a square based on its algebraic notation
    public static Color getOriginalColor(String pos) {
        int col = pos.charAt(0) - 'a';
        int row = pos.charAt(1) - '1';
        return (row + col) % 2 == 0 ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR;
    }
    
    /**
     * Check if Stockfish is being used
     */
    public boolean isUsingStockfish() {
        return useStockfish;
    }
    
    /**
     * Clean up resources when done with the board
     */
    public void cleanup() {
        if (useStockfish) {
            StockfishManager.getInstance().closeEngine("board");
            useStockfish = false;
        }
    }
}





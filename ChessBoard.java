import java.util.HashMap;
import java.util.Map;

public class ChessBoard {
    private Map<String, String> board;

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

    public boolean isLegalMove(String from, String to, String piece, boolean whiteTurn) {
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
            default: return false;
        }
    }

    public void performMove(String from, String to) {
        String piece = board.remove(from);
        board.put(to, piece);
    }

    public String getPieceAt(String pos) {
        return board.get(pos);
    }

    public Map<String, String> getBoard() {
        return board;
    }
}


import java.util.Map;

public class Referee {

    public static boolean isLegalMove(String from, String to, String pieceCode, Map<String, String> board) {
        if (from.equals(to)) return false;

        int fromCol = from.charAt(0) - 'a';
        int fromRow = from.charAt(1) - '1';
        int toCol = to.charAt(0) - 'a';
        int toRow = to.charAt(1) - '1';

        String color = pieceCode.substring(0, 1);
        String type = pieceCode.substring(1);

        String target = board.get(to);
        if (target != null && target.startsWith(color)) {
            return false; // Cannot capture own piece
        }

        int dCol = toCol - fromCol;
        int dRow = toRow - fromRow;

        switch (type) {
            case "P":
                return validatePawnMove(from, to, color, board);
            case "R":
                return validateRookMove(fromRow, fromCol, toRow, toCol, board);
            case "N":
                return (Math.abs(dCol) == 2 && Math.abs(dRow) == 1) || (Math.abs(dCol) == 1 && Math.abs(dRow) == 2);
            case "B":
                return validateBishopMove(fromRow, fromCol, toRow, toCol, board);
            case "Q":
                return validateBishopMove(fromRow, fromCol, toRow, toCol, board) ||
                       validateRookMove(fromRow, fromCol, toRow, toCol, board);
            case "K":
                return Math.abs(dCol) <= 1 && Math.abs(dRow) <= 1;
            default:
                return false;
        }
    }

    private static boolean validatePawnMove(String from, String to, String color, Map<String, String> board) {
        int fromCol = from.charAt(0) - 'a';
        int fromRow = from.charAt(1) - '1';
        int toCol = to.charAt(0) - 'a';
        int toRow = to.charAt(1) - '1';

        int direction = color.equals("w") ? 1 : -1;
        int startRow = color.equals("w") ? 1 : 6;
        String target = board.get(to);

        if (fromCol == toCol) {
            if (toRow - fromRow == direction && target == null) return true;
            if (fromRow == startRow && toRow - fromRow == 2 * direction && target == null) {
                // Check if square in between is empty
                String midSquare = "" + from.charAt(0) + (fromRow + direction + 1);
                return board.get(midSquare) == null;
            }
        } else if (Math.abs(fromCol - toCol) == 1 && toRow - fromRow == direction && target != null) {
            return true;
        }

        return false;
    }

    private static boolean validateRookMove(int fromRow, int fromCol, int toRow, int toCol, Map<String, String> board) {
        if (fromRow != toRow && fromCol != toCol) return false;

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            String pos = "" + (char) ('a' + currentCol) + (currentRow + 1);
            if (board.get(pos) != null) return false;

            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }

    private static boolean validateBishopMove(int fromRow, int fromCol, int toRow, int toCol, Map<String, String> board) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) return false;

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow && currentCol != toCol) {
            String pos = "" + (char) ('a' + currentCol) + (currentRow + 1);
            if (board.get(pos) != null) return false;

            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }
}


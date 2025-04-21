import java.util.Map;

public class Referee extends User {
    private int assignedMatches[];

    public Referee(int ID, String name, String country, String username, String passwordHash) {
        super(ID, name, country, username, passwordHash, 1); // Role 1 for referee
        this.assignedMatches = new int[20]; // Can manage up to 20 matches
    }

    // Assign a match to this referee
    public boolean assignMatch(int matchID) {
        for (int i = 0; i < assignedMatches.length; i++) {
            if (assignedMatches[i] == 0) {
                assignedMatches[i] = matchID;
                return true;
            }
        }
        return false; // No free slots
    }

    // Count assigned matches
    public int getAssignedMatchCount() {
        int count = 0;
        for (int id : assignedMatches) {
            if (id != 0) count++;
        }
        return count;
    }

    // Check if referee has capacity for more matches
    public boolean hasCapacity() {
        return getAssignedMatchCount() < assignedMatches.length;
    }

    // Start a match
    public void startMatch(int matchID) {
        System.out.println("Referee " + getName() + " starting match #" + matchID);
    }

    // Check rule violation
    public boolean checkRuleViolation(String move) {
        // In a real system, would check if a move violates chess rules
        return move.contains("illegal");
    }

    // Resolve dispute
    public String resolveDispute(int matchID, int decision) {
        String result = "";
        switch (decision) {
            case 1:
                result = "White wins by referee decision";
                break;
            case 2:
                result = "Black wins by referee decision";
                break;
            case 3:
                result = "Draw by referee decision";
                break;
            default:
                result = "Match continues";
                break;
        }
        System.out.println("Referee " + getName() + " resolved dispute in match #" + matchID + ": " + result);
        return result;
    }

    // Check if a move is legal
    public static boolean isLegalMove(String from, String to, String pieceCode, java.util.Map<String, String> piecePositions) {
        // Check for null parameters
        if (from == null || to == null || pieceCode == null || piecePositions == null) {
            return false;
        }
        
        // Basic validation of coordinates
        if (from.length() != 2 || to.length() != 2) return false;
        
        char fromColChar = from.charAt(0);
        char fromRowChar = from.charAt(1);
        char toColChar = to.charAt(0);
        char toRowChar = to.charAt(1);
        
        // Check if coordinates are valid
        if (fromColChar < 'a' || fromColChar > 'h' || fromRowChar < '1' || fromRowChar > '8' ||
            toColChar < 'a' || toColChar > 'h' || toRowChar < '1' || toRowChar > '8') {
            return false;
        }
        
        // Convert to zero-based indices
        int fromCol = fromColChar - 'a';
        int fromRow = fromRowChar - '1';
        int toCol = toColChar - 'a';
        int toRow = toRowChar - '1';
        
        // Simple movement check for demonstration
        if (from.equals(to)) return false; // Can't move to the same square
        
        // Check if source has a piece
        if (pieceCode == null) return false;
        
        // Check if destination has a piece of the same color
        String destPiece = piecePositions.get(to);
        if (destPiece != null && destPiece.charAt(0) == pieceCode.charAt(0)) {
            return false; // Can't capture own piece
        }
        
        char pieceType = pieceCode.charAt(1);
        char pieceColor = pieceCode.charAt(0);
        
        // Calculate move deltas
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        
        // Now validate movement based on piece type
        switch (pieceType) {
            case 'P': // Pawn
                return validatePawnMove(fromCol, fromRow, toCol, toRow, pieceColor, destPiece != null, piecePositions);
            case 'R': // Rook
                return validateStraightMove(fromCol, fromRow, toCol, toRow, piecePositions);
            case 'N': // Knight
                return validateKnightMove(colDiff, rowDiff);
            case 'B': // Bishop
                return validateDiagonalMove(fromCol, fromRow, toCol, toRow, piecePositions);
            case 'Q': // Queen
                return validateStraightMove(fromCol, fromRow, toCol, toRow, piecePositions) || 
                       validateDiagonalMove(fromCol, fromRow, toCol, toRow, piecePositions);
            case 'K': // King
                return validateKingMove(colDiff, rowDiff);
            default:
                return false; // Unknown piece type
        }
    }
    
    private static boolean validatePawnMove(int fromCol, int fromRow, int toCol, int toRow, char pieceColor, boolean isCapture, Map<String, String> piecePositions) {
        int direction = (pieceColor == 'w') ? 1 : -1; // White moves up, black moves down
        int startRow = (pieceColor == 'w') ? 1 : 6;   // Starting rows for pawns
        
        // Regular move forward
        if (fromCol == toCol && !isCapture) {
            // Single square forward
            if (toRow - fromRow == direction) {
                return true;
            }
            
            // Double square forward from starting position
            if (fromRow == startRow && toRow - fromRow == 2 * direction) {
                // Check if the square in between is empty
                int midRow = fromRow + direction;
                String midPos = "" + (char)(fromCol + 'a') + (char)(midRow + '1');
                return piecePositions.get(midPos) == null;
            }
        }
        
        // Capture move (diagonal)
        if (Math.abs(toCol - fromCol) == 1 && toRow - fromRow == direction && isCapture) {
            return true;
        }

        return false;
    }

    private static boolean validateKnightMove(int colDiff, int rowDiff) {
        return (Math.abs(colDiff) == 1 && Math.abs(rowDiff) == 2) || 
               (Math.abs(colDiff) == 2 && Math.abs(rowDiff) == 1);
    }
    
    private static boolean validateKingMove(int colDiff, int rowDiff) {
        return Math.abs(colDiff) <= 1 && Math.abs(rowDiff) <= 1;
    }
    
    private static boolean validateStraightMove(int fromCol, int fromRow, int toCol, int toRow, Map<String, String> piecePositions) {
        // If not a straight move (horizontal or vertical)
        if (fromCol != toCol && fromRow != toRow) {
            return false;
        }
        
        // Determine which direction we're moving
        int rowStep = 0;
        if (fromRow < toRow) rowStep = 1;
        else if (fromRow > toRow) rowStep = -1;
        
        int colStep = 0;
        if (fromCol < toCol) colStep = 1;
        else if (fromCol > toCol) colStep = -1;
        
        // Check all squares in between for pieces
        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow || currentCol != toCol) {
            String pos = "" + (char)(currentCol + 'a') + (char)(currentRow + '1');
            if (piecePositions.get(pos) != null) {
                return false; // Path is blocked
            }

            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }

    private static boolean validateDiagonalMove(int fromCol, int fromRow, int toCol, int toRow, Map<String, String> piecePositions) {
        // If not a diagonal move
        if (Math.abs(toCol - fromCol) != Math.abs(toRow - fromRow)) {
            return false;
        }
        
        // Determine which direction we're moving
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        
        // Check all squares in between for pieces
        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;

        while (currentRow != toRow && currentCol != toCol) {
            String pos = "" + (char)(currentCol + 'a') + (char)(currentRow + '1');
            if (piecePositions.get(pos) != null) {
                return false; // Path is blocked
            }

            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }
    
    // Check if a position is in check
    public static boolean isInCheck(java.util.Map<String, String> piecePositions, boolean isWhite) {
        // Placeholder for check detection
        // In a real system, would check if the king is under attack
        return false;
    }
    
    // Check if a position is checkmate
    public static boolean isCheckmate(java.util.Map<String, String> piecePositions, boolean isWhite) {
        // Placeholder for checkmate detection
        // In a real system, would check if the king is in check and has no legal moves
        return false;
    }
}


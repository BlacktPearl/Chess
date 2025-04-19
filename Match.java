import java.util.ArrayList;
import java.util.List;

public class Match {
    private int matchID;
    private Player player1;
    private Player player2;
    private Player winner;
    private String timeControl;
    private String status;
    private List<Move> moveHistory = new ArrayList<>();
    private boolean isWhiteTurn = true;

    private long whiteTimeLeft; // in milliseconds
    private long blackTimeLeft;
    private long lastMoveTimestamp;

    public Match(int matchID, Player player1, Player player2, String timeControl) {
        this.matchID = matchID;
        this.player1 = player1;
        this.player2 = player2;
        this.timeControl = timeControl;
        this.status = "Pending";

        // Parse timeControl like "10|0"
        String[] parts = timeControl.split("\\|");
        int minutes = Integer.parseInt(parts[0]);
        whiteTimeLeft = blackTimeLeft = minutes * 60 * 1000;
    }

    public void startMatch() {
        this.status = "Ongoing";
        lastMoveTimestamp = System.currentTimeMillis();
    }

    public Player getCurrentPlayer() {
        return isWhiteTurn ? player1 : player2;
    }

    public void toggleTurn() {
        long now = System.currentTimeMillis();
        long duration = now - lastMoveTimestamp;

        if (isWhiteTurn) {
            whiteTimeLeft -= duration;
        } else {
            blackTimeLeft -= duration;
        }

        isWhiteTurn = !isWhiteTurn;
        lastMoveTimestamp = now;

        checkForTimeout();
    }

    private void checkForTimeout() {
        if (whiteTimeLeft <= 0) {
            winner = player2;
            status = "Completed (White timed out)";
        } else if (blackTimeLeft <= 0) {
            winner = player1;
            status = "Completed (Black timed out)";
        }
    }

    public void recordMove(Move move) {
        moveHistory.add(move);
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
        this.status = "Completed";
    }

    public String getStatus() {
        return status;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public long getWhiteTimeLeft() {
        return whiteTimeLeft;
    }

    public long getBlackTimeLeft() {
        return blackTimeLeft;
    }
}



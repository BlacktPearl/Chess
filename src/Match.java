import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class Match {
    private int matchID;
    private Player player1;
    private Player player2;
    private Player winner;
    private Player loser;
    private String timeControl;
    private String status;
    private List<Move> moveHistory = new ArrayList<>();
    private boolean isWhiteTurn = true;
    private Timer whiteTimer;
    private Timer blackTimer;

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
        whiteTimer = new Timer(1000, e -> toggleTurn());
        blackTimer = new Timer(1000, e -> toggleTurn());
        whiteTimer.start();
        lastMoveTimestamp = System.currentTimeMillis() + 1000; // Start with 1 second subtracted for accuracy
    }

    public Player getCurrentPlayer() {
        return isWhiteTurn ? player1 : player2;
    }

    public void toggleTurn() {
        long now = System.currentTimeMillis();
        long duration = now - lastMoveTimestamp ; // Subtract 1 second for timer accuracy

        if (isWhiteTurn) {
            blackTimer.stop();
            whiteTimeLeft -= duration;
            whiteTimer.start();
        } else {
            whiteTimer.stop();
            blackTimeLeft -= duration;
            blackTimer.start();
        }
        lastMoveTimestamp = now;

        checkForTimeout();
    }

    private void checkForTimeout() {
        if (whiteTimeLeft <= 0) {
            status = "Completed (White timed out)";
            whiteTimer.stop();
            blackTimer.stop();
        } else if (blackTimeLeft <= 0) {
            status = "Completed (Black timed out)";
            whiteTimer.stop();
            blackTimer.stop();
        }
    }

    public void recordMove(Move move) {
        moveHistory.add(move);
        if (moveHistory.size() == 1) {
            whiteTimer.start(); // Start white's timer on the first move
        }
        if (move.isLegal()){
            isWhiteTurn = !isWhiteTurn;
        }
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
        winner.changePlayerStats(1,0, 0,20);

    }

    public void setLoser(Player loser){
        this.loser = loser;
        this.status = "Completed";
        loser.changePlayerStats(0,1, 0,-10);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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



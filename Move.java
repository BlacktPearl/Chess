public class Move {
    private String from;
    private String to;
    private String piece;
    private String timestamp;
    private boolean legal;
    private Player player;

    public Move(String from, String to, String piece, String timestamp, boolean legal, Player player) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.timestamp = timestamp;
        this.legal = legal;
        this.player = player;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPiece() {
        return piece;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isLegal() {
        return legal;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMoveDetails() {
        return piece + " from " + from + " to " + to + (legal ? "" : " (ILLEGAL)") + " by " + player.getUsername();
    }
}



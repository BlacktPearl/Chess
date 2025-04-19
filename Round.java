import java.util.List;
import java.util.ArrayList;

public class Round {
    private int roundID;
    private List<Match> matches;
    private String status;

    public Round() {
        matches = new ArrayList<>();
        status = "scheduled";
    }

    public void scheduleMatches() {
        // Create matches with unique IDs and timestamps
        matches.clear();
        long currentTime = System.currentTimeMillis();
        
        // For demo purposes, create 4 sample matches
        for (int i = 0; i < 4; i++) {
            matches.add(new Match((int)currentTime + i));
        }
        System.out.println("Scheduled " + matches.size() + " matches for round " + roundID);
    }

    public void startRound() {
        status = "in progress";
    }

    public void endRound() {
        status = "completed";
    }

    public String getRoundDetails() {
        return "Round " + roundID + " - " + status;
    }

    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    public int getRoundID() {
        return roundID;
    }
}

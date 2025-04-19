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

    public void scheduleMatches(List<Player> players) {
        matches.clear();
        long currentTime = System.currentTimeMillis();

        int matchId = 1000;

        for (int i = 0; i < players.size(); i += 2) {
            if (i + 1 < players.size()) {
                Player player1 = players.get(i);
                Player player2 = players.get(i + 1);
                Match match = new Match(matchId++, player1, player2, "10|0");
                matches.add(match);
                System.out.println("Scheduled match: " + player1.getUsername() + " vs " + player2.getUsername());
            } else {
                System.out.println("No opponent for: " + players.get(i).getUsername() + " — given a bye.");
            }
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

    public List<Match> getMatches() {
        return matches;
    }
}


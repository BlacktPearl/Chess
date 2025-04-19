import java.util.List;
import java.util.ArrayList;

public class Leaderboard {
    private List<Integer> playerIDs;
    private List<Float> points;
    private List<Integer> rankings;

    public Leaderboard() {
        playerIDs = new ArrayList<>();
        points = new ArrayList<>();
        rankings = new ArrayList<>();
    }

    public void updateLeaderboard(int playerID, float points) {
        int index = playerIDs.indexOf(playerID);
        if (index != -1) {
            this.points.set(index, points);
        } else {
            playerIDs.add(playerID);
            this.points.add(points);
            rankings.add(playerIDs.size());
        }
        sortLeaderboard();
    }

    private void sortLeaderboard() {
        // Create list of indices to sort
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < playerIDs.size(); i++) {
            indices.add(i);
        }

        // Sort indices based on points (descending)
        indices.sort((i1, i2) -> Float.compare(points.get(i2), points.get(i1)));

        // Reorder all lists based on sorted indices
        List<Integer> sortedPlayerIDs = new ArrayList<>();
        List<Float> sortedPoints = new ArrayList<>();
        List<Integer> sortedRankings = new ArrayList<>();

        for (int i = 0; i < indices.size(); i++) {
            int originalIndex = indices.get(i);
            sortedPlayerIDs.add(playerIDs.get(originalIndex));
            sortedPoints.add(points.get(originalIndex));
            sortedRankings.add(i + 1); // Rankings start at 1
        }

        // Update the original lists
        playerIDs = sortedPlayerIDs;
        points = sortedPoints;
        rankings = sortedRankings;
    }

    public String getTopPlayer(int n) {
        return "Player " + playerIDs.get(n) + ": " + points.get(n) + " points";
    }

    public void displayLeaderboard() {
        for (int i = 0; i < playerIDs.size(); i++) {
            System.out.println((i+1) + ". Player " + playerIDs.get(i) + ": " + points.get(i));
        }
    }
}

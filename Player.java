import java.util.ArrayList;
import java.util.List;

public class Player extends User {
    private int rating;
    private int wins;
    private int losses;
    private int draws;
    private float totalPoints;
    private List<String> gameHistory;

    public Player(int ID, String name, String country, int initialRating) {
        super(ID, name, country);
        this.rating = initialRating;
        this.gameHistory = new ArrayList<>();
    }

    public void updateRating(int opponentRating, double result) {
        // Elo rating system implementation
        double expectedScore = 1.0 / (1 + Math.pow(10, (opponentRating - rating) / 400.0));
        int kFactor = rating < 2100 ? 32 : (rating < 2400 ? 24 : 16);
        rating += kFactor * (result - expectedScore);
    }

    public void recordGameResult(String opponentName, String result, int opponentRating) {
        gameHistory.add(opponentName + ": " + result);
        switch (result) {
            case "win":
                wins++;
                updateRating(opponentRating, 1.0);
                break;
            case "loss":
                losses++;
                updateRating(opponentRating, 0.0);
                break;
            case "draw":
                draws++;
                updateRating(opponentRating, 0.5);
                break;
        }
        calculateTotalPoints();
    }

    private void calculateTotalPoints() {
        totalPoints = wins + (draws * 0.5f);
    }

    public float getPerformanceRating() {
        int totalGames = wins + losses + draws;
        return totalGames > 0 ? (totalPoints / totalGames) * 100 : 0;
    }

    public List<String> getGameHistory() {
        return new ArrayList<>(gameHistory);
    }

    public String getDetailedStats() {
        return String.format("Rating: %d | W: %d | L: %d | D: %d | Score: %.1f/%.1f",
                rating, wins, losses, draws, totalPoints, wins + losses + draws);
    }

    public int getRating() {
        return rating;
    }
}

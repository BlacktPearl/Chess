import java.util.Objects;

public class Player extends User {
    private int rating;
    private int wins;
    private int losses;
    private int draws;
    private float totalPoints;

    public Player(String name, int rating, String country) {
        super(0, name, country, name, "", 0); // Default role 0 for player
        this.rating = rating;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.totalPoints = 0.0f;
    }
    
    public Player(String name, int rating, String country, String username, String passwordHash, int id) {
        super(id, name, country, username, passwordHash, 0); // Role 0 for player
        this.rating = rating;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.totalPoints = 0.0f;
    }

    // Record a win for this player
    public void recordWin() {
        wins++;
        totalPoints += 1.0f;
    }

    // Record a loss for this player
    public void recordLoss() {
        losses++;
    }

    // Record a draw for this player
    public void recordDraw() {
        draws++;
        totalPoints += 0.5f;
    }

    // Get player's rating
    public int getRating() {
        return rating;
    }

    // Get player's total points
    public float getTotalPoints() {
        return totalPoints;
    }
    
    // Get player's wins
    public int getWins() {
        return wins;
    }
    
    // Get player's losses
    public int getLosses() {
        return losses;
    }
    
    // Get player's draws
    public int getDraws() {
        return draws;
    }
    
    // Update player's rating after a match
    public void updateRating(int change) {
        rating += change;
    }
    
    // Get player stats as a string
    public String getPlayerStats() {
        return String.format("%s: Rating: %d, Record: %d-%d-%d, Points: %.1f", 
            getName(), rating, wins, losses, draws, totalPoints);
    }

    @Override
    public String toString() {
        return getName() + " (" + rating + ")";
    }
}



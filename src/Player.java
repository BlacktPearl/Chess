import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

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
        try (BufferedReader reader = new BufferedReader(new FileReader("playerStats.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(getName())) {
                    rating = Integer.parseInt(parts[4].trim());
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return rating;
    }

    // Get player's total points
    public float getTotalPoints() {
        return totalPoints;
    }

    // Get player's wins
    public int getWins() {
        try (BufferedReader reader = new BufferedReader(new FileReader("playerStats.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(getName())) {
                    wins = Integer.parseInt(parts[1].trim());
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return wins;
    }

    // Get player's losses
    public int getLosses() {
        try (BufferedReader reader = new BufferedReader(new FileReader("playerStats.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(getName())) {
                    losses = Integer.parseInt(parts[2].trim());
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return losses;
    }

    // Get player's draws
    public int getDraws() {
        try (BufferedReader reader = new BufferedReader(new FileReader("playerStats.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(getName())) {
                    draws = Integer.parseInt(parts[3].trim());
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
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

    public void changePlayerStats(int newWin, int newLoss, int newDraw, int newPoints){
        try (BufferedReader reader = new BufferedReader(new FileReader("playerStats.txt"))){
            StringBuilder updatedContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(getName())){
                    int updatedWins = Integer.parseInt(parts[1].trim()) + newWin;
                    int updatedLosses = Integer.parseInt(parts[2].trim()) + newLoss;
                    int updatedDraws = Integer.parseInt(parts[3].trim()) + newDraw;
                    int updatedTotalPoints = Integer.parseInt(parts[4].trim()) + newPoints;
                    line = String.format("%s,%d,%d,%d,%d", getName(), updatedWins, updatedLosses, updatedDraws, updatedTotalPoints);
                }
                updatedContent.append(line).append("\n");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("playerStats.txt"))){
                writer.write(updatedContent.toString());
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }



    @Override
    public String toString() {
        return getName() + " (" + rating + ")";
    }
}




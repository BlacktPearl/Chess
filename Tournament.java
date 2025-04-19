import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tournament {
    private int tournamentID;
    private String name;
    private String location;
    private String startDate;
    private String endDate;
    private String timeControl; // Format: "baseTime+increment" (e.g. "90+30" for 90 minutes + 30 sec increment)
    private List<Player> players;
    private List<Round> rounds;
    private String status;
    private int currentRound;
    private int maxRounds;


public String getLeaderboard() {
    StringBuilder leaderboard = new StringBuilder("🏆 Leaderboard:\n");

    // Sort players by rating descending
    players.stream()
        .sorted((p1, p2) -> Integer.compare(p2.getRating(), p1.getRating()))
        .forEach(player -> {
            leaderboard.append(player.getUsername())
                .append(" (Rating: ")
                .append(player.getRating())
                .append(", Country: ")
                .append(player.getCountry())
                .append(")\n");
        });

    return leaderboard.toString();
}



    public Tournament(int id, String name, String location, String startDate, String timeControl, int maxRounds) {
        this.tournamentID = id;
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.timeControl = timeControl;
        this.maxRounds = maxRounds;
        this.players = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.status = "upcoming";
        this.currentRound = 0;
    }

    public boolean startTournament() {
        if (players.size() < 2) {
            throw new IllegalStateException("Cannot start tournament with less than 2 players");
        }
        
        if (!status.equals("upcoming")) {
            throw new IllegalStateException("Tournament has already started or completed");
        }

        status = "in progress";
        System.out.println("Starting tournament: " + name);
        
        // Initialize first round
        currentRound = 0;
        scheduleRounds();
        
        // Notify all players
        for (Player player : players) {
            System.out.println("Notified player: " + player.getName());
        }
        
        return true;
    }

    public boolean addPlayer(Player player) {
        if (status.equals("in progress") || status.equals("completed")) {
            return false;
        }
        players.add(player);
        return true;
    }

    public void scheduleRounds() {
        // Swiss tournament pairing
        Collections.sort(players, Comparator.comparingInt(Player::getRating).reversed());

        for (int i = 0; i < maxRounds; i++) {
            Round round = new Round();
            round.setRoundID(i + 1);

            List<Player> pairedPlayers = new ArrayList<>();
            List<Player> availablePlayers = new ArrayList<>(players);

            while (availablePlayers.size() >= 2) {
                Player player1 = availablePlayers.remove(0);
                Player player2 = findBestOpponent(player1, availablePlayers, pairedPlayers);

                if (player2 == null) {
                    // No suitable opponent found, give bye to player1
                    System.out.println("No suitable opponent found for " + player1.getName() + ", giving bye.");
                    break; // or handle bye situation differently
                }

                availablePlayers.remove(player2);
                pairedPlayers.add(player1);
                pairedPlayers.add(player2);

                // In real system would create matches between player1 and player2
                round.scheduleMatches(players);
            }

            rounds.add(round);
        }
    }

    private Player findBestOpponent(Player player1, List<Player> availablePlayers, List<Player> pairedPlayers) {
        // Find the best opponent based on rating and previous opponents
        Player bestOpponent = null;
        int bestRatingDifference = Integer.MAX_VALUE;

        for (Player player2 : availablePlayers) {
            if (pairedPlayers.contains(player2)) {
                continue; // Skip already paired players
            }

            int ratingDifference = Math.abs(player1.getRating() - player2.getRating());

            if (ratingDifference < bestRatingDifference) {
                bestOpponent = player2;
                bestRatingDifference = ratingDifference;
            }
        }

        return bestOpponent;
    }

    public boolean advanceRound() {
        if (currentRound >= rounds.size()) return false;
        rounds.get(currentRound).startRound();
        currentRound++;
        if (currentRound == rounds.size()) {
            endTournament();
        }
        return true;
    }

    public void endTournament() {
        status = "completed";
        System.out.println("Tournament " + name + " has ended!");

        // Calculate final standings based on player ratings
        Collections.sort(players, Comparator.comparingInt(Player::getRating).reversed());

        System.out.println("\n--- Final Standings ---");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.println((i + 1) + ". " + player.getName() + " (Rating: " + player.getRating() + ")");
        }
    }

    public String getTournamentDetails() {
        return String.format("%s (ID: %d)\nLocation: %s\nDates: %s to %s\nTime Control: %s\nStatus: %s\nPlayers: %d\nRounds: %d/%d",
                name, tournamentID, location, startDate, endDate, timeControl, status, players.size(), currentRound, maxRounds);
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        if (timeControl == null || !timeControl.matches("\\d+\\+\\d+")) {
            throw new IllegalArgumentException("Time control must be in format 'minutes+increment' (e.g., 60+15)");
        }
        this.timeControl = timeControl;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isRegistrationOpen() {
        return status.equals("upcoming");
    }
}


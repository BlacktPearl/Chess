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
            System.out.println("Cannot start tournament with less than 2 players");
            return false;
        }
        
        if (!status.equals("upcoming")) {
            System.out.println("Tournament has already started or completed");
            return false;
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
            
            // Simple pairing algorithm (top half vs bottom half)
            int half = players.size() / 2;
            for (int j = 0; j < half; j++) {
                // In real system would create matches between players[j] and players[j+half]
                round.scheduleMatches();
            }
            
            rounds.add(round);
        }
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
        // Calculate final standings
    }

    public String getTournamentDetails() {
        return String.format("%s (ID: %d)\nLocation: %s\nDates: %s to %s\nTime Control: %s\nStatus: %s\nPlayers: %d\nRounds: %d/%d",
                name, tournamentID, location, startDate, endDate, timeControl, status, players.size(), currentRound, maxRounds);
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        if (timeControl.matches("\\d+\\+\\d+")) {
            this.timeControl = timeControl;
        } else {
            throw new IllegalArgumentException("Time control must be in format 'minutes+increment'");
        }
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

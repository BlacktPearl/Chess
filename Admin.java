import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private List<Integer> managedTournaments;
    private int maxTournaments = 10;

    public Admin(int ID, String name, String country, String username, String passwordHash, int role) {
        super(ID, name, country, username, passwordHash, role);
        this.managedTournaments = new ArrayList<>();
    }

    public int createTournament(String name, String location, String startDate, String timeControl) {
        if (managedTournaments.size() >= maxTournaments) {
            return -1; // Reached maximum tournaments
        }
        
        int newTournamentId = generateTournamentId();
        managedTournaments.add(newTournamentId);
        
        // In a real system, would create and store the tournament object
        return newTournamentId;
    }

    public boolean removeTournament(int tournamentID) {
        return managedTournaments.remove(Integer.valueOf(tournamentID));
    }

    public boolean modifyTournamentDetails(int tournamentID, Tournament modifiedTournament) {
        if (!managedTournaments.contains(tournamentID)) {
            return false;
        }
        // In a real system, would update the tournament object
        return true;
    }

    public List<Integer> getManagedTournaments() {
        return new ArrayList<>(managedTournaments);
    }

    public boolean isManagingTournament(int tournamentID) {
        return managedTournaments.contains(tournamentID);
    }

    public int getRemainingTournamentSlots() {
        return maxTournaments - managedTournaments.size();
    }

    private int generateTournamentId() {
        return (int)(Math.random() * 1000000);
    }

    public boolean addPlayerToTournament(int tournamentID, Player player) {
        if (!isManagingTournament(tournamentID)) {
            return false;
        }
        // In a real system, would add player to the tournament
        return true;
    }

    public boolean startTournament(int tournamentID) {
        if (!isManagingTournament(tournamentID)) {
            return false;
        }
        // In a real system, would start the tournament
        return true;
    }
}

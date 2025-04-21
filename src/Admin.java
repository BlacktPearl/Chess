import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private int[] managedTournaments;
    
    public Admin(int ID, String name, String country, String username, String passwordHash, int role) {
        super(ID, name, country, username, passwordHash, role);
        managedTournaments = new int[10]; // Can manage up to 10 tournaments
    }
    
    // Create a new tournament
    public Tournament createTournament(String name, String location, String startDate, String timeControl) {
        int nextTournamentId = getNextTournamentId();
        Tournament tournament = new Tournament(nextTournamentId, name, location, startDate, timeControl, 5);
        
        // Assign this tournament to the admin
        for (int i = 0; i < managedTournaments.length; i++) {
            if (managedTournaments[i] == 0) {
                managedTournaments[i] = nextTournamentId;
                break;
            }
        }
        
        saveTournament(tournament);
        return tournament;
    }
    
    // Remove a tournament
    public boolean removeTournament(int tournamentId) {
        // Remove from managed tournaments
        boolean found = false;
        for (int i = 0; i < managedTournaments.length; i++) {
            if (managedTournaments[i] == tournamentId) {
                managedTournaments[i] = 0;
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        // In a real system, would remove from database
        System.out.println("Tournament #" + tournamentId + " removed by admin " + getUsername());
        return true;
    }
    
    // Modify tournament details
    public boolean modifyTournament(int tournamentId, String newDetails) {
        // In a real system, would update tournament details in database
        System.out.println("Tournament #" + tournamentId + " modified by admin " + getUsername() + 
                          ": " + newDetails);
        return true;
    }
    
    // Count managed tournaments
    public int getManagedTournamentCount() {
        int count = 0;
        for (int id : managedTournaments) {
            if (id != 0) count++;
        }
        return count;
    }
    
    // Check if admin can manage more tournaments
    public boolean hasCapacity() {
        return getManagedTournamentCount() < managedTournaments.length;
    }
    
    // Get all managed tournaments
    public List<Integer> getManagedTournaments() {
        List<Integer> tournaments = new ArrayList<>();
        for (int id : managedTournaments) {
            if (id != 0) {
                tournaments.add(id);
            }
        }
        return tournaments;
    }
    
    // Assign a referee to a tournament
    public boolean assignReferee(Referee referee, Tournament tournament) {
        // In a real system, would update tournament-referee association in database
        System.out.println("Referee " + referee.getUsername() + " assigned to tournament " + 
                          tournament.getTournamentID() + " by admin " + getUsername());
        return true;
    }
    
    private int getNextTournamentId() {
        // In a real system, would get from database
        // For now, just return a random number for demonstration
        return (int)(Math.random() * 10000) + 1;
    }
    
    private void saveTournament(Tournament tournament) {
        // In a real system, would save to database
        System.out.println("Tournament saved: " + tournament.getName());
    }
}

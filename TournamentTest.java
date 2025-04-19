import java.util.Scanner;

public class TournamentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Create test tournament
        Tournament tournament = new Tournament(1, "Chess Championship", "New York", 
            "2025-05-01", "90+30", 5);
        
        // Add test players
        tournament.addPlayer(new Player(101, "Alice", "USA", 1500));
        tournament.addPlayer(new Player(102, "Bob", "UK", 1600));
        tournament.addPlayer(new Player(103, "Charlie", "Germany", 1700));
        
        // Test interface
        while (true) {
            System.out.println("\n--- Chess Tournament Test ---");
            System.out.println("1. View Tournament Details");
            System.out.println("2. Start Tournament");
            System.out.println("3. Advance Round");
            System.out.println("4. View Leaderboard");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    System.out.println(tournament.getTournamentDetails());
                    break;
                case 2:
                    if (tournament.startTournament()) {
                        System.out.println("Tournament started successfully!");
                    } else {
                        System.out.println("Failed to start tournament");
                    }
                    break;
                case 3:
                    if (tournament.advanceRound()) {
                        System.out.println("Advanced to next round");
                    } else {
                        System.out.println("Cannot advance round");
                    }
                    break;
                case 4:
                    // Demo leaderboard
                    Leaderboard lb = new Leaderboard();
                    lb.updateLeaderboard(101, 2.5f);
                    lb.updateLeaderboard(102, 3.0f);
                    lb.updateLeaderboard(103, 1.5f);
                    lb.displayLeaderboard();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

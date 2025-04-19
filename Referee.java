public class Referee {
    private int[] assignedMatches = new int[20];

    public void startMatch(int matchID) {
        System.out.println("Starting match with ID: " + matchID);
    }

    public void checkRules(String move) {
        System.out.println("Checking rules for move: " + move);
    }

    public void resolveDisputes(int matchID, String decision) {
        System.out.println("Resolving dispute for Match " + matchID + ": " + decision);
    }

    // Getters and setters for assignedMatches if needed
}


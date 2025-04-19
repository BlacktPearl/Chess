public class Main {
    public static void main(String[] args) {
        Player player1 = new Player("Alice", 1500, "USA");
        Player player2 = new Player("Bob", 1450, "UK");

        Match match = new Match(1, player1, player2, "10|0");
        match.startMatch();

        // Example manual move input
        Move move1 = new Move("e2", "e4", "P", "2025-04-17 10:00", true, player1);
        match.recordMove(move1);

        for (Move m : match.getMoveHistory()) {
            System.out.println(m.getMoveDetails());
        }
    }
}


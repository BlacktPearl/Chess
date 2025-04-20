public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            User user = MatchGUI.showRegistrationDialog();
            if (user != null) {
                // Create two Player objects for testing purposes
                Player player1 = new Player(user.getName(), 1200, user.getCountry());
                Player player2 = new Player("Opponent", 1200, "USA");
                Match match = new Match(1, player1, player2, "10|0");
                match.startMatch();
                new MatchGUI(match).setVisible(true);
            } else {
                System.out.println("Registration canceled.");
            }
        });
    }
}

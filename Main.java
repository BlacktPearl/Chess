public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Player[] players = MatchGUI.showRegistrationDialog();
            if (players != null && players.length == 2) {
                Match match = new Match(1, players[0], players[1], "10|0");
                match.startMatch();
                new MatchGUI(match).setVisible(true);
            } else {
                System.out.println("Match canceled or not enough players.");
            }
        });
    }
}



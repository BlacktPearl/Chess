import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminGUI extends JFrame implements ActionListener {
    private JButton startTournamentButton, seeTournamentsButton, logoutButton;

    public AdminGUI() {
        setTitle("Admin Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        startTournamentButton = new JButton("Start Tournament");
        seeTournamentsButton = new JButton("See Tournaments");
        logoutButton = new JButton("Logout");

        startTournamentButton.addActionListener(this);
        seeTournamentsButton.addActionListener(this);
        logoutButton.addActionListener(this);

        add(startTournamentButton);
        add(seeTournamentsButton);
        add(logoutButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startTournamentButton) {
            // TODO: Implement start tournament functionality
            JOptionPane.showMessageDialog(this, "Start tournament functionality not implemented yet.");
        } else if (e.getSource() == seeTournamentsButton) {
            // TODO: Implement see tournaments functionality
            JOptionPane.showMessageDialog(this, "See tournaments functionality not implemented yet.");
        } else if (e.getSource() == logoutButton) {
            // TODO: Implement logout functionality
            JOptionPane.showMessageDialog(this, "Logout functionality not implemented yet.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }
}

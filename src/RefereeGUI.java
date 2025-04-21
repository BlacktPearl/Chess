import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RefereeGUI extends JFrame implements ActionListener {
    private JButton listMatchesButton, recordResultButton, logoutButton;

    public RefereeGUI() {
        setTitle("Referee Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        listMatchesButton = new JButton("List Matches");
        recordResultButton = new JButton("Record Result");
        logoutButton = new JButton("Logout");

        listMatchesButton.addActionListener(this);
        recordResultButton.addActionListener(this);
        logoutButton.addActionListener(this);

        add(listMatchesButton);
        add(recordResultButton);
        add(logoutButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == listMatchesButton) {
            // TODO: Implement list matches functionality
            JOptionPane.showMessageDialog(this, "List matches functionality not implemented yet.");
        } else if (e.getSource() == recordResultButton) {
            // TODO: Implement record result functionality
            JOptionPane.showMessageDialog(this, "Record result functionality not implemented yet.");
        } else if (e.getSource() == logoutButton) {
            // TODO: Implement logout functionality
            JOptionPane.showMessageDialog(this, "Logout functionality not implemented yet.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RefereeGUI refereeGUI = new RefereeGUI();
            refereeGUI.setVisible(true);
        });
    }
}

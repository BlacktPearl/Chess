import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            // Set working directory to JAR file location
            setWorkingDirectory();
            
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application with login screen
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
    
    /**
     * Sets the working directory to the JAR file's location
     * This ensures file paths work correctly regardless of how the application is launched
     */
    private static void setWorkingDirectory() {
        try {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            // Decode URL encoding
            path = java.net.URLDecoder.decode(path, "UTF-8");
            
            // If running from JAR
            if (path.endsWith(".jar")) {
                // Get the directory containing the JAR
                path = new File(path).getParent();
            }
            
            // If path is not null, set working directory
            if (path != null) {
                System.setProperty("user.dir", path);
                System.out.println("Working directory set to: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error setting working directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

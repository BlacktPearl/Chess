import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.io.*;

public class User {
    private int ID;
    private String name;
    private String country;
    private String username;
    public String passwordHash;
    private boolean isLoggedIn;
    private int role;

    public User(int ID, String name, String country, String username, String passwordHash, int role) {
        this.ID = ID;
        this.name = name;
        this.country = country;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isLoggedIn = false;
        this.role = role;
    }

    public int getRole() {
        return role;
    }

    public boolean login(String username, String password) {
        if (isLoggedIn || username == null || username.trim().isEmpty()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[0].equals(username)) {
                    String storedPasswordHash = parts[1];
                    if (verifyPasswordHash(password, storedPasswordHash)) {
                        this.ID = Integer.parseInt(parts[5]);
                        this.name = parts[3];
                        this.country = parts[4];
                        this.username = username.trim();
                        this.passwordHash = storedPasswordHash;
                        this.role = Integer.parseInt(parts[2]);
                        this.isLoggedIn = true;
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
            return false;
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public boolean changeUsername(String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return false;
        }
        this.username = newUsername.trim();
        return true;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = Objects.requireNonNull(country, "Country cannot be null");
    }

    public int getID() {
        return ID;
    }

    public boolean verifyPasswordHash(String password, String storedHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(storedHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static User createUser(int ID, String name, String country, String username, String password, int role) {
        String passwordHash = hashPassword(password);
        User newUser = new User(ID, name, country, username, passwordHash, role);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            String line = String.format("%s,%s,%d,%s,%s,%d", username, passwordHash, role, name, country, ID);
            bw.write(line);
            bw.newLine();
            return newUser;
        } catch (IOException e) {
            System.err.println("Error writing user data: " + e.getMessage());
            return null;
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}

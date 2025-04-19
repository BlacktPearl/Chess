import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class User {
    private int ID;
    private String name;
    private String country;
    private String username;
    private String passwordHash;
    private boolean isLoggedIn;

    public User(int ID, String name, String country) {
        this.ID = ID;
        this.name = name;
        this.country = country;
        this.isLoggedIn = false;
    }

    public boolean login(String username, String password) {
        if (isLoggedIn || username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            
            this.username = username.trim();
            this.passwordHash = sb.toString();
            this.isLoggedIn = true;
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
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

    public boolean verifyPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(passwordHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static User createUser(int ID, String name, String country, String username, String password) {
        User newUser = new User(ID, name, country);
        if (newUser.login(username, password)) {
            return newUser;
        } else {
            return null;
        }
    }
}

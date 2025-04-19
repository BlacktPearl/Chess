public class Player {
    private String username;
    private int rating;
    private String country;

    public Player(String username, int rating, String country) {
        this.username = username;
        this.rating = rating;
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    // Add this for compatibility with old code using getName()
    public String getName() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return username + " (" + rating + ")";
    }
}



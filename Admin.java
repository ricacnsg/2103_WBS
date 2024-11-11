package wbs_2103;

public class Admin {
    private String Name, username, password;
    private int AdminID, ContactInfo;

    // Constructors
    public Admin(String Name, String username, String password, int AdminID, int ContactInfo) {
        this.Name = Name;
        setUsername(username);
        setPassword(password);
        setAdminID(AdminID);
        this.ContactInfo = ContactInfo;
    }

    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is invalid!");
        }
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAdminID(int AdminID) {
        if (AdminID <= 0) {
            throw new IllegalArgumentException("Admin ID must be a positive integer");
        }
        this.AdminID = AdminID;
    }

    public int getAdminID() {
        return AdminID;
    }

    public boolean Login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean isAdminValid() {
        return AdminID > 0 && username != null && !username.isEmpty();
    }

    public void retrieveHistoryData() {
        // Implementation for retrieving history data
    }
}


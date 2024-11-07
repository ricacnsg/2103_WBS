public class Admin {
    private static final Logger logger = Logger.getLogger(Admin.class.getName());
    private String Name, username, password;
    private int AdminID, ContactInfo;

    //constructors
    public Admin(String Name, String username, String password, int AdminID, int ContactInfo){
        this.Name = Name;
        setUsername(username);
        setPassword(password);
        setAdminID(AdminID);
        this.ContactInfo = ContactInfo;

    public void setUsername(String username){
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is invalid!");
        }
        this.username = username;
        logger.info("Username has been set to: " + username);

    }

    public String getUsername(){
        // Blocks of code on how to get the username of the admin
        logger.info("Username retrieved: " + username);
        return username;
    }

    public void setPassword(String password){
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password;
        logger.info("Password has been updated");
    }

    public String getPassword(){
        // Blocks of code on how to get the password of the admin

        logger.info("Password retrieved");
        return password;
    }

    public void setAdminID(int AdminID){
        if (AdminID <= 0) {
            throw new IllegalArgumentException("Admin ID must be a positive integer");
        }
        this.AdminID = AdminID;
        logger.info("Admin ID has been set to: " + AdminID);

    }
    
    public int getAdminID(){
        // Blocks of code on how to get the ID of the admin

        logger.info("Admin ID retrieved: " + AdminID);
        return AdminID;
    }

    public boolean Login(String username, String password){
        // Jason: changed the return type to boolean
        // blocks of code to login

        logger.info("Login attempt for username: " + username);
        return this.username.equals(username) && this.password.equals(password);
    }
    // mga boss di pa ako sure dito
    public boolean isAdminValid(){

        return AdminID > 0 && username != null && !username.isEmpty();
    }

    public void retrieveHistoryData(){

    }
}

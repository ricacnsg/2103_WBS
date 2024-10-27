public class Admin {
    private String Name, username, password;
    private int AdminID, ContactInfo;

    //constructors
    public Admin(String Name, String username, String password, int AdminID, int ContactInfo){
        this.username = username;
        this.password = password;
        this.AdminID = AdminID;
        this.Name = Name;
        this.ContactInfo = ContactInfo;
    }

    public void setUsername(String username){
        this.username = username;

    }

    public String getUsername(){
        // Blocks of code on how to get the username of the admin
        return username;
    }

    public void setPassword(String password){
        this.password = password;

    }

    public String getPassword(){
        // Blocks of code on how to get the password of the admin

        return password;
    }

    public void setAdminID(int AdminID){
        this.AdminID = AdminID;

    }
    
    public int getAdminID(){
        // Blocks of code on how to get the ID of the admin

        return AdminID;
    }

    public boolean Login(String username, String password){
        // Jason: changed the return type to boolean
        // blocks of code to login

        return this.username.equals(username) && this.password.equals(password);
    }
    // mga boss di pa ako sure dito
    public boolean isAdminValid(){

        return adminID > 0 && username != null && !username.isEmpty();
    }

    public void retrieveHistoryData(){

    }
}

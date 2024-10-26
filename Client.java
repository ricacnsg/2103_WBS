public class Client {
    private int houseNumber, contactNumber;
    private String location, clientStatus, cUsername, randPass;

    //constructor
    public Client(int houseNumber, int contactNumber, String location, String cUsername, String randPass){
        this.houseNumber = houseNumber;
        this.contactNumber = contactNumber;
        this.location = location;
        this.cUsername = cUsername;
        this.randPass = randPass;
    }

    public void setHouseNumber(int houseNumber){
        this.houseNumber = houseNumber;
    }

    public int getHouseNumber(){
        //blocks of code on how to get house number

        return houseNumber;
    }

    public void setclientStatus(String clientStatus){
        this.clientStatus = clientStatus;
    }

    public String getclientStatus(){
        //blocks of code on how to get client status

        return clientStatus;
    }

    //Rica: added setters and getters for contact number and location
    public void setcontactNumber(int contactNumber){
        this.contactNumber = contactNumber;
    }

    public int getcontactNumber(){
        //block of code on how to get contact number of client

        return contactNumber;
    }

    public void setlocation(String location){
        this.location = location;
    }

    public String getlocation(){
        //block of code on how to get the location of the client

        return location;
    }

    public void setcUsername(String cUsername){
        this.cUsername = cUsername;
    }

    public String getcUsername(){
        //blocks of code on how to get client username

        return cUsername;
    }

    public String getrandPass(){
        //blocks of code to give the client random password when they created their account

        return randPass;
    }

    public void updateInfo(){
        //user will update their personal info

    }

    public void comptoAdmin(){
        //send complaint to the admin

    }

    public void createAcc(String cUsername){
        //blocks of code to create an account

    }

    public boolean login(String cUsername, String randPass){
        //Rica: changed the return type to boolean
        //blocks of code to login

        return this.cUsername.equals(cUsername) && this.randPass.equals(randPass);
    }

    public void clientInfo(){
        //blocks of code that will display the date when client had water meter and all of his/her transaction
        
    }
    
}

package wbs_2103;

import java.util.Scanner;
import java.util.Random;

public class Client {
    private int houseNumber, meterID;
    private long contactNumber;
    private String location, clientStatus, cUsername, randPass;

    //constructor
    public Client(int houseNumber, long contactNumber, String location, String cUsername, String randPass, int meterID){
        this.houseNumber = houseNumber;
        this.contactNumber = contactNumber;
        this.location = location;
        this.cUsername = cUsername;
        this.randPass = randPass;
        this.meterID = meterID;
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
    public void setcontactNumber(long contactNumber){
        this.contactNumber = contactNumber;
    }

    public long getcontactNumber(){
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

    public void setmeterID(int meterID){
        this.meterID = meterID;
    }

    public int getmeterID(){
        if(this.meterID == 0) {
            Random random = new Random();
            this.meterID = 1000 + random.nextInt(9000);
        }
        return meterID;

    }

    public String getrandPass(){
        //blocks of code to give the client random password when they created their account

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder pass = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 6; i++){
            int index = random.nextInt(chars.length());
            pass.append(chars.charAt(index));
        }

        this.randPass = pass.toString();
        return randPass;
    }

    public void updateInfo(){
        //user will update their personal info

        Scanner scan = new Scanner(System.in);

        System.out.println("Update Client Information");

        System.out.println("Enter new Username: ");
        String newuserN = scan.nextLine();
        if(!newuserN.isEmpty()){
            this.cUsername = newuserN;
        }

        System.out.println(" Enter new House Number: ");
        int newhouseN = scan.nextInt();
        if(newhouseN > 0){
            this.houseNumber = newhouseN;
        }

        System.out.println("Enter new Contact Number: ");
        long newcontact = scan.nextInt();
        if(newcontact > 0){
            this.contactNumber = newcontact;
        }

        System.out.println("Enter new Location: ");
        String newLoc = scan.nextLine();
        if(newLoc.isEmpty()){
            this.location = newLoc;
        }



    }

    public void comptoAdmin(){
        //send complaint to the admin

    }

    public void createAcc(String cUsername){
        //blocks of code to create an account

        Scanner scan = new Scanner(System.in);

        System.out.println("Enter your desired Username: ");
        String usern = scan.nextLine();
        this.cUsername = usern;

        System.out.println("Enter House Number: ");
        int houseN = scan.nextInt();
        this.houseNumber = houseN;

        System.out.println("Enter your Contact Number: ");
        Long contactN = scan.nextLong();
        this.contactNumber = contactN;
        scan.nextLine();

        System.out.println("Enter your Location: ");
        String loc = scan.nextLine();
        this.location = loc;

        System.out.println("Enter how many meters do you have?: ");
        int meters = scan.nextInt();
        scan.nextLine();

        //to generate and display the meterID 
        for(int i = 1; i <= meters; i++){
            int meterID = getmeterID();
            System.out.println("MeterID for " + i + ":" + meterID);
            this.meterID = meterID;
        }

        //to generate and display random password
        this.randPass = getrandPass();
        System.out.println("Account Created Successfully! ");
        System.out.println("Username: " + cUsername);
        System.out.println("Your Password is: " + this.randPass);

    }

    public boolean login(String cUsername, String randPass){
        return this.cUsername.equals(cUsername) && this.randPass.equals(randPass);
    }

    public void loginAcc(){
        Scanner scan  = new Scanner(System.in);

        System.out.println("Enter Username: ");
        String usern = scan.nextLine();

        System.out.println("Enter Password: ");
        String pass = scan.nextLine();

        boolean LoginSuccess = login(usern, pass);

        if(LoginSuccess){
            System.out.println("Login Successful! Welcome " + usern);
        } else {
            System.out.println("Login Failed! ");
        }
    }

    public void clientInfo(){
        //blocks of code that will display the date when client had water meter and all of his/her transaction
        
        System.out.println("\n Client Information ");
        System.out.println("Username:" + this.cUsername);
        System.out.println("Houser Number: " + this.houseNumber);
        System.out.println("Contact Number: " + this.contactNumber);
        System.out.println("Location: " + this.location);
    }
    
}

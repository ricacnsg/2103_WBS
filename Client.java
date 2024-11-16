package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.Random;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;

public class Client {
    private int houseNumber, meterID;
    private long contactNumber;
    private String location, clientStatus, cUsername, randPass;
    private Connection connect;
    private Component rootPane;

    //constructor
    public Client(){
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
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

    public String generaterandPass(){
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

    public void createAcc(String Location, long ContactNumber, String ClientStatus, String ClientUsername, String RandPass){
        //blocks of code to create an account

        try {
            Statement stmt = connect.createStatement();
            //String query = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, RandPass) VALUES ("+client.getlocation()+", "+client.getcontactNumber()+", "+client.getclientStatus()+", "+client.getcUsername()+", "+client.getrandPass()+")";
            String query = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, RandPass) VALUES ('"
               + getlocation() + "', '"
               + getcontactNumber() + "', '"
               + getclientStatus() + "', '"
               + getcUsername() + "', '"
               + generaterandPass() + "')";

            stmt.execute(query);
            JOptionPane.showMessageDialog(null,"ACCOUNT SUCCESSFULLY CREATED!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }

    }

    public boolean login(String cUsername, String randPass){
        //make a query for getting values from the database para macheck if tama ang username and password
         String query = "SELECT * FROM client WHERE ClientUsername = ? AND RandPass = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            // Set the username and password in the prepared statement to avoid SQL injection
            pstmt.setString(1, cUsername);
            pstmt.setString(2, randPass);

            ResultSet rs = pstmt.executeQuery();

            // Check if there is at least one row that matches the query
            if (rs.next()) {
                return true; // Login successful
            } else {
                return false; // Login failed
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage());
        }
        return false;     
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

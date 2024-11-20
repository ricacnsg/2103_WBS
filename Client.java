package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;
import java.util.Scanner;

public class Client {
    private int houseNumber, meterID;
    private long contactNumber;
    private String location, clientStatus, cUsername, randPass, complaint;
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
        return cUsername;
    }
    
    public void setmeterID(int meterID){
        //tatanggalin 'tong setter na 'to
        this.meterID = meterID;
    }
    
    public int getmeterID(){
        //tatanggalin 'tong getter na 'to
        if(this.meterID == 0) {
            Random random = new Random();
            this.meterID = 1000 + random.nextInt(9000);
        }
        return meterID;
    }
    
    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }
    
    public String getComplaint() {
        try {
            JOptionPane.showMessageDialog(null,"Complaint/Review is Succesfully Submitted!");    
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
        return complaint;
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
    
    public void updateInfo(String cUsername, int houseNumber, long contactNumber, String location){
        //user will update their personal info
        String query = "UPDATE client" + "SET cUsername + ?, location + ?, contactNumber = ? " + 
                       "WHERE ClientID = ?";
        
        try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            preparedStatement.setString(1, getcUsername());
            preparedStatement.setString(2, getlocation());
            preparedStatement.setInt(3, getHouseNumber());
            preparedStatement.setLong(4, getcontactNumber());
            
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0){
                System.out.println("Client information updated successfully.");
            } else {
                System.out.println("No client found with the given ID.");
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    public void createAcc(String Location, long ContactNumber, String ClientStatus, String ClientUsername, String RandPass) {
    String createAccQuery = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, RandPass) VALUES (?, ?, ?, ?, ?)";
    String createMeterUsageQuery = "INSERT INTO meterusage (clientID, MeterUsage, balance) VALUES (?, 0, 0)"; // Example structure

    try {
        // Use a transaction
        connect.setAutoCommit(false);

        // Insert into the client table
        try (PreparedStatement pstmt = connect.prepareStatement(createAccQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, Location);
            pstmt.setLong(2, ContactNumber);
            pstmt.setString(3, ClientStatus);
            pstmt.setString(4, ClientUsername);
            pstmt.setString(5, RandPass);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            // Get the generated clientID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int clientID = generatedKeys.getInt(1);

                    // Insert into the meterusage table
                    try (PreparedStatement meterStmt = connect.prepareStatement(createMeterUsageQuery)) {
                        meterStmt.setInt(1, clientID);
                        meterStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        }

        // Commit transaction
        connect.commit();

        JOptionPane.showMessageDialog(
            null,
            "Your generated password is: " + RandPass + "\nNote: Please save your password",
            "ACCOUNT SUCCESSFULLY CREATED!",
            JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception e) {
        try {
            connect.rollback(); // Rollback transaction if any exception occurs
        } catch (SQLException rollbackEx) {
            JOptionPane.showMessageDialog(null, "Error during rollback: " + rollbackEx.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    } finally {
        try {
            connect.setAutoCommit(true); // Restore default behavior
        } catch (SQLException autoCommitEx) {
            JOptionPane.showMessageDialog(null, "Error setting auto-commit: " + autoCommitEx.getMessage());
        }
    }
}

    public boolean login(String cUsername, String randPass){
        //make a query for getting values from the database para macheck if tama ang username and password
        String query = "SELECT * FROM client WHERE ClientUsername = ? AND RandPass = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, cUsername);
            pstmt.setString(2, randPass);

            ResultSet rs = pstmt.executeQuery();

             return rs.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage());
        }
        return false;     
    }
// tatanggalin ang method na 'to
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

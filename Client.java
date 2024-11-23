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
    private int clientID, meterID;
    private String contactNumber;
    private String location, clientStatus, cUsername, randPass, complaint;
    private Connection connect;
    private Component rootPane;

    //constructor
    public Client(){
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }

    public void setclientID(int clientID){
        this.clientID = clientID;
    }

    public int getclientID(){
        return clientID;
    }

    public void setclientStatus(String clientStatus){
        this.clientStatus = clientStatus;
    }

    public String getclientStatus(){
        //blocks of code on how to get client status

        return clientStatus;
    }

    //Rica: added setters and getters for contact number and location
    public void setcontactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }

    public String getcontactNumber(){
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
            String complaint1 = "Walang tubig samin.";
            String complaint2 = "Nasira ang metro namin, paki-ayos po.";
            String complaint3 = "Mahina ang tubig samin.";
            String complaint4 = "Malabo/hindi clear ang tubig samin.";
            String complaint5 = "May tagas po ang tubo dito samin, paki-ayos po.";
            
            
            
            
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
    
    public void updateInfo(String cUsername, String contactNumber, String location, String clientStatus, int clientID){
        int parameterIndex = 1;

        StringBuilder queryBuilder = new StringBuilder("UPDATE client SET ");
        boolean willUpdate = false;

        if (cUsername != null && !cUsername.isEmpty()) {
            queryBuilder.append("ClientUsername = ?, ");
            willUpdate = true;
        }
        if (contactNumber != null && !contactNumber.isEmpty()) {
            queryBuilder.append("ContactNumber = ?, ");
            willUpdate = true;
        }
        if (location != null && !location.isEmpty()) {
            queryBuilder.append("Location = ?, ");
            willUpdate = true;
        }
        if (clientStatus != null && !clientStatus.isEmpty()) {
            queryBuilder.append("ClientStatus = ?, ");
            willUpdate = true;
        }
        
        queryBuilder.setLength(queryBuilder.length() - 2);

        queryBuilder.append(" WHERE clientID = ?");
        String query = queryBuilder.toString();

        try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            if (clientID == 0) {
                throw new Exception("ClientID is required to fill.");
            }
            
            if (cUsername != null && !cUsername.isEmpty()) {
                preparedStatement.setString(parameterIndex++, cUsername);
            }
            if (contactNumber != null && !contactNumber.isEmpty()) {
                preparedStatement.setLong(parameterIndex++, Long.parseLong(contactNumber));
            }
            if (location != null && !location.isEmpty()) {
                preparedStatement.setString(parameterIndex++, location);
            }
            if (clientStatus != null && !clientStatus.isEmpty()) {
                preparedStatement.setString(parameterIndex++, clientStatus);
            }
            preparedStatement.setInt(parameterIndex, clientID);

            preparedStatement.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Changes are updated");
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    public void createAcc(String Location, String ContactNumber, String ClientStatus, String ClientUsername, String RandPass) {
    String createAccQuery = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, RandPass) VALUES (?, ?, ?, ?, ?)";
    String createMeterUsageQuery = "INSERT INTO meterusage (clientID, MeterUsage, balance) VALUES (?, 0, 0)";

    try {
        connect.setAutoCommit(false);

        try (PreparedStatement pstmt = connect.prepareStatement(createAccQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, Location);
            pstmt.setLong(2, Long.parseLong(ContactNumber));
            pstmt.setString(3, ClientStatus);
            pstmt.setString(4, ClientUsername);
            pstmt.setString(5, RandPass);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int clientID = generatedKeys.getInt(1);

                    try (PreparedStatement meterStmt = connect.prepareStatement(createMeterUsageQuery)) {
                        meterStmt.setInt(1, clientID);
                        meterStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        }

        connect.commit();

        JOptionPane.showMessageDialog(
            null,
            "Your generated password is: " + RandPass + "\nNote: Please save your password",
            "ACCOUNT SUCCESSFULLY CREATED!",
            JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception e) {
        try {
            connect.rollback();
        } catch (SQLException rollbackEx) {
            JOptionPane.showMessageDialog(null, "Error during rollback: " + rollbackEx.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    } finally {
        try {
            connect.setAutoCommit(true);
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
//tatanggalin din itu
    public void clientInfo(){
        //blocks of code that will display the date when client had water meter and all of his/her transaction
        
        System.out.println("\n Client Information ");
        System.out.println("Username:" + this.cUsername);
        //System.out.println("Houser Number: " + this.houseNumber);
        System.out.println("Contact Number: " + this.contactNumber);
        System.out.println("Location: " + this.location);
    }
    
}

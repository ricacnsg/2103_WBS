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
import java.util.ArrayList;

public class Client {
    MeterUsage meterusage = new MeterUsage();
    protected int clientID, meterID;
    private String contactNumber;
    private String location, clientStatus, cUsername, randPass, complaint, password;
    private boolean isActive;
    private Connection connect;
    private Component rootPane;

    //constructor
    public Client(){
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }
    
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    
    public void setpassword(String password){
        this.password = password;
    }
    
    public String getpassword(){
        return password;
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
        return complaint;
    }
    
 /*
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
*/
    
    public void updateInfo(String cUsername, String contactNumber, String location, String clientStatus, int clientID) {
    int parameterIndex = 1;

        // Build query for updating the client table
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

        // Remove the trailing comma and space
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE clientID = ?");

        String clientQuery = queryBuilder.toString();

        // Query for updating the meterUsage table
        String meterUsageQuery = "UPDATE meterusage SET ClientStatus = ? WHERE clientID = ?";

        try {
            if (clientID == 0) {
                throw new Exception("ClientID is required to fill.");
            }

            // Update the client table
            try (PreparedStatement clientPreparedStatement = connect.prepareStatement(clientQuery)) {
                if (cUsername != null && !cUsername.isEmpty()) {
                    clientPreparedStatement.setString(parameterIndex++, cUsername);
                }
                if (contactNumber != null && !contactNumber.isEmpty()) {
                    clientPreparedStatement.setLong(parameterIndex++, Long.parseLong(contactNumber));
                }
                if (location != null && !location.isEmpty()) {
                    clientPreparedStatement.setString(parameterIndex++, location);
                }
                if (clientStatus != null && !clientStatus.isEmpty()) {
                    clientPreparedStatement.setString(parameterIndex++, clientStatus);
                }
                clientPreparedStatement.setInt(parameterIndex, clientID);

                clientPreparedStatement.executeUpdate();
            }

            // Update the meterUsage table (only if clientStatus is provided)
            if (clientStatus != null && !clientStatus.isEmpty()) {
                try (PreparedStatement meterUsagePreparedStatement = connect.prepareStatement(meterUsageQuery)) {
                    meterUsagePreparedStatement.setString(1, clientStatus);
                    meterUsagePreparedStatement.setInt(2, clientID);

                    meterUsagePreparedStatement.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null, "Changes have been updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
}


   public int createAcc(String Location, String ContactNumber, String ClientStatus, String ClientUsername, String password) {
    String createAccQuery = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, password) VALUES (?, ?, ?, ?, ?)";
    String createMeterUsageQuery = "INSERT INTO meterusage (clientID, PrevReading, CurrentReading, balance, ClientStatus, Date) VALUES (?, 0, 0, 0, ?, NOW())";

    int clientID = -1;  // Initialize to -1 if creation fails
    try {
        connect.setAutoCommit(false);

        try (PreparedStatement pstmt = connect.prepareStatement(createAccQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, Location);
            pstmt.setLong(2, Long.parseLong(ContactNumber));
            pstmt.setString(3, ClientStatus);
            pstmt.setString(4, ClientUsername);
            pstmt.setString(5, password);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    clientID = generatedKeys.getInt(1);

                    try (PreparedStatement meterStmt = connect.prepareStatement(createMeterUsageQuery)) {
                        meterStmt.setInt(1, clientID);
                        meterStmt.setString(2, ClientStatus);
                        meterStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        }

        connect.commit();
/*
        JOptionPane.showMessageDialog(
            null,
            "Your generated password is: " + RandPass + "\nNote: Please save your password",
            "ACCOUNT SUCCESSFULLY CREATED!",
            JOptionPane.INFORMATION_MESSAGE
        );
*/
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

    return clientID;  // Return the clientID after successful account creation
}
   
    public boolean login(int clientID, String username, String password){
         boolean isValid = false;
        String query = "SELECT clientID FROM client WHERE clientID = ? AND ClientUsername = ? AND password = ?";
        
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {

            pstmt.setInt(1, clientID);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SharedData.clientID = rs.getInt("clientID");
                    ClientState.verifiedID = SharedData.clientID;
                    ClientState.isVerified = true;
                    isValid = true;
                } else {
                    ClientState.isVerified = false;
                    ClientState.verifiedID = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;    
    }

    public String getFormattedMeterUsageByClientID(int clientID) throws SQLException {
        //meterusage.getRandomReading(clientID); 

        StringBuilder meterUsageDetails = new StringBuilder();
        String query = "SELECT meterUsageID, PrevReading, CurrentReading, clientStatus, balance " +
                       "FROM meterusage WHERE clientID = ?";
        try (PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, clientID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int meterUsageID = resultSet.getInt("meterUsageID");
                int prevReading = resultSet.getInt("PrevReading");
                int currentReading = resultSet.getInt("CurrentReading");
                String clientStatus = resultSet.getString("clientStatus");
                double balance = resultSet.getDouble("balance");

                meterUsageDetails.append("Meter ID: ").append(meterUsageID).append("\n")
                                 .append("Prev Reading: ").append(prevReading).append("\n")
                                 .append("Current Reading: ").append(currentReading).append("\n")
                                 .append("Client Status: ").append(clientStatus).append("\n")
                                 .append("Balance: ").append(balance).append("\n")
                                 .append("-------------------------\n");
            }
        }

        return meterUsageDetails.toString();
    }

    
    public void saveComplaint(int clientID, String complainMsg){
            String query = "INSERT INTO Complaint (ClientID, ComplainMsg) VALUES (?, ?)";
            try (PreparedStatement pstmt = connect.prepareStatement(query)) {
                pstmt.setInt(1, clientID);
                pstmt.setString(2, complainMsg);
                
                               
                pstmt.executeUpdate();
                
            
                }catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    
    public ArrayList<String> filterPaymentByClientID(int clientID) {
    ArrayList<String> filteredPayments = new ArrayList<>();
    String query = "SELECT paymentID, clientID, input_payment, charges, total, paid_meter, payment_date FROM tpayment WHERE clientID = ?";

    try (PreparedStatement pstmt = connect.prepareStatement(query)) {
        pstmt.setInt(1, clientID);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
String row = rs.getInt("paymentID") + ", " 
            + rs.getInt("clientID") + ", " 
            + rs.getBigDecimal("input_payment") + ", " 
            + rs.getDouble("charges") + ", " 
            + rs.getBigDecimal("total") + ", " 
            + rs.getDouble("paid_meter") + ", " 
            + rs.getDate("payment_date");

            filteredPayments.add(row);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
    return filteredPayments;
}

   // public String password(String password) {
     //   throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    //}
    
    
    
    //PARA SA GUI2
     public boolean fetchActiveStatus(int clientID) {
        try {
            String query = "SELECT ClientStatus FROM client WHERE clientID = ?";
            PreparedStatement stmt = connect.prepareStatement(query);
            stmt.setInt(1, clientID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("ClientStatus"); 
                isActive = "ACTIVE".equalsIgnoreCase(status);
                return isActive;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 


    
        
        
}


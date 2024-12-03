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
import javax.swing.JLabel;


public class Client {
    MeterUsage meterusage = new MeterUsage();
    protected int clientID, meterID;
    private String contactNumber;
    private String location, clientStatus, cUsername, randPass, complaint, password;
    private boolean isActive;
    private Connection connect;
    private Component rootPane;

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
        return clientStatus;
    }

    public void setcontactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }

    public String getcontactNumber(){
        return contactNumber;
    }

    public void setlocation(String location){
        this.location = location;
    }

    public String getlocation(){
        return location;
    }

    public void setcUsername(String cUsername){
        this.cUsername = cUsername;
    }

    public String getcUsername(){
        return cUsername;
    }
    
    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }
    
    public String getComplaint() {
        return complaint;
    }
    
    


   public int createAcc(String Location, String ContactNumber, String ClientStatus, String ClientUsername, String password) {
    String createAccQuery = "INSERT INTO client (Location, ContactNumber, ClientStatus, ClientUsername, password) VALUES (?, ?, ?, ?, ?)";
    String createMeterUsageQuery = "INSERT INTO meterusage (clientID, PrevReading, CurrentReading, balance, ClientStatus, Date) VALUES (?, 0, 0, 0, ?, NOW())";

    int clientID = -1;  
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

    return clientID;  
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
    
    public void fetchClientInfo(int clientID, JLabel clientInfoLabel) {
        String query = "SELECT clientID, Location, ContactNumber, ClientStatus, ClientUsername, password FROM client WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("clientID");
                String location = rs.getString("Location");
                String contactNumber = rs.getString("ContactNumber");
                String clientStatus = rs.getString("ClientStatus");
                String clientUsername = rs.getString("ClientUsername");
                String password = rs.getString("password");

                // Format the client information as a string
                String clientInfo = "<html><b>Client ID:</b> " + id +
                                    "<br><b>Location:</b> " + location +
                                    "<br><b>Contact Number:</b> " + contactNumber +
                                    "<br><b>Client Status:</b> " + clientStatus +
                                    "<br><b>Username:</b> " + clientUsername + 
                                    "<br><b>Password:</b> " + password +"</html>";

                clientInfoLabel.setText(clientInfo);
            } else {
                clientInfoLabel.setText("<html><b>Error:</b> No client found with ID: " + clientID + "</html>");
            }
        } catch (SQLException e) {
            clientInfoLabel.setText("<html><b>Error:</b> " + e.getMessage() + "</html>");
        }
    }
    
    public void updateInfo(String cUsername, String contactNumber, String password, String location, String ClientStatus, int clientID) {
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
        if (ClientStatus != null && !ClientStatus.isEmpty()) {
            queryBuilder.append("ClientStatus = ?, ");
            willUpdate = true;
        }

        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE clientID = ?");

        String clientQuery = queryBuilder.toString();

        String meterUsageQuery = "UPDATE meterusage SET ClientStatus = ? WHERE clientID = ?";

        try {
            if (clientID == 0) {
                throw new Exception("ClientID is required to fill.");
            }

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
                if (ClientStatus != null && !ClientStatus.isEmpty()) {
                    clientPreparedStatement.setString(parameterIndex++, ClientStatus);
                }
                clientPreparedStatement.setInt(parameterIndex, clientID);

                clientPreparedStatement.executeUpdate();
            }

            if (ClientStatus != null && !ClientStatus.isEmpty()) {
                try (PreparedStatement meterUsagePreparedStatement = connect.prepareStatement(meterUsageQuery)) {
                    meterUsagePreparedStatement.setString(1, ClientStatus);
                    meterUsagePreparedStatement.setInt(2, clientID);

                    meterUsagePreparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
}

    
    public void saveComplaint(int clientID, String complainMsg){
        String query = "INSERT INTO complaint (ClientID, ComplainMsg) VALUES (?, ?)";

        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);  
            pstmt.setString(2, complainMsg);  

            pstmt.executeUpdate();  
        } catch (SQLException e) {
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


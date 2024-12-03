package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;

public class Admin {
    private String Name, username, password;
    private int AdminID, ContactInfo;
    private Connection connect;
    private Component rootPane;
    Client client = new Client();

    // Constructors
    public Admin() {
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
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

    public boolean Login(int adminID, String username, String password) {
        boolean isValid = false;
        String query = "SELECT * FROM admin WHERE username = ? AND password = ? AND admin_id = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, adminID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SharedData.adminID = rs.getInt("admin_id");
                    ClientState.verifiedID = SharedData.adminID;
                    ClientState.isVerified = true;
                    isValid = true;
                } else {
                    ClientState.isVerified = false;
                    ClientState.verifiedID = -1;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage());
        }
        return isValid;  
    }

    public boolean isAdminValid() {
        return AdminID > 0 && username != null && !username.isEmpty();
    }
       
    public ArrayList<String> retrieveClientInfo() {
        ArrayList<String> clientInfo = new ArrayList<>();

        String query = "SELECT c.clientID, m.meterUsageID, t.total, t.paid_meter, m.Date, c.ClientStatus " +
                       "FROM client c " +
                       "INNER JOIN meterusage m ON c.clientID = m.clientID " +
                       "LEFT JOIN tpayment t ON c.clientID = t.clientID";

        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }

                clientInfo.add(String.join(", ", row));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage());
        }
        return clientInfo;
    }
   
    public String[] fetchUnacknowledgedComplaint() throws Exception {
        String query = "SELECT clientID, complainMsg FROM complaint WHERE isAcknowledged = FALSE ORDER BY complaintID ASC LIMIT 1";
        try (PreparedStatement pstmt = connect.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String fetchedClientID = String.valueOf(rs.getInt("clientID"));
                String complaint = rs.getString("complainMsg");
                return new String[]{fetchedClientID, complaint};
            } else {
                throw new Exception("No unacknowledged complaints found.");
            }
        } catch (Exception e) {
            throw new Exception("Error fetching unacknowledged complaint: " + e.getMessage());
        }
    }

    public void acknowledgeComplaint(int clientID) throws Exception {
        String query = "UPDATE complaint SET isAcknowledged = TRUE WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("No complaint found for client ID: " + clientID);
            }
        } catch (SQLException e) {
            throw new Exception("Error acknowledging complaint: " + e.getMessage());
        }
    }
    
    public double getTotalPayments() throws SQLException {
        String query = "SELECT SUM(total) AS totalPaid FROM tpayment";
        double totalPayments = 0;

        try (PreparedStatement statement = connect.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                totalPayments = resultSet.getDouble("totalPaid");
            }
        }

        return totalPayments;
    }



}


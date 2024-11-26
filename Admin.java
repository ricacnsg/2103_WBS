package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;
import java.util.ArrayList;

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

    public boolean Login(String username, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

             return rs.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Error: " + e.getMessage());
        }
        return false;  
    }

    public boolean isAdminValid() {
        return AdminID > 0 && username != null && !username.isEmpty();
    }
       
    public ArrayList<String> retrieveClientInfo(){
        ArrayList<String> clientInfo = new ArrayList<>();
        String query = "SELECT c.clientID, m.meterUsageID, c.ClientUsername,  m.CurrentReading, c.ClientStatus" +
                        " FROM client c, meterusage m " +
                        "WHERE c.clientID = m.clientID";

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
}


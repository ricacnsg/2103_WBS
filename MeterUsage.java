package wbs_2103;

import java.time.LocalDate;
import java.util.Random;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;

public class MeterUsage{
    private int currentBalance, previousReading, currentReading;
    private LocalDate lastReadingDate;


    protected int meterID;

    protected String clientStatus;


    Random rand = new Random();
    
    private Connection connect;
    private Component rootPane;
    
    //constructor
    public MeterUsage(){

        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
            
    }
    
    public int getMeterID() {
        return meterID;
    }

    public void setMeterID(int meterID) {
        this.meterID = meterID;
    }

    public void setBalance(int currentBalance){
        this.currentBalance = currentBalance;
    }

    public int getBalance(){
        return currentBalance;
    }

    public void setCurrentReading(int currentReading){
        this.currentReading = currentReading;
    }

    public int getCurrentReading(){
        return currentReading;
    }

    public void setPrevReading(int previousReading){
        this.previousReading = previousReading;
    }

    public int getPrevReading(){
        return previousReading;
    }
    
    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }
    
    public LocalDate getLastReadingDate() {
        return lastReadingDate;
    }

    public void setLastReadingDate(LocalDate lastReadingDate) {
        this.lastReadingDate = lastReadingDate;
    }

    
    
public void updateReadings(int clientID) {
    try {
        String fetchQuery = "SELECT clientStatus, PrevReading, CurrentReading, Date FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
            fetchStmt.setInt(1, clientID);
            ResultSet rs = fetchStmt.executeQuery();

            if (rs.next()) {
                clientStatus = rs.getString("clientStatus");
                previousReading = rs.getInt("PrevReading");
                currentReading = rs.getInt("CurrentReading");
                lastReadingDate = rs.getDate("Date").toLocalDate();
            }
        }

        if ("ACTIVE".equalsIgnoreCase(clientStatus)) {
            LocalDate currentDate = LocalDate.now();
            
            // Only update if it's been more than 1 day since the last update
            if (lastReadingDate != null && lastReadingDate.isBefore(currentDate.minusDays(1))) {
                int increment = lastReadingDate.plusMonths(1).isBefore(currentDate)
                        ? (int) (Math.random() * (10 - 6 + 1)) + 6  // High usage if > 1 month overdue
                        : (int) (Math.random() * (5 - 1 + 1)) + 1;  // Regular usage

                int newReading = previousReading + increment;

                // Update the readings only if overdue
                String updateQuery = "UPDATE meterusage SET PrevReading = ?, CurrentReading = ?, Date = ? WHERE clientID = ?";
                try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, previousReading);
                    updateStmt.setInt(2, newReading);
                    updateStmt.setDate(3, java.sql.Date.valueOf(currentDate));
                    updateStmt.setInt(4, clientID);
                    updateStmt.executeUpdate();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Client is INACTIVE.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching/updating readings: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    
    public void getRandomReading(int clientID) {
    String clientStatus = "";
    try {
        String fetchQuery = "SELECT clientStatus, PrevReading, CurrentReading, Date " +
                            "FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
            fetchStmt.setInt(1, clientID);
            ResultSet rs = fetchStmt.executeQuery();

            int previousReading = 0;
            LocalDate lastReadingDate = null;
            if (rs.next()) {
                clientStatus = rs.getString("clientStatus");
                previousReading = rs.getInt("PrevReading");
                lastReadingDate = rs.getDate("Date").toLocalDate();
            }

            LocalDate currentDate = LocalDate.now();
            if ("ACTIVE".equalsIgnoreCase(clientStatus)) {
                if (lastReadingDate == null || lastReadingDate.isBefore(currentDate)) {
                    int increment;
                    if (lastReadingDate == null || lastReadingDate.plusDays(30).isBefore(currentDate)) {
                        increment = (int) (Math.random() * (10 - 6 + 1)) + 6; 
                    } else {
                        increment = (int) (Math.random() * (5 - 1 + 1)) + 1; 
                    }

                    int newReading = previousReading + increment;

                    // Instead of inserting a new row, update the existing one
                    String updateQuery = "UPDATE meterusage SET PrevReading = ?, CurrentReading = ?, Date = ? WHERE clientID = ?";
                    try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, previousReading);
                        updateStmt.setInt(2, newReading);
                        updateStmt.setDate(3, java.sql.Date.valueOf(currentDate));
                        updateStmt.setInt(4, clientID);
                        updateStmt.executeUpdate();
                    }
                }
            } else if ("INACTIVE".equalsIgnoreCase(clientStatus)) {
                JOptionPane.showMessageDialog(null, "Client status is INACTIVE.", "Status Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error updating reading: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
/*

    public void updatePrevReadingAfterPayment(int clientID) {
        try {
            // Step 1: Fetch the latest readings (Prev and Current) from the database
            String fetchQuery = "SELECT PrevReading, CurrentReading FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
            try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
                fetchStmt.setInt(1, clientID);
                ResultSet rs = fetchStmt.executeQuery();

                int prevReading = 0;
                int currentReading = 0;
                if (rs.next()) {
                    prevReading = rs.getInt("PrevReading");
                    currentReading = rs.getInt("CurrentReading");
                }

                // Step 2: Update the PrevReading for the next cycle (after payment or new reading)
                String updatePrevReadingQuery = "UPDATE meterusage SET PrevReading = ? WHERE clientID = ? AND CurrentReading = ?";
                try (PreparedStatement updatePrevStmt = connect.prepareStatement(updatePrevReadingQuery)) {
                    updatePrevStmt.setInt(1, currentReading);  // Set PrevReading as the current one
                    updatePrevStmt.setInt(2, clientID);
                    updatePrevStmt.setInt(3, currentReading);
                    updatePrevStmt.executeUpdate();
                }
            }

            // Step 3: Now, you can proceed with the balance calculation or new reading logic

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating PrevReading: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

*/
    


}



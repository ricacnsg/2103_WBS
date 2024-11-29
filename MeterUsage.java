package wbs_2103;

import java.time.LocalDate;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class MeterUsage{
    private int currentBalance, previousReading, currentReading;
    private LocalDate lastReadingDate;
    private long overDueDays;
    private double charge = 0;

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
    
    public long getOverdueDays() {
        return overDueDays;
    }

    // Method to calculate and store overdueDays
    public void calculateOverdueDays(LocalDate lastPaymentDate) {
        if (lastPaymentDate != null) {
            this.overDueDays = ChronoUnit.DAYS.between(lastPaymentDate, LocalDate.now());
        } else {
            this.overDueDays = 0; // Default to no overdue days
        }
    }
    
    //PARA SA GUI2
    
    public Map<String, Object> getMeterReadingDetails(int clientID) {
        Map<String, Object> details = new HashMap<>();
        try {
            // Fetch client active status
            Client client = new Client();
            boolean isActive = client.fetchActiveStatus(clientID);

            // Default values for new accounts
            double currentReading = 0.0;
            double previousReading = 0.0;
            double outstandingBalance = 0.0;
            String lastReadingDate = "No records yet";

            // Fetch actual details if available
            try {
                currentReading = fetchCurrentReading(clientID);
            } catch (Exception ignored) {
                currentReading = 0.0; // Default value if no record exists
            }

            try {
                previousReading = fetchPreviousReading(clientID);
            } catch (Exception ignored) {
                previousReading = 0.0; // Default value if no record exists
            }

            try {
                outstandingBalance = getBalance();
            } catch (Exception ignored) {
                outstandingBalance = 0.0; // Default value if no record exists
            }

            try {
                LocalDate lastDate = fetchLastReadingDate(clientID);
                if (lastDate != null) {
                    lastReadingDate = lastDate.toString();
                }
            } catch (Exception ignored) {
                lastReadingDate = "No records yet"; // Default value if no record exists
            }

            String todayDate = java.time.LocalDate.now().toString();

            // Populate details map
            details.put("isActive", isActive);
            details.put("currentReading", currentReading);
            details.put("lastReadingDate", lastReadingDate);
            details.put("previousReading", previousReading);
            details.put("outstandingBalance", outstandingBalance);
            details.put("todayDate", todayDate);
        } catch (Exception e) {
            details.put("error", "Error retrieving meter reading details: " + e.getMessage());
        }
        return details;
    }





        // Fetch Current Reading
    public double fetchCurrentReading(int clientID) throws Exception {
        String query = "SELECT CurrentReading FROM meterusage WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.currentReading = rs.getInt("CurrentReading");
                    return this.currentReading;
                }
            }
        }
        return 0;
    }

    // Fetch Previous Reading
    public double fetchPreviousReading(int clientID) throws Exception {
        String query = "SELECT PrevReading FROM meterusage WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.previousReading = rs.getInt("PrevReading");
                    return this.previousReading;
                }
            }
        }
        return 0;
    }
    
    public LocalDate fetchLastReadingDate(int clientID) throws Exception {
        String query = "SELECT payment_date FROM tpayment WHERE clientID = ? ORDER BY payment_date DESC LIMIT 1";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID); // Set the clientID parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.lastReadingDate = rs.getDate("payment_date").toLocalDate();
                    return this.lastReadingDate; // Return the latest payment date
                }
            }
        }
        return null; // Return null if no payment date is found
    }

    // Update Readings
    // may aayusin pa dapat dito dapat mauupdate din sha habang natakbo ang oras
/*
    public void updateReadings(int clientID, double newReading) throws Exception {
        String query = "UPDATE meterusage SET PrevReading = CurrentReading, CurrentReading = ? WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setDouble(1, newReading);
            pstmt.setInt(2, clientID);
            pstmt.executeUpdate();
        }
    }
*/
public void updateReadings(int clientID) throws Exception {
    String fetchQuery = "SELECT CurrentReading, Date FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
    String updateQuery = "UPDATE meterusage SET CurrentReading = ?, Date = ? WHERE clientID = ?";

    try {
        double currentReading = 0;
        LocalDateTime lastReadingDateTime = null;

        // Fetch the current reading and last update timestamp
        try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
            fetchStmt.setInt(1, clientID);
            try (ResultSet rs = fetchStmt.executeQuery()) {
                if (rs.next()) {
                    currentReading = rs.getDouble("CurrentReading");
                    lastReadingDateTime = rs.getTimestamp("Date").toLocalDateTime();
                }
            }
        }

        if (lastReadingDateTime != null) {
            // Calculate seconds elapsed since the last update
            LocalDateTime now = LocalDateTime.now();
            long secondsElapsed = java.time.Duration.between(lastReadingDateTime, now).getSeconds();

            if (secondsElapsed > 0) {
                // Add 0.25 cubic meters for every second
                double additionalUsage = secondsElapsed * 0.000005; 
                double newReading = currentReading + additionalUsage;

                // Update the current reading in the database
                try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                    updateStmt.setDouble(1, newReading);
                    updateStmt.setTimestamp(2, java.sql.Timestamp.valueOf(now));
                    updateStmt.setInt(3, clientID);
                    updateStmt.executeUpdate();
                }
            }
        }
    } catch (SQLException e) {
        throw new Exception("Error updating readings: " + e.getMessage(), e);
    }
}

    public void updatePreviousReading(int clientID) throws SQLException {
        String updatePreviousReadingQuery = "UPDATE meterusage SET PrevReading = CurrentReading WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(updatePreviousReadingQuery)) {
            pstmt.setInt(1, clientID);
            pstmt.executeUpdate();
        }
    }





    // Calculate Amount
    public double calculateAmount(double cubicMeterUsage) {
        return cubicMeterUsage * 15; // Assume cost is 25 per cubic meter
    }
    
    public double calculateCharges(long overDueDays) {
        charge = overDueDays * 10;
        return charge;
    }
    
    public LocalDate fetchLastPaymentDate(int clientID) throws Exception {
        String query = "SELECT MAX(payment_date) AS lastPaymentDate FROM tpayment WHERE clientID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Date date = rs.getDate("lastPaymentDate");
                    if (date != null) {
                        return date.toLocalDate();
                    }
                }
            }
        }
        return LocalDate.now().minusDays(1);
    }

}

 /*   
    
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


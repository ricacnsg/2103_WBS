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

    public void calculateOverdueDays(LocalDate lastPaymentDate) {
        if (lastPaymentDate != null) {
            this.overDueDays = ChronoUnit.DAYS.between(lastPaymentDate, LocalDate.now());
        } else {
            this.overDueDays = 0; 
        }
    }
    
    public Map<String, Object> getMeterReadingDetails(int clientID) {
        Map<String, Object> details = new HashMap<>();
        try {
            Client client = new Client();
            boolean isActive = client.fetchActiveStatus(clientID);

            double currentReading = 0.0;
            double previousReading = 0.0;
            double outstandingBalance = 0.0;
            String lastReadingDate = "No records yet";


            try {
                currentReading = fetchCurrentReading(clientID);
            } catch (Exception ignored) {
                currentReading = 0.0; 
            }

            try {
                previousReading = fetchPreviousReading(clientID);
            } catch (Exception ignored) {
                previousReading = 0.0; 
            }

            try {
                outstandingBalance = getBalance();
            } catch (Exception ignored) {
                outstandingBalance = 0.0; 
            }

            try {
                LocalDate lastDate = fetchLastReadingDate(clientID);
                if (lastDate != null) {
                    lastReadingDate = lastDate.toString();
                }
            } catch (Exception ignored) {
                lastReadingDate = "No records yet";
            }

            String todayDate = java.time.LocalDate.now().toString();

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
            pstmt.setInt(1, clientID); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.lastReadingDate = rs.getDate("payment_date").toLocalDate();
                    return this.lastReadingDate; 
                }
            }
        }
        return null; 
    }

    public void updateReadings(int clientID) throws Exception {
    String fetchQuery = "SELECT CurrentReading, Date FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
    String updateQuery = "UPDATE meterusage SET CurrentReading = ?, Date = ? WHERE clientID = ?";
    String statusQuery = "SELECT ClientStatus FROM client WHERE clientID = ?"; 

    try {
        double currentReading = 0;
        LocalDateTime lastReadingDateTime = null;
        String clientStatus = "ACTIVE";  

        try (PreparedStatement statusStmt = connect.prepareStatement(statusQuery)) {
            statusStmt.setInt(1, clientID);
            try (ResultSet rs = statusStmt.executeQuery()) {
                if (rs.next()) {
                    clientStatus = rs.getString("ClientStatus");  // Assuming the status is stored as a string
                }
            }
        }


        if ("INACTIVE".equalsIgnoreCase(clientStatus)) {
            System.out.println("Client is inactive. No updates will be made.");
            return;  
        }

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
            LocalDateTime now = LocalDateTime.now();
            long secondsElapsed = java.time.Duration.between(lastReadingDateTime, now).getSeconds();

            if (secondsElapsed > 0) {
                double additionalUsage = secondsElapsed * 0.00001; 
                double newReading = currentReading + additionalUsage;

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


    public double calculateAmount(double cubicMeterUsage) {
        return cubicMeterUsage * 15; 
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

 


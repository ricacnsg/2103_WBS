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

public class Meterusage{
    //Client client = new Client();
    private int currentBalance, previousReading, currentReading;
    private LocalDate lastReadingDate;
    protected int meterID;

    protected String clientStatus;


    Random rand = new Random();
    
    private Connection connect;
    private Component rootPane;
    
    //constructor
    public Meterusage(){
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

    // Increment and save random reading based on client status
    public void getRandomReading(int clientID) {
    String clientStatus = "";
    try {
        // Fetch the current client status, reading, and last reading date
        String fetchQuery = "SELECT clientStatus, MeterUsage, Date " +
                            "FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
        try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
            fetchStmt.setInt(1, clientID);
            ResultSet rs = fetchStmt.executeQuery();

            int previousReading = 0;
            LocalDate lastReadingDate = LocalDate.now().minusDays(31); // Default to a date outside the range
            if (rs.next()) {
                clientStatus = rs.getString("clientStatus");
                previousReading = rs.getInt("MeterUsage");
                lastReadingDate = rs.getDate("Date").toLocalDate();
            }

            if ("ACTIVE".equalsIgnoreCase(clientStatus)) {
                int increment;
                if (lastReadingDate.plusDays(30).isBefore(LocalDate.now())) {
                    increment = (int) (Math.random() * (10 - 6 + 1)) + 6; // Random increment between 6 and 10
                } else {
                    increment = (int) (Math.random() * (5 - 1 + 1)) + 1; // Random increment between 1 and 5
                }

                int newReading = previousReading + increment;
                LocalDate currentDate = LocalDate.now();

                // Update the existing meter usage record in the database
                String updateQuery = "UPDATE meterusage SET MeterUsage = ?, Date = ? " +
                                     "WHERE clientID = ? AND Date = (SELECT MAX(Date) FROM meterusage WHERE clientID = ?)";
                try (PreparedStatement updateStmt = connect.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, newReading);
                    updateStmt.setDate(2, java.sql.Date.valueOf(currentDate));
                    updateStmt.setInt(3, clientID);
                    updateStmt.setInt(4, clientID);
                    updateStmt.executeUpdate();
                }
            } else if ("INACTIVE".equalsIgnoreCase(clientStatus)) {
                JOptionPane.showMessageDialog(null, "Client status is INACTIVE.", "Status Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error updating reading: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    


}

/*
    public void displayMeterUsage(){
        changeDate(-30);

        if("ACTIVE".equals(this.clientStatus)){
            System.out.println("\nClient's Meter ID: " + meterID);
            System.out.println("Client Meter Status: " + clientStatus);
            System.out.println("Date Today: " + LocalDate.now());
            System.out.println("Date of Last Reading: " + LocalDate.now().minusDays(30));
            System.out.println("Previous Reading(last reading): " + getPrevReading());
            System.out.println("Current Reading(as of today): " + getCurrentReading() + "\n");
        }
        else if("INACTIVE".equals(clientStatus)){
            System.out.println("Client's Meter ID: " + meterID);
            System.out.println("Balance to Pay: " + currentBalance);
        }
    }
*/

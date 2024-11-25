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

    public void getRandomReading(int clientID) {
        String clientStatus = "";
        try {
            String fetchQuery = "SELECT clientStatus, MeterUsage, Date " +
                                "FROM meterusage WHERE clientID = ? ORDER BY Date DESC LIMIT 1";
            try (PreparedStatement fetchStmt = connect.prepareStatement(fetchQuery)) {
                fetchStmt.setInt(1, clientID);
                ResultSet rs = fetchStmt.executeQuery();

                int previousReading = 0;
                LocalDate lastReadingDate = null;
                if (rs.next()) {
                    clientStatus = rs.getString("clientStatus");
                    previousReading = rs.getInt("MeterUsage");
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

                        // Insert a new meter usage record into the database
                        String insertQuery = "INSERT INTO meterusage (clientID, MeterUsage, Date) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = connect.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, clientID);
                            insertStmt.setInt(2, newReading);
                            insertStmt.setDate(3, java.sql.Date.valueOf(currentDate));
                            insertStmt.executeUpdate();
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

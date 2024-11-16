package wbs_2103;

import java.time.LocalDate;
import java.util.Random;

public class MeterUsage{
    
    private int currentBalance, previousReading, currentReading;
    private LocalDate lastReadingDate;
    protected int meterID;
    protected String clientStatus;
    Random rand = new Random();
    
    //constructor
    public MeterUsage(int currentBalance, int previousReading, int currentReading, Client client){
        this.meterID = client.getmeterID();
        this.clientStatus = client.getclientStatus();
        this.previousReading = previousReading;
        this.currentBalance = currentBalance;
        this.currentReading = currentReading;

        //Rica: deduct the days to test if it works
        this.lastReadingDate = LocalDate.now().minusDays(30);
            
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
    
    //Rica: added changeDate method for testing
    public void changeDate(int days){
        lastReadingDate = lastReadingDate.plusDays(days);
        getReading();
    }

    public void getReading(){
        if("ACTIVE".equals(this.clientStatus)){      
            if(lastReadingDate.plusDays(30).isBefore(LocalDate.now())){
                int i = rand.nextInt(6, 10);
                currentReading += previousReading + i;
                lastReadingDate = LocalDate.now();
            }
            else{
                int i = rand.nextInt(1, 5);
                currentReading += previousReading + i;
                lastReadingDate = LocalDate.now();
            }
        }
        else if("INACTIVE".equals(clientStatus)){
            System.out.println("You're current status is " + clientStatus);
        }
    }

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
}

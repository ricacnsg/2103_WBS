import java.time.LocalDate;

public class MeterUsage {
    private int currentBalance, currentReading;
    private LocalDate date;

    //constructor
    public MeterUsage(int currentBalance, int currentReading){
        this.currentBalance = currentBalance;
        this.currentReading = currentReading;
        this.date = LocalDate.now();
    }

    //Rica: added setters and getters for currentBalance and currentReading
    public void setBalance(int currentBalance){
        this.currentBalance = currentBalance;
    }

    public int getBalance(){
        //blocks of code checking if client have balance amount to pay

        return currentBalance;
    }

    public void setReading(int currentReading){
        this.currentReading = currentReading;
    }

    public int getReading(){
        //blocks of code getting the current reading of client's meter

        return currentReading;
    }

    public void displayMeterUsage(){
        //blocks of code showing client's meter usage

        System.out.println("Date Today: " + date);
    }


}

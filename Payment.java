package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import wbs_2103.Control_Connector.DBConnect;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Payment {

    private String PaymentMethod, Pin;
    private double ratePerUnit = 15.0;
    private double charges = 20.0;
    private int usage;


    private int clientID, PaymentDate;
    private double BalancethisMonth, InputPayment, CurrentBalance, change;
    private Connection connect;
    private Component rootPane;
    
    // Constructor
    public Payment() {
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }

    // Getters and Setters
    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    public String getPin() {
        return "****"; // Concealed pin
    }

    public void setPin(String Pin) {
        if (Pin == null || Pin.length() != 4 || !Pin.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid PIN. PIN must be a 4-digit number.");
        }
        this.Pin = Pin;
    }
    
    public int getusage(){
        return usage;
    }
    
   public void setusage(int usage){
       this.usage = usage;
   }

    public int getClientID() {
        return clientID;
    }

    public void setHouseNumber(int clientID) {
        this.clientID = clientID;
    }

    public int getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(int PaymentDate) {
        this.PaymentDate = PaymentDate;
    }

    public double getBalancethisMonth() {
        return BalancethisMonth;
    }

    public void setBalancethisMonth(double BalancethisMonth) {
        this.BalancethisMonth = BalancethisMonth;
    }

    public double getInputPayment() {
        return InputPayment;
    }

    public void setInputPayment(int InputPayment) {
        this.InputPayment = InputPayment;
    }

    public double getCurrentBalance() {
        return CurrentBalance;
    }

    public void setCurrentBalance(double CurrentBalance) {
        this.CurrentBalance = CurrentBalance;
    }
    
    public double getRatePerUnit() {
        return ratePerUnit;
    }

    public void setRatePerUnit(double ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }
    
    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }
    
    public void setPaymentAmount(double InputPayment) {
        this.InputPayment = InputPayment;
    }

    public double getChange() {
        return change;
    }

    // Check if the payment is sufficient
    public boolean isPaymentSufficient() {
        return InputPayment >= getBalancethisMonth();
    }

    // Calculate change
    public double calculateChange() {
        if (isPaymentSufficient()) {
            change = InputPayment - getBalancethisMonth();
        } else {
            change = 0;
        }
        return change;
    }

    // Get the remaining balance if payment is insufficient
    public double getRemainingBalance() {
        return getBalancethisMonth() - InputPayment;
    }

    // Process payment (you can connect this to a database or just simulate it)
    public String processPayment() {
        if (isPaymentSufficient()) {
            return "Payment successful! Change: " + String.format("%.2f", calculateChange());
        } else {
            return "Payment failed! Insufficient amount. Remaining balance: " + String.format("%.2f", getRemainingBalance());
        }
    }


    // Method to calculate the bill and update balance
 public void updatePaymentFields(int clientID) {
MeterUsage meterUsage = new MeterUsage();
    meterUsage.updateReadings(clientID); // Fetch readings (current & previous)

    setusage(meterUsage.getCurrentReading() - meterUsage.getPrevReading());
    double balanceThisMonth = usage * ratePerUnit;
    setBalancethisMonth(balanceThisMonth);

    // Handle overdue charges (late fees)
    LocalDate lastReadingDate = meterUsage.getLastReadingDate();
    long monthsOverdue = 0;
    if (lastReadingDate != null) {
        monthsOverdue = ChronoUnit.MONTHS.between(lastReadingDate, LocalDate.now());
    }

    if (monthsOverdue > 0) {
        charges = 10.0 * monthsOverdue;  // Example: $10 per month overdue
        JOptionPane.showMessageDialog(null, "Charges applied: " + charges, "Overdue Charges", JOptionPane.INFORMATION_MESSAGE);
    } else {
        charges = 0.0;
        JOptionPane.showMessageDialog(null, "No overdue charges.", "Overdue Charges", JOptionPane.INFORMATION_MESSAGE);
    }

    // Update balance with charges
    double currentBalance = balanceThisMonth + charges;
    setCurrentBalance(currentBalance);
}



    // Method to update the balance in the database
    private void updateBalanceInDatabase(int clientID, double newBalance) {
        // Add logic to update the balance in the database here
        // Example: Using a SQL query to update balance for the client
        try {
            String updateBalanceQuery = "UPDATE meterusage SET balance = ? WHERE clientID = ?";
            try (PreparedStatement updateStmt = connect.prepareStatement(updateBalanceQuery)) {
                updateStmt.setDouble(1, newBalance);
                updateStmt.setInt(2, clientID);
                updateStmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "Balance updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating balance: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to process payment online
    public void payOnline() {
        switch (PaymentMethod.toLowerCase()) {
            case "gcash":
                processGcashPayment();
                break;
            case "paymaya":
                processPaymayaPayment();
                break;
            case "paypal":
                processPaypalPayment();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid payment method. Please select GCash, Paymaya, or Paypal.", "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Gcash payment
    private void processGcashPayment() {
        JOptionPane.showMessageDialog(null, "Processing payment via GCash...");
    }

    // Paymaya payment
    private void processPaymayaPayment() {
        JOptionPane.showMessageDialog(null, "Processing payment via Paymaya...");
    }

    // Paypal payment
    private void processPaypalPayment() {
        JOptionPane.showMessageDialog(null, "Processing payment via Paypal...");
    }
    
     public void insertPaymentData(int clientID, int meterUsageID, double paymentAmount, double total, double charges, double change, int paid_meter) {
        // SQL query to insert payment details
        String query = "INSERT INTO tpayment (clientID, input_payment, current_balance, balanacethismonth, charges, total, sukli, paid_meter) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        
        try (PreparedStatement ps = connect.prepareStatement(query)) {
            // Set parameters
            ps.setInt(1, clientID);               // clientID
            //ps.setInt(2, meterUsageID);           // meterUsageID
            ps.setDouble(2, paymentAmount);       // input_payment
            ps.setDouble(3, total);               // current_balance
            ps.setDouble(4, BalancethisMonth);    // balance_this_month
            ps.setDouble(5, charges);             // charges
            ps.setDouble(6, total);               // total
            ps.setDouble(7, change);              // change
            ps.setInt(8, getusage());

            // Execute the query
            ps.executeUpdate();
            
            // Optionally show a message or log the success
            JOptionPane.showMessageDialog(null, "Payment successfully recorded.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error recording payment: " + e.getMessage());
        }
    }

    // Method to generate the receipt
    public void generateReceipt() {
        JOptionPane.showMessageDialog(null, "Receipt:\n" +
                "clientID: " + clientID + "\n" +
                "Payment Date: " + PaymentDate + "\n" +
                "Payment Method: " + PaymentMethod + "\n" +
                "Amount Paid: " + InputPayment + "\n" +
                "Remaining Balance: " + CurrentBalance);
    }
}

    


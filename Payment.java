package wbs_2103;

import java.sql.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import wbs_2103.Control_Connector.DBConnect;

public class Payment {
    MeterUsage meterusage = new MeterUsage();
    private String PaymentMethod, Pin;
    private double ratePerUnit = 15.0;
    private double charges = 20.0;

    private int meterID;

    public int getMeterID() {
        return meterID;
    }

    public void setMeterID(int meterID) {
        this.meterID = meterusage.getMeterID();
    }
    private int clientID;
    private LocalDate paymentDate; // Changed from int to LocalDate for better date handling
    private double BalancethisMonth, InputPayment, CurrentBalance, change;
    private Connection connect;

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

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
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

    public void setInputPayment(double InputPayment) {
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

    public double getChange() {
        return change;
    }

    // Check if the payment is sufficient
    public boolean isPaymentSufficient() {
        return InputPayment >= BalancethisMonth;
    }

    // Calculate change
    public double calculateChange() {
        if (isPaymentSufficient()) {
            change = InputPayment - BalancethisMonth;
        } else {
            change = 0;
        }
        return change;
    }

    // Get the remaining balance if payment is insufficient
    public double getRemainingBalance() {
        return BalancethisMonth - InputPayment;
    }

    // Process payment
    public String processPayment(int clientID, int meterID, double inputPayment) {
        this.clientID = clientID;
        this.meterID = meterID;
        this.InputPayment = inputPayment;

        // Check if payment is sufficient
        if (inputPayment >= BalancethisMonth) {
            double change = inputPayment - BalancethisMonth;
            updatePaymentInDatabase(change); // Insert payment into the database
            return "Payment successful! Change: " + String.format("%.2f", change);
        } else {
            double remainingBalance = BalancethisMonth - inputPayment;
            updatePaymentInDatabase(0); // Insert payment into the database with no change
            return "Payment failed! Insufficient amount. Remaining balance: " + String.format("%.2f", remainingBalance);
        }
    }

    // Method to update payment details in the database
    private void updatePaymentInDatabase(double change) {
        try {
            String insertPaymentQuery = "INSERT INTO payment_table (meterID, paymentdate, payment_method, balanacethismonth, input_payment, current_balance) VALUES (?, ?, ?, ?, ?, ?)";

            LocalDate paymentDate = LocalDate.now(); // Get current date
            double newBalance = BalancethisMonth - InputPayment; // Calculate remaining balance

            try (PreparedStatement pstmt = connect.prepareStatement(insertPaymentQuery)) {
                pstmt.setInt(1, meterID); // meterID
                pstmt.setDate(2, java.sql.Date.valueOf(paymentDate)); // paymentdate
                pstmt.setString(3, PaymentMethod); // payment_method (change to dynamic input)
                pstmt.setDouble(4, BalancethisMonth); // balanacethismonth
                pstmt.setDouble(5, InputPayment); // input_payment
                pstmt.setDouble(6, newBalance); // current_balance
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Payment processed successfully.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error processing payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to set balance for the month
    public void setBalanceThisMonth(double balance) {
        this.BalancethisMonth = balance;
    }

    // Method to get the balance for this month
    public double getBalanceThisMonth() {
        return this.BalancethisMonth;
    }

    // Method to update the balance in the database
    private void updateBalanceInDatabase(int clientID, double newBalance) {
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

    // Method to generate the receipt
    public void generateReceipt() {
        JOptionPane.showMessageDialog(null, "Receipt:\n" +
                "clientID: " + clientID + "\n" +
                "Payment Date: " + paymentDate + "\n" +
                "Payment Method: " + PaymentMethod + "\n" +
                "Amount Paid: " + InputPayment + "\n" +
                "Remaining Balance: " + CurrentBalance);
    }
}


    


package wbs_2103;

import java.awt.Component;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import wbs_2103.Control_Connector.DBConnect;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Payment {

    private String PaymentMethod, Pin;
    private double ratePerUnit = 15.0;
    private double charges = 20.0;
    private int usage;


    private int clientID, PaymentDate;
    private double BalancethisMonth, InputPayment, CurrentBalance, change;
    private Connection connect;
    private Component rootPane;
    
    public Payment() {
        DBConnect dbconnect = new DBConnect();
        this.connect = dbconnect.getConnection();
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    public String getPin() {
        return Pin;
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

    public boolean isPaymentSufficient() {
        return InputPayment >= getBalancethisMonth();
    }

    public double calculateChange() {
        if (isPaymentSufficient()) {
            change = InputPayment - getBalancethisMonth();
        } else {
            change = 0;
        }
        return change;
    }
    
    public void recordPayment(int clientID, double payment, double charges, double totalPaid, double paid_meter, double change, String payment_method) throws Exception {
        String query = "INSERT INTO tpayment (clientID, input_payment, charges, total, paid_meter, payment_date, sukli, payment_method) VALUES (?, ?, ?, ?, ?, NOW(), ?, ?)";
        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setInt(1, clientID);
            pstmt.setDouble(2, payment);
            pstmt.setDouble(3, charges);
            pstmt.setDouble(4, totalPaid);
            pstmt.setDouble(5, paid_meter);
            pstmt.setDouble(6, change);
            pstmt.setString(7, payment_method);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error inserting payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  
}

    


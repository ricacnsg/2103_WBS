package wbs_2103;
public class Payment {

    private String PaymentMethod, Pin;
    private int HouseNumber, PaymentDate, BalancethisMonth, InputPayment, CurrentBalance;
    
    //constructor
    public Payment(String PaymentMethod, int HouseNumber, int PaymentDate, int BalancethisMonth, int InputPayment){
        this.PaymentMethod = PaymentMethod;
        this.HouseNumber = HouseNumber;
        this.PaymentDate = PaymentDate;
        this.BalancethisMonth = BalancethisMonth;
        this.InputPayment = InputPayment;
        this.CurrentBalance = CurrentBalance;
        this.Pin = Pin;
        
    }
    
    public void setPin(String Pin){
        if (Pin == null || Pin.length() != 4 || !Pin.matches("\\d+")){
            throw new IllegalArgumentException("Invalid PIN. PIN must be a 4-digit number.");
        }
        
        this.Pin = Pin;
    }
    
    public String getPin(){
        return "****";
    }
    
    public void payOnline(){
        switch (PaymentMethod.toLowerCase()){
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
                System.out.println("Invalid payment method, Please select GCash, Paymaya, or Paypal");
        }
        
        if (InputPayment <= CurrentBalance){
            CurrentBalance -= InputPayment;
            generateReceipt();
        } else{
            System.out.println("Payment amount exceeds current balance, Payment failed.");
        }

    }
    //Gcash
    private void processGcashPayment(){
        System.out.println("Processing payment via GCash...");
    }
    //Paymaya
    private void processPaymayaPayment(){
        System.out.println("Processing payment via Paymaya...");
    }
    //Paypal
    private void processPaypalPayment(){
        System.out.println("Processing payment via Paypal...");
    }
    public void generateReceipt(){
        System.out.println("Receipt:");
        System.out.println("House Number: " + HouseNumber);
        System.out.println("Payment Date: " + PaymentDate);
        System.out.println("Payment Method: " + PaymentMethod);
        System.out.println("Amount Paid: " + InputPayment);
        System.out.println("Remaining Balance: " + CurrentBalance);
    }
        
    }
    


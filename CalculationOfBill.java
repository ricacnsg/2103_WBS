package wbs_2103;

public class CalculationOfBill {
    private int HouseNumber, PrevReading, CurrentReading, BalancethisMonth, CurrentBalance;
    
    private static final double RATE_PER_CUBIC_METER = 15.0; // 15pesos per cubic meter
    private static final double PENALTY_FEE = 20.0; 
    
    //true if late, false if on time
    private boolean isLatePayment;
    
    //Constructor
    public CalculationOfBill(int HouseNumber, int PrevReading, int CurrentReading, int CurrentBalance) {
        this.HouseNumber = HouseNumber;
        this.PrevReading = PrevReading;
        this.CurrentReading = CurrentReading;
        this.CurrentBalance = CurrentBalance;
        this.isLatePayment = isLatePayment;
    }

    public void calculateBill(){
        int usageInCubicMeters = CurrentReading - PrevReading;
        double BalanceThisMonth = (int)(usageInCubicMeters * RATE_PER_CUBIC_METER);
        double TotalBalance = BalanceThisMonth + CurrentBalance;
        
        System.out.println("House Number: " + HouseNumber);
        System.out.println("Previous Reading: " + PrevReading + "cubic meters");
        System.out.println("Current Reading: " + CurrentReading + "cubic meters");
        System.out.println("Water Usage: " + usageInCubicMeters + "cubic meters");
        System.out.println("Bill for This Month: ₱" + BalanceThisMonth);
        System.out.println("Total Balance Due: ₱" + TotalBalance);
        

    }

    public void charges(){
        double totalCharges = CurrentBalance;
        
        if (isLatePayment){
            System.out.println("Penalty Fee: ₱" + PENALTY_FEE);
            totalCharges += PENALTY_FEE;
        }
        
        System.out.println("Total Charges: ₱" + totalCharges);
    }
}

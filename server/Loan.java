package newbank.server;
import java.util.UUID;

public class Loan {
    private String loanId;
    private String loanerAccNum;
    private String borrowerAccNum;
    private double amount;

    public Loan(String loanerAccNum, String borrowerAccNum, double amount) {
        this.loanId = UUID.randomUUID().toString();
        this.loanerAccNum = loanerAccNum;
        this.borrowerAccNum = borrowerAccNum;
        this.amount = amount;
    }

    public String getLoanId() {
        return this.loanId;
    }

    public String getLoanerAccNum() {
        return this.loanerAccNum;
    }

    public String getBorrowerAccNum() {
        return this.borrowerAccNum;
    }

    public double getLoanAmount() { return this.amount; }
}

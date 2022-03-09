package newbank.server;
import java.util.UUID;

public class Loan {
    private String loanId;
    private Account loanerAcc;
    private Account borrowerAcc;
    private double amount;

    public Loan(Account loanerAcc, Account borrowerAcc, double amount) {
        this.loanId = UUID.randomUUID().toString();
        this.loanerAcc = loanerAcc;
        this.borrowerAcc = borrowerAcc;
        this.amount = amount;
    }

    public String getLoanId() {
        return this.loanId;
    }

    public Account getLoanerAcc() {
        return this.loanerAcc;
    }

    public Account getBorrowerAcc() {
        return this.borrowerAcc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

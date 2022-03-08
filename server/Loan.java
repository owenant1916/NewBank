package newbank.server;
import java.util.UUID;

public class Loan {
    private String loanId;
    private String loanerAccName;
    private String borrowerAccName;
    private double amount;

    public Loan(String loanerAccName, String borrowerAccName, double amount) {
        this.loanId = UUID.randomUUID().toString();
        this.loanerAccName = loanerAccName;
        this.borrowerAccName = borrowerAccName;
        this.amount = amount;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getLoanerAccName() {
        return loanerAccName;
    }

    public String getBorrowerAccName() {
        return borrowerAccName;
    }
}

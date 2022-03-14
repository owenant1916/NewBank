package newbank.server;

import java.util.ArrayList;

public class LoanLedger {
    private ArrayList<Loan> loans;

    public LoanLedger() {
        this.loans = new ArrayList<Loan>();
    }

    public Loan getLoan(String ID) {
        for (int i = 0; i < this.loans.size(); i++) {
            if(loans.get(i).getLoanId() == ID) {
                return loans.get(i);
            }
        }
        return null;
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
    }

    public ArrayList<Loan> getLoans() {
        return loans;
    }
}

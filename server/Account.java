package newbank.server;
import java.util.ArrayList;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private double currentBalance;
	private ArrayList<Loan> loans;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
	}

	public void createLoan(Account borrower, double amount) {
		// update account balances with loan amount
		this.currentBalance -= amount;
		borrower.currentBalance += amount;
		// create new instance of Loan
		Loan newLoan = new Loan(this, borrower, amount);
		// add instance of Loan to both accounts
		this.loans.add(newLoan);
		borrower.loans.add(newLoan);
	}

	public void repayLoan(Loan loan, double paymentAmount) {
		// if the payment amount is over the loan or equal to it
		if(paymentAmount >= loan.getAmount()) {
			// then update accounts by full loan amount
			this.currentBalance -= loan.getAmount();
			loan.getLoanerAcc().currentBalance += loan.getAmount();
			// remove the loan from the account arrays
			this.loans.remove(this.loans.indexOf(loan));
			loan.getLoanerAcc().loans.remove(this.loans.indexOf(loan));
		}
		// if the payment amount is under the loan amount
		else {
			// update balances with payment amount
			this.currentBalance -= paymentAmount;
			loan.getLoanerAcc().currentBalance += paymentAmount;
			// update loan with outstanding amount
			loan.setAmount(loan.getAmount() - paymentAmount);
		}
	}

	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public String getAccountName() {
		return accountName;
	}
}
//test
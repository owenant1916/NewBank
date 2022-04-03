package newbank.server;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Account {
	
	private String accountName;
	private String accountNum;
	private double openingBalance;
	private double currentBalance;
	private ArrayList<String> loans;

	//list of transactions associated with the account
	private ArrayList<Double> deposits;
	private ArrayList<Double> withdrawals;

	public Account(String accountName, String accountNum, double openingBalance, double currentBalance,
			ArrayList<Double> deposits, ArrayList<Double> withdrawals) {
		this.accountName = accountName;
		this.accountNum = accountNum;
		this.openingBalance = openingBalance;
		this.currentBalance = currentBalance;
		this.accountNum = accountNum;
		this.deposits = deposits;
		this.withdrawals = withdrawals;
	}

	public String toString() {
		return (accountName + ": " + currentBalance);
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getAccountNum() {
		return accountNum;
	}

	public String getAccountName() {
		return accountName;
	}

	public ArrayList<String> getLoans() {
		return loans;
	}

	public void addCashFromLoan(double amt) { currentBalance += amt;};

	public void loan_removeCash(double amount) { currentBalance-=amount; };

	public void loan_addCash(double amount) { currentBalance+= amount; };

	public void deposit(double depositAmt)
	{
		this.currentBalance += depositAmt;
		this.deposits.add(depositAmt);

		//update account in account database
		AccountDatabase accDb = new AccountDatabase();
		accDb.update(this);
	}

	public void withdraw(double withdrawalAmt)
	{
		this.currentBalance -= withdrawalAmt;
		this.withdrawals.add(withdrawalAmt);

		//update account in account database
		AccountDatabase accDb = new AccountDatabase();
		accDb.update(this);
	}

	public ArrayList<Double> getDepositsHistory() {
		return deposits;
	}

	public ArrayList<Double> getWithdrawalsHistory() {
		return withdrawals;
	}

	public void setDepositsHistory(ArrayList<Double> deposits) {
		this.deposits = deposits;
	}

	public void setWithdrawalsHistory(ArrayList<Double> withdrawals) {
		this.withdrawals = withdrawals;
	}
}

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
		return (accountName + ": " + openingBalance);
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance += currentBalance;
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

	public void deposit(Double depositAmt)
	{
		this.currentBalance += depositAmt;
		this.deposits.add(depositAmt);
	}

	public void withdrawal(Double withdrawalAmt)
	{
		this.currentBalance -= withdrawalAmt;
		this.withdrawals.add(withdrawalAmt);
	}
}

package newbank.server;
import java.util.ArrayList;

public class Account {
	
	private String accountName;
	private String accountNum;
	private double openingBalance;
	private double currentBalance;
	private ArrayList<String> loans;

	public Account(String accountName, double openingBalance, String accountNum) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
		this.accountNum = accountNum;
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
}

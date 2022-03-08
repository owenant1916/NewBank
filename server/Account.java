package newbank.server;

import java.util.ArrayList;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private ArrayList<Loan> loans;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

}

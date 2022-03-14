package newbank.server;
import java.util.ArrayList;

public class Customer extends User{
	private ArrayList<Account> accounts;
	private ArrayList<String> loans;

	public Customer(String name) {
		super(name);
		accounts = new ArrayList<Account>();
		loans = new ArrayList<String>();
	}

	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public String getUserType() { return "customer";};

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public ArrayList<String> getLoans() {
		return loans;
	}

	public void addLoan(String loanId) {
		this.loans.add(loanId);
	}

}

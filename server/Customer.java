package newbank.server;
import java.util.ArrayList;

public class Customer extends User{
	
	private ArrayList<Account> accounts;
	
	public Customer(String name) {
		super(name);
		accounts = new ArrayList<>();
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
}

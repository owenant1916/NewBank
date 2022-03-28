package newbank.server;
import java.util.ArrayList;

public class Customer extends User{
	//customer info
	private Integer age;
	private String address;
	private Integer income;

	//customer account information, accounts indexed by account number and list of associated loans
	private ArrayList<Account> accounts;
	private ArrayList<String> loans;

	public Customer(String name, String password, String customerID, Integer age, String address,
			Integer income, ArrayList<Account> accounts) {
		super(name,password, customerID);
		this.age = age;
		this.address = address;
		this.income = income;
		this.accounts = accounts;
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

	public String getAddress(){
		return this.address;
	}

	public Integer getIncome(){
		return this.income;
	}

	public Integer getAge(){
		return this.age;
	}
}

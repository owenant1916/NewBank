package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private LoanLedger loanLedger;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("Bhagy");
		bhagy.addAccount(new Account("Main", 1000.0, "88305634"));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer("Christina");
		christina.addAccount(new Account("Savings", 1500.0, "46284039"));
		customers.put("Christina", christina);
		
		Customer john = new Customer("John");
		john.addAccount(new Account("Checking", 250.0, "00194762"));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	public void processLoan(double amount, String borrowerAccNum) {
		String loanerAccNum = findLoaner(amount); // find loaner and remove money from their account
		findAccountByNum(borrowerAccNum).setCurrentBalance(amount); // find borrower's account and add money to it

		Loan newLoan = new Loan(loanerAccNum, borrowerAccNum, amount); // Create loan object with above info
		findCustomerByAcc(loanerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		findCustomerByAcc(borrowerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		this.loanLedger.addLoan(newLoan); // Add to ledger
	}


	// Searches for a suitable account to loan the money from
	public String findLoaner(double amount) {
		for(Map.Entry<String, Customer> set : customers.entrySet()) {
			ArrayList<Account> customerAccounts = set.getValue().getAccounts();
			// iterate over arraylist to find an account with at least 10 * the loan amount
			for(int i = 0; i < customerAccounts.size(); i++) {
				if(customerAccounts.get(i).getCurrentBalance() > (amount * 10)) {
					customerAccounts.get(i).setCurrentBalance(0 - amount); // update account balance of loaner
					return customerAccounts.get(i).getAccountNum(); // return account num
				}
			}
		}
		return null;
	}

	// method for finding an Account object but its account number
	public Account findAccountByNum(String accountNum) {
		for(Map.Entry<String, Customer> set : customers.entrySet()) {
			ArrayList<Account> customerAccounts = set.getValue().getAccounts();
			for(int i = 0; i < customerAccounts.size(); i++) {
				if(customerAccounts.get(i).getAccountNum() == accountNum) {
					return customerAccounts.get(i);
				}
			}
		}
		System.out.println("This account does not exist.");
		return null;
	};

	// Method for finding a customer by their account number
	public Customer findCustomerByAcc(String accountNum) {
		for(Map.Entry<String, Customer> set : customers.entrySet()) {
			ArrayList<Account> customerAccounts = set.getValue().getAccounts();
			for(int i = 0; i < customerAccounts.size(); i++) {
				if(customerAccounts.get(i).getAccountNum() == accountNum) {
					return set.getValue();
				}
			}
		}
		System.out.println("This account does not exist.");
		return null;
	};

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}

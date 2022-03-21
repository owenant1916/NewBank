package newbank.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<UserID,Customer> customers;
	private HashMap<UserID,BankManager> bankManagers;
	private LoanLedger loanLedger;

	//member variables to hold potential requests for both user types
	private HashMap<Integer, String> customerRequests;
	private HashMap<Integer, String> bankManagerRequests;

	private NewBank() {
		this.customers = new HashMap<>();
		this.bankManagers = new HashMap<>();
		this.loanLedger = new LoanLedger();
		addTestData();

		//populate possible requests from customer request config files
		BufferedReader customerRequestReader = null;
		try {
			customerRequestReader = new BufferedReader(new FileReader("./src/newbank/testing/CustomerRequests"));
		}catch (IOException e){
			e.printStackTrace();
			System.out.println("Customer request file not found.");
			System.exit(0);
		}

		try {
			String line, key, processName;
			this.customerRequests = new HashMap<Integer, String>();
			while ((line = customerRequestReader.readLine()) != null) {
				// process the line.
				key = line.substring(1,2);
				processName = line.substring(3, line.length()-1);
				this.customerRequests.put(Integer.parseInt(key),processName);
			}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Failure reading customer request file.");
			System.exit(0);
		}

		//populate possible requests from customer request config files
		BufferedReader bankManagerRequestReader = null;
		try {
			bankManagerRequestReader = new BufferedReader(new FileReader("./src/newbank/testing/BankManagerRequests"));
		}catch (IOException e){
			e.printStackTrace();
			System.out.println("Bank Manager request file not found.");
			System.exit(0);
		}

		try {
			String line, key, processName;
			this.bankManagerRequests = new HashMap<Integer, String>();
			while ((line = bankManagerRequestReader.readLine()) != null) {
				// process the line.
				key = line.substring(1,2);
				processName = line.substring(3, line.length()-1);
				this.bankManagerRequests.put(Integer.parseInt(key),processName);
			}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Failure reading Bank Manager request file.");
			System.exit(0);
		}
	}

	public LoanLedger getLoanLedger() {
		return loanLedger;
	}

	public HashMap<Integer, String> getCustomerRequests(){
		return this.customerRequests;
	}

	public HashMap<Integer, String> getBankManagerRequests(){
		return this.bankManagerRequests;
	}

	private void addTestData() {
		//get customer data from database
		CustomerDatabase customerDb = new CustomerDatabase();
		customers = customerDb.getCustomers();

		//get bank manager data from database
		BankManagerDatabase managerDb = new BankManagerDatabase();
		bankManagers = managerDb.getBankManagers();

		//testing code for loan functionality
		customers.get(new UserID("1001")).addAccount(new Account("Main", 1000.0, "88305634"));
		customers.get(new UserID("1002")).addAccount(new Account("Savings", 1500.0, "46284039"));
		customers.get(new UserID("1003")).addAccount(new Account("Checking", 250.0, "00194762"));
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized User checkLogInDetails(String userName, String password) {
		//check through customers for a match
		for(Customer cust: customers.values()) {
			if (cust.getName().equals(userName) && cust.getPassword().equals(password)) {
				return cust;
			}
		}

		//check through bank managers for a match
		for(BankManager manager: bankManagers.values()) {
			if (manager.getName().equals(userName) && manager.getPassword().equals(password)) {
				return manager;
			}
		}

		return null;
	}

	public String processLoan(double amount, String borrowerAccNum) {
		Utilities utilities = new Utilities(customers); // created here for now so that it uses up-to-date customer info
		String loanerAccNum = findLoaner(amount, borrowerAccNum); // find loaner and remove money from their account
		utilities.findAccountByNum(borrowerAccNum).setCurrentBalance(amount); // find borrower's account and add money to it
		Loan newLoan = new Loan(loanerAccNum, borrowerAccNum, amount); // Create loan object with above info
		utilities.findCustomerByAcc(loanerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		utilities.findCustomerByAcc(borrowerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		this.loanLedger.addLoan(newLoan); // Add to ledger
		System.out.println("Success! Account: " + borrowerAccNum + "'s balance is now " +
				utilities.findAccountByNum(borrowerAccNum).getCurrentBalance());
		return "Account: " + borrowerAccNum + "'s balance is now " + utilities.findAccountByNum(borrowerAccNum).getCurrentBalance();
	}


	// Searches for a suitable account to loan the money from
	public String findLoaner(double amount, String borrowerAccNum) {
		for(Map.Entry<UserID, Customer> set : customers.entrySet()) {
			ArrayList<Account> customerAccounts = set.getValue().getAccounts();
			// iterate over arraylist to find an account with at least 10 * the loan amount
			for(int i = 0; i < customerAccounts.size(); i++) {
				if(customerAccounts.get(i).getCurrentBalance() > (amount * 2)) // check account has enough {
					if(!customerAccounts.get(i).getAccountNum().equals(borrowerAccNum)) { // check not borrower's account
						customerAccounts.get(i).setCurrentBalance(0 - amount); // update account balance of loaner
						return customerAccounts.get(i).getAccountNum(); // return account num
					}
				}
			}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(User loggedInUser, String request) {
		Scanner myObj = new Scanner(System.in);
		if (loggedInUser.getUserType().equals("customer")) {
			switch (request) {
				//needs to be maintained in sync with request files
				case "1":
					return showMyAccounts(loggedInUser.getUserID());
				//case "2" : return depositCash();
				// case "3" : return withdrawCash();
				case "4":
					System.out.println("Enter the account number of the account you wish to deposit money into");
					String borrowerAccNum = myObj.nextLine().toString();

					System.out.println("Enter the amount you wish to borrow");
					Double amount = myObj.nextDouble();
					myObj.close();
					return processLoan(amount, "00194762");
				//case "5" : repayLoan(blah blah);
				default:
					return "FAIL";
			}
		}else if(loggedInUser.getUserType().equals("bank manager")){
			switch (request) {
				//needs to be maintained in sync with request files
				//case "1": return createAccount();
				//case "2" :return deleteAccount();
				default:
					return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(UserID customer) {
		return (customers.get(customer).accountsToString());
	}

}

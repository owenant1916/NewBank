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
		loadData();

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

	private void loadData() {
		//get customer data from database
		CustomerDatabase customerDb = new CustomerDatabase();
		customers = customerDb.getCustomers();

		//get bank manager data from database
		BankManagerDatabase managerDb = new BankManagerDatabase();
		bankManagers = managerDb.getBankManagers();
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
		System.out.println("Success! " + utilities.findAccountByNum(borrowerAccNum).getAccountName()
				+ " balance is now " + utilities.findAccountByNum(borrowerAccNum).getCurrentBalance());
		return utilities.findAccountByNum(borrowerAccNum).getAccountName() + " balance is now " +
				utilities.findAccountByNum(borrowerAccNum).getCurrentBalance();
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
		Scanner myScanner = new Scanner(System.in);
		ArrayList<Account> accounts = customers.get(loggedInUser.getUserID()).getAccounts();
		if (loggedInUser.getUserType().equals("customer")) {
			switch (request) {
				//needs to be maintained in sync with request files
				case "1":
					return showMyAccounts(loggedInUser.getUserID());
				case "2" : return depositCash(loggedInUser);
				case "3" : return withdrawCash(loggedInUser);
				case "4":
					// User chooses an account
					System.out.println("Select the account from which you wish to request a loan");
					for(int i = 0; i < accounts.size(); i++) {
						System.out.println((i + 1) + " - " + accounts.get(i).toString()); // added 1 sp
					}
					int account = myScanner.nextInt();

					// User enters loan amount
					System.out.println("Enter the requested loan amount");
					Double amount = myScanner.nextDouble();
					myScanner.close();
					return processLoan(amount, accounts.get(account - 1).getAccountNum());
//				case "5": repay loan
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

	private String depositCash(User customer) {
		Scanner myScanner = new Scanner(System.in);

		//ask customer to choose account
		System.out.println("Select the account you wish to deposit cash:");
		Customer cust = (Customer) customer;
		ArrayList<Account> custAccounts = cust.getAccounts();
		for(int i = 0; i < custAccounts.size(); i++) {
			System.out.println((i + 1) + " - " + custAccounts.get(i).toString()); // added 1 sp
		}
		int selection = myScanner.nextInt();

		//ask for deposit amount
		System.out.println("Enter the deposit amount");
		Double depositAmt = myScanner.nextDouble();
		myScanner.close();

		//add amount to account and also into database and register deposit transaction
		boolean cashDeposited = cust.depositInAccount(depositAmt, custAccounts.get(selection-1).getAccountNum());
		if (cashDeposited) {
			return "Cash deposited.";
		}else{
			return "FAIL";
		}
	}

	private String withdrawCash(User customer) {
		Scanner myScanner = new Scanner(System.in);

		//ask customer to choose account
		System.out.println("Select the account you wish to withdraw cash from:");
		Customer cust = (Customer) customer;
		ArrayList<Account> custAccounts = cust.getAccounts();
		for(int i = 0; i < custAccounts.size(); i++) {
			System.out.println((i + 1) + " - " + custAccounts.get(i).toString()); // added 1 sp
		}
		int selection = myScanner.nextInt();

		//ask for deposit amount
		System.out.println("Enter the withdrawal amount");
		Double withdrawalAmt = myScanner.nextDouble();
		myScanner.close();

		//add amount to account and also into database and register deposit transaction
		boolean cashWithdrawn = cust.withdrawFromAccount(withdrawalAmt, custAccounts.get(selection-1).getAccountNum());
		if (cashWithdrawn) {
			return "Cash withdrawn.";
		}else{
			return "FAIL";
		}
	}
}

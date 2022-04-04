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

	//process functions to perform operations for client
	public synchronized String showMyAccounts_process(User user) {
		Customer cust = (Customer) user;
		return cust.accountsToString();
	}

	public synchronized String depositCash_process(double depositAmt, String accountNum,
			Customer cust) {
		//add amount to account and also into database and register deposit transaction
		boolean cashDeposited = cust.depositInAccount(depositAmt,accountNum);
		if (cashDeposited) {
			return "Cash deposited.";
		}else{
			return "FAIL";
		}
	}

	public synchronized String withdrawCash_process(double withdrawalAmt, String accountNum, Customer cust) {
		//add amount to account and also into database and register deposit transaction
		boolean cashWithdrawn = cust.withdrawFromAccount(withdrawalAmt, accountNum);
		if (cashWithdrawn) {
			return "Cash withdrawn.";
		}else{
			return "FAIL";
		}
	}
	public synchronized String Loan_process(double amount, String borrowerAccNum) {
		Utilities utilities = new Utilities(customers); // created here for now so that it uses up-to-date customer info
		String loanerAccNum = findLoaner(amount, borrowerAccNum); // find loaner and remove money from their account
		//utilities.findAccountByNum(borrowerAccNum).setCurrentBalance(amount); // find borrower's account and add money to it
		utilities.findAccountByNum(borrowerAccNum).addCashFromLoan(amount); // find borrower's account and add money to it
		Loan newLoan = new Loan(loanerAccNum, borrowerAccNum, amount); // Create loan object with above info
		utilities.findCustomerByAcc(loanerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		utilities.findCustomerByAcc(borrowerAccNum).addLoan(newLoan.getLoanId()); // Add automatically generated loan ID to accounts
		this.loanLedger.addLoan(newLoan); // Add to ledger
		System.out.println("Success! " + utilities.findAccountByNum(borrowerAccNum).getAccountName()
				+ " balance is now " + utilities.findAccountByNum(borrowerAccNum).getCurrentBalance());
		return utilities.findAccountByNum(borrowerAccNum).getAccountName() + " balance is now " +
				utilities.findAccountByNum(borrowerAccNum).getCurrentBalance();
	}

	public synchronized  String repayLoan_process(String loanID, String paidFromAccNum) {
		Utilities utilities = new Utilities(customers);
		double loanAmount = loanLedger.getLoan(loanID).getLoanAmount();
		//remove cash from account to pay for loan
		utilities.findAccountByNum(paidFromAccNum).loan_removeCash(loanAmount);
		//add cash to loaner account
		utilities.findAccountByNum(loanLedger.getLoan(loanID).getLoanerAccNum()).loan_addCash(loanAmount);
		//remove loan IDs from both customers
		utilities.findCustomerByAcc(paidFromAccNum).removeLoan(loanID);
		utilities.findCustomerByAcc(loanLedger.getLoan(loanID).getLoanerAccNum()).removeLoan(loanID);
		//remove loan from ledger
		this.loanLedger.removeLoan(loanLedger.getLoan(loanID));
		return "Success! Loan paid " + utilities.findAccountByNum(paidFromAccNum).getAccountName() + " balance is now " +
				utilities.findAccountByNum(paidFromAccNum).getCurrentBalance();
				
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
}

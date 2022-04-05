package newbank.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
//------Auto-check-in code - can be deleted later-------
import java.io.FileReader;
//------------------------------------------------------
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
//-------------------------------------------
import java.io.FileNotFoundException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	//------Auto-check-in code - can be deleted later-------
	private BufferedReader in_auto_checkin;
	//------------------------------------------------------
	private PrintWriter out;
	private Scanner myScanner;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = bank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		myScanner = new Scanner(in);
		//------Auto-check-in code - can be deleted later-------
		try {
			in_auto_checkin = new BufferedReader(
					new FileReader("./src/newbank/testing/AutoCheckIn_UserCredentials"));
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("User credentials file not found.");
			System.exit(0);
		}
		//------------------------------------------------------
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			User loggedInUser = runLogIn();
			// if the user is authenticated then get requests from the user and process them 
			if(loggedInUser != null) {
				//display initial interface
				out.println("Log In Successful.");
				interfaceDisplay(loggedInUser);
				//get user requests
				while(true) {
					String request = in.readLine();
					System.out.println("Request from " + loggedInUser.getUserID().getKey());
					//select option chosen by user
					if (loggedInUser.getUserType().equals("customer")) {
						switch (request) {
							//needs to be maintained in sync with request files
							case "1":  showMyAccounts_Interface(loggedInUser); break;
							case "2" : depositCash_Interface(loggedInUser); break;
							case "3" : withdrawCash_Interface(loggedInUser); break;
							case "4":  Loan_Interface(loggedInUser); break;
							case "5" : Repayloan_Interface(loggedInUser); break;
							case "6": transactionStatement_Interface(loggedInUser); break;
							case "7": changePasswordInterface(loggedInUser); break;
				    default:
							System.out.println("FAIL");
						}
					}else if(loggedInUser.getUserType().equals("bank manager")){
						switch (request) {
							//needs to be maintained in sync with request files
							//case "1": return createAccount_Interface(); break;
							//case "2" :return deleteAccount_Interface(); break;
							default:
								System.out.println("FAIL");
						}
					}
				}
			}
			else {
				out.println("Log In Failed");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	//interface and input collection functions for handling client IO
	private User runLogIn(){
		String userName = "";
		String password = "";
		try {
			// ask for user name
			out.println("Enter Username");
			//------Updated Auto-check-in code - can be reverted later-------
			//userName = in.readLine();
			userName = in_auto_checkin.readLine();
			//---------------------------------------------------------------
			// ask for password
			out.println("Enter Password");
			//------Updated Auto-check-in code - can be reverted later-------
			// password = in.readLine();
			password = in_auto_checkin.readLine();
			//---------------------------------------------------------------
			out.println("Checking Details...");
		}catch (IOException e){
			System.out.println("Log-in failed.");
			System.exit(0);
		}

		// authenticate user and user object from bank for use in subsequent requests
		User user = bank.checkLogInDetails(userName, password);
		return user;
	}

	private void showMyAccounts_Interface(User user){
		String response = bank.showMyAccounts_process(user);
		out.println(response);
		String response1 = bank.showCustomerLoans_process(user);
		out.println("Assgined loans:");
		out.println(response1);
	}

	private void depositCash_Interface(User user){
		//ask customer to choose account
		out.println("Select the account you wish to deposit cash:");
		Customer cust = (Customer) user;
		ArrayList<Account> custAccounts = cust.getAccounts();
		for(int i = 0; i < custAccounts.size(); i++) {
			out.println((i + 1) + " - " + custAccounts.get(i).toString()); // added 1 sp
		}
		int selection = myScanner.nextInt();

		//ask for deposit amount
		out.println("Enter the deposit amount");
		Double depositAmt = myScanner.nextDouble();

		//perform fraud check
		Account acc = custAccounts.get(selection-1);
		String response = "";
		ArrayList<Double> depositsHist = acc.getDepositsHistory();
		double avgDeposit = 0;
		if (depositsHist.size() >= 12){
			for(int i=0; i < depositsHist.size(); i++) {
				avgDeposit += depositsHist.get(i);
			}
			avgDeposit = avgDeposit/(1.0 * depositsHist.size());
		}
		if (depositAmt > 5 * acc.getOpeningBalance() || (avgDeposit >0 && depositAmt > 5 * avgDeposit)){
			response = "Deposit exceeds fraud threshold. Please contact Bank Manager to proceed";
		}else{
			response = bank.depositCash_process(depositAmt, acc.getAccountNum(), cust);
		}
		out.println(response);
	}

	private void withdrawCash_Interface(User user){
		//ask customer to choose account
		out.println("Select the account you wish to withdraw cash from:");
		Customer cust = (Customer) user;
		ArrayList<Account> custAccounts = cust.getAccounts();
		for(int i = 0; i < custAccounts.size(); i++) {
			out.println((i + 1) + " - " + custAccounts.get(i).toString()); // added 1 sp
		}
		int selection = myScanner.nextInt();

		//ask for withdrawal amount
		out.println("Enter the withdrawal amount");
		Double withdrawalAmt = myScanner.nextDouble();

		//perform fraud check
		Account acc = custAccounts.get(selection-1);
		String response = "";
		ArrayList<Double> withdrawalHist = acc.getWithdrawalsHistory();
		double avgWithdrawal = 0;
		if (withdrawalHist.size() >= 12){
			for(int i=0; i < withdrawalHist.size(); i++) {
				avgWithdrawal += withdrawalHist.get(i);
			}
			avgWithdrawal = avgWithdrawal/ (1.0 * withdrawalHist.size());
		}
		if (withdrawalAmt > 5 * acc.getOpeningBalance() || (avgWithdrawal > 0 && withdrawalAmt > 5 * avgWithdrawal)){
			response = "Withdrawal exceeds fraud threshold. Please contact Bank Manager to proceed";
		}else{
			response = bank.withdrawCash_process(withdrawalAmt, acc.getAccountNum(), cust);
		}
		out.println(response);
	}

	private void Loan_Interface(User user) {
		Customer cust = (Customer) user;
		ArrayList<Account> accounts = cust.getAccounts();
		// User chooses an account
		out.println("Select the account from which you wish to request a loan");
		for(int i = 0; i < accounts.size(); i++) {
			out.println((i + 1) + " - " + accounts.get(i).toString()); // added 1 sp
		}
		int account = myScanner.nextInt();

		// User enters loan amount
		out.println("Enter the requested loan amount");
		Double amount = myScanner.nextDouble();

		String response = bank.Loan_process(amount, accounts.get(account-1).getAccountNum());
		out.println(response);
	}

	private void Repayloan_Interface(User user) {
		Customer cust = (Customer) user;
		ArrayList<Account> accounts = cust.getAccounts();
		ArrayList<String> loans = cust.getLoans();
		if(accounts.size()==0) {
			out.println("Error! Logged in user does have any registered accounts");
			return;
		}
		if(loans.size()==0) {
			out.println("Error! Logged in user does not have any assigned loans");
			return;
		}
		out.println("Select which loan you would like to repay");
		for (int i = 0; i < loans.size();i++) {
			out.println((i + 1) + " - " + loans.get(i));
		}
		String loanToPayID = loans.get(myScanner.nextInt()-1);
		out.println("select which account do you want to pay from");
		for(int i = 0; i < accounts.size(); i++) {
			out.println((i + 1) + " - " + accounts.get(i).toString());
		}
		String accountNumToPayFrom = accounts.get(myScanner.nextInt()-1).getAccountNum();
		String response = bank.repayLoan_process(loanToPayID, accountNumToPayFrom);
		out.println(response);
	}

	private void transactionStatement_Interface(User user){
		Customer cust = (Customer) user;
		//output transactions to screen and file
		ArrayList<Account> accounts = cust.getAccounts();
		//Write JSON file
		try (BufferedWriter file = new BufferedWriter(new FileWriter("./out/TransactionHistory"))) {
			for (int i = 0; i < accounts.size(); i++) {
				//output account name
				String accName = accounts.get(i).getAccountName() + ":";
				out.println(accName);
				file.write(accName);
				file.newLine();
				//output deposits
				out.println("Deposits");
				file.write("Deposits");
				file.newLine();
				out.println(accounts.get(i).getDepositsHistory());
				ArrayList<Double> depositHist = accounts.get(i).getDepositsHistory();
				String deposits = "";
				for (int j = 0; j < depositHist.size(); j++){
					deposits += depositHist.get(i).toString() + ",";
				}
				file.write(deposits);
				file.newLine();
				//output withdrawals
				out.println("Withdrawals");
				file.write("Withdrawals");
				file.newLine();
				out.println(accounts.get(i).getWithdrawalsHistory());
				ArrayList<Double> withdrawalHist = accounts.get(i).getWithdrawalsHistory();
				String withdrawals = "";
				for (int j = 0; j < withdrawalHist.size(); j++){
					withdrawals += withdrawalHist.get(i).toString() + ",";
				}
				file.write(withdrawals);
			}
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to output transactions files.");
			System.exit(0);
		}
	}

	private void changePasswordInterface (User user){
		out.println("Enter your password:");
		String newPassword = myScanner.next();

		List<String> list = new ArrayList<String>();

		try {
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("./src/newbank/data/CustomerData"));

			FileWriter file = new FileWriter("./src/newbank/data/CustomerData");

			for (Object o : jsonArray) {
				JSONObject person = (JSONObject) o;
				String name = (String) person.get("name");
				if (user.getName().equals(name)) {
					person.remove("password");
					person.put("password", newPassword);
					list.add(person.toString());
				} else {
					list.add(person.toString());
				}
			}
			file.write(list.toString());
			file.flush();
			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		out.println("Your password has been changed to: " + newPassword);

	}

	//function to provide a nicer interface on the command line
	private void interfaceDisplay(User user){
		out.println("Welcome " + user.getName() + " to New Bank!");
		out.println("Please choose from the following options.....");
		//output possible requests based on user type
		HashMap<Integer, String> requests = null;
		if (user.getUserType() == "bank manager"){
			requests = bank.getBankManagerRequests();
		}else if (user.getUserType() == "customer"){
			requests = bank.getCustomerRequests();

			//in the case of a customer logging in also display account balances
			Customer castedUser = (Customer)user;
			out.println(castedUser.accountsToString());
		}
		for (Map.Entry<Integer, String> entry : requests.entrySet()) {
			Integer intKey = entry.getKey();
			String process = entry.getValue();
			out.println(intKey.toString() + "." + process);
		}
	}
}

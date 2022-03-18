package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//------Auto-check-in code - can be deleted later-------
import java.io.FileReader;
//------------------------------------------------------
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	//------Auto-check-in code - can be deleted later-------
	private BufferedReader in_auto_checkin;
	//------------------------------------------------------
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
		System.out.println(bank.findCustomerByAcc("46284039").getAccounts().get(0).getCurrentBalance());
		System.out.println(bank.findCustomerByAcc("46284039").getLoans());
		System.out.println(bank.findCustomerByAcc("88305634").getLoans());
		bank.processLoan(120, "88305634");
		System.out.println(bank.findCustomerByAcc("46284039").getAccounts().get(0).getCurrentBalance());
		System.out.println(bank.findCustomerByAcc("46284039").getLoans());
		System.out.println(bank.findCustomerByAcc("88305634").getLoans());
		System.out.println(bank.getLoanLedger().getLoans().get(0).getBorrowerAccNum());
		try {
			// ask for user name
			out.println("Enter Username");
			//------Updated Auto-check-in code - can be reverted later-------
			//String userName = in.readLine();
			String userName = in_auto_checkin.readLine();
			//---------------------------------------------------------------
			// ask for password
			out.println("Enter Password");
			//------Updated Auto-check-in code - can be reverted later-------
			//String password = in.readLine();
			String password = in_auto_checkin.readLine();
			//---------------------------------------------------------------
			out.println("Checking Details...");
			// authenticate user and user object from bank for use in subsequent requests
			User user = bank.checkLogInDetails(userName, password);
			// if the user is authenticated then get requests from the user and process them 
			if(user != null) {
				out.println("Log In Successful.");
				interfaceDisplay(user);
				while(true) {
					String request = in.readLine();
					System.out.println("Request from " + user.getUserID().getKey());
					String response = bank.processRequest(user, request);
					out.println(response);
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

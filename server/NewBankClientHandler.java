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
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customer = bank.checkLogInDetails(userName, password);
			// if the user is authenticated then get requests from the user and process them 
			if(customer != null) {
				out.println("Log In Successful.");
				interfaceDisplay(userName);
				while(true) {
					String request = in.readLine();
					System.out.println("Request from " + customer.getKey());
					String response = bank.processRequest(customer, request);
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
	private void interfaceDisplay(String userName){
		out.println("Welcome " + userName + " to New Bank!");
		out.println("Please choose from the following options.....");
		//TODO: make user type dependent on who logged in
		String userType = "customer";
		//output possible requests based on user type
		HashMap<Integer, String> requests = null;
		if (userType == "bank manager"){
			requests = bank.getBankManagerRequests();
		}else if (userType == "customer"){
			requests = bank.getCustomerRequests();
		}
		for (Map.Entry<Integer, String> entry : requests.entrySet()) {
			Integer intKey = entry.getKey();
			String process = entry.getValue();
			out.println(intKey.toString() + "." + process);
		}
	}

}

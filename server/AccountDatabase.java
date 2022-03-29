package newbank.server;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AccountDatabase {
  //this map contains all the accounts keyed using the account number
  private HashMap<String, Account> accounts;

  public AccountDatabase(){
    //read in account data as JSON files and create account objects
    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    accounts = new HashMap<String,Account>();

    try {
      //Read JSON file
      FileReader reader = new FileReader("./src/newbank/data/AccountData");
      Object obj = jsonParser.parse(reader);

      JSONArray accountList = (JSONArray) obj;

      //Iterate over account array
      accountList.forEach( acc -> parseAccount( (JSONObject) acc) );

    }catch (IOException e){
      e.printStackTrace();
      System.out.println("Account data file not found.");
      System.exit(0);
    } catch (ParseException e) {
      e.printStackTrace();
      System.out.println("Account data file failed to parse.");
      System.exit(0);
    }
  }

  private void parseAccount(JSONObject accountObject){
    //get account info
    String accName = (String) accountObject.get("account name");
    String accNum = (String) accountObject.get("account ID");
    double openingBalance = (double) accountObject.get("opening balance");
    double currentBalance = (double) accountObject.get("current balance");
    ArrayList<Double> deposits = new ArrayList<Double>();
    deposits = (ArrayList<Double>) accountObject.get("deposit transactions");
    ArrayList<Double> withdrawals = new ArrayList<Double>();
    withdrawals = (ArrayList<Double>) accountObject.get("withdrawal transactions");

    //create account object
    Account acc = new Account(accName, accNum, openingBalance, currentBalance, deposits, withdrawals);
    accounts.put(accNum, acc);
  }

  public HashMap<String,Account> getAccounts(){
    return this.accounts;
  }

  //update a database entry for a given account
  public boolean update(Account changedAcc){
    //first we update the accounts hashmap
    if (accounts.containsKey(changedAcc.getAccountNum())){
      Account accToBeModified = accounts.get(changedAcc.getAccountNum());
      accToBeModified.setCurrentBalance(changedAcc.getCurrentBalance());
      accToBeModified.setDepositsHistory(changedAcc.getDepositsHistory());
      accToBeModified.setWithdrawalsHistory(changedAcc.getWithdrawalsHistory());
    } else{
      return false;
    }
    //secondly we output the whole accounts hashmap to a JSON file
    JSONArray accountList = new JSONArray();

    for (HashMap.Entry<String, Account> acc : accounts.entrySet()){
      JSONObject accObject = new JSONObject();
      accObject.put("account name", acc.getValue().getAccountName());
      accObject.put("account ID", acc.getValue().getAccountNum());
      accObject.put("opening balance", acc.getValue().getOpeningBalance());
      accObject.put("current balance", acc.getValue().getCurrentBalance());
      accObject.put("deposit transactions", acc.getValue().getDepositsHistory());
      accObject.put("withdrawal transactions", acc.getValue().getWithdrawalsHistory());
      accountList.add(accObject);
    }

    //Write JSON file
    try (FileWriter file = new FileWriter("./src/newbank/data/AccountData")) {
      file.write(accountList.toJSONString());
      file.flush();

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Account data file failed to update.");
      System.exit(0);
    }
    return true;
  }
}

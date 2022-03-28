package newbank.server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;

import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CustomerDatabase {
  private HashMap<UserID,Customer> customers;

  public CustomerDatabase(){
    //first read in account data as this will be needed when creating customer objects
    AccountDatabase accountsDb = new AccountDatabase();
    HashMap<String, Account> accountsData = accountsDb.getAccounts();

    //read in customer data as JSON files and create customer objects
    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    customers = new HashMap<UserID,Customer>();

    try {
      //Read JSON file
      FileReader reader = new FileReader("./src/newbank/data/CustomerData");
      Object obj = jsonParser.parse(reader);

      JSONArray customerList = (JSONArray) obj;
      System.out.println(customerList);

      //Iterate over customer array
      customerList.forEach( cust -> parseCustomer( (JSONObject) cust, accountsData ) );

    }catch (IOException e){
      e.printStackTrace();
      System.out.println("Customer data file not found.");
      System.exit(0);
    } catch (ParseException e) {
      e.printStackTrace();
      System.out.println("Customer data file failed to parse.");
      System.exit(0);
    }
  }

  private void parseCustomer(JSONObject customerObject, HashMap<String, Account> accountsData){
    //get customer info
    String name = (String) customerObject.get("name");
    String password = (String) customerObject.get("password");
    String customerID = (String) customerObject.get("customer ID");
    Integer age = Integer.parseInt((String) customerObject.get("age"));
    String address = (String) customerObject.get("address");
    Integer income = Integer.parseInt((String) customerObject.get("income"));
    ArrayList<String> accountNums = new ArrayList<String>();
    accountNums = (ArrayList<String>) customerObject.get("accounts");

    //create account objects using accounts data and account numbers
    ArrayList<Account> accounts = new ArrayList<Account>();
    for(int i = 0; i < accountNums.size(); i++) {
      for (Map.Entry<String, Account> acc : accountsData.entrySet()) {
        if (acc.getKey().equals(accountNums.get(i))) {
          accounts.add(acc.getValue());
        }
      }
    }

    //create customer object
    Customer cust = new Customer(name, password, customerID, age, address, income, accounts);
    customers.put(cust.getUserID(), cust);
  }

  public HashMap<UserID,Customer> getCustomers(){
    return customers;
  }
}

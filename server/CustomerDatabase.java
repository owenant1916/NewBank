package newbank.server;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CustomerDatabase {
  private HashMap<String,Customer> customers;

  public CustomerDatabase(){
    //read in customer data as JSON files and create customer objects
    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    customers = new HashMap<String,Customer>();

    try {
      //Read JSON file
      FileReader reader = new FileReader("./src/newbank/data/CustomerData");
      Object obj = jsonParser.parse(reader);

      JSONArray customerList = (JSONArray) obj;
      System.out.println(customerList);

      //Iterate over employee array
      customerList.forEach( emp -> parseCustomer( (JSONObject) emp ) );

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

  private void parseCustomer(JSONObject customerObject){
    //get customer info
    String name = (String) customerObject.get("name");
    String password = (String) customerObject.get("password");
    Integer age = Integer.parseInt((String) customerObject.get("age"));
    String address = (String) customerObject.get("address");
    Integer income = Integer.parseInt((String) customerObject.get("income"));

    //create customer object
    Customer cust = new Customer(name, password, age, address, income);
    customers.put(name, cust);
  }

  public HashMap<String,Customer> getCustomers(){
    return customers;
  }
}

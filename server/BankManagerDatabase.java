package newbank.server;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BankManagerDatabase {
  private HashMap<String,BankManager> bankManagers;

  public BankManagerDatabase(){
    //read in customer data as JSON files and create customer objects
    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    bankManagers = new HashMap<String,BankManager>();

    try {
      //Read JSON file
      FileReader reader = new FileReader("./src/newbank/data/BankManagerData");
      Object obj = jsonParser.parse(reader);

      JSONArray managerList = (JSONArray) obj;
      System.out.println(managerList);

      //Iterate over employee array
      managerList.forEach( emp -> parseBankManager( (JSONObject) emp ) );

    }catch (IOException e){
      e.printStackTrace();
      System.out.println("Bank Manager data file not found.");
      System.exit(0);
    } catch (ParseException e) {
      e.printStackTrace();
      System.out.println("Bank Manager data file failed to parse.");
      System.exit(0);
    }
  }

  private void parseBankManager(JSONObject managerObject){
    //get bank manager info
    String name = (String) managerObject.get("name");
    String password = (String) managerObject.get("password");
    Integer staffID = Integer.parseInt((String) managerObject.get("staffID"));

    //create bank manager object
    BankManager manager = new BankManager(name, password, staffID);
    bankManagers.put(name, manager);
  }

  public HashMap<String,BankManager> getBankManagers(){
    return bankManagers;
  }
}
package newbank.server;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BankManagerDatabase {
  private HashMap<UserID, BankManager> bankManagers;

  public BankManagerDatabase(){
    //read in customer data as JSON files and create customer objects
    //JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    bankManagers = new HashMap<UserID,BankManager>();

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
    String staffID = (String) managerObject.get("staff ID");

    //create bank manager object
    BankManager manager = new BankManager(name, password, staffID);
    bankManagers.put(manager.getUserID(), manager);
  }

  public HashMap<UserID,BankManager> getBankManagers(){
    return bankManagers;
  }
}
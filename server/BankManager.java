package newbank.server;

public class BankManager extends User{

  public BankManager(String name, String password, String staffID){
    super(name,password, staffID);
  }

  public String getUserType(){return "bank manager";};
}

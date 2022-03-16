package newbank.server;

public class BankManager extends User{
  private Integer staffID;

  public BankManager(String name, String password, Integer staffID){
    super(name,password);
    this.staffID = staffID;
  }

  public String getUserType(){return "bank manager";};
}

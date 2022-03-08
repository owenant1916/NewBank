package newbank.server;

public class BankManager extends User{
  private Integer staffID;

  public BankManager(String name, Integer staffID){
    super(name);
    this.staffID = staffID;
  }

  public String getUserType(){return "bank manager";};
}

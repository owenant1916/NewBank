package newbank.server;

public abstract class User {
  private String name;
  private String password;
  private UserID userID;

  public User(String name, String password, String userID){
    this.name = name;
    this.password = password;
    this.userID = new UserID(userID);
  }

  public void setName(String name){
    this.name = name;
  }

  public String getName(){
    return this.name;
  }

  public String getPassword(){
    return this.password;
  }

  public UserID getUserID(){ return this.userID; }

  public abstract String getUserType();
}

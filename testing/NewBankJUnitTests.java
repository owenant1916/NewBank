package newbank.testing;
import newbank.server.Customer;
import newbank.server.NewBank;
import newbank.server.User;
import newbank.server.Account;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class NewBankJUnitTests {
  NewBank bank = NewBank.getBank();
  User user = bank.checkLogInDetails("DummyTestUser", "pa55word");

  @Test
  public void testlogIn() {
    String customerID = user.getUserID().getKey();
    String customerIDTest = "999";
    assertEquals(customerID,customerIDTest);
  }

  @Test
  public void testDepositCash(){
    Customer cust = (Customer) user;
    Account acc = cust.getAccounts().get(0);
    String accNum = acc.getAccountNum();
    double initialBalance = acc.getCurrentBalance();

    String res = bank.depositCash_process(500.0, accNum, cust);
    double delta = 0;
    assertEquals(initialBalance + 500.0, acc.getCurrentBalance(), delta);
  }

  @Test
  public void testWithdrawCash(){
    Customer cust = (Customer) user;
    Account acc = cust.getAccounts().get(0);
    String accNum = acc.getAccountNum();
    double initialBalance = acc.getCurrentBalance();

    String res = bank.withdrawCash_process(5, accNum, cust);
    double delta = 0;
    assertEquals(initialBalance - 5, acc.getCurrentBalance(), delta);
  }
}

package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utilities {
    private HashMap<UserID,Customer> customers;
    public Utilities(HashMap<UserID,Customer> customers) {
        this.customers = customers;
    }

    public Customer findCustomerByAcc(String accountNum) {
        for(Map.Entry<UserID, Customer> set : customers.entrySet()) {
            ArrayList<Account> customerAccounts = set.getValue().getAccounts();
            for(int i = 0; i < customerAccounts.size(); i++) {
                if(customerAccounts.get(i).getAccountNum() == accountNum) {
                    return set.getValue();
                }
            }
        }
        System.out.println("This account does not exist.");
        return null;
    };

    public Account findAccountByNum(String accountNum) {
        for(Map.Entry<UserID, Customer> set : customers.entrySet()) {
            ArrayList<Account> customerAccounts = set.getValue().getAccounts();
            for(int i = 0; i < customerAccounts.size(); i++) {
                if(customerAccounts.get(i).getAccountNum() == accountNum) {
                    return customerAccounts.get(i);
                }
            }
        }
        System.out.println("This account does not exist.");
        return null;
    };

}

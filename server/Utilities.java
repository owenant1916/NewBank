package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean passwordChecker(String newPassword){
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
        Matcher matcher = pattern.matcher(newPassword);
        boolean matchFound = matcher.find();
        if(matchFound) {
            return true;
        } else {
            return false;
        }
    }

}

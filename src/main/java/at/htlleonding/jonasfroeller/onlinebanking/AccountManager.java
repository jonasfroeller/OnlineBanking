package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.Serializable;
import java.util.HashMap;

public class AccountManager implements Serializable {
    private final HashMap<String, Double> accounts;

    public AccountManager() {
        this.accounts = new HashMap<>();
    }

    public synchronized boolean performTransaction(Transaction transaction) {
        String iban = transaction.getIban();
        double balance = transaction.getBalance();

        // check if account exists, create if not
        if (!this.accounts.containsKey(iban)) {
            this.accounts.put(iban, balance);
            return true;
        }

        double savedBalance = this.accounts.get(iban);
        double resultingBalance = savedBalance + balance;

        // check if withdrawal doesn't cause negative balance
        if (resultingBalance < 0.0) {
            return false;
        }

        // insert resulting balance into the map
        this.accounts.put(iban, resultingBalance);
        return true;
    }

    public double getBalanceOfAccount(String iban) throws AccountNotFoundException {
        if (this.accounts.containsKey(iban)) {
            return this.accounts.get(iban);
        } else {
            throw new AccountNotFoundException();
        }
    }
}

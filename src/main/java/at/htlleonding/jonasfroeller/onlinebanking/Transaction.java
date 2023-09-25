package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Locale;

public class Transaction implements Serializable {
    private String iban;
    private double balance;

    public static boolean isValidIban(String iban) {
        // https://en.wikipedia.org/wiki/International_Bank_Account_Number

        try {
            String ibanClean = iban.strip().replaceAll(" ", "");
            String ibanRearranged = ibanClean.substring(4) + ibanClean.substring(0, 4);
            StringBuilder ibanReplaced = new StringBuilder();

            for (char currentChar : ibanRearranged.toCharArray()) {
                if (Character.isLetter(currentChar) && Character.isUpperCase(currentChar)) {
                    ibanReplaced.append(currentChar - 'A' + 10);
                } else {
                    ibanReplaced.append(currentChar);
                }
            }

            BigInteger ibanDigitized = new BigInteger(ibanReplaced.toString());
            int rest = ibanDigitized.mod(new BigInteger("97")).intValue();

            if (rest == 1) {
                return true;
            }

        } catch (Exception ignored) {
            return false;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s: %.2f,-", this.iban, this.balance);
    }

    public Transaction(String iban, double amount) {
        this.setIban(iban);
        this.setBalance(amount);
    }

    public String getIban() {
        return iban;
    }

    public double getBalance() {
        return balance;
    }

    private void setIban(String iban) {
        if (isValidIban(iban)) {
            this.iban = iban;
        } else {
            throw new IllegalArgumentException("IBAN is invalid!");
        }
    }

    private void setBalance(double amount) {
        this.balance = amount;
    }
}

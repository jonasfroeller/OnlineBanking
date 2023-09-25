package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagerTest {
    @Test
    void testGetBalanceNotExists() {
        AccountManager accountManager = new AccountManager();

        assertThrows(AccountNotFoundException.class, () -> {
            accountManager.getBalanceOfAccount("DE88200800000970375700");
        });
    }

    @Test
    void testTransferAccountNotExists() throws AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        boolean isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 19.74));

        assertTrue(isSuccess);

        double balance = accountManager.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(19.74, balance, 0.001);
    }

    @Test
    void testMultipleTransfersSameAccount() throws AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        boolean isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 10.00));
        assertTrue(isSuccess);
        isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 5.00));
        assertTrue(isSuccess);
        isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 20.00));
        assertTrue(isSuccess);
        isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", -10.00));
        assertTrue(isSuccess);

        double balance = accountManager.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(25.00, balance, 0.001);
    }

    @Test
    void testTransferBelowZero() throws AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        boolean isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 10.00));
        assertTrue(isSuccess);
        isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", -15.00));
        assertFalse(isSuccess);

        double balance = accountManager.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(10.00, balance, 0.001);
    }

    @Test
    void testMultipleAccounts() throws AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        boolean isSuccess = accountManager.performTransaction(new Transaction("DE88200800000970375700", 13.00));
        assertTrue(isSuccess);
        isSuccess = accountManager.performTransaction(new Transaction("AT022050302101023600", 12.50));
        assertTrue(isSuccess);

        double balance = accountManager.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(13.00, balance, 0.001);
        balance = accountManager.getBalanceOfAccount("AT022050302101023600");
        assertEquals(12.50, balance, 0.001);
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException, AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        accountManager.performTransaction(new Transaction("DE88200800000970375700", 50.6));
        accountManager.performTransaction(new Transaction("AT022050302101023600", 70.8));

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("accounts.ser"));
        objectOutputStream.writeObject(accountManager);

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("accounts.ser"));
        AccountManager accountManagerDeserialized = (AccountManager) objectInputStream.readObject();

        double balance = accountManagerDeserialized.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(50.6, balance, 0.001);
        balance = accountManagerDeserialized.getBalanceOfAccount("AT022050302101023600");
        assertEquals(70.8, balance, 0.001);
    }

    @Test
    void testThreading() throws InterruptedException, AccountNotFoundException {
        AccountManager accountManager = new AccountManager();

        accountManager.performTransaction(new Transaction("DE88200800000970375700", 0.0));

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    accountManager.performTransaction(new Transaction("DE88200800000970375700", 1.0));
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                try {
                    accountManager.performTransaction(new Transaction("DE88200800000970375700", 0.5));
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        double balance = accountManager.getBalanceOfAccount("DE88200800000970375700");
        assertEquals(2000.0, balance, 0.001);
    }
}
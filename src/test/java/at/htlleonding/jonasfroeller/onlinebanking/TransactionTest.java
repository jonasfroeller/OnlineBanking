package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    @Test
    void testConstructor() {
        Transaction transaction = new Transaction("AT022050302101023600", 130.0);
        assertEquals("AT022050302101023600", transaction.getIban());
        assertEquals(130.0, transaction.getBalance(), 0.001);
    }

    @Test
    void testConstructorInvalidIban() {
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction transaction = new Transaction("DE88200800000970375710", 130.0);
        });
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        Transaction transaction = new Transaction("PL37109024020000000610000434", 17.5);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("transaction.ser"));
        objectOutputStream.writeObject(transaction);

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("transaction.ser"));
        Transaction transactionDeserialized = (Transaction) objectInputStream.readObject();

        assertEquals("PL37109024020000000610000434", transactionDeserialized.getIban());
        assertEquals(17.5, transactionDeserialized.getBalance(), 0.001);
    }

    @Test
    void testIbanValidation() {
        assertTrue(Transaction.isValidIban("DE88200800000970375700"));
        assertTrue(Transaction.isValidIban("DE88 2008 0000 0970 3757 00"));
        assertTrue(Transaction.isValidIban("GB82 WEST 1234 5698 7654 32"));
        assertTrue(Transaction.isValidIban("GB82 WEST1234    5698 7654 32"));
        assertTrue(Transaction.isValidIban("AT022050302101023600"));
        assertTrue(Transaction.isValidIban("PL37109024020000000610000434"));
        assertTrue(Transaction.isValidIban("             PL37109024020000000610000434            "));
        assertTrue(Transaction.isValidIban("SM86U0322509800000000270100"));

        assertFalse(Transaction.isValidIban("1234"));
        assertFalse(Transaction.isValidIban(""));
        assertFalse(Transaction.isValidIban("Das funktioniert nicht."));
        assertFalse(Transaction.isValidIban("PL37109024%'20000000610000434"));
        assertFalse(Transaction.isValidIban("AT022050302151023600"));
        assertFalse(Transaction.isValidIban("PL37105024020000000610000434"));
        assertFalse(Transaction.isValidIban("DE88 2006 0000 0970 3757 00"));
        assertFalse(Transaction.isValidIban("DE88200800000970375710"));
        assertFalse(Transaction.isValidIban("SM86U0322507800000000270100"));
    }

    @Test
    void testToString() {
        Transaction transaction = new Transaction("PL37109024020000000610000434", 17.5);

        assertEquals("PL37109024020000000610000434: 17.50,-", transaction.toString());
    }
}
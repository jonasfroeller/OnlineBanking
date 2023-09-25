package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class BankServerWorker implements Runnable {
    private static int totalWorkers;
    private final AccountManager accountManager;
    private final Socket socketClient;

    public BankServerWorker(AccountManager accountManager, Socket socketClient) {
        this.accountManager = accountManager;
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        totalWorkers++;
        System.out.printf("SERVER-THREAD [%d]: Starting.", totalWorkers);

        try (this.socketClient) {
            try (ObjectInputStream streamInput = new ObjectInputStream(socketClient.getInputStream());
                 PrintWriter streamOutput = new PrintWriter(socketClient.getOutputStream(), true)) {

                Transaction transaction;
                while ((transaction = (Transaction) streamInput.readObject()) != null) {
                    System.out.printf("SERVER-THREAD: Received object \"%s\".%n", transaction);

                    boolean isSuccess = accountManager.performTransaction(transaction);

                    if (!isSuccess) {
                        streamOutput.println("Transaction unsuccessful.");
                    } else {
                        double balance = accountManager.getBalanceOfAccount(transaction.getIban());
                        streamOutput.printf("Transaction successful. New balance: %.2f.%n", balance);
                    }
                }

                System.out.println("SERVER-THREAD: Received null. Finishing.");
            }
        } catch (Exception e) {
            System.err.println("SERVER-THREAD: Crashed!");
            throw new RuntimeException(e);
        }

        System.out.println("SERVER-THREAD: Stopped.");
    }
}

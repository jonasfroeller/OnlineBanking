package at.htlleonding.jonasfroeller.onlinebanking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class BankClient {
    private final static String IP_ADDRESS = "127.0.0.1";
    private final static int PORT = 5173;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String iban = null;
        boolean isValid = false;

        while (!isValid) {
            System.out.println("Please enter your IBAN: ");
            iban = scanner.nextLine();

            if (Transaction.isValidIban(iban)) {
                System.out.println("IBAN is valid.");
                isValid = true;
            } else {
                System.out.println("Invalid IBAN entered. Please try again.");
            }
        }

        try (Socket socketClient = new Socket(IP_ADDRESS, PORT)) {
            try (BufferedReader streamInput = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                 ObjectOutputStream streamOutput = new ObjectOutputStream(socketClient.getOutputStream())) {
                boolean isRunning = true;

                while (isRunning) {
                    System.out.print("Enter amount for new transaction (or type \"quit\" to quit the application): ");
                    String amountRaw = scanner.nextLine();

                    if (amountRaw.equals("quit")) {
                        streamOutput.writeObject(null);
                        isRunning = false;
                    } else {
                        try {
                            Transaction transaction = new Transaction(iban, Double.parseDouble(amountRaw));

                            streamOutput.writeObject(transaction);
                            String response = streamInput.readLine();

                            System.out.println("Response from server: " + response);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format!");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("CLIENT: Crashed.");
            ex.printStackTrace();
        }
    }
}
package at.htlleonding.jonasfroeller.onlinebanking;

import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    private final static int PORT = 5173;

    public static void main(String[] args) {
        System.out.println("SERVER: Starting...");

        AccountManager accountManager = new AccountManager();

        try (ServerSocket socketServer = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("SERVER: Listening for a connection...");

                Socket socketClient = socketServer.accept();
                System.out.println("SERVER: Connection accepted!");

                Thread thread = new Thread(new BankServerWorker(accountManager, socketClient));
                thread.start();
            }
        } catch (Exception e) {
            System.err.println("SERVER: Crashed!");
            e.printStackTrace();
        }

        System.out.println("SERVER: Stopped.");
    }
}

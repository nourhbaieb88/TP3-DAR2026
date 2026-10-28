package activite32.client;

import activite32.common.Operation;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestCharge {
    public static void main(String[] args) throws InterruptedException {
        String ip = (args.length > 0) ? args[0] : "localhost";
        int nbClients = 5;
        int nbOperations = 20;

        Thread[] threads = new Thread[nbClients];
        for (int i = 0; i < nbClients; i++) {
            final int id = i + 1;
            threads[i] = new Thread(() -> {
                try (Socket s = new Socket(ip, 1234)) {
                    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                    in.readObject();                          // message de bienvenue

                    for (int j = 1; j <= nbOperations; j++) {
                        out.writeObject(new Operation(j, "+", id));
                        out.flush();
                        in.readObject();
                    }
                    out.writeObject(Operation.fin());
                    out.flush();
                    in.readObject();
                    System.out.println("Client de test " + id + " : " + nbOperations + " opérations envoyées.");
                } catch (Exception e) {
                    System.out.println("Client de test " + id + " : erreur " + e.getMessage());
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Test terminé : " + (nbClients * nbOperations)
                + " opérations attendues. Vérifiez le compteur global sur le serveur.");
    }
}
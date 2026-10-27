package serverPackage;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 1234;

    // Compteur du numéro d'ordre de connexion (thread-safe)
    private static final AtomicInteger compteurClients = new AtomicInteger(0);

    public static void main(String[] args) {
        // Pas d'adresse précise : le serveur écoute sur toutes les interfaces
        // (localhost, 192.168.x.x, etc.), ce qui permet les tests locaux ET réseau.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Serveur multi-thread démarré.");
            System.out.println("Port : " + PORT);
            System.out.println("Adresses IP de cette machine :");
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isUp() && !ni.isLoopback()) {
                    for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                        if (a instanceof Inet4Address) {
                            System.out.println(" - " + a.getHostAddress() + " (" + ni.getDisplayName() + ")");
                        }
                    }
                }
            }
            System.out.println("En attente de clients...\n");

            while (true) {
                Socket socket = serverSocket.accept();          // bloquant
                int numero = compteurClients.incrementAndGet(); // client n°1, n°2...

                System.out.println("Client n°" + numero + " connecté : "
                        + socket.getRemoteSocketAddress());

                // Un thread dédié par client, le serveur retourne aussitôt à accept()
                new ClientHandler(socket, numero).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
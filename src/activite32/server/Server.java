package activite32.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int PORT = 1234;
    private static final AtomicInteger compteurClients = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Serveur calculatrice multi-thread (objets) démarré.");
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
                Socket socket = serverSocket.accept();
                int numero = compteurClients.incrementAndGet();

                System.out.println("Client n°" + numero + " connecté : "
                        + socket.getRemoteSocketAddress());

                new ClientProcess(socket, numero).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
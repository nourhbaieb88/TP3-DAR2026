package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    // "localhost" pour les tests locaux.
    // Pour le test en réseau : mettez l'IP de la machine serveur (ex. 192.168.1.10)
    // ou passez-la en argument : java clientPackage.client 192.168.1.10
    private static final String DEFAULT_SERVER_IP = "localhost";
    private static final int PORT = 1234;

    private static final Pattern OPERATION =
            Pattern.compile("^\\s*([+-]?\\d+)\\s*([+\\-*/])\\s*([+-]?\\d+)\\s*$");

    public static void main(String[] args) {
        String serverIp = (args.length > 0) ? args[0] : DEFAULT_SERVER_IP;

        try {
            System.out.println("Je suis un client pas encore connecté…");

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(serverIp), PORT), 5000);
            System.out.println("Je suis un client connecté au serveur " + serverIp + ":" + PORT);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);

            // Réception du numéro d'ordre envoyé par le serveur
            System.out.println(">>> " + in.readUTF());

            System.out.println("\n===== CALCULATRICE =====");
            System.out.println("Tapez une opération (ex. 55 * 25). Opérateurs : + - * /");
            System.out.println("Tapez 'q' pour quitter.");

            while (true) {
                System.out.print("\nOpération : ");
                String saisie = sc.nextLine().trim();

                if (saisie.equalsIgnoreCase("q")) {
                    out.writeBoolean(false);
                    out.flush();
                    System.out.println(in.readUTF());   // "Au revoir !"
                    break;
                }

                Matcher m = OPERATION.matcher(saisie);
                if (!m.matches()) {
                    System.out.println("Syntaxe invalide. Format attendu : <entier> <+|-|*|/> <entier>");
                    continue;
                }

                int x, y;
                try {
                    x = Integer.parseInt(m.group(1));
                    y = Integer.parseInt(m.group(3));
                } catch (NumberFormatException e) {
                    System.out.println("Nombre trop grand (limite : entiers 32 bits).");
                    continue;
                }
                String operateur = m.group(2);

                out.writeBoolean(true);
                out.writeInt(x);
                out.writeUTF(operateur);
                out.writeInt(y);
                out.flush();

                System.out.println("Résultat : " + in.readUTF());
            }

            sc.close();
            in.close();
            out.close();
            socket.close();
            System.out.println("Connexion fermée côté client.");

        } catch (IOException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}
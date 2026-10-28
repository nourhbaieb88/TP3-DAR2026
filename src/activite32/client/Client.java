package activite32.client;

import activite32.common.Operation;
import activite32.common.Resultat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

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

            // Même ordre que le serveur : Output (+ flush) puis Input
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);

            System.out.println(">>> " + in.readObject());

            System.out.println("\n===== CALCULATRICE (communication par objets) =====");
            System.out.println("Tapez une opération (ex. 55 * 25). Opérateurs : + - * /");
            System.out.println("Tapez 'q' pour quitter.");

            while (true) {
                System.out.print("\nOpération : ");
                String saisie = sc.nextLine().trim();

                if (saisie.equalsIgnoreCase("q")) {
                    out.writeObject(Operation.fin());
                    out.flush();
                    Resultat r = (Resultat) in.readObject();
                    System.out.println(r.getMessage());
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

                out.writeObject(new Operation(x, m.group(2), y));
                out.flush();

                Resultat r = (Resultat) in.readObject();
                System.out.println("Résultat : " + r.getMessage()
                        + "   [opération n°" + r.getNumeroOperation() + " traitée par le serveur]");
            }

            sc.close();
            in.close();
            out.close();
            socket.close();
            System.out.println("Connexion fermée côté client.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}
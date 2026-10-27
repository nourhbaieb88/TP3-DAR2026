package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int numero;

    public ClientHandler(Socket socket, int numero) {
        super("Client-" + numero);
        this.socket = socket;
        this.numero = numero;
    }

    @Override
    public void run() {
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {

            // Envoi du numéro d'ordre au client
            out.writeUTF("Bienvenue ! Vous êtes le client n°" + numero);
            out.flush();

            while (true) {
                System.out.println("[Client n°" + numero + "] En attente d'une opération...");
                boolean continuer = in.readBoolean();   // true = opération, false = quitter

                if (!continuer) {
                    out.writeUTF("Au revoir !");
                    out.flush();
                    break;
                }

                int x = in.readInt();
                String op = in.readUTF();
                int y = in.readInt();
                System.out.println("[Client n°" + numero + "] Reçu : " + x + " " + op + " " + y);

                String resultat = calculer(x, op, y);

                out.writeUTF(resultat);
                out.flush();
                System.out.println("[Client n°" + numero + "] Résultat envoyé : " + resultat);
            }

        } catch (EOFException e) {
            System.out.println("[Client n°" + numero + "] Connexion interrompue par le client.");
        } catch (IOException e) {
            System.out.println("[Client n°" + numero + "] Erreur : " + e.getMessage());
        }
        System.out.println("Client n°" + numero + " déconnecté.");
    }

    private static String calculer(int x, String op, int y) {
        switch (op) {
            case "+":
                return x + " + " + y + " = " + ((long) x + y);
            case "-":
                return x + " - " + y + " = " + ((long) x - y);
            case "*":
                return x + " * " + y + " = " + ((long) x * y);
            case "/":
                if (y == 0) {
                    return "Erreur : division par zéro impossible";
                }
                return x + " / " + y + " = " + ((double) x / y);
            default:
                return "Erreur : opérateur invalide (" + op + ")";
        }
    }
}
package activite32.server;

import activite32.common.Operation;
import activite32.common.Resultat;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientProcess extends Thread {
    private final Socket socket;
    private final int numero;

    public ClientProcess(Socket socket, int numero) {
        super("ClientProcess-" + numero);
        this.socket = socket;
        this.numero = numero;
    }

    @Override
    public void run() {
        try {
            // Ordre important : OutputStream d'abord (+ flush), puis InputStream
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("Bienvenue ! Vous êtes le client n°" + numero);
            out.flush();

            while (true) {
                Operation op = (Operation) in.readObject();

                if (op.isFin()) {
                    out.writeObject(new Resultat("Au revoir !", false, 0));
                    out.flush();
                    break;
                }

                System.out.println("[Client n°" + numero + "] Reçu : " + op);
                Resultat resultat = calculer(op);
                out.writeObject(resultat);
                out.flush();
            }

        } catch (EOFException e) {
            System.out.println("[Client n°" + numero + "] Connexion interrompue par le client.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Client n°" + numero + "] Erreur : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) { }
            System.out.println("Client n°" + numero + " déconnecté.");
        }
    }

    private Resultat calculer(Operation op) {
        int x = op.getX();
        int y = op.getY();
        String message;
        boolean erreur = false;

        switch (op.getOperateur()) {
            case "+": message = x + " + " + y + " = " + ((long) x + y); break;
            case "-": message = x + " - " + y + " = " + ((long) x - y); break;
            case "*": message = x + " * " + y + " = " + ((long) x * y); break;
            case "/":
                if (y == 0) {
                    message = "Erreur : division par zéro impossible";
                    erreur = true;
                } else {
                    message = x + " / " + y + " = " + ((double) x / y);
                }
                break;
            default:
                message = "Erreur : opérateur invalide (" + op.getOperateur() + ")";
                erreur = true;
        }

        // Section critique : compteur global partagé entre tous les ClientProcess
        int n = CompteurGlobal.incrementerEtAfficher(numero, op.toString());
        return new Resultat(message, erreur, n);
    }
}
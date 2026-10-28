package activite32.server;

public class CompteurGlobal {
    private static int valeur = 0;
    private static final Object VERROU = new Object();

    // Incrémentation et affichage dans le même bloc synchronisé :
    // un seul thread à la fois, et l'affichage reste dans l'ordre du compteur.
    public static int incrementerEtAfficher(int numeroClient, String operation) {
        synchronized (VERROU) {
            valeur++;
            System.out.println("[Compteur global] = " + valeur
                    + "  (client n°" + numeroClient + " : " + operation + ")");
            return valeur;
        }
    }

    public static int getValeur() {
        synchronized (VERROU) {
            return valeur;
        }
    }
}
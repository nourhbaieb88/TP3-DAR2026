package activite32.common;

import java.io.Serializable;

public class Resultat implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final boolean erreur;
    private final int numeroOperation;   // valeur du compteur global

    public Resultat(String message, boolean erreur, int numeroOperation) {
        this.message = message;
        this.erreur = erreur;
        this.numeroOperation = numeroOperation;
    }

    public String getMessage() { return message; }
    public boolean isErreur() { return erreur; }
    public int getNumeroOperation() { return numeroOperation; }
}
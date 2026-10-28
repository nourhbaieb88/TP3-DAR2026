package activite32.common;

import java.io.Serializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int x;
    private final String operateur;
    private final int y;
    private final boolean fin;   // true = le client veut quitter

    public Operation(int x, String operateur, int y) {
        this.x = x;
        this.operateur = operateur;
        this.y = y;
        this.fin = false;
    }

    private Operation() {
        this.x = 0;
        this.operateur = "";
        this.y = 0;
        this.fin = true;
    }

    public static Operation fin() {
        return new Operation();
    }

    public int getX() { return x; }
    public String getOperateur() { return operateur; }
    public int getY() { return y; }
    public boolean isFin() { return fin; }

    @Override
    public String toString() {
        return x + " " + operateur + " " + y;
    }
}
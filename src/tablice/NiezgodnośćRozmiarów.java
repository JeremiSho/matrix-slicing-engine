package tablice;

/**
 * Wyjątek zgłaszany, gdy operacja nie może być wykonana
 * z powodu niezgodności rozmiarów tablic.
 */
public class NiezgodnośćRozmiarów extends Exception {
    public NiezgodnośćRozmiarów(String message) {
        super(message);
    }
}
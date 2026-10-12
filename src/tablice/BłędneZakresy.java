package tablice;

/**
 * Wyjątek zgłaszany, gdy podano błędne zakresy
 * podczas tworzenia wycinka tablicy.
 */
public class BłędneZakresy extends Exception {
    public BłędneZakresy(String message) {
        super(message);
    }
}
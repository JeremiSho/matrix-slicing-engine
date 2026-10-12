package tablice;

/**
 * Wyjątek zgłaszany przy próbie dostępu do elementu tablicy
 * przy użyciu nieprawidłowego indeksu.
 */
public class ZłyIndeks extends Exception {
    public ZłyIndeks(String message) {
        super(message);
    }
}
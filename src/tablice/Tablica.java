package tablice;

public interface Tablica {

    int wymiar();

    int liczba_elementów();

    int[] kształt();

    Tablica kopia();

    Tablica negacja();

    void zaneguj();

    void transponuj();

    double daj(int... indeksy) throws ZłyIndeks;

    void ustaw(double wartosc, int... indeksy) throws ZłyIndeks;

    @Override
    String toString();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
package tablice;

public class SkalarWycinek implements Skalar {

    private final SkalarDane oryginał;

    public SkalarWycinek(SkalarDane oryginał) {
        if (oryginał == null) {
            throw new IllegalArgumentException("Oryginalny skalar nie może być null dla wycinka.");
        }
        this.oryginał = oryginał;
    }

    @Override
    public int wymiar() {
        return oryginał.wymiar();
    }

    @Override
    public int liczba_elementów() {
        return oryginał.liczba_elementów();
    }

    @Override
    public int[] kształt() {
        return oryginał.kształt();
    }

    @Override
    public Skalar kopia() {
        return oryginał.kopia();
    }

    @Override
    public Skalar negacja() {
        return oryginał.negacja();
    }

    @Override
    public void zaneguj() {
        oryginał.zaneguj();
    }

    @Override
    public void transponuj() {
        oryginał.transponuj();
    }

    @Override
    public double daj() {
        return oryginał.daj();
    }

    @Override
    public void ustaw(double wartość) {
        oryginał.ustaw(wartość);
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        return oryginał.daj(indeksy);
    }

    @Override
    public void ustaw(double wartość, int... indeksy) throws ZłyIndeks {
        oryginał.ustaw(wartość, indeksy);
    }

    @Override
    public Skalar suma(Skalar skalar) {
        return oryginał.suma(skalar);
    }

    @Override
    public Wektor suma(Wektor wektor) {
        return oryginał.suma(wektor);
    }

    @Override
    public Macierz suma(Macierz macierz) {
        return oryginał.suma(macierz);
    }

    @Override
    public Skalar iloczyn(Skalar skalar) {
        return oryginał.iloczyn(skalar);
    }

    @Override
    public Wektor iloczyn(Wektor wektor) {
        return oryginał.iloczyn(wektor);
    }

    @Override
    public Macierz iloczyn(Macierz macierz) {
        return oryginał.iloczyn(macierz);
    }

    @Override
    public void dodaj(Skalar skalar) {
        oryginał.dodaj(skalar);
    }

    @Override
    public void przemnóż(Skalar skalar) {
        oryginał.przemnóż(skalar);
    }

    @Override
    public void przypisz(Skalar skalar) {
        oryginał.przypisz(skalar);
    }

    @Override
    public Skalar wycinek() {
        return this;
    }

    @Override
    public String toString() {
        return "[" + oryginał.daj() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return oryginał.equals(obj);
    }

    @Override
    public int hashCode() {
        return oryginał.hashCode();
    }
}
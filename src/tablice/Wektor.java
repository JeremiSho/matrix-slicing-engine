package tablice;

public interface Wektor extends Tablica {

    boolean czyPoziomy();

    @Override
    Wektor kopia();

    @Override
    Wektor negacja();

    double daj(int indeks) throws ZłyIndeks;

    void ustaw(int indeks, double wartosc) throws ZłyIndeks;

    Wektor suma(Skalar skalar);

    Wektor suma(Wektor wektor) throws NiezgodnośćRozmiarów;

    Macierz suma(Macierz macierz) throws NiezgodnośćRozmiarów;

    Wektor iloczyn(Skalar skalar);

    Tablica iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów;

    Wektor iloczyn(Macierz macierz) throws NiezgodnośćRozmiarów;

    void dodaj(Skalar skalar);

    void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów;

    void przemnóż(Skalar skalar);

    void przypisz(Skalar skalar);

    void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów;

    Wektor wycinek(int odIndeks, int doIndeks) throws BłędneZakresy;
}
package tablice;

public interface Macierz extends Tablica {

    int liczbaWierszy();

    int liczbaKolumn();

    @Override
    Macierz kopia();

    @Override
    Macierz negacja();

    double daj(int wiersz, int kolumna) throws ZłyIndeks;

    void ustaw(int wiersz, int kolumna, double wartosc) throws ZłyIndeks;

    Macierz suma(Skalar skalar);

    Macierz suma(Wektor wektor) throws NiezgodnośćRozmiarów;

    Macierz suma(Macierz macierz) throws NiezgodnośćRozmiarów;

    Macierz iloczyn(Skalar skalar);

    Wektor iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów;

    Macierz iloczyn(Macierz macierz) throws NiezgodnośćRozmiarów;

    void dodaj(Skalar skalar);

    void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów;

    void dodaj(Macierz macierz) throws NiezgodnośćRozmiarów;

    void przemnóż(Skalar skalar);

    void przemnóż(Macierz macierz) throws NiezgodnośćRozmiarów;

    void przypisz(Skalar skalar);

    void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów;

    void przypisz(Macierz macierz) throws NiezgodnośćRozmiarów;

    Macierz wycinek(int odWiersz, int doWiersz, int odKolumna, int doKolumna) throws BłędneZakresy;
}
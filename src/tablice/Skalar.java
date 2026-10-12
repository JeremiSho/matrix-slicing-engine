package tablice;

public interface Skalar extends Tablica {

    @Override
    Skalar kopia();

    @Override
    Skalar negacja();

    double daj();

    void ustaw(double wartosc);

    Skalar suma(Skalar skalar);

    Wektor suma(Wektor wektor);

    Macierz suma(Macierz macierz);

    Skalar iloczyn(Skalar skalar);

    Wektor iloczyn(Wektor wektor);

    Macierz iloczyn(Macierz macierz);

    void dodaj(Skalar skalar);

    void przemnóż(Skalar skalar);

    void przypisz(Skalar skalar);

    Skalar wycinek();
}
package tablice;

import java.util.Arrays;
import java.util.Objects;

public class WektorDane implements Wektor {

    private final double[] dane;
    private boolean poziomy;

    public WektorDane(double[] dane, boolean poziomy) {
        if (dane == null || dane.length == 0)
            throw new IllegalArgumentException("Wektor nie może być pusty.");
        this.dane = Arrays.copyOf(dane, dane.length);
        this.poziomy = poziomy;
        normalizuj();
    }

    private void obsłużBłądLogiczny(String nazwaMetody, String komunikatDodatkowy, Throwable przyczyna) {
        System.err.println("Krytyczny błąd logiczny w " + getClass().getSimpleName() + "." + nazwaMetody + ":");
        System.err.println(komunikatDodatkowy);
        System.err.println("Wektor 'this': " + this);
        if (przyczyna != null) {
            System.err.println("Oryginalny wyjątek: " + przyczyna.getMessage());
            przyczyna.printStackTrace(System.err);
        }
        throw new AssertionError("Krytyczny błąd logiczny (" + nazwaMetody + "): " + komunikatDodatkowy, przyczyna);
    }

    private void normalizuj() {
        for (int i = 0; i < dane.length; i++)
            dane[i] = (dane[i] == 0) ? 0 : dane[i];
    }

    @Override
    public boolean czyPoziomy() {
        return poziomy;
    }

    @Override
    public int wymiar() {
        return 1;
    }

    @Override
    public int liczba_elementów() {
        return dane.length;
    }

    @Override
    public int[] kształt() {
        return new int[]{dane.length};
    }

    @Override
    public Wektor kopia() {
        return new WektorDane(dane, poziomy);
    }

    @Override
    public Wektor negacja() {
        double[] wynik = new double[dane.length];
        for (int i = 0; i < dane.length; i++)
            wynik[i] = -dane[i];
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public void zaneguj() {
        for (int i = 0; i < dane.length; i++)
            dane[i] *= -1;
        normalizuj();
    }

    @Override
    public void transponuj() {
        poziomy = !poziomy;
    }

    @Override
    public double daj(int indeks) throws ZłyIndeks {
        if (indeks < 0 || indeks >= dane.length)
            throw new ZłyIndeks("Indeks " + indeks + " poza zakresem [0, " + (dane.length - 1) + "]");
        return dane[indeks];
    }

    @Override
    public void ustaw(int indeks, double wartosc) throws ZłyIndeks {
        if (indeks < 0 || indeks >= dane.length)
            throw new ZłyIndeks("Indeks " + indeks + " poza zakresem [0, " + (dane.length - 1) + "]");
        dane[indeks] = wartosc;
        normalizuj();
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 1)
            throw new ZłyIndeks("Wektor wymaga 1 indeksu, podano: " + (indeksy == null ? 0 : indeksy.length));
        return daj(indeksy[0]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 1)
            throw new ZłyIndeks("Wektor wymaga 1 indeksu do ustawienia, podano: " + (indeksy == null ? 0 : indeksy.length));
        ustaw(indeksy[0], wartosc);
    }

    @Override
    public Wektor suma(Skalar skalar) {
        double[] wynik = new double[dane.length];
        double wartość = skalar.daj();
        for (int i = 0; i < dane.length; i++)
            wynik[i] = dane[i] + wartość;
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public Wektor suma(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (dane.length != wektor.liczba_elementów() || poziomy != wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("Wektory muszą mieć ten sam rozmiar i orientację do dodawania.");
        double[] wynik = new double[dane.length];
        for (int i = 0; i < dane.length; i++) {
            try {
                wynik[i] = dane[i] + wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("suma(Wektor)", "Niespodziewany ZłyIndeks od argumentu wektor.daj(" + i + ")", e);
            }
        }
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public Macierz suma(Macierz macierz) throws NiezgodnośćRozmiarów {
        int wiersze = macierz.liczbaWierszy();
        int kolumny = macierz.liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        try {
            if (poziomy) {
                if (dane.length != kolumny)
                    throw new NiezgodnośćRozmiarów("Długość wektora poziomego musi odpowiadać liczbie kolumn macierzy.");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = macierz.daj(i, j) + dane[j];
            } else {
                if (dane.length != wiersze)
                    throw new NiezgodnośćRozmiarów("Długość wektora pionowego musi odpowiadać liczbie wierszy macierzy.");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = macierz.daj(i, j) + dane[i];
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Macierz)", "Niespodziewany ZłyIndeks od macierz.daj() lub daj() (błąd wewnętrzny)", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Wektor iloczyn(Skalar skalar) {
        double[] wynik = new double[dane.length];
        double wartość = skalar.daj();
        for (int i = 0; i < dane.length; i++)
            wynik[i] = dane[i] * wartość;
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public Tablica iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (dane.length != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("Wektory muszą mieć ten sam rozmiar do mnożenia.");
        try {
            if (poziomy == wektor.czyPoziomy()) {
                double suma = 0;
                for (int i = 0; i < dane.length; i++)
                    suma += dane[i] * wektor.daj(i);
                return new SkalarDane(suma);
            } else {
                double[][] wynik;
                if (!poziomy && wektor.czyPoziomy()) {
                    wynik = new double[dane.length][wektor.liczba_elementów()];
                    for (int i = 0; i < dane.length; i++)
                        for (int j = 0; j < wektor.liczba_elementów(); j++)
                            wynik[i][j] = dane[i] * wektor.daj(j);
                } else {
                    wynik = new double[1][1];
                    double suma = 0;
                    for (int k = 0; k < dane.length; k++)
                        suma += dane[k] * wektor.daj(k);
                    wynik[0][0] = suma;
                }
                return new MacierzDane(wynik);
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Wektor)", "Niespodziewany ZłyIndeks od wektor.daj()", e);
            return null;
        }
    }

    @Override
    public Wektor iloczyn(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (!poziomy)
            throw new NiezgodnośćRozmiarów("Mnożenie wektor * macierz: wektor musi być poziomy.");
        if (dane.length != macierz.liczbaWierszy())
            throw new NiezgodnośćRozmiarów("Złe wymiary dla Wektor * Macierz.");
        int kolumny = macierz.liczbaKolumn();
        double[] wynik = new double[kolumny];
        try {
            for (int j = 0; j < kolumny; j++) {
                double suma = 0;
                for (int k = 0; k < dane.length; k++)
                    suma += dane[k] * macierz.daj(k, j);
                wynik[j] = suma;
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Macierz)", "Niespodziewany ZłyIndeks od macierz.daj()", e);
        }
        return new WektorDane(wynik, true);
    }

    @Override
    public void dodaj(Skalar skalar) {
        double wartość = skalar.daj();
        for (int i = 0; i < dane.length; i++)
            dane[i] += wartość;
        normalizuj();
    }

    @Override
    public void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (dane.length != wektor.liczba_elementów() || poziomy != wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("Wektory muszą mieć ten sam rozmiar i orientację do dodawania w miejscu.");
        for (int i = 0; i < dane.length; i++) {
            try {
                dane[i] += wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("dodaj(Wektor)", "Niespodziewany ZłyIndeks od argumentu wektor.daj(" + i + ")", e);
            }
        }
        normalizuj();
    }

    @Override
    public void przemnóż(Skalar skalar) {
        double wartość = skalar.daj();
        for (int i = 0; i < dane.length; i++)
            dane[i] *= wartość;
        normalizuj();
    }

    @Override
    public void przypisz(Skalar skalar) {
        Arrays.fill(dane, skalar.daj());
        normalizuj();
    }

    @Override
    public void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (dane.length != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("Przypisanie wektora: długości muszą być zgodne.");
        for (int i = 0; i < dane.length; i++) {
            try {
                dane[i] = wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("przypisz(Wektor)", "Niespodziewany ZłyIndeks od argumentu wektor.daj(" + i + ")", e);
            }
        }
        poziomy = wektor.czyPoziomy();
        normalizuj();
    }

    @Override
    public Wektor wycinek(int odIndeks, int doIndeks) throws BłędneZakresy {
        if (odIndeks < 0 || odIndeks > doIndeks || doIndeks >= dane.length)
            throw new BłędneZakresy("Błędne zakresy dla wycinka wektora: od=" + odIndeks + ", do=" + doIndeks + " dla dł=" + dane.length);
        return new WektorWycinek(dane, odIndeks, doIndeks - odIndeks + 1, poziomy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < dane.length; i++) {
            sb.append(dane[i]);
            if (i < dane.length - 1)
                sb.append(poziomy ? " " : "; ");
        }
        sb.append("] (").append(poziomy ? "poziomy" : "pionowy").append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Wektor that))
            return false;
        if (liczba_elementów() != that.liczba_elementów() || czyPoziomy() != that.czyPoziomy())
            return false;
        try {
            for (int i = 0; i < dane.length; i++)
                if (Double.compare(daj(i), that.daj(i)) != 0)
                    return false;
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("equals(Wektor)", "Niespodziewany ZłyIndeks", e);
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int wynik = Objects.hash(poziomy, wymiar(), Arrays.hashCode(kształt()));
        wynik = 31 * wynik + Arrays.hashCode(kształt());
        for (double elem : dane)
            wynik = 31 * wynik + Double.hashCode(elem);
        return wynik;
    }
}
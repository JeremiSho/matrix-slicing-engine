package tablice;

import java.util.Arrays;
import java.util.Objects;

public class WektorWycinek implements Wektor {

    private final double[] daneGłówne;
    private final int przesunięcie;
    private final int długość;
    private boolean poziomy;

    public WektorWycinek(double[] daneGłówne, int offset, int długość, boolean poziomy) {
        if (daneGłówne == null)
            throw new IllegalArgumentException("Dane źródłowe dla WektorWycinek nie mogą być null.");
        if (offset < 0 || długość <= 0 || offset + długość > daneGłówne.length) {
            throw new IllegalArgumentException(
                    String.format("Nieprawidłowe parametry wycinka: offset=%d, długość=%d dla danych o dł=%d",
                            offset, długość, daneGłówne.length)
            );
        }
        this.daneGłówne = daneGłówne;
        this.przesunięcie = offset;
        this.długość = długość;
        this.poziomy = poziomy;
        normalizuj();
    }

    private void obsłużBłądLogiczny(String nazwaMetody, String komunikatDodatkowy, Throwable przyczyna) {
        System.err.println("KRYTYCZNY BŁĄD LOGICZNY w " + getClass().getSimpleName() + "." + nazwaMetody + ":");
        System.err.println(komunikatDodatkowy);
        System.err.println("Wycinek wektora 'this': " + this);
        if (przyczyna != null) {
            System.err.println("Oryginalny wyjątek: " + przyczyna.getMessage());
            przyczyna.printStackTrace(System.err);
        }
        throw new AssertionError("Krytyczny błąd logiczny (" + nazwaMetody + "): " + komunikatDodatkowy, przyczyna);
    }

    private void normalizuj() {
        for (int i = 0; i < daneGłówne.length; i++)
            daneGłówne[i] = (daneGłówne[i] == 0) ? 0 : daneGłówne[i];
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
        return długość;
    }

    @Override
    public int[] kształt() {
        return new int[]{długość};
    }

    @Override
    public Wektor kopia() {
        double[] dane = new double[długość];
        System.arraycopy(daneGłówne, przesunięcie, dane, 0, długość);
        return new WektorDane(dane, poziomy);
    }

    @Override
    public Wektor negacja() {
        double[] wynik = new double[długość];
        for (int i = 0; i < długość; i++)
            wynik[i] = -daneGłówne[przesunięcie + i];
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public void zaneguj() {
        for (int i = 0; i < długość; i++)
            daneGłówne[przesunięcie + i] *= -1;
        normalizuj();
    }

    @Override
    public void transponuj() {
        poziomy = !poziomy;
    }

    @Override
    public double daj(int indeks) throws ZłyIndeks {
        if (indeks < 0 || indeks >= długość)
            throw new ZłyIndeks("Indeks " + indeks + " poza zakresem wycinka [0, " + (długość - 1) + "]");
        return daneGłówne[przesunięcie + indeks];
    }

    @Override
    public void ustaw(int indeks, double wartosc) throws ZłyIndeks {
        if (indeks < 0 || indeks >= długość)
            throw new ZłyIndeks("Indeks " + indeks + " poza zakresem wycinka [0, " + (długość - 1) + "]");
        daneGłówne[przesunięcie + indeks] = wartosc;
        normalizuj();
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 1)
            throw new ZłyIndeks("WektorWycinek wymaga 1 indeksu.");
        return daj(indeksy[0]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 1)
            throw new ZłyIndeks("WektorWycinek wymaga 1 indeksu.");
        ustaw(indeksy[0], wartosc);
        normalizuj();
    }

    @Override
    public Wektor suma(Skalar skalar) {
        double[] wynik = new double[długość];
        double wartość = skalar.daj();
        for (int i = 0; i < długość; i++)
            wynik[i] = daneGłówne[przesunięcie + i] + wartość;
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public Wektor suma(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (długość != wektor.liczba_elementów() || poziomy != wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("WektorWycinek.suma(Wektor): złe wymiary/orientacja.");
        double[] wynik = new double[długość];
        for (int i = 0; i < długość; i++) {
            try {
                wynik[i] = daj(i) + wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("suma(Wektor)", "Niespodziewany ZłyIndeks od daj lub wektor.daj", e);
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
                if (długość != kolumny)
                    throw new NiezgodnośćRozmiarów("Wycinek.suma(Macierz poziomy): złe wymiary");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = macierz.daj(i, j) + daj(j);
            } else {
                if (długość != wiersze)
                    throw new NiezgodnośćRozmiarów("Wycinek.suma(Macierz pionowy): złe wymiary");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = macierz.daj(i, j) + daj(i);
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Macierz)", "Niespodziewany ZłyIndeks od daj lub macierz.daj", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Wektor iloczyn(Skalar skalar) {
        double[] wynik = new double[długość];
        double wartość = skalar.daj();
        for (int i = 0; i < długość; i++)
            wynik[i] = daneGłówne[przesunięcie + i] * wartość;
        return new WektorDane(wynik, poziomy);
    }

    @Override
    public Tablica iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (długość != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("Wycinek.iloczyn(Wektor): różne długości");
        try {
            if (poziomy == wektor.czyPoziomy()) {
                double suma = 0;
                for (int i = 0; i < długość; i++)
                    suma += daj(i) * wektor.daj(i);
                return new SkalarDane(suma);
            } else {
                double[][] wynik;
                if (!poziomy && wektor.czyPoziomy()) {
                    wynik = new double[długość][wektor.liczba_elementów()];
                    for (int i = 0; i < długość; i++)
                        for (int j = 0; j < wektor.liczba_elementów(); j++)
                            wynik[i][j] = daj(i) * wektor.daj(j);
                } else {
                    wynik = new double[1][1];
                    double suma = 0;
                    for (int i = 0; i < długość; i++)
                        suma += daj(i) * wektor.daj(i);
                    wynik[0][0] = suma;
                }
                return new MacierzDane(wynik);
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Wektor)", "Niespodziewany ZłyIndeks od daj lub wektor.daj", e);
            return null;
        }
    }

    @Override
    public Wektor iloczyn(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (!poziomy)
            throw new NiezgodnośćRozmiarów("Wycinek.iloczyn(Macierz): wycinek musi być poziomy");
        if (długość != macierz.liczbaWierszy())
            throw new NiezgodnośćRozmiarów("Wycinek.iloczyn(Macierz): złe wymiary");
        int kolumny = macierz.liczbaKolumn();
        double[] wynik = new double[kolumny];
        try {
            for (int j = 0; j < kolumny; j++) {
                double suma = 0;
                for (int k = 0; k < długość; k++)
                    suma += daj(k) * macierz.daj(k, j);
                wynik[j] = suma;
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Macierz)", "Niespodziewany ZłyIndeks od daj lub macierz.daj", e);
        }
        return new WektorDane(wynik, true);
    }

    @Override
    public void dodaj(Skalar skalar) {
        for (int i = 0; i < długość; i++)
            daneGłówne[przesunięcie + i] += skalar.daj();
        normalizuj();
    }

    @Override
    public void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (długość != wektor.liczba_elementów() || poziomy != wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("Wycinek.dodaj(Wektor): złe wymiary");
        for (int i = 0; i < długość; i++) {
            try {
                ustaw(i, daj(i) + wektor.daj(i));
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("dodaj(Wektor)", "Niespodziewany ZłyIndeks od daj/ustaw lub wektor.daj", e);
            }
        }
        normalizuj();
    }

    @Override
    public void przemnóż(Skalar skalar) {
        for (int i = 0; i < długość; i++)
            daneGłówne[przesunięcie + i] *= skalar.daj();
        normalizuj();
    }

    @Override
    public void przypisz(Skalar skalar) {
        for (int i = 0; i < długość; i++)
            daneGłówne[przesunięcie + i] = skalar.daj();
        normalizuj();
    }

    @Override
    public void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (długość != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("Wycinek.przypisz(Wektor): złe wymiary");
        for (int i = 0; i < długość; i++) {
            try {
                ustaw(i, wektor.daj(i));
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("przypisz(Wektor)", "Niespodziewany ZłyIndeks od wektor.daj", e);
            }
        }
        poziomy = wektor.czyPoziomy();
        normalizuj();
    }

    @Override
    public Wektor wycinek(int odIndeks, int doIndeks) throws BłędneZakresy {
        if (odIndeks < 0 || odIndeks > doIndeks || doIndeks >= długość)
            throw new BłędneZakresy("Błędne zakresy dla wycinka z wycinka wektora: od=" + odIndeks + ", do=" + doIndeks + " dla wycinka o dł=" + długość);
        int nowePrzesunięcie = przesunięcie + odIndeks;
        int nowaDługość = doIndeks - odIndeks + 1;
        return new WektorWycinek(daneGłówne, nowePrzesunięcie, nowaDługość, poziomy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < długość; i++) {
            sb.append(daneGłówne[przesunięcie + i]);
            if (i < długość - 1)
                sb.append(poziomy ? " " : "; ");
        }
        sb.append("] (wycinek, ").append(poziomy ? "poziomy" : "pionowy").append(")");
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
            for (int i = 0; i < długość; i++)
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
        int wynik = Objects.hash(przesunięcie, długość, poziomy, wymiar(), Arrays.hashCode(kształt()));
        wynik = 31 * wynik + Arrays.hashCode(kształt());
        for (int i = 0; i < długość; i++)
            wynik = 31 * wynik + Double.hashCode(daneGłówne[przesunięcie + i]);
        return wynik;
    }
}
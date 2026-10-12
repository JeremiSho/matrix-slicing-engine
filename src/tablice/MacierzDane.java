package tablice;

import java.util.Arrays;
import java.util.Objects;

public class MacierzDane implements Macierz {

    private final double[][] dane;
    private boolean czyTransponowana;

    public MacierzDane(double[][] dane) {
        if (dane == null || dane.length == 0 || dane[0] == null || dane[0].length == 0)
            throw new IllegalArgumentException("Macierz nie może być pusta.");
        int wiersze = dane.length;
        int kolumny = dane[0].length;
        for (int i = 1; i < wiersze; i++)
            if (dane[i] == null || dane[i].length != kolumny)
                throw new IllegalArgumentException("Wszystkie wiersze macierzy muszą mieć taką samą długość.");
        this.dane = new double[wiersze][kolumny];
        for (int i = 0; i < wiersze; i++)
            this.dane[i] = Arrays.copyOf(dane[i], kolumny);
        czyTransponowana = false;
        normalizuj();
    }

    private void obsłużBłądLogiczny(String nazwaMetody, String komunikatDodatkowy, Throwable przyczyna) {
        System.err.println("KRYTYCZNY BŁĄD LOGICZNY wektor " + getClass().getSimpleName() + "." + nazwaMetody + ":");
        System.err.println(komunikatDodatkowy);
        System.err.println("Macierz 'this': " + this);
        if (przyczyna != null) {
            System.err.println("Oryginalny wyjątek: " + przyczyna.getMessage());
            przyczyna.printStackTrace(System.err);
        }
        throw new AssertionError("Krytyczny błąd logiczny (" + nazwaMetody + "): " + komunikatDodatkowy, przyczyna);
    }

    private void normalizuj() {
        for (int i = 0; i < dane.length; i++)
            for (int j = 0; j < dane[i].length; j++)
                dane[i][j] = (dane[i][j] == 0) ? 0 : dane[i][j];
    }

    @Override
    public int liczbaWierszy() {
        if (dane.length == 0)
            return 0;
        return czyTransponowana ? dane[0].length : dane.length;
    }

    @Override
    public int liczbaKolumn() {
        if (dane.length == 0)
            return 0;
        return czyTransponowana ? dane.length : dane[0].length;
    }

    @Override
    public int wymiar() {
        return 2;
    }

    @Override
    public int liczba_elementów() {
        if (dane.length == 0)
            return 0;
        return dane.length * dane[0].length;
    }

    @Override
    public int[] kształt() {
        return new int[]{liczbaWierszy(), liczbaKolumn()};
    }

    @Override
    public Macierz kopia() {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] noweDane = new double[wiersze][kolumny];
        for (int i = 0; i < wiersze; i++) {
            for (int j = 0; j < kolumny; j++) {
                try {
                    noweDane[i][j] = daj(i, j);
                } catch (ZłyIndeks e) {
                    obsłużBłądLogiczny("kopia()", "Niespodziewany ZłyIndeks przy kopiowaniu.", e);
                }
            }
        }
        return new MacierzDane(noweDane);
    }

    @Override
    public Macierz negacja() {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynikDane = new double[wiersze][kolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    wynikDane[i][j] = -daj(i, j);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("negacja()", "Niespodziewany ZłyIndeks.", e);
        }
        return new MacierzDane(wynikDane);
    }

    @Override
    public void zaneguj() {
        for (int i = 0; i < dane.length; i++)
            for (int j = 0; j < dane[0].length; j++)
                dane[i][j] = -dane[i][j];
        normalizuj();
    }

    @Override
    public void transponuj() {
        czyTransponowana = !czyTransponowana;
    }

    @Override
    public double daj(int wiersz, int kolumna) throws ZłyIndeks {
        if (wiersz < 0 || wiersz >= liczbaWierszy() || kolumna < 0 || kolumna >= liczbaKolumn())
            throw new ZłyIndeks("Niepoprawny indeks: wiersz=" + wiersz + ", kolumna=" + kolumna +
                    " dla macierzy " + liczbaWierszy() + "x" + liczbaKolumn());
        if (czyTransponowana)
            return dane[kolumna][wiersz];
        else
            return dane[wiersz][kolumna];
    }

    @Override
    public void ustaw(int wiersz, int kolumna, double wartosc) throws ZłyIndeks {
        if (wiersz < 0 || wiersz >= liczbaWierszy() || kolumna < 0 || kolumna >= liczbaKolumn())
            throw new ZłyIndeks("Niepoprawny indeks: wiersz=" + wiersz + ", kolumna=" + kolumna +
                    " dla macierzy " + liczbaWierszy() + "x" + liczbaKolumn());
        if (czyTransponowana)
            dane[kolumna][wiersz] = wartosc;
        else
            dane[wiersz][kolumna] = wartosc;
        normalizuj();
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 2)
            throw new ZłyIndeks("Macierz wymaga 2 indeksów.");
        return daj(indeksy[0], indeksy[1]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 2)
            throw new ZłyIndeks("Macierz wymaga 2 indeksów.");
        ustaw(indeksy[0], indeksy[1], wartosc);
    }

    @Override
    public Macierz suma(Skalar skalar) {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    wynik[i][j] = daj(i, j) + wartość;
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Skalar)", "Niespodziewany ZłyIndeks.", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Macierz suma(Wektor wektor) throws NiezgodnośćRozmiarów {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        try {
            if (wektor.czyPoziomy()) {
                if (wektor.liczba_elementów() != kolumny)
                    throw new NiezgodnośćRozmiarów("Macierz.suma(Wektor poziomy): zła długość wektora.");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = daj(i, j) + wektor.daj(j);
            } else {
                if (wektor.liczba_elementów() != wiersze)
                    throw new NiezgodnośćRozmiarów("Macierz.suma(Wektor pionowy): zła długość wektora.");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = daj(i, j) + wektor.daj(i);
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Wektor)", "Niespodziewany ZłyIndeks od argumentu 'wektor' lub 'this'", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Macierz suma(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaWierszy() != macierz.liczbaWierszy() || liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("Suma macierz+macierz: złe wymiary");
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] d = new double[wiersze][kolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    d[i][j] = daj(i, j) + macierz.daj(i, j);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Macierz)", "Niespodziewany ZłyIndeks od argumentu 'macierz' lub 'this'", e);
        }
        return new MacierzDane(d);
    }

    @Override
    public Macierz iloczyn(Skalar skalar) {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] d = new double[wiersze][kolumny];
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    d[i][j] = daj(i, j) * wartość;
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Skalar)", "Niespodziewany ZłyIndeks.", e);
        }
        return new MacierzDane(d);
    }

    @Override
    public Wektor iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("macierz*wektor: wektor musi być pionowy");
        if (liczbaKolumn() != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("macierz*wektor: złe wymiary.");
        int wiersze = liczbaWierszy();
        double[] wynik = new double[wiersze];
        try {
            for (int i = 0; i < wiersze; i++) {
                double sum = 0;
                for (int k = 0; k < liczbaKolumn(); k++)
                    sum += daj(i, k) * wektor.daj(k);
                wynik[i] = sum;
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Wektor)", "Niespodziewany ZłyIndeks od 'wektor' lub 'this'", e);
        }
        return new WektorDane(wynik, false);
    }

    @Override
    public Macierz iloczyn(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaKolumn() != macierz.liczbaWierszy())
            throw new NiezgodnośćRozmiarów("macierz*macierz: złe wymiary");
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        int macierzKolumny = macierz.liczbaKolumn();
        double[][] d = new double[wiersze][macierzKolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < macierzKolumny; j++) {
                    double sum = 0;
                    for (int k = 0; k < kolumny; k++)
                        sum += daj(i, k) * macierz.daj(k, j);
                    d[i][j] = sum;
                }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Macierz)", "Niespodziewany ZłyIndeks od 'macierz' lub 'this'", e);
        }
        return new MacierzDane(d);
    }

    @Override
    public void dodaj(Skalar skalar) {
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, daj(i, j) + wartość);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("dodaj(Skalar)", "Niespodziewany ZłyIndeks.", e);
        }
        normalizuj();
    }

    @Override
    public void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów {
        try {
            if (wektor.czyPoziomy()) {
                if (wektor.liczba_elementów() != liczbaKolumn())
                    throw new NiezgodnośćRozmiarów("dodaj macierz+Wp");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, daj(i, j) + wektor.daj(j));
            } else {
                if (wektor.liczba_elementów() != liczbaWierszy())
                    throw new NiezgodnośćRozmiarów("dodaj macierz+Wv");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, daj(i, j) + wektor.daj(i));
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("dodaj(Wektor)", "Niespodziewany ZłyIndeks od 'wektor' lub 'this'", e);
        }
        normalizuj();
    }

    @Override
    public void dodaj(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaWierszy() != macierz.liczbaWierszy() || liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("dodaj macierz+macierz");
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, daj(i, j) + macierz.daj(i, j));
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("dodaj(Macierz)", "Niespodziewany ZłyIndeks od 'macierz' lub 'this'", e);
        }
        normalizuj();
    }

    @Override
    public void przemnóż(Skalar skalar) {
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, daj(i, j) * wartość);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("przemnóż(Skalar)", "Niespodziewany ZłyIndeks.", e);
        }
        normalizuj();
    }

    @Override
    public void przemnóż(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaKolumn() != macierz.liczbaWierszy())
            throw new NiezgodnośćRozmiarów("Przemnóż: l. kolumn pierwszej (" + liczbaKolumn() + ") != l. wierszy drugiej (" + macierz.liczbaWierszy() + ").");
        if (liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("Przemnóż: l. kolumn drugiej (" + macierz.liczbaKolumn() + ") != l. kolumn pierwszej (" + liczbaKolumn() + ").");
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++) {
                    double suma = 0;
                    for (int k = 0; k < kolumny; k++)
                        suma += daj(i, k) * macierz.daj(k, j);
                    wynik[i][j] = suma;
                }
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    ustaw(i, j, wynik[i][j]);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("przemnóż(Macierz)", "Niespodziewany ZłyIndeks.", e);
        }
        normalizuj();
    }

    @Override
    public void przypisz(Skalar skalar) {
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, wartość);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("przypisz(Skalar)", "Niespodziewany ZłyIndeks.", e);
        }
        normalizuj();
    }

    @Override
    public void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów {
        try {
            if (wektor.czyPoziomy()) {
                if (wektor.liczba_elementów() != liczbaKolumn())
                    throw new NiezgodnośćRozmiarów("przypisz Wp do macierz");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, wektor.daj(j));
            } else {
                if (wektor.liczba_elementów() != liczbaWierszy())
                    throw new NiezgodnośćRozmiarów("przypisz Wv do macierz");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, wektor.daj(i));
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("przypisz(Wektor)", "Niespodziewany ZłyIndeks od 'wektor' lub 'this'", e);
        }
        normalizuj();
    }

    @Override
    public void przypisz(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaWierszy() != macierz.liczbaWierszy() || liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("przypisz macierz do macierz");
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, macierz.daj(i, j));
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("przypisz(Macierz)", "Niespodziewany ZłyIndeks od 'macierz' lub 'this'", e);
        }
        normalizuj();
    }

    @Override
    public Macierz wycinek(int odWiersz, int doWiersz, int odKolumna, int doKolumna) throws BłędneZakresy {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        if (odWiersz < 0 || odWiersz > doWiersz || doWiersz >= wiersze)
            throw new BłędneZakresy("Błędny zakres wierszy dla wycinka: od=" + odWiersz + ", do=" + doWiersz + " (macierz " + wiersze + "x" + kolumny + ")");
        if (odKolumna < 0 || odKolumna > doKolumna || doKolumna >= kolumny)
            throw new BłędneZakresy("Błędny zakres kolumn dla wycinka: od=" + odKolumna + ", do=" + doKolumna + " (macierz " + wiersze + "x" + kolumny + ")");
        int przesunięcieWiersza;
        int przesunięcieKolumny;
        int wierszeWycinka;
        int kolumnyWycinka;
        if (czyTransponowana) {
            przesunięcieWiersza = odKolumna;
            przesunięcieKolumny = odWiersz;
            wierszeWycinka = doKolumna - odKolumna + 1;
            kolumnyWycinka = doWiersz - odWiersz + 1;

        } else {
            przesunięcieWiersza = odWiersz;
            przesunięcieKolumny = odKolumna;
            wierszeWycinka = doWiersz - odWiersz + 1;
            kolumnyWycinka = doKolumna - odKolumna + 1;
        }
        return new MacierzWycinek(dane, przesunięcieWiersza, przesunięcieKolumny, wierszeWycinka, kolumnyWycinka, false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        try {
            for (int i = 0; i < liczbaWierszy(); i++) {
                sb.append("[");
                for (int j = 0; j < liczbaKolumn(); j++) {
                    sb.append(daj(i, j));
                    if (j < liczbaKolumn() - 1)
                        sb.append(" ");
                }
                sb.append("]");
                if (i < liczbaWierszy() - 1)
                    sb.append(", ");
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("toString()", "Niespodziewany ZłyIndeks.", e);
            return "";
        }
        sb.append("} (").append(liczbaWierszy()).append("x").append(liczbaKolumn()).append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Macierz that))
            return false;
        if (liczbaWierszy() != that.liczbaWierszy() || liczbaKolumn() != that.liczbaKolumn())
            return false;
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    if (Double.compare(daj(i, j), that.daj(i, j)) != 0)
                        return false;
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("equals(Macierz)", "Niespodziewany ZłyIndeks.", e);
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int wynik = Objects.hash(czyTransponowana, wymiar(), Arrays.hashCode(kształt()));
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    wynik = 31 * wynik + Double.hashCode(daj(i, j));
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("hashCode()", "Niespodziewany ZłyIndeks.", e);
        }
        return wynik;
    }
}
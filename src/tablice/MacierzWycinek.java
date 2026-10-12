package tablice;

import java.util.Arrays;
import java.util.Objects;

public class MacierzWycinek implements Macierz {

    private final double[][] daneGłówne;
    private final int przesunięcieWiersza;
    private final int przesunięcieKolumny;
    private final int wierszeWycinka;
    private final int kolumnyWycinka;
    private boolean czyTransponowany;

    public MacierzWycinek(double[][] daneGłówne, int przesunięcieWiersza, int przesunięcieKolumny, int wierszeWycinka, int kolumnyWycinka, boolean czyTransponowany) {
        if (daneGłówne == null)
            throw new IllegalArgumentException("Dane źródłowe dla MacierzWycinek nie mogą być null.");
        if (przesunięcieWiersza < 0 || wierszeWycinka < 0 ||
                przesunięcieKolumny < 0 || kolumnyWycinka < 0 ||
                (wierszeWycinka > 0 && przesunięcieWiersza + wierszeWycinka > daneGłówne.length) ||
                (kolumnyWycinka > 0 && daneGłówne.length > 0 && wierszeWycinka > 0 && (przesunięcieKolumny + kolumnyWycinka > daneGłówne[0].length))) {
            boolean poprawnyWymiar = true;
            if (wierszeWycinka > 0 && (przesunięcieWiersza + wierszeWycinka > daneGłówne.length))
                poprawnyWymiar = false;
            if (kolumnyWycinka > 0 && daneGłówne.length > 0 && wierszeWycinka > 0 && (przesunięcieKolumny + kolumnyWycinka > daneGłówne[0].length))
                poprawnyWymiar = false;
            if (!poprawnyWymiar) {
                throw new IllegalArgumentException(String.format(
                        "Nieprawidłowe parametry dla MacierzWycinek: offsW=%wynik, offsK=%wynik, fizW=%wynik, fizK=%wynik dla danych %dx%wynik",
                        przesunięcieWiersza, przesunięcieKolumny, wierszeWycinka, kolumnyWycinka,
                        daneGłówne.length, (daneGłówne.length > 0 ? daneGłówne[0].length : 0)
                ));
            }
        }
        this.daneGłówne = daneGłówne;
        this.przesunięcieWiersza = przesunięcieWiersza;
        this.przesunięcieKolumny = przesunięcieKolumny;
        this.wierszeWycinka = wierszeWycinka;
        this.kolumnyWycinka = kolumnyWycinka;
        this.czyTransponowany = czyTransponowany;
        normalizuj();
    }

    private void obsłużBłądLogiczny(String nazwaMetody, String komunikatDodatkowy, Throwable przyczyna) {
        System.err.println("KRYTYCZNY BŁĄD LOGICZNY wektor " + getClass().getSimpleName() + "." + nazwaMetody + ":");
        System.err.println(komunikatDodatkowy);
        System.err.println("Wycinek macierzy 'this': " + this);
        if (przyczyna != null) {
            System.err.println("Oryginalny wyjątek: " + przyczyna.getMessage());
            przyczyna.printStackTrace(System.err);
        }
        throw new AssertionError("Krytyczny błąd logiczny (" + nazwaMetody + "): " + komunikatDodatkowy, przyczyna);
    }

    private void normalizuj() {
        for (int i = 0; i < daneGłówne.length; i++)
            for (int j = 0; j < daneGłówne[i].length; j++)
                daneGłówne[i][j] = (daneGłówne[i][j] == 0) ? 0 : daneGłówne[i][j];
    }

    @Override
    public int liczbaWierszy() {
        return czyTransponowany ? kolumnyWycinka : wierszeWycinka;
    }

    @Override
    public int liczbaKolumn() {
        return czyTransponowany ? wierszeWycinka : kolumnyWycinka;
    }

    @Override
    public int wymiar() {
        return 2;
    }

    @Override
    public int liczba_elementów() {
        return wierszeWycinka * kolumnyWycinka;
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
        for (int i = 0; i < wiersze; i++)
            for (int j = 0; j < kolumny; j++) {
                try {
                    noweDane[i][j] = daj(i, j);
                } catch (ZłyIndeks e) {
                    obsłużBłądLogiczny("kopia()", "Niespodziewany ZłyIndeks.", e);
                }
            }
        return new MacierzDane(noweDane);
    }

    @Override
    public Macierz negacja() {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] noweDane = new double[wiersze][kolumny];
        for (int i = 0; i < wiersze; i++)
            for (int j = 0; j < kolumny; j++) {
                try {
                    noweDane[i][j] = -daj(i, j);
                } catch (ZłyIndeks e) {
                    obsłużBłądLogiczny("negacja()", "Niespodziewany ZłyIndeks.", e);
                }
            }
        return new MacierzDane(noweDane);
    }

    @Override
    public void zaneguj() {
        for (int i = 0; i < wierszeWycinka; i++) {
            for (int j = 0; j < kolumnyWycinka; j++) {
                daneGłówne[przesunięcieWiersza + i][przesunięcieKolumny + j] = -daneGłówne[przesunięcieWiersza + i][przesunięcieKolumny + j];
            }
        }
        normalizuj();
    }

    @Override
    public void transponuj() {
        czyTransponowany = !czyTransponowany;
    }

    @Override
    public double daj(int wiersz, int kolumna) throws ZłyIndeks {
        if (wiersz < 0 || wiersz >= liczbaWierszy() || kolumna < 0 || kolumna >= liczbaKolumn())
            throw new ZłyIndeks("Indeks poza zakresem wycinka: wiersz_widoku=" + wiersz + ", kolumna_widoku=" + kolumna +
                    " dla wycinka " + liczbaWierszy() + "x" + liczbaKolumn());
        if (czyTransponowany)
            return daneGłówne[przesunięcieWiersza + kolumna][przesunięcieKolumny + wiersz];
        else
            return daneGłówne[przesunięcieWiersza + wiersz][przesunięcieKolumny + kolumna];
    }

    @Override
    public void ustaw(int wiersz, int kolumna, double wartosc) throws ZłyIndeks {
        if (wiersz < 0 || wiersz >= liczbaWierszy() || kolumna < 0 || kolumna >= liczbaKolumn())
            throw new ZłyIndeks("Indeks poza zakresem wycinka: wiersz_widoku=" + wiersz + ", kolumna_widoku=" + kolumna +
                    " dla wycinka " + liczbaWierszy() + "x" + liczbaKolumn());
        if (czyTransponowany)
            daneGłówne[przesunięcieWiersza + kolumna][przesunięcieKolumny + wiersz] = wartosc;
        else
            daneGłówne[przesunięcieWiersza + wiersz][przesunięcieKolumny + kolumna] = wartosc;
        normalizuj();
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 2)
            throw new ZłyIndeks("MacierzWycinek wymaga 2 indeksów.");
        return daj(indeksy[0], indeksy[1]);
    }

    @Override
    public void ustaw(double wartosc, int... indeksy) throws ZłyIndeks {
        if (indeksy == null || indeksy.length != 2)
            throw new ZłyIndeks("MacierzWycinek wymaga 2 indeksów.");
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
            obsłużBłądLogiczny("suma(Skalar)", "Niespodziewany ZłyIndeks", e);
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
                    throw new NiezgodnośćRozmiarów("MWycinek.suma(Wp)");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = daj(i, j) + wektor.daj(j);
            } else {
                if (wektor.liczba_elementów() != wiersze)
                    throw new NiezgodnośćRozmiarów("MWycinek.suma(Wv)");
                for (int i = 0; i < wiersze; i++)
                    for (int j = 0; j < kolumny; j++)
                        wynik[i][j] = daj(i, j) + wektor.daj(i);
            }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Wektor)", "Niespodziewany ZłyIndeks od 'wektor' lub 'this'", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Macierz suma(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaWierszy() != macierz.liczbaWierszy() || liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("MWycinek.suma(macierz)");
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < kolumny; j++)
                    wynik[i][j] = daj(i, j) + macierz.daj(i, j);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("suma(Macierz)", "Niespodziewany ZłyIndeks od 'macierz' lub 'this'", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Macierz iloczyn(Skalar skalar) {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        double[][] wynik = new double[wiersze][kolumny];
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < wiersze; i++) for (int j = 0; j < kolumny; j++) wynik[i][j] = daj(i, j) * wartość;
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Skalar)", "Niespodziewany ZłyIndeks", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public Wektor iloczyn(Wektor wektor) throws NiezgodnośćRozmiarów {
        if (wektor.czyPoziomy())
            throw new NiezgodnośćRozmiarów("MWycinek*wektor: wektor musi być pionowy");
        if (liczbaKolumn() != wektor.liczba_elementów())
            throw new NiezgodnośćRozmiarów("MWycinek*wektor: złe wymiary");
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
            throw new NiezgodnośćRozmiarów("MWycinek*macierz: złe wymiary");
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        int macierzKolumny = macierz.liczbaKolumn();
        double[][] wynik = new double[wiersze][macierzKolumny];
        try {
            for (int i = 0; i < wiersze; i++)
                for (int j = 0; j < macierzKolumny; j++) {
                    double sum = 0;
                    for (int k = 0; k < kolumny; k++)
                        sum += daj(i, k) * macierz.daj(k, j);
                    wynik[i][j] = sum;
                }
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("iloczyn(Macierz)", "Niespodziewany ZłyIndeks od 'macierz' lub 'this'", e);
        }
        return new MacierzDane(wynik);
    }

    @Override
    public void dodaj(Skalar skalar) {
        double wartość = skalar.daj();
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    ustaw(i, j, daj(i, j) + wartość);
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("dodaj(Skalar)", "Niespodziewany ZłyIndeks", e);
        }
        normalizuj();
    }

    @Override
    public void dodaj(Wektor wektor) throws NiezgodnośćRozmiarów {
        try {
            if (wektor.czyPoziomy()) {
                if (wektor.liczba_elementów() != liczbaKolumn())
                    throw new NiezgodnośćRozmiarów("dodaj MWycinek+Wp");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, daj(i, j) + wektor.daj(j));
            } else {
                if (wektor.liczba_elementów() != liczbaWierszy())
                    throw new NiezgodnośćRozmiarów("dodaj MWycinek+Wv");
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
            throw new NiezgodnośćRozmiarów("dodaj MWycinek+macierz");
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
            obsłużBłądLogiczny("przemnoz(Skalar)", "Niespodziewany ZłyIndeks", e);
        }
        normalizuj();
    }

    @Override
    public void przemnóż(Macierz macierz) throws NiezgodnośćRozmiarów {
        if (liczbaKolumn() != macierz.liczbaWierszy())
            throw new NiezgodnośćRozmiarów("Przemnóż wycinek: l. kolumn != l. wierszy macierz");
        if (liczbaKolumn() != macierz.liczbaKolumn())
            throw new NiezgodnośćRozmiarów("Przemnóż wycinek: l. kolumn != l. kolumn macierz");
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
            obsłużBłądLogiczny("przypisz(Skalar)", "Niespodziewany ZłyIndeks", e);
        }
        normalizuj();
    }

    @Override
    public void przypisz(Wektor wektor) throws NiezgodnośćRozmiarów {
        try {
            if (wektor.czyPoziomy()) {
                if (wektor.liczba_elementów() != liczbaKolumn())
                    throw new NiezgodnośćRozmiarów("przypisz Wp do MWycinek");
                for (int i = 0; i < liczbaWierszy(); i++)
                    for (int j = 0; j < liczbaKolumn(); j++)
                        ustaw(i, j, wektor.daj(j));
            } else {
                if (wektor.liczba_elementów() != liczbaWierszy())
                    throw new NiezgodnośćRozmiarów("przypisz Wv do MWycinek");
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
            throw new NiezgodnośćRozmiarów("przypisz macierz do MWycinek");
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
    public Macierz wycinek(int wierszOd, int wierszDo, int kolumnaOd, int kolumnaDo) throws BłędneZakresy {
        int wiersze = liczbaWierszy();
        int kolumny = liczbaKolumn();
        if (wierszOd < 0 || wierszOd > wierszDo || wierszDo >= wiersze || kolumnaOd < 0 || kolumnaOd > kolumnaDo || kolumnaDo >= kolumny)
            throw new BłędneZakresy("Błędne zakresy dla wycinka z wycinka macierzy.");
        int noweWierszeWycinka, noweKolumnyWycinka;
        int nowePrzesunięcieWiersza, nowePrzesunięcieKolumny;
        if (czyTransponowany) {
            nowePrzesunięcieWiersza = przesunięcieWiersza + kolumnaOd;
            nowePrzesunięcieKolumny = przesunięcieKolumny + wierszOd;
            noweWierszeWycinka = kolumnaDo - kolumnaOd + 1;
            noweKolumnyWycinka = wierszDo - wierszOd + 1;
        } else {
            nowePrzesunięcieWiersza = przesunięcieWiersza + wierszOd;
            nowePrzesunięcieKolumny = przesunięcieKolumny + kolumnaOd;
            noweWierszeWycinka = wierszDo - wierszOd + 1;
            noweKolumnyWycinka = kolumnaDo - kolumnaOd + 1;
        }
        return new MacierzWycinek(daneGłówne, nowePrzesunięcieWiersza, nowePrzesunięcieKolumny, noweWierszeWycinka, noweKolumnyWycinka, czyTransponowany);
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
        sb.append("} (wycinek, ").append(liczbaWierszy()).append("x").append(liczbaKolumn()).append(")");
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
            obsłużBłądLogiczny("equals(Macierz)", "Niespodziewany ZłyIndeks", e);
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int wynik = Objects.hash(przesunięcieWiersza, przesunięcieKolumny, wierszeWycinka, kolumnyWycinka, czyTransponowany, wymiar(), Arrays.hashCode(kształt()));
        try {
            for (int i = 0; i < liczbaWierszy(); i++)
                for (int j = 0; j < liczbaKolumn(); j++)
                    wynik = 31 * wynik + Double.hashCode(daj(i, j));
        } catch (ZłyIndeks e) {
            obsłużBłądLogiczny("hashCode()", "Niespodziewany ZłyIndeks", e);
        }
        return wynik;
    }
}
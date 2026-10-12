package tablice;

import java.util.Arrays;
import java.util.Objects;

public class SkalarDane implements Skalar {

    private double wartość;

    public SkalarDane(double wartość) {
        this.wartość = wartość;
        normalizuj();
    }

    private void obsłużBłądLogiczny(String nazwaMetody, String komunikatDodatkowy, Throwable przyczyna) {
        System.err.println("Krytyczny błąd logiczny wektor " + getClass().getSimpleName() + "." + nazwaMetody + ":");
        System.err.println(komunikatDodatkowy);
        if (przyczyna != null) {
            System.err.println("Oryginalny wyjątek: " + przyczyna.getMessage());
            przyczyna.printStackTrace(System.err);
        }
        throw new AssertionError("Krytyczny błąd logiczny (" + nazwaMetody + "): " + komunikatDodatkowy, przyczyna);
    }

    private void normalizuj() {
        wartość = (wartość == 0) ? 0 : wartość;
    }

    @Override
    public int wymiar() {
        return 0;
    }

    @Override
    public int liczba_elementów() {
        return 1;
    }

    @Override
    public int[] kształt() {
        return new int[]{};
    }

    @Override
    public Skalar kopia() {
        return new SkalarDane(wartość);
    }

    @Override
    public Skalar negacja() {
        return new SkalarDane(-wartość);
    }

    @Override
    public void zaneguj() {
        ustaw(-daj());
        normalizuj();
    }

    @Override
    public void transponuj() {
        /* Brak implementacji dla skalara */
    }

    @Override
    public double daj() {
        return wartość;
    }

    @Override
    public void ustaw(double wartość) {
        this.wartość = wartość;
        normalizuj();
    }

    @Override
    public double daj(int... indeksy) throws ZłyIndeks {
        if (indeksy != null && indeksy.length != 0)
            throw new ZłyIndeks("Skalar wymaga 0 indeksów, podano: " + indeksy.length);
        return daj();
    }

    @Override
    public void ustaw(double wartość, int... indeksy) throws ZłyIndeks {
        if (indeksy != null && indeksy.length != 0)
            throw new ZłyIndeks("Skalar wymaga 0 indeksów do ustawienia, podano: " + indeksy.length);
        ustaw(wartość);
    }

    @Override
    public Skalar suma(Skalar skalar) {
        return new SkalarDane(daj() + skalar.daj());
    }

    @Override
    public Wektor suma(Wektor wektor) {
        double[] noweDane = new double[wektor.liczba_elementów()];
        for (int i = 0; i < wektor.liczba_elementów(); i++) {
            try {
                noweDane[i] = daj() + wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("suma(Wektor)", "Niespodziewany ZłyIndeks od argumentu wektor.daj(" + i + ")", e);
            }
        }
        return new WektorDane(noweDane, wektor.czyPoziomy());
    }

    @Override
    public Macierz suma(Macierz m) {
        double[][] noweDane = new double[m.liczbaWierszy()][m.liczbaKolumn()];
        for (int i = 0; i < m.liczbaWierszy(); i++) {
            for (int j = 0; j < m.liczbaKolumn(); j++) {
                try {
                    noweDane[i][j] = daj() + m.daj(i, j);
                } catch (ZłyIndeks e) {
                    obsłużBłądLogiczny("suma(Macierz)", "Niespodziewany ZłyIndeks od argumentu m.daj(" + i + "," + j + ")", e);
                }
            }
        }
        return new MacierzDane(noweDane);
    }

    @Override
    public Skalar iloczyn(Skalar skalar) {
        return new SkalarDane(daj() * skalar.daj());
    }

    @Override
    public Wektor iloczyn(Wektor wektor) {
        double[] noweDane = new double[wektor.liczba_elementów()];
        for (int i = 0; i < wektor.liczba_elementów(); i++) {
            try {
                noweDane[i] = daj() * wektor.daj(i);
            } catch (ZłyIndeks e) {
                obsłużBłądLogiczny("iloczyn(Wektor)", "Niespodziewany ZłyIndeks od argumentu wektor.daj(" + i + ")", e);
            }
        }
        return new WektorDane(noweDane, wektor.czyPoziomy());
    }

    @Override
    public Macierz iloczyn(Macierz m) {
        double[][] noweDane = new double[m.liczbaWierszy()][m.liczbaKolumn()];
        for (int i = 0; i < m.liczbaWierszy(); i++) {
            for (int j = 0; j < m.liczbaKolumn(); j++) {
                try {
                    noweDane[i][j] = daj() * m.daj(i, j);
                } catch (ZłyIndeks e) {
                    obsłużBłądLogiczny("iloczyn(Macierz)", "Niespodziewany ZłyIndeks od argumentu m.daj(" + i + "," + j + ")", e);
                }
            }
        }
        return new MacierzDane(noweDane);
    }

    @Override
    public void dodaj(Skalar skalar) {
        ustaw(daj() + skalar.daj());
        normalizuj();
    }

    @Override
    public void przemnóż(Skalar skalar) {
        ustaw(daj() * skalar.daj());
        normalizuj();
    }

    @Override
    public void przypisz(Skalar skalar) {
        ustaw(skalar.daj());
        normalizuj();
    }

    @Override
    public Skalar wycinek() {
        return new SkalarWycinek(this);
    }

    @Override
    public String toString() {
        return "[" + daj() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Skalar that))
            return false;
        return (Double.compare(daj(), that.daj()) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(daj(), wymiar(), Arrays.hashCode(kształt()));
    }
}
# Wielowymiarowy Silnik Tablic i Wycinków

Obiektowy silnik w języku Java do operacji na wielowymiarowych strukturach danych typu `double` (skalary 0D, wektory 1D poziome/pionowe, macierze 2D). Projekt implementuje arytmetykę międzywymiarową, aktywne widoki danych (wycinki) oraz modyfikacje struktur w miejscu.

## Główne Funkcjonalności

* **Hierarchia 0D / 1D / 2D**: Polimorficzny model klas dla skalarów, wektorów (z flagą orientacji) oraz prostokątnych macierzy.
* **Aktywne Widoki (Zero-Copy Views)**: Metoda `wycinek` tworzy podwidok na istniejących danych bez alokacji nowej tablicy - zmiany w wycinku modyfikują tablicę źródłową (obsługuje zagnieżdżone wycinki).
* **Dwa Tryby Operacji**:
  * Funkcyjne (nowy obiekt): `suma`, `iloczyn`, `negacja`.
  * W miejscu (mutujące stan): `dodaj`, `przemnóż`, `zaneguj`, `przypisz`.
* **Operacje Międzywymiarowe**:
  * Skalar można łączyć arytmetycznie z dowolną strukturą (operacja element po elemencie).
  * Działania wektor-macierz wykonywane są wierszowo lub kolumnowo zależnie od orientacji wektora.
  * Iloczyn wektorów tej samej orientacji zwraca skalar, a przeciwnej - macierz.
* **Nadzorowane Wyjątki**: Dedykowana hierarchia wyjątków weryfikująca poprawność logiczną: `BłędneZakresy`, `NiezgodnośćRozmiarów`, `ZłyIndeks`.

---

## Architektura Klas

```
src/
└── tablice/
    ├── Tablica.java              # Kontrakt bazowy (wymiar, kształt, indeksowanie varargs)
    ├── Skalar.java               # 0D (SkalarDane, SkalarWycinek)
    ├── Wektor.java               # 1D z orientacją (WektorDane, WektorWycinek)
    ├── Macierz.java              # 2D (MacierzDane, MacierzWycinek)
    └── [Wyjątki]                 # BłędneZakresy, NiezgodnośćRozmiarów, ZłyIndeks
```

## Kompilacja i Testy

Wymagania: **JDK 11+**

```bash
# Kompilacja
javac -d out src/tablice/*.java src/test/TabliceTest.java

# Uruchomienie testów
java -cp out test.TabliceTest
```

## Przykład Użycia

```java
import tablice.*;

// Inicjalizacja macierzy 2x3 i poziomego wektora
Macierz m = new MacierzDane(new double[][] {
    { 1.0, 3.5, -12.0 },
    { -5.0, 8.0, 3.0 }
});
Wektor v = new WektorDane(new double[] { 3.0, 1.5, -2.0 }, false);

// Operacja międzywymiarowa (dodanie wektora do wierszy macierzy)
Macierz wynik = (Macierz) m.suma(v);

// Aktywny wycinek (wiersze 0..1, kolumny 1..2)
Macierz sub = m.wycinek(0, 1, 1, 2);
sub.ustaw(99.0, 0, 0); // Zmienia wartość bezpośrednio w źródłowej macierzy 'm'
```
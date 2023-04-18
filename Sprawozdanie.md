# Symetryczny algorytm szyfrowania DES
### Dawid Sośnia, Wiktoria Płonka

## Etapy działania algorytmu
- Wygenerowanie 64-bitowego klucza
- Podział 64-bitowego klucza na 16 48-bitowych podkluczy
- Podział danych na 64 bitowe fragmenty
- Dla każdego fragmentu 16 rund:
    - Permutacja fragmentu danych
    - Podział fragmentu na segment lewy i prawy
    - Rozszerzenie prawego segmentu do 48 bitów
    - XOR prawego segmentu z odpowiednim podkluczem
    - sprowadzenie prawego segmentu do 32-bitowego segmentu
    - permutacja prawego segmentu danych
    - Zamiana miejsc lewego i prawego segmentu danych
- Ostateczna zamiana miejsc lewego i prawego segmentu danych
- Permutacja 64-bitowego fragmentu

Do najważniejszych z podanych wyżej funkcji należą:
### Podział 64-bitowego klucza na 16 48-bitowych podkluczy
```java
public byte[][] getSubkeys() {
    final byte[] PC1 = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
    final byte[] PC2 = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};
    final byte[] SHIFTS = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    int halfKeySize = 28;
    byte[] activeKey = Lib.selectBits(this.b_key, PC1);
    byte[] c = Lib.selectBits(activeKey, 0, halfKeySize);
    byte[] d = Lib.selectBits(activeKey, halfKeySize, halfKeySize);
    byte[][] subKeysLocal = new byte[16][];

    for (int k = 0; k < 16; k++) {
        c = Lib.rotateLeft(c, halfKeySize, SHIFTS[k]);
        d = Lib.rotateLeft(d, halfKeySize, SHIFTS[k]);
        byte[] cd = Lib.joinByteArrays(c, 0, halfKeySize, d, 0, halfKeySize);
        subKeysLocal[k] = Lib.selectBits(cd, PC2);
    }

    return subKeysLocal;
}
```

### Działania na prawym segmencie danych
```java
private byte[] computeRightBlock(byte[] in) {
    byte[] out = new byte[4];
    byte current;
    for (byte byteCounter = 0; byteCounter < 4; byteCounter++) {
        for (byte bitCounter = 7; bitCounter >= 0; bitCounter--) {
            current = (byte) (in[bitCounter] >>> this.shift[byteCounter]);
            current = (byte) (current & 1);
            current = (byte) (current << (bitCounter));
            out[3 - byteCounter] = (byte) (out[3 - byteCounter] | current);
        }
    }
    
    return out;
}
```

### Działania na lewym segmencie danych
```java
private byte[] computeLeftBlock(byte[] in) {
    byte[] out = new byte[4];
    byte current;

    for (byte byteCounter = 4; byteCounter < 8; byteCounter++) {
        for (byte bitCounter = 7; bitCounter >= 0; bitCounter--) {
            current = (byte) (in[bitCounter] >>> this.shift[byteCounter]);
            current = (byte) (current & 1);
            current = (byte) (current << (bitCounter));
            out[7 - byteCounter] = (byte) (out[7 - byteCounter] | current);
        }
    }

    return out;
}
```
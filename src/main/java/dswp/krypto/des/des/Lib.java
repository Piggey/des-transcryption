package dswp.krypto.des.des;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Lib {
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));

        return data;
    }

    public static String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (byte i : data) {
            sb.append(String.format("%02x", i));
        }

        return sb.toString();
    }

    public static byte[] XORByteArrays(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];

        for (int i = 0; i < a.length; i++)
            out[i] = (byte) (a[i] ^ b[i]);

        return out;
    }

    /** wybiera bity z tablicy bajtów i zwraca w nowej tablicy
     *  wybierane te bity, które wskazane w drugim parametrze(każdy bajt tablicy map jest numerem bitu do pobrania z tablicy in)
     */
    public static byte[] selectBits(byte[] in, byte[] map) {
        int numOfBytes = (map.length - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];

        for (int i = 0; i < map.length; i++) {
            int val = getBitAt(in, map[i] - 1);
            setBitAt(out, i, val);
        }

        return out;
    }

    /** zwraca podaną ilość bitów od podanej pozycji z tablicy bajtów */
    public static byte[] selectBits(byte[] in, int pos, int len)
    {
        int numOfBytes = (len - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < len; i++) {
            int val = getBitAt(in, pos + i);
            setBitAt(out, i, val);
        }

        return out;
    }

    public static int getBitAt(byte[] data, int poz) {
        int posByte = poz / 8;
        int posBit = poz % 8;
        byte valByte = data[posByte];

        return valByte >> (7 - posBit) & 1;
    }

    public static void setBitAt(byte[] data, int pos, int val) {
        byte oldByte = data[pos / 8];
        oldByte = (byte) (((0xFF7F >> (pos % 8)) & oldByte) & 0x00FF);
        byte newByte = (byte) ((val << (7 - (pos % 8))) | oldByte);
        data[pos / 8] = newByte;
    }

    /** cykliczne przesunięcie bitów w lewo o zadana ilość pozycji */
    public static byte[] rotateLeft(byte[] in, int len, int step)
    {
        byte[] out = new byte[(len - 1) / 8 + 1];

        for (int i = 0; i < len; i++)
        {
            int val = getBitAt(in, (i + step) % len);
            setBitAt(out, i, val);
        }

        return out;
    }

    public static byte[] joinByteArrays(byte[] a, int aStart, int aLength, byte[] b, int bStart, int bLength)
    {
        int numOfBytes = (aLength + bLength - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        int j = 0;
        for (int i = aStart; i < aLength; i++)
        {
            int val = getBitAt(a, i);
            setBitAt(out, j, val);
            j++;
        }
        for (int i = bStart; i < bLength; i++)
        {
            int val = getBitAt(b, i);
            setBitAt(out, j, val);
            j++;
        }

        return out;
    }

    public static Path getFileFromExplorer(String msg) {
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, msg);
        jfc.setVisible(true);
        File f = jfc.getSelectedFile();
        return Path.of(f.getPath());
    }
}

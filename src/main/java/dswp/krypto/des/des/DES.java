package dswp.krypto.des.des;

import java.nio.charset.StandardCharsets;

public class DES {
    private byte[] b_key;
    private byte[][] subKeys;
    private final byte[] shift = { 1, 3, 5, 7, 0, 2, 4, 6 };
    private final byte[] pBlock = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };
    private final byte[] sBox = {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, // S1
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13,
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, // S2
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9,
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, // S3
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12,
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, // S4
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14,
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, // S5
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3,
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, // S6
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13,
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, // S7
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12,
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, // S8
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
    };

    public DES() {
        setKeyHex("0123456789ABCDEF");
    }

    /**
     * Ustawia klucz ze stringa którego wartości są interpretowane jako poszczególne znaki i zamieniane na bity.
     * jeden znak to dwa bajty
     *
     */
    public boolean setKeyString(String key) {
        if (key.getBytes(StandardCharsets.UTF_8).length != 8) {
            return false;
        }

        this.b_key = key.getBytes(StandardCharsets.UTF_8);
        subKeys = getSubkeys();
        return true;
    }

    /** Ustawia klucz ze stringa którego wartości są interpretowane jako zapis HEX */
    public void setKeyHex(String key) {
        this.b_key = Lib.hexStringToByteArray(key);
        this.subKeys = getSubkeys();
    }

    public byte[] encode(byte[] message) throws Exception {
        int len = (message.length / 2 % 4 != 0) ? (message.length / 8 + 1) * 8 : message.length;
        byte[] result = new byte[len];
        byte[] tempBlock = new byte[8];

        for (int i = 0; i < (message.length / 8); i++) {
            tempBlock = encodeBlock(message, i * 8);
            System.arraycopy(tempBlock, 0, result, i * 8, 8);
        }

        if (message.length / 2 % 4 != 0) {
            for (int i = 0; i < 8; i++) {
                if (i + (message.length / 8) * 8 < message.length)
                    tempBlock[i] = message[i + (message.length / 8) * 8];
                else
                    tempBlock[i] = 0;
            }
            tempBlock = encodeBlock(tempBlock, 0);
            System.arraycopy(tempBlock, 0, result, (message.length / 8) * 8, 8);
        }

        return result;
    }

    public byte[] decode(byte[] encrypted) throws Exception {
        byte[] tmpResult = new byte[encrypted.length];
        byte[] tempBlock;

        for (int i = 0; i < (encrypted.length / 8); i++) {
            tempBlock = decodeBlock(encrypted, i * 8);
            System.arraycopy(tempBlock, 0, tmpResult, i * 8, tempBlock.length);
        }

        int cnt = 0;
        for (int i = 1; i < 9; i += 2) {
            if (tmpResult[tmpResult.length - i] == 0 && tmpResult[tmpResult.length - i - 1] == 0)
                cnt += 2;
            else
                break;
        }

        byte[] result = new byte[tmpResult.length - cnt];
        System.arraycopy(tmpResult, 0, result, 0, tmpResult.length - cnt);
        return result;
    }

    private byte[] encodeBlock(byte[] data, int beginIndex) throws Exception {
        byte[] msg = new byte[8];
        System.arraycopy(data, beginIndex, msg, 0, 8);

        return encrypt(msg);
    }

    private byte[] decodeBlock(byte[] data, int beginIndex) throws Exception {
        byte[] msg = new byte[8];
        System.arraycopy(data, beginIndex, msg, 0, 8);

        return decrypt(msg);
    }

    /** szyfruje 64 bity wiadomości */
    private byte[] encrypt(byte[] theMsg) throws Exception {
        if (theMsg.length != 8) {
            throw new Exception("Część wiadomości nie ma 8 bajtów długości");
        }

        byte[] r = computeRightBlock(theMsg);
        byte[] l = computeLeftBlock(theMsg);

        for (int k = 0; k < 16; k++) {
            byte[] rBackup = r;

            r = computeExtendedBlock(r);
            r = Lib.XORByteArrays(r, subKeys[k]);
            r = sBlocks(r);
            r = Lib.selectBits(r, pBlock);
            r = Lib.XORByteArrays(l, r);
            l = rBackup;
        }

        return computeResultBlock(l, r);
    }

    /** deszyfruje 64 bity zaszyfrowanej wiadomosci */
    private byte[] decrypt(byte[] theMsg) throws Exception {
        if (theMsg.length != 8) {
            throw new Exception("Część wiadomości nie ma 8 bajtów długości");
        }

        byte[] r = computeRightBlock(theMsg);
        byte[] l = computeLeftBlock(theMsg);
        int numOfSubKeys = subKeys.length;

        for (int k = 0; k < numOfSubKeys; k++) {
            byte[] rBackup = r;

            r = computeExtendedBlock(r);
            r = Lib.XORByteArrays(r, subKeys[numOfSubKeys - k - 1]);
            r = sBlocks(r);
            r = Lib.selectBits(r, pBlock);
            r = Lib.XORByteArrays(l, r);
            l = rBackup;
        }

        return computeResultBlock(l, r);
    }

    //generuje 16 podkluczy
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

    private byte[] computeExtendedBlock(byte[] block) {
        byte[] extendedBlock = new byte[6];
        short current;
        byte pBit = 31;
        byte changer = 0;
        for (int bit = 0; bit < 48; bit++) {
            current = (short) (block[pBit / 8] >> (7 - (pBit % 8)));
            current = (short) (current & 1);
            current = (short) (current << (7 - (bit % 8)));
            extendedBlock[bit / 8] = (byte) (extendedBlock[bit / 8] | (current));
            if (++changer == 6) {
                changer = 0;
                pBit--;
            } else
                pBit = (byte) ((++pBit) % 32);
        }

        return extendedBlock;
    }

    private byte[] computeResultBlock(byte[] l, byte[] r) {
        byte[] data = new byte[8];
        byte[] result = new byte[8];
        byte current;
        System.arraycopy(l, 0, data, 4, 4);
        System.arraycopy(r, 0, data, 0, 4);
        int currBit = 0;
        for (int readBitPos = 7; readBitPos >= 0; readBitPos--) {
            for (int readBytePos = 4; readBytePos < 8; readBytePos++) {
                current = (byte) (data[readBytePos] >> (7 - (readBitPos)));
                current = (byte) (current & 1);
                current = (byte) (current << (7 - (currBit % 8)));
                result[currBit / 8] = (byte) (result[currBit / 8] | current);
                currBit += 2;
            }
        }
        currBit = 1;
        for (int readBitPos = 7; readBitPos >= 0; readBitPos--) {
            for (int readBytePos = 0; readBytePos < 4; readBytePos++) {
                current = (byte) (data[readBytePos] >> (7 - (readBitPos)));
                current = (byte) (current & 1);
                current = (byte) (current << (7 - (currBit % 8)));
                result[currBit / 8] = (byte) (result[currBit / 8] | current);
                currBit += 2;
            }
        }
        return result;
    }

    private byte[] sBlocks(byte[] data) {
        byte row;
        byte col;
        data = create6BitData(data);
        byte[] result = new byte[data.length / 2];
        byte lowerHalfByte = 0;
        byte halfByte;
        for (int b = 0; b < data.length; b++) {
            row = (byte) (((data[b] >> 6) & 2) | ((data[b] >> 2) & 1));
            col = (byte) ((data[b] >> 3) & 15);
            halfByte = sBox[64 * b + 16 * row + col];
            if (b % 2 == 0)
                lowerHalfByte = halfByte;
            else
                result[b / 2] = (byte) (16 * lowerHalfByte + halfByte);
        }
        return result;
    }

    private byte[] create6BitData(byte[] data) {
        int numOfBytes = (8 * data.length - 1) / 6 + 1;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < numOfBytes; i++) {
            for (int j = 0; j < 6; j++) {
                int val = Lib.getBitAt(data, 6 * i + j);
                Lib.setBitAt(out, 8 * i + j, val);
            }
        }
        return out;
    }
}

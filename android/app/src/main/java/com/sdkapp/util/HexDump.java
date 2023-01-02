/*
 * Copyright (C) 2014 zhuxy   Ekemp Co.,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.sdkapp.util;

public class HexDump {
    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();

        byte[] line = new byte[16];
        int lineIndex = 0;

        result.append("\n0x");
        result.append(toHexString(offset));

        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == 16) {
                result.append(" ");

                for (int j = 0; j < 16; j++) {
                    if (line[j] > ' ' && line[j] < '~') {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(".");
                    }
                }

                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }

            byte b = array[i];
            result.append(" ");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

            line[lineIndex++] = b;
        }

        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            count++;
            for (int i = 0; i < count; i++) {
                result.append(" ");
            }

            for (int i = 0; i < lineIndex; i++) {
                if (line[i] > ' ' && line[i] < '~') {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }
        }

        return result.toString();
    }

    public static String dumpHex(byte[] array) {
        return dumpHex(array, 0, array.length);
    }

    public static String dumpHex(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();

        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            if (i != offset) {
                result.append("");
            }
            result.append("");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

        }
        return result.toString();
    }

    public static String dumpHexWithOut0x(byte[] array) {
        return dumpHexWithOut0x(array, 0, array.length);
    }

    public static String dumpHexWithOut0x(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();

        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            if (i != offset) {
            }
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

        }
        return result.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
            buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
        }

        return new String(buf);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = new byte[1];
        array[0] = b;
        return array;
    }

    public static byte[] toByteArray(int i) {
        byte[] array = new byte[4];

        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);

        return array;
    }

    private static int toByte(char c) {
        if (c >= '0' && c <= '9')
            return (c - '0');
        if (c >= 'A' && c <= 'F')
            return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f')
            return (c - 'a' + 10);

        throw new RuntimeException("Invalid hex char '" + c + "'");
    }

    /**
     * 2个16进制数相加成int
     * <p/>
     * int是有符号的，byte转成int后byte的符号位成为了int的符号位。
     *
     * @param h 高位
     * @param l 低位
     * @return
     */
    public static int addTwoBytes(int h, int l) {
        h = h & 0xff;
        l = l & 0xff;
        return ((h << 8) | l);
    }

    public static int byteToInt(byte b) {
        return b;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
                    .charAt(i + 1)));
        }

        return buffer;
    }

    public static int lanma2Can(int h, int l) {
        return addTwoBytes(h, l) >> 5;
    }

    public static int can2Lanma(int h, int l) {
        return addTwoBytes(h, l) << 5;
    }

    /**
     * 获取蓝马高位
     *
     * @param h 真实高位
     * @param l 真实低位
     * @return
     */
    public static byte getLanMaH(int h, int l) {
        return HexDump.toByteArray(HexDump.can2Lanma(h, l))[2];
    }

    /**
     * 获取蓝马低位
     *
     * @param h 真实高位
     * @param l 真实低位
     * @return
     */
    public static byte getLanMaL(int h, int l) {
        return HexDump.toByteArray(HexDump.can2Lanma(h, l))[3];
    }


    /**
     * 获取真实高位
     *
     * @param h 蓝马高位
     * @param l 蓝马低位
     * @return
     */
    public static byte getTrueH(int h, int l) {
        return HexDump.toByteArray(HexDump.lanma2Can(h, l))[2];
    }

    /**
     * 获取真实低位
     *
     * @param h 蓝马高位
     * @param l 蓝马低位
     * @return
     */
    public static byte getTrueL(int h, int l) {
        return HexDump.toByteArray(HexDump.lanma2Can(h, l))[3];
    }

    //1028 01 68 96 转 0x10 0x28 0x01 0x68 0x96
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}

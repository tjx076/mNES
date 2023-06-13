package org.wrpg.mnes.utils;

import org.wrpg.mnes.utils.SystemPropertyUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class HexUtils {

    public static final String NEWLINE = SystemPropertyUtil.get("line.separator", "\n");

    public static String prettyPrintBytes(String name, byte[] bytes) {

        ByteBuffer msg = ByteBuffer.wrap(bytes);

        int length = msg.remaining();

        if (length == 0) {
            StringBuilder buf = new StringBuilder(name.length() + 4);
            buf.append(name).append(": 0B");
            return buf.toString();
        } else {
            int outputLength = name.length() + 2 + 10 + 1;

            int rows = length / 16 + (length % 15 == 0? 0 : 1) + 4;
            int hexDumpLength = 2 + rows * 80;
            outputLength += hexDumpLength;

            StringBuilder buf = new StringBuilder(outputLength);
            buf.append(name).append(": ").append(length).append('B');

            buf.append(NEWLINE);
            appendPrettyHexDump(buf, msg);


            return buf.toString();
        }
    }

    private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf) {
        appendPrettyHexDump(dump, buf, buf.position(), buf.remaining());
    }

    private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf, int offset, int length) {
        HexUtil.appendPrettyHexDump(dump, buf, offset, length);
    }

    private static final class HexUtil {

        private static final char[] BYTE2CHAR = new char[256];
        private static final char[] HEXDUMP_TABLE = new char[256 * 4];
        private static final String[] HEXPADDING = new String[16];
        private static final String[] HEXDUMP_ROWPREFIXES = new String[65536 >>> 4];
        private static final String[] BYTE2HEX = new String[256];
        private static final String[] BYTEPADDING = new String[16];

        static {
            final char[] DIGITS = "0123456789abcdef".toCharArray();
            for (int i = 0; i < 256; i ++) {
                HEXDUMP_TABLE[ i << 1     ] = DIGITS[i >>> 4 & 0x0F];
                HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i       & 0x0F];
            }

            int i;

            // Generate the lookup table for hex dump paddings
            for (i = 0; i < HEXPADDING.length; i ++) {
                int padding = HEXPADDING.length - i;
                StringBuilder buf = new StringBuilder(padding * 3);
                for (int j = 0; j < padding; j ++) {
                    buf.append("   ");
                }
                HEXPADDING[i] = buf.toString();
            }

            // Generate the lookup table for the start-offset header in each row (up to 64KiB).
            for (i = 0; i < HEXDUMP_ROWPREFIXES.length; i ++) {
                StringBuilder buf = new StringBuilder(12);
                buf.append(NEWLINE);
                buf.append(Long.toHexString(i << 4 & 0xFFFFFFFFL | 0x100000000L));
                buf.setCharAt(buf.length() - 9, '|');
                buf.append('|');
                HEXDUMP_ROWPREFIXES[i] = buf.toString();
            }

            // Generate the lookup table for byte-to-hex-dump conversion
            for (i = 0; i < BYTE2HEX.length; i ++) {
                BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
            }

            // Generate the lookup table for byte dump paddings
            for (i = 0; i < BYTEPADDING.length; i ++) {
                int padding = BYTEPADDING.length - i;
                StringBuilder buf = new StringBuilder(padding);
                for (int j = 0; j < padding; j ++) {
                    buf.append(' ');
                }
                BYTEPADDING[i] = buf.toString();
            }

            // Generate the lookup table for byte-to-char conversion
            for (i = 0; i < BYTE2CHAR.length; i ++) {
                if (i <= 0x1f || i >= 0x7f) {
                    BYTE2CHAR[i] = '.';
                } else {
                    BYTE2CHAR[i] = (char) i;
                }
            }
        }

        public static int checkPositiveOrZero(int i, String name) {
            if (i < 0) {
                throw new IllegalArgumentException(name + " : " + i + " (expected: >= 0)");
            } else {
                return i;
            }
        }

        public static short getUnsignedByte(ByteBuffer buffer, int index) {
            return (short) (buffer.get(index) & 0xFF);
        }

        private static String hexDump(ByteBuffer buffer, int fromIndex, int length) {
            checkPositiveOrZero(length, "length");
            if (length == 0) {
                return "";
            }

            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];

            int srcIdx = fromIndex;
            int dstIdx = 0;
            for (; srcIdx < endIndex; srcIdx ++, dstIdx += 2) {
                System.arraycopy(
                        HEXDUMP_TABLE, getUnsignedByte(buffer, srcIdx) << 1,
                        buf, dstIdx, 2);
            }

            return new String(buf);
        }

        private static String hexDump(byte[] array, int fromIndex, int length) {
            checkPositiveOrZero(length, "length");
            if (length == 0) {
                return "";
            }

            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];

            int srcIdx = fromIndex;
            int dstIdx = 0;
            for (; srcIdx < endIndex; srcIdx ++, dstIdx += 2) {
                System.arraycopy(
                        HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1,
                        buf, dstIdx, 2);
            }

            return new String(buf);
        }

        private static String prettyHexDump(ByteBuffer buffer, int offset, int length) {
            if (length == 0) {
                return StringUtil.EMPTY_STRING;
            } else {
                int rows = length / 16 + ((length & 15) == 0? 0 : 1) + 4;
                StringBuilder buf = new StringBuilder(rows * 80);
                appendPrettyHexDump(buf, buffer, offset, length);
                return buf.toString();
            }
        }

        public static boolean isOutOfBounds(int index, int length, int capacity) {
            return (index | length | capacity | index + length | capacity - (index + length)) < 0;
        }

        private static void appendPrettyHexDump(StringBuilder dump, ByteBuffer buf, int offset, int length) {
            if (isOutOfBounds(offset, length, buf.capacity())) {
                throw new IndexOutOfBoundsException(
                        "expected: " + "0 <= offset(" + offset + ") <= offset + length(" + length
                                + ") <= " + "buf.capacity(" + buf.capacity() + ')');
            }
            if (length == 0) {
                return;
            }
            dump.append(
                    "         +-------------------------------------------------+" +
                            NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" +
                            NEWLINE + "+--------+-------------------------------------------------+----------------+");

            final int fullRows = length >>> 4;
            final int remainder = length & 0xF;

            // Dump the rows which have 16 bytes.
            for (int row = 0; row < fullRows; row ++) {
                int rowStartIndex = (row << 4) + offset;

                // Per-row prefix.
                appendHexDumpRowPrefix(dump, row, rowStartIndex);

                // Hex dump
                int rowEndIndex = rowStartIndex + 16;
                for (int j = rowStartIndex; j < rowEndIndex; j ++) {
                    dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
                }
                dump.append(" |");

                // ASCII dump
                for (int j = rowStartIndex; j < rowEndIndex; j ++) {
                    dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
                }
                dump.append('|');
            }

            // Dump the last row which has less than 16 bytes.
            if (remainder != 0) {
                int rowStartIndex = (fullRows << 4) + offset;
                appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

                // Hex dump
                int rowEndIndex = rowStartIndex + remainder;
                for (int j = rowStartIndex; j < rowEndIndex; j ++) {
                    dump.append(BYTE2HEX[getUnsignedByte(buf, j)]);
                }
                dump.append(HEXPADDING[remainder]);
                dump.append(" |");

                // Ascii dump
                for (int j = rowStartIndex; j < rowEndIndex; j ++) {
                    dump.append(BYTE2CHAR[getUnsignedByte(buf, j)]);
                }
                dump.append(BYTEPADDING[remainder]);
                dump.append('|');
            }

            dump.append(NEWLINE +
                    "+--------+-------------------------------------------------+----------------+");
        }

        private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
            if (row < HEXDUMP_ROWPREFIXES.length) {
                dump.append(HEXDUMP_ROWPREFIXES[row]);
            } else {
                dump.append(NEWLINE);
                dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
        }
    }



    private static class StringUtil {

        public static final String EMPTY_STRING = "";

        private static final String[] BYTE2HEX_PAD = new String[256];
        private static final String[] BYTE2HEX_NOPAD = new String[256];
        private static final byte[] HEX2B;


        static {
            // Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
            for (int i = 0; i < BYTE2HEX_PAD.length; i++) {
                String str = Integer.toHexString(i);
                BYTE2HEX_PAD[i] = i > 0xf ? str : ('0' + str);
                BYTE2HEX_NOPAD[i] = str;
            }
            // Generate the lookup table that converts an hex char into its decimal value:
            // the size of the table is such that the JVM is capable of save any bounds-check
            // if a char type is used as an index.
            HEX2B = new byte[Character.MAX_VALUE + 1];
            Arrays.fill(HEX2B, (byte) -1);
            HEX2B['0'] = (byte) 0;
            HEX2B['1'] = (byte) 1;
            HEX2B['2'] = (byte) 2;
            HEX2B['3'] = (byte) 3;
            HEX2B['4'] = (byte) 4;
            HEX2B['5'] = (byte) 5;
            HEX2B['6'] = (byte) 6;
            HEX2B['7'] = (byte) 7;
            HEX2B['8'] = (byte) 8;
            HEX2B['9'] = (byte) 9;
            HEX2B['A'] = (byte) 10;
            HEX2B['B'] = (byte) 11;
            HEX2B['C'] = (byte) 12;
            HEX2B['D'] = (byte) 13;
            HEX2B['E'] = (byte) 14;
            HEX2B['F'] = (byte) 15;
            HEX2B['a'] = (byte) 10;
            HEX2B['b'] = (byte) 11;
            HEX2B['c'] = (byte) 12;
            HEX2B['d'] = (byte) 13;
            HEX2B['e'] = (byte) 14;
            HEX2B['f'] = (byte) 15;
        }

        public static String byteToHexStringPadded(int value) {
            return BYTE2HEX_PAD[value & 255];
        }

    }

}

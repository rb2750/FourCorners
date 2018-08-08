package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serialization
{
    public static final byte[] HEADER = "RCL".getBytes();
    public static final short VERSION = 0x0001;

    public static int writeBytes(byte[] dest, int pointer, byte[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            dest[pointer++] = src[i];
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, short[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, char[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, int[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, long[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, float[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, double[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, boolean[] src)
    {
        assert(dest.length >= pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            pointer = writeBytes(dest, pointer, src[i]);
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, byte value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.BYTE));
        dest[pointer++] = value;

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, short value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.SHORT));
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, char value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.CHAR));
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, int value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.INTEGER));
        dest[pointer++] = (byte)((value >> 24) & 0xFF);
        dest[pointer++] = (byte)((value >> 16) & 0xFF);
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, long value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.LONG));
        dest[pointer++] = (byte)((value >> 56) & 0xFF);
        dest[pointer++] = (byte)((value >> 48) & 0xFF);
        dest[pointer++] = (byte)((value >> 40) & 0xFF);
        dest[pointer++] = (byte)((value >> 32) & 0xFF);
        dest[pointer++] = (byte)((value >> 24) & 0xFF);
        dest[pointer++] = (byte)((value >> 16) & 0xFF);
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, float value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.FLOAT));
        return writeBytes(dest, pointer, Float.floatToIntBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, double value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.DOUBLE));
        return writeBytes(dest, pointer, Double.doubleToLongBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, boolean value)
    {
        assert(dest.length >= pointer + SerialType.getSize(SerialType.BOOLEAN));
        dest[pointer++] = (byte)(value ? 1 : 0);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, String string)
    {
        pointer = writeBytes(dest, pointer, (short)string.length());
        return writeBytes(dest, pointer, string.getBytes());
    }

    public static byte readByte(byte[] src, int pointer)
    {
        return src[pointer];
    }

    public static void readBytes(byte[] src, int pointer, byte[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = src[pointer + i];
        }
    }

    public static short readShort(byte[] src, int pointer)
    {
        return (short) ((src[pointer] << 8) | (src[pointer + 1]));
    }

    public static void readShorts(byte[] src, int pointer, short[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readShort(src, pointer);
            pointer += 2;
        }
    }

    public static char readChar(byte[] src, int pointer)
    {
        return (char) ((src[pointer] << 8) | (src[pointer + 1]));
    }

    public static void readChars(byte[] src, int pointer, char[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readChar(src, pointer);
            pointer += 2;
        }
    }

    public static int readInt(byte[] src, int pointer)
    {
        return ByteBuffer.wrap(src, pointer, 4).getInt();
        //return (int)((src[pointer] << 24) | (src[pointer + 1] << 16) | (src[pointer + 2] << 8) | (src[pointer + 3]));
    }

    public static void readInts(byte[] src, int pointer, int[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readShort(src, pointer);
            pointer += 4;
        }
    }

    public static long readLong(byte[] src, int pointer)
    {
        return (long)((src[pointer] << 56) | (src[pointer + 1] << 48) | (src[pointer + 2] << 40) | (src[pointer + 3] << 32) |
                      (src[pointer + 4] << 24) | (src[pointer + 5] << 16) | (src[pointer + 6] << 8) | (src[pointer + 7]));
    }

    public static void readLongs(byte[] src, int pointer, long[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readShort(src, pointer);
            pointer += 8;
        }
    }

    public static float readFloat(byte[] src, int pointer)
    {
        return Float.intBitsToFloat(readInt(src, pointer));
    }

    public static void readFloats(byte[] src, int pointer, float[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readFloat(src, pointer);
            pointer += 4;
        }
    }

    public static double readDouble(byte[] src, int pointer)
    {
        return Double.longBitsToDouble(readLong(src, pointer));
    }

    public static void readDoubles(byte[] src, int pointer, double[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readDouble(src, pointer);
            pointer += 8;
        }
    }

    public static boolean readBoolean(byte[] src, int pointer)
    {
        return src[pointer] != 0;
    }

    public static void readBooleans(byte[] src, int pointer, boolean[] dest)
    {
        for (int i = 0; i < dest.length; i++)
        {
            dest[i] = readBoolean(src, pointer);
            pointer += 1;
        }
    }

    public static String readString(byte[] src, int pointer, int length)
    {
        return new String(src, pointer, length);
    }
}

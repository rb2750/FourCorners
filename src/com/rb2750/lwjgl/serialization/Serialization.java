package com.rb2750.lwjgl.serialization;

public class Serialization
{
    public static final byte[] HEADER = "RCL".getBytes();
    public static final short VERSION = 0x0001;

    public static int writeBytes(byte[] dest, int pointer, byte[] src)
    {
        assert(dest.length > pointer + src.length);

        for (int i = 0; i < src.length; i++)
        {
            dest[pointer++] = src[i];
        }

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, byte value)
    {
        assert(dest.length > pointer + Type.getSize(Type.BYTE));
        dest[pointer++] = value;

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, short value)
    {
        assert(dest.length > pointer + Type.getSize(Type.SHORT));
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, char value)
    {
        assert(dest.length > pointer + Type.getSize(Type.CHAR));
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, int value)
    {
        assert(dest.length > pointer + Type.getSize(Type.INT));
        dest[pointer++] = (byte)((value >> 24) & 0xFF);
        dest[pointer++] = (byte)((value >> 16) & 0xFF);
        dest[pointer++] = (byte)((value >> 8) & 0xFF);
        dest[pointer++] = (byte)((value) & 0xFF);

        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, long value)
    {
        assert(dest.length > pointer + Type.getSize(Type.LONG));
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
        assert(dest.length > pointer + Type.getSize(Type.FLOAT));
        return writeBytes(dest, pointer, Float.floatToIntBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, double value)
    {
        assert(dest.length > pointer + Type.getSize(Type.DOUBLE));
        return writeBytes(dest, pointer, Double.doubleToLongBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, boolean value)
    {
        assert(dest.length > pointer + Type.getSize(Type.BOOLEAN));
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

    public static short readShort(byte[] src, int pointer)
    {
        return (short) ((src[pointer] << 8) | (src[pointer + 1]));
    }

    public static char readChar(byte[] src, int pointer)
    {
        return (char) ((src[pointer] << 8) | (src[pointer + 1]));
    }

    public static int readInt(byte[] src, int pointer)
    {
        return (int)((src[pointer] << 24) | (src[pointer + 1] << 16) | (src[pointer + 2] << 8) | (src[pointer + 3]));
    }

    public static long readLong(byte[] src, int pointer)
    {
        return (long)((src[pointer] << 56) | (src[pointer + 1] << 48) | (src[pointer + 2] << 40) | (src[pointer + 3] << 32) |
                      (src[pointer + 4] << 24) | (src[pointer + 5] << 16) | (src[pointer + 6] << 8) | (src[pointer + 7]));
    }

    public static float readFloat(byte[] src, int pointer)
    {
        return Float.intBitsToFloat(readInt(src, pointer));
    }

    public static double readDouble(byte[] src, int pointer)
    {
        return Double.longBitsToDouble(readLong(src, pointer));
    }

    public static boolean readBoolean(byte[] src, int pointer)
    {
        return src[pointer] != 0;
    }
}

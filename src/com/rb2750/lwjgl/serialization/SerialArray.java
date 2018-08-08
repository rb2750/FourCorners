package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialArray
{
    public static final byte CONTAINER_TYPE = SerialContainerType.ARRAY;
    public short nameLength;
    public byte[] name;
    @Getter
    public int size = 1 + 2 + 4 + 1 + 4;
    public byte type;
    public int count;

    public byte[] data;
    public short[] shortData;
    public char[] charData;
    public int[] intData;
    public long[] longData;
    public float[] floatData;
    public double[] doubleData;
    public boolean[] booleanData;

    private static SerialArray createArray(String name, byte type)
    {
        SerialArray serialArray = new SerialArray();
        serialArray.setName(name);
        serialArray.type = type;

        return serialArray;
    }

    public static SerialArray createByte(String name, byte[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.BYTE);
        serialArray.count = values.length;
        serialArray.data = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createShort(String name, short[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.SHORT);
        serialArray.count = values.length;
        serialArray.shortData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createChar(String name, char[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.CHAR);
        serialArray.count = values.length;
        serialArray.charData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createInteger(String name, int[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.INTEGER);
        serialArray.count = values.length;
        serialArray.intData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createLong(String name, long[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.LONG);
        serialArray.count = values.length;
        serialArray.longData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createFloat(String name, float[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.FLOAT);
        serialArray.count = values.length;
        serialArray.floatData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createDouble(String name, double[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.DOUBLE);
        serialArray.count = values.length;
        serialArray.doubleData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray createBoolean(String name, boolean[] values)
    {
        SerialArray serialArray = createArray(name, SerialType.BOOLEAN);
        serialArray.count = values.length;
        serialArray.booleanData = values;
        serialArray.updateSize();

        return serialArray;
    }

    public static SerialArray deserialize(byte[] data, int pointer)
    {
        byte containerType = data[pointer++];
        assert (containerType == CONTAINER_TYPE);

        SerialArray result = new SerialArray();
        result.nameLength = readShort(data, pointer);
        pointer += 2;
        result.name = readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = readInt(data, pointer);
        pointer += 4;

        result.type = data[pointer++];

        result.count = readInt(data, pointer);
        pointer += 4;

        switch (result.type)
        {
            case SerialType.BYTE:
                result.data = new byte[result.count];
                readBytes(data, pointer, result.data);
                break;
            case SerialType.SHORT:
                result.shortData = new short[result.count];
                readShorts(data, pointer, result.shortData);
                break;
            case SerialType.CHAR:
                result.charData = new char[result.count];
                readChars(data, pointer, result.charData);
                break;
            case SerialType.INTEGER:
                result.intData = new int[result.count];
                readInts(data, pointer, result.intData);
                break;
            case SerialType.LONG:
                result.longData = new long[result.count];
                readLongs(data, pointer, result.longData);
                break;
            case SerialType.FLOAT:
                result.floatData = new float[result.count];
                readFloats(data, pointer, result.floatData);
                break;
            case SerialType.DOUBLE:
                result.doubleData = new double[result.count];
                readDoubles(data, pointer, result.doubleData);
                break;
            case SerialType.BOOLEAN:
                result.booleanData = new boolean[result.count];
                readBooleans(data, pointer, result.booleanData);
                break;
        }

        return result;
    }

    private void updateSize()
    {
        size += getDataSize();
    }

    public void setName(String name)
    {
        assert(name.length() < Short.MAX_VALUE);

        if (this.name != null)
            size -= this.name.length;

        nameLength = (short)name.length();
        this.name = name.getBytes();
        size += nameLength;
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);
        pointer = writeBytes(dest, pointer, type);
        pointer = writeBytes(dest, pointer, count);

        switch (type)
        {
            case SerialType.BYTE:
                pointer = writeBytes(dest, pointer, data);
                break;
            case SerialType.SHORT:
                pointer = writeBytes(dest, pointer, shortData);
                break;
            case SerialType.CHAR:
                pointer = writeBytes(dest, pointer, charData);
                break;
            case SerialType.INTEGER:
                pointer = writeBytes(dest, pointer, intData);
                break;
            case SerialType.LONG:
                pointer = writeBytes(dest, pointer, longData);
                break;
            case SerialType.FLOAT:
                pointer = writeBytes(dest, pointer, floatData);
                break;
            case SerialType.DOUBLE:
                pointer = writeBytes(dest, pointer, doubleData);
                break;
            case SerialType.BOOLEAN:
                pointer = writeBytes(dest, pointer, booleanData);
                break;
        }

        return pointer;
    }

    public int getDataSize()
    {
        switch (type)
        {
            case SerialType.UNKNOWN: assert (false); return 1;
            case SerialType.BYTE: return data.length * SerialType.getSize(SerialType.BYTE);
            case SerialType.SHORT: return shortData.length * SerialType.getSize(SerialType.SHORT);
            case SerialType.CHAR: return charData.length * SerialType.getSize(SerialType.CHAR);
            case SerialType.INTEGER: return intData.length * SerialType.getSize(SerialType.INTEGER);
            case SerialType.LONG: return longData.length * SerialType.getSize(SerialType.LONG);
            case SerialType.FLOAT: return floatData.length * SerialType.getSize(SerialType.FLOAT);
            case SerialType.DOUBLE: return doubleData.length * SerialType.getSize(SerialType.DOUBLE);
            case SerialType.BOOLEAN: return booleanData.length * SerialType.getSize(SerialType.BOOLEAN);
        }

        assert (false);
        return 1;
    }

    public String getName()
    {
        return new String(name, 0, nameLength);
    }
}

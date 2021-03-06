package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialField extends SerialBase
{
    public static final byte CONTAINER_TYPE = SerialContainerType.FIELD;
    public byte type;
    public byte[] data;

    private static SerialField createField(String name, byte type)
    {
        SerialField field = new SerialField();
        field.setName(name);
        field.type = type;
        field.data = new byte[SerialType.getSize(type)];

        return field;
    }

    public static SerialField createByte(String name, byte value)
    {
        SerialField field = createField(name, SerialType.BYTE);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createShort(String name, short value)
    {
        SerialField field = createField(name, SerialType.SHORT);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createChar(String name, char value)
    {
        SerialField field = createField(name, SerialType.CHAR);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createInteger(String name, int value)
    {
        SerialField field = createField(name, SerialType.INTEGER);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createLong(String name, long value)
    {
        SerialField field = createField(name, SerialType.LONG);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createFloat(String name, float value)
    {
        SerialField field = createField(name, SerialType.FLOAT);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createDouble(String name, double value)
    {
        SerialField field = createField(name, SerialType.DOUBLE);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField createBoolean(String name, boolean value)
    {
        SerialField field = createField(name, SerialType.BOOLEAN);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static SerialField deserialize(byte[] data, int pointer)
    {
        byte containerType = data[pointer++];
        assert (containerType == CONTAINER_TYPE);

        SerialField result = new SerialField();
        result.nameLength = readShort(data, pointer);
        pointer += 2;
        result.name = readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.type = data[pointer++];

        result.data = new byte[SerialType.getSize(result.type)];
        readBytes(data, pointer, result.data);

        return result;
    }

    public int getSize()
    {
        assert(data.length == SerialType.getSize(type));
        return 1 + 2 + name.length + 1 + data.length;
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, type);
        pointer = writeBytes(dest, pointer, data);

        return pointer;
    }

    public byte getByte()
    {
        if (type != SerialType.BYTE)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a byte.");

        return data[0];
    }

    public short getShort()
    {
        if (type != SerialType.SHORT)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a short.");

        return readShort(data, 0);
    }

    public char getChar()
    {
        if (type != SerialType.CHAR)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a char.");

        return readChar(data, 0);
    }

    public int getInteger()
    {
        if (type != SerialType.INTEGER)
            throw new IllegalArgumentException("Field '" + getName() + "' is not an integer.");

        return readInt(data, 0);
    }

    public long getLong()
    {
        if (type != SerialType.LONG)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a long.");

        return readLong(data, 0);
    }

    public float getFloat()
    {
        if (type != SerialType.FLOAT)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a float.");

        return readFloat(data, 0);
    }

    public double getDouble()
    {
        if (type != SerialType.DOUBLE)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a double.");

        return readDouble(data, 0);
    }

    public boolean getBoolean()
    {
        if (type != SerialType.BOOLEAN)
            throw new IllegalArgumentException("Field '" + getName() + "' is not a boolean.");

        return readBoolean(data, 0);
    }
}

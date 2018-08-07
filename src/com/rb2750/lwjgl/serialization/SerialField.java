package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialField
{
    public static final byte CONTAINER_TYPE = SerialContainerType.FIELD;
    public short nameLength;
    public byte[] name;
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

    public int getSize()
    {
        assert(data.length == SerialType.getSize(type));
        return 1 + 2 + name.length + 1 + data.length;
    }

    public void setName(String name)
    {
        assert(name.length() < Short.MAX_VALUE);
        nameLength = (short)name.length();
        this.name = name.getBytes();
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
}

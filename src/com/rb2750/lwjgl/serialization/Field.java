package com.rb2750.lwjgl.serialization;

import static com.rb2750.lwjgl.serialization.Serialization.*;

public class Field
{
    public static final byte CONTAINER_TYPE = ContainerType.FIELD;
    public short nameLength;
    public byte[] name;
    public byte type;
    public byte[] data;

    public static Field createField(String name, byte type)
    {
        Field field = new Field();
        field.setName(name);
        field.type = type;
        field.data = new byte[Type.getSize(type)];

        return field;
    }

    public static Field createByte(String name, byte value)
    {
        Field field = createField(name, Type.BYTE);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createShort(String name, short value)
    {
        Field field = createField(name, Type.SHORT);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createChar(String name, char value)
    {
        Field field = createField(name, Type.CHAR);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createInteger(String name, int value)
    {
        Field field = createField(name, Type.INT);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createLong(String name, long value)
    {
        Field field = createField(name, Type.LONG);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createFloat(String name, float value)
    {
        Field field = createField(name, Type.FLOAT);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createDouble(String name, double value)
    {
        Field field = createField(name, Type.DOUBLE);
        writeBytes(field.data, 0, value);

        return field;
    }

    public static Field createBoolean(String name, boolean value)
    {
        Field field = createField(name, Type.BOOLEAN);
        writeBytes(field.data, 0, value);

        return field;
    }

    public int getSize()
    {
        assert(data.length == Type.getSize(type));
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

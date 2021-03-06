package com.rb2750.lwjgl.serialization;

import static com.rb2750.lwjgl.serialization.Serialization.*;

public class SerialString extends SerialBase
{
    public static final byte CONTAINER_TYPE = SerialContainerType.STRING;

    public int count = 0;

    public char[] characters;

    private SerialString()
    {
        size += 1 + 4;
    }

    public static SerialString create(String name, String data)
    {
        SerialString serialString = new SerialString();
        serialString.setName(name);
        serialString.count = data.length();
        serialString.characters = data.toCharArray();
        serialString.updateSize();

        return serialString;
    }

    public static SerialString deserialize(byte[] data, int pointer)
    {
        byte containerType = data[pointer++];
        assert (containerType == CONTAINER_TYPE);

        SerialString result = new SerialString();
        result.nameLength = readShort(data, pointer);
        pointer += 2;
        result.name = readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = readInt(data, pointer);
        pointer += 4;

        result.count = readInt(data, pointer);
        pointer += 4;

        result.characters = new char[result.count];
        readChars(data, pointer, result.characters);

        return result;
    }

    private void updateSize()
    {
        size += getDataSize();
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);
        pointer = writeBytes(dest, pointer, count);
        pointer = writeBytes(dest, pointer, characters);

        return pointer;
    }

    public int getDataSize()
    {
        return characters.length * SerialType.getSize(SerialType.CHAR);
    }

    public String getString()
    {
        return new String(characters);
    }
}

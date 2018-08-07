package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.rb2750.lwjgl.serialization.Serialization.writeBytes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialString
{
    public static final byte CONTAINER_TYPE = SerialContainerType.STRING;
    public short nameLength;
    public byte[] name;
    @Getter
    public int size = 1 + 2 + 4 + 4;
    public int count = 0;

    public char[] characters;

    public static SerialString create(String name, String data)
    {
        SerialString serialString = new SerialString();
        serialString.setName(name);
        serialString.count = data.length();
        serialString.characters = data.toCharArray();
        serialString.updateSize();

        return serialString;
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
}

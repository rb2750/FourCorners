package com.rb2750.lwjgl.serialization;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.rb2750.lwjgl.serialization.Serialization.writeBytes;

public class SerialObject
{
    public static final byte CONTAINER_TYPE = SerialContainerType.OBJECT;
    public short nameLength;
    public byte[] name;
    @Getter
    private int size = 1 + 2 + 4 + 4 + 4 + 4;

    private List<SerialField> fields = new ArrayList<>();
    private List<SerialString> strings = new ArrayList<>();
    private List<SerialArray> arrays = new ArrayList<>();

    public SerialObject(String name)
    {
        setName(name);
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

    public void addField(SerialField field)
    {
        fields.add(field);
        size += field.getSize();
    }

    public void addString(SerialString string)
    {
        strings.add(string);
        size += string.getSize();
    }

    public void addArray(SerialArray array)
    {
        arrays.add(array);
        size += array.getSize();
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, fields.size());
        for (SerialField field : fields)
            pointer = field.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, strings.size());
        for (SerialString string : strings)
            pointer = string.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, arrays.size());
        for (SerialArray array : arrays)
            pointer = array.getBytes(dest, pointer);

        return pointer;
    }
}

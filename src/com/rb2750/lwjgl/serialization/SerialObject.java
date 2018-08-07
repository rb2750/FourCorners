package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialObject
{
    public static final byte CONTAINER_TYPE = SerialContainerType.OBJECT;
    public short nameLength;
    public byte[] name;
    @Getter
    private int size = 1 + 2 + 4 + 4 + 4 + 4;

    private static final int SIZE_OFFSET = 1 + 2 + 4;

    public int fieldCount;
    private List<SerialField> fields = new ArrayList<>();

    public int stringCount;
    private List<SerialString> strings = new ArrayList<>();

    public int arrayCount;
    private List<SerialArray> arrays = new ArrayList<>();

    public SerialObject(String name)
    {
        setName(name);
    }

    public String getName()
    {
        return new String(name, 0, nameLength);
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
        fieldCount = fields.size();
    }

    public void addString(SerialString string)
    {
        strings.add(string);
        size += string.getSize();
        stringCount = strings.size();
    }

    public void addArray(SerialArray array)
    {
        arrays.add(array);
        size += array.getSize();
        arrayCount = arrays.size();
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, fieldCount);
        for (SerialField field : fields)
            pointer = field.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, stringCount);
        for (SerialString string : strings)
            pointer = string.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, arrayCount);
        for (SerialArray array : arrays)
            pointer = array.getBytes(dest, pointer);

        return pointer;
    }

    public static SerialObject deserialize(byte[] data, Integer pointer)
    {
        byte containerType = data[pointer++];
        assert(containerType == CONTAINER_TYPE);

        SerialObject result = new SerialObject();
        result.nameLength = readShort(data, pointer);
        pointer += 2;
        result.name = readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = readInt(data, pointer);
        pointer += 4;

        pointer += result.size - SIZE_OFFSET - result.nameLength;

        if (true)
            return result;

        result.fieldCount = readInt(data, pointer);
        pointer += 4;

        // TODO: Fields

        result.stringCount = readInt(data, pointer);
        pointer += 4;

        // TODO: Strings

        result.arrayCount = readInt(data, pointer);
        pointer += 4;

        // TODO: Arrays

        return result;
    }
}

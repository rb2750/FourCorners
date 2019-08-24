package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialObject extends SerialBase
{
    public static final byte CONTAINER_TYPE = SerialContainerType.OBJECT;

    private int fieldCount;
    public Map<String, SerialField> fields = new HashMap<>();

    private int stringCount;
    public Map<String, SerialString> strings = new HashMap<>();

    private int arrayCount;
    public Map<String, SerialArray> arrays = new HashMap<>();

    public SerialObject(String name)
    {
        size += 1 + 4 + 4 + 4;
        setName(name);
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

        result.fieldCount = readInt(data, pointer);
        pointer += 4;

        for (int i = 0; i < result.fieldCount; i++)
        {
            SerialField field = SerialField.deserialize(data, pointer);
            result.fields.put(field.getName(), field);
            pointer += field.getSize();
        }

        result.stringCount = readInt(data, pointer);
        pointer += 4;

        for (int i = 0; i < result.stringCount; i++)
        {
            SerialString string = SerialString.deserialize(data, pointer);
            result.strings.put(string.getName(), string);
            pointer += string.getSize();
        }

        result.arrayCount = readInt(data, pointer);
        pointer += 4;

        for (int i = 0; i < result.arrayCount; i++)
        {
            SerialArray array = SerialArray.deserialize(data, pointer);
            result.arrays.put(array.getName(), array);
            pointer += array.getSize();
        }

        return result;
    }

    public void addField(SerialField field)
    {
        if (fields.containsKey(field.getName()))
            throw new IllegalArgumentException("Field '" + field.getName() + "' already exists in object '" + getName() + "'.");

        fields.put(field.getName(), field);
        size += field.getSize();
        fieldCount = fields.size();
    }

    public SerialField getField(String name)
    {
        SerialField result = fields.get(name);

        if (result == null)
            throw new IllegalArgumentException("Object '" + getName() + "' does not contain field '" + name + "'.");

        return result;
    }

    public void addString(SerialString string)
    {
        if (strings.containsKey(string.getName()))
            throw new IllegalArgumentException("String '" + string.getName() + "' already exists in object '" + getName() + "'.");

        strings.put(string.getName(), string);
        size += string.getSize();
        stringCount = strings.size();
    }

    public SerialString getString(String name)
    {
        SerialString result = strings.get(name);

        if (result == null)
            throw new IllegalArgumentException("Object '" + getName() + "' does not contain string '" + name + "'.");

        return result;
    }

    public void addArray(SerialArray array)
    {
        if (arrays.containsKey(array.getName()))
            throw new IllegalArgumentException("Array '" + array.getName() + "' already exists in object '" + getName() + "'.");

        arrays.put(array.getName(), array);
        size += array.getSize();
        arrayCount = arrays.size();
    }

    public SerialArray getArray(String name)
    {
        SerialArray result = arrays.get(name);

        if (result == null)
            throw new IllegalArgumentException("Object '" + getName() + "' does not contain array '" + name + "'.");

        return result;
    }

    /**
     * Combines this SerialObject with the given SerialObject.
     * @param object SerialObject to combine from.
     * @return This SerialObject.
     */
    public SerialObject combineObject(SerialObject object)
    {
        for (SerialField field : object.fields.values())
        {
            addField(field);
        }

        for (SerialString string : object.strings.values())
        {
            addString(string);
        }

        for (SerialArray array : object.arrays.values())
        {
            addArray(array);
        }

        return this;
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, fieldCount);
        for (SerialField field : fields.values())
            pointer = field.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, stringCount);
        for (SerialString string : strings.values())
            pointer = string.getBytes(dest, pointer);

        pointer = writeBytes(dest, pointer, arrayCount);
        for (SerialArray array : arrays.values())
            pointer = array.getBytes(dest, pointer);

        return pointer;
    }
}

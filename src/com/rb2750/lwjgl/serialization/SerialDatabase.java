package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialDatabase extends SerialBase
{
    public static final byte[] HEADER = "RCLDB".getBytes();
    public static final String HEADER_STRING = new String(HEADER);
    public static final short VERSION = 0x0100;
    public static final byte CONTAINER_TYPE = SerialContainerType.DATABASE;

    private int objectCount;
    public Map<String, SerialObject> objects = new HashMap<>();

    public SerialDatabase(String name)
    {
        size += HEADER.length + 2 + 1 + 4;
        setName(name);
    }

    public static SerialDatabase deserialize(byte[] data)
    {
        int pointer = 0;
        //assert(readString(data, pointer, HEADER.length).getBytes().equals(HEADER));
        pointer += HEADER.length;

        if (readShort(data, pointer) != VERSION)
        {
            System.err.println("[SerialDatabase] Unable to deserialize database: invalid version.");
            return null;
        }

        pointer += 2;

        byte containerType = readByte(data, pointer++);
        assert(containerType == CONTAINER_TYPE);

        SerialDatabase result = new SerialDatabase();

        result.nameLength = readShort(data, pointer);
        pointer += 2;
        result.name = readString(data, pointer, result.nameLength).getBytes();
        pointer += result.nameLength;

        result.size = readInt(data, pointer);
        pointer += 4;

        result.objectCount = readInt(data, pointer);
        pointer += 4;

        for (int i = 0; i < result.objectCount; i++)
        {
            SerialObject object = SerialObject.deserialize(data, pointer);
            result.objects.put(object.getName(), object);
            pointer += object.getSize();
        }

        return result;
    }

    public static SerialDatabase deserializeFromFile(String filePath)
    {
        byte[] buffer = null;

        try
        {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath));
            buffer = new byte[stream.available()];
            stream.read(buffer);
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return deserialize(buffer);
    }

    public void serializeToFile(String path)
    {
        byte[] data = new byte[getSize()];

        getBytes(data, 0);

        try
        {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
            stream.write(data);
            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addObject(SerialObject object)
    {
        if (objects.containsKey(object.getName()))
            throw new IllegalArgumentException("Object " + object.getName() + " already exists in database " + getName() + ".");

        objects.put(object.getName(), object);
        size += object.getSize();
        objectCount = objects.size();
    }

    public SerialObject getObject(String name)
    {
        SerialObject result = objects.get(name);

        if (result == null)
            throw new IllegalArgumentException("Database '" + getName() + "' does not contain object '" + name + "'.");

        return result;
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, HEADER);
        pointer = writeBytes(dest, pointer, VERSION);
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, objectCount);
        for (SerialObject object : objects.values())
            pointer = object.getBytes(dest, pointer);

        return pointer;
    }
}

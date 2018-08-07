package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.rb2750.lwjgl.serialization.Serialization.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialDatabase
{
    public static final byte[] HEADER = "RCLDB".getBytes();
    public static final byte CONTAINER_TYPE = SerialContainerType.DATABASE;
    public short nameLength;
    public byte[] name;
    @Getter
    private int size = HEADER.length + 1 + 2 + 4 + 4;

    private int objectCount;
    public List<SerialObject> objects = new ArrayList<>();

    public SerialDatabase(String name)
    {
        setName(name);
    }

    public static SerialDatabase deserialize(byte[] data)
    {
        int pointer = 0;
        pointer += HEADER.length;

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
            result.objects.add(object);
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

    public void addObject(SerialObject object)
    {
        objects.add(object);
        size += object.getSize();
        objectCount = objects.size();
    }

    public int getBytes(byte[] dest, int pointer)
    {
        pointer = writeBytes(dest, pointer, HEADER);
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        pointer = writeBytes(dest, pointer, size);

        pointer = writeBytes(dest, pointer, objectCount);
        for (SerialObject object : objects)
            pointer = object.getBytes(dest, pointer);

        return pointer;
    }
}

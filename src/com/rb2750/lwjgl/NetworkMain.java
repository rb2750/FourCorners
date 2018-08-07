package com.rb2750.lwjgl;

import com.rb2750.lwjgl.serialization.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class NetworkMain
{
    private static Random random = new Random();

    private static void printBytes(byte[] data)
    {
        for (int i = 0; i < data.length; i++)
        {
            System.out.printf("0x%x ", data[i]);
        }
    }

    private static void saveToFile(String path, byte[] data)
    {
        try
        {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
            stream.write(data);
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void serializationTest()
    {

    }

    public static void deserializationTest()
    {
        SerialDatabase database = SerialDatabase.deserializeFromFile("test.rcl");
        System.out.println(database.getName());
    }

    public static void main(String[] args)
    {
        deserializationTest();
    }
}

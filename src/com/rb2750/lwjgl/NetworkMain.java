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
        int[] data = new int[50000];

        for (int i = 0; i < data.length; i++)
        {
            data[i] = random.nextInt();
        }

        SerialDatabase database = new SerialDatabase("Database");
        SerialArray array = SerialArray.createInteger("RandomNumbers", data);
        SerialField field = SerialField.createInteger("Integer", 8);
        SerialField xPos = SerialField.createShort("xPos", (short)2);
        SerialField yPos = SerialField.createShort("yPos", (short)2);

        SerialObject object = new SerialObject("Entity");
        object.addArray(array);
        object.addArray(SerialArray.createChar("Test String", "Hello World!".toCharArray()));
        object.addField(field);
        object.addField(xPos);
        object.addField(yPos);
        object.addString(SerialString.create("Example Serial String", "A very beautiful string - nothing is better than this string right here!"));

        database.addObject(object);
        database.addObject(new SerialObject("A good object 1"));
        SerialObject c = new SerialObject("A good object 2");
        c.addField(SerialField.createBoolean("A good boolean", true));
        database.addObject(c);
        database.addObject(new SerialObject("A good object 3"));
        database.addObject(new SerialObject("A good object 4"));

        byte[] stream = new byte[database.getSize()];
        database.getBytes(stream, 0);
        saveToFile("test.rcl", stream);
    }

    public static void deserializationTest()
    {
        SerialDatabase database = SerialDatabase.deserializeFromFile("test.rcl");

        System.out.println("Database: " + database.getName());

        for (int i = 0; i < database.objects.size(); i++)
        {
            System.out.println("\t" + database.objects.get(i).getName());
        }
    }

    public static void main(String[] args)
    {
        serializationTest();
        deserializationTest();
    }
}

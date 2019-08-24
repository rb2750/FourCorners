package com.rb2750.lwjgl;

import com.rb2750.lwjgl.networking.client.Client;
import com.rb2750.lwjgl.networking.server.Server;
import com.rb2750.lwjgl.serialization.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class NetworkMain
{
    private static Random random = new Random();

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

        database.serializeToFile("test.rcl");
    }

    public static void deserializationTest()
    {
        SerialDatabase database = SerialDatabase.deserializeFromFile("test.rcl");

        System.out.println("Database v" + SerialDatabase.VERSION + ": " + database.getName());

        for (SerialObject object : database.objects.values())
        {
            System.out.println("\tObject: " + object.getName());

            for (SerialField field : object.fields.values())
            {
                System.out.println("\t\tField Name: " + field.getName());
            }

            for (SerialString string : object.strings.values())
            {
                System.out.println("\t\tString Name: " + string.getName());
                System.out.println("\t\t\tValue: " + string.getString());
            }

            for (SerialArray array : object.arrays.values())
            {
                System.out.println("\t\tArray Name: " + array.getName());
            }
        }

        System.out.println(database.getObject("A good object 2").getField("A good boolean").getBoolean());
        System.out.println(database.getObject("Entity").getString("Example Serial String").getString());
    }

    public static void main(String[] args)
    {
        Server server = new Server(2626);
        server.start();

        InetAddress address = null;

        try
        {
            address = InetAddress.getByName("localhost");
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        int port = 8192;
        //server.send(new byte[] {0, 1, 2}, address, port);

    }
}

package com.rb2750.lwjgl;

import com.rb2750.lwjgl.serialization.Field;

public class NetworkMain
{
    private static void printBytes(byte[] data)
    {
        for (int i = 0; i < data.length; i++)
        {
            System.out.printf("0x%x ", data[i]);
        }
    }

    public static void main(String[] args)
    {
        //byte[] data = new byte[16];

        Field field = Field.createLong("Test", 8);

        byte[] data = new byte[100];
        field.getBytes(data, 0);


//        int pointer = writeBytes(data, 0, true);
//        pointer = writeBytes(data, pointer, false);
//        String name = "Hello!";
//        pointer = writeBytes(data, pointer, name);

        printBytes(data);
    }
}

package com.rb2750.lwjgl.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils
{
    public static String loadAsString(String file)
    {
        StringBuilder result = new StringBuilder();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buffer = "";

            while ((buffer = reader.readLine()) != null)
            {
                result.append(buffer + '\n');
            }

            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result.toString();
    }
}

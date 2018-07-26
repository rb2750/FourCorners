package com.rb2750.lwjgl.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BufferUtils
{
    public static ByteBuffer createByteBuffer(byte[] array)
    {
        ByteBuffer result = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
        result.put(array).flip();

        return result;
    }

    public static FloatBuffer createFloatBuffer(float[] array)
    {
        FloatBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        result.put(array).flip();

        return result;
    }

    public static IntBuffer createIntBuffer(int[] array)
    {
        IntBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
        result.put(array).flip();

        return result;
    }
}

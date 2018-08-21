package com.rb2750.lwjgl.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.stb.STBVorbis.*;

public class AudioMaster
{
    private static long device = -1;
    private static long context = -1;

    private static List<ShortBuffer> buffers = new ArrayList<>();

    public static void init()
    {
        String deviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(deviceName);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        int[] attributes = {0};

        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public static int loadSound(String filePath)
    {
        if (device == -1 || context == -1)
            throw new IllegalStateException("[AudioMaster] init() must be called before loadSound(String).");

        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelsBuffer, sampleRateBuffer);

        return 1;
    }

    public static void cleanUp()
    {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}

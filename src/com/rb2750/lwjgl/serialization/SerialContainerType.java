package com.rb2750.lwjgl.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerialContainerType
{
    public static final byte UNKNOWN = 0;
    public static final byte FIELD = 1;
    public static final byte ARRAY = 2;
    public static final byte STRING = 3;
    public static final byte OBJECT = 4;
    public static final byte DATABASE = 5;
}

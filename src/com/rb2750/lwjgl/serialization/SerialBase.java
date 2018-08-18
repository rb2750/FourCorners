package com.rb2750.lwjgl.serialization;

import lombok.Getter;

public abstract class SerialBase
{
    protected short nameLength;
    protected byte[] name;

    @Getter
    protected int size = 2 + 4;

    public void setName(String name)
    {
        assert(name.length() < Short.MAX_VALUE);

        if (this.name != null)
            size -= this.name.length;

        nameLength = (short)name.length();
        this.name = name.getBytes();
        size += nameLength;
    }

    public String getName()
    {
        return new String(name, 0, nameLength);
    }
}

package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.*;
import org.joml.Vector3f;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Light
{
    @Getter
    @Setter
    private Vector3f colour;
    @Getter
    @Setter
    private float intensity;

    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.addField(SerialField.createFloat("R", colour.x));
        result.addField(SerialField.createFloat("G", colour.y));
        result.addField(SerialField.createFloat("B", colour.z));

        result.addField(SerialField.createFloat("Intensity", intensity));

        return result;
    }

    protected void deserialize(SerialObject object)
    {
        colour = new Vector3f(object.getField("R").getFloat(),
                              object.getField("G").getFloat(),
                              object.getField("B").getFloat());

        intensity = object.getField("Intensity").getFloat();
    }
}

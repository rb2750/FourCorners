package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.*;
import org.joml.Vector3f;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointLight
{
    @Getter
    @Setter
    private Light base;
    @Getter
    @Setter
    private Attenution atten;
    @Getter
    @Setter
    private Vector3f position;
    @Getter
    @Setter
    private float range;

    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.combineObject(base.serialize("BaseLight"));
        result.combineObject(atten.serialize("Atten"));

        result.addField(SerialField.createFloat("X", position.x));
        result.addField(SerialField.createFloat("Y", position.y));
        result.addField(SerialField.createFloat("Z", position.z));
        result.addField(SerialField.createFloat("Range", range));

        return result;
    }

    public static PointLight deserialize(SerialObject object)
    {
        PointLight result = new PointLight();

        result.base = new Light();
        result.base.deserialize(object);
        result.atten = new Attenution();
        result.atten.deserialize(object);

        result.position = new Vector3f(object.getField("X").getFloat(),
                                       object.getField("Y").getFloat(),
                                       object.getField("Z").getFloat());

        result.range = object.getField("Range").getFloat();

        return result;
    }
}

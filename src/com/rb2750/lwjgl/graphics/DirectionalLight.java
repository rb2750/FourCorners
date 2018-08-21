package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectionalLight
{
    @Getter
    @Setter
    private Light base;
    @Getter
    private Vector3f direction;

    public DirectionalLight(Light base, Vector3f direction)
    {
        this.base = base;
        this.direction = direction.normalize();
    }

    public void setDirection(Vector3f direction, boolean normalized)
    {
        if (!normalized)
            this.direction = direction.normalize();
        else
            this.direction = direction;
    }

    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.combineObject(base.serialize("BaseLight"));

        result.addField(SerialField.createFloat("DirX", direction.x));
        result.addField(SerialField.createFloat("DirY", direction.y));
        result.addField(SerialField.createFloat("DirZ", direction.z));

        return result;
    }

    public static DirectionalLight deserialize(SerialObject object)
    {
        DirectionalLight result = new DirectionalLight();

        result.base = new Light();
        result.base.deserialize(object);
        result.direction = new Vector3f(object.getField("DirX").getFloat(),
                                        object.getField("DirY").getFloat(),
                                        object.getField("DirZ").getFloat());

        return result;
    }
}

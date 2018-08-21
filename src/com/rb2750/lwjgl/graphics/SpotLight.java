package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotLight
{
    @Getter
    @Setter
    private PointLight pointLight;
    @Getter
    private Vector3f direction;
    @Getter
    @Setter
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f direction, float cutoff)
    {
        this.pointLight = pointLight;
        this.direction = direction.normalize();
        this.cutoff = cutoff;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction.normalize();
    }

    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.combineObject(pointLight.serialize("PointLight"));

        result.addField(SerialField.createFloat("DirX", direction.x));
        result.addField(SerialField.createFloat("DirY", direction.y));
        result.addField(SerialField.createFloat("DirZ", direction.z));
        result.addField(SerialField.createFloat("Cutoff", cutoff));

        return result;
    }

    public static SpotLight deserialize(SerialObject object)
    {
        SpotLight result = new SpotLight();

        result.pointLight = PointLight.deserialize(object);
        result.direction = new Vector3f(object.getField("DirX").getFloat(),
                                        object.getField("DirY").getFloat(),
                                        object.getField("DirZ").getFloat());
        result.cutoff = object.getField("Cutoff").getFloat();

        return result;
    }
}

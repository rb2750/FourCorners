package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.serialization.SerialField;
import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attenution
{
    @Getter
    @Setter
    private float constant;
    @Getter
    @Setter
    private float linear;
    @Getter
    @Setter
    private float exponent;

    public SerialObject serialize(String name)
    {
        SerialObject result = new SerialObject(name);

        result.addField(SerialField.createFloat("Constant", constant));
        result.addField(SerialField.createFloat("Linear", linear));
        result.addField(SerialField.createFloat("Exponent", exponent));

        return result;
    }

    public void deserialize(SerialObject object)
    {
        constant = object.getField("Constant").getFloat();
        linear = object.getField("Linear").getFloat();
        exponent = object.getField("Exponent").getFloat();
    }
}

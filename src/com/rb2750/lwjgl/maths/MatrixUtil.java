package com.rb2750.lwjgl.maths;

import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.util.BufferUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatrixUtil
{
    public static Matrix4f transformation(Vector3f translation, float angleX, float angleY, float angleZ, Vector3f scale)
    {
        Matrix4f result = new Matrix4f().identity()
                                         .translate(translation)
                                         .rotate((float)Math.toRadians(angleX), 1.0f, 0.0f, 0.0f)
                                         .rotate((float)Math.toRadians(angleY), 0.0f, 1.0f, 0.0f)
                                         .rotate((float)Math.toRadians(angleZ), 0.0f, 0.0f, 1.0f)
                                         .scale(scale);

        return result;
    }

    public static Matrix4f view(Camera camera)
    {
        Vector3f cameraPos = camera.getPosition();
        Vector3f negCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Matrix4f result = new Matrix4f().identity()
                                        .rotate((float)Math.toRadians(camera.getPitch()), 1.0f, 0.0f, 0.0f)
                                        .rotate((float)Math.toRadians(camera.getYaw()), 0.0f, 1.0f, 0.0f)
                                        .rotate((float)Math.toRadians(camera.getRoll()), 0.0f, 0.0f, 1.0f)
                                        .translate(negCameraPos);

        return result;
    }

    public static FloatBuffer toFloatBuffer(Matrix4f matrix)
    {
        float[] elements = new float[16];

        matrix.get(elements);

        return BufferUtils.createFloatBuffer(elements);
    }
}

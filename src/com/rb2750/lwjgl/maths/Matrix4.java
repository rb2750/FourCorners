package com.rb2750.lwjgl.maths;

import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.util.BufferUtils;
import lombok.NoArgsConstructor;

import java.nio.FloatBuffer;

@NoArgsConstructor
public class Matrix4
{
    public final int SIZE = 4 * 4;
    public float[] elements = new float[SIZE];

    public static Matrix4 identity()
    {
        Matrix4 result = new Matrix4();

        result.elements[0 + 0 * 4] = 1.0f;
        result.elements[1 + 1 * 4] = 1.0f;
        result.elements[2 + 2 * 4] = 1.0f;
        result.elements[3 + 3 * 4] = 1.0f;

        return result;
    }

    public static Matrix4 orthographic(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix4 result = identity();

        result.elements[0 + 0 * 4] = 2.0f / (right - left);
        result.elements[1 + 1 * 4] = 2.0f / (top - bottom);
        result.elements[2 + 2 * 4] = -2.0f / (far - near);

        result.elements[0 + 3 * 4] = -((right + left) / (right - left));
        result.elements[1 + 3 * 4] = -((top + bottom) / (top - bottom));
        result.elements[2 + 3 * 4] = -((far + near) / (far - near));

        return result;
    }

    public static Matrix4 projection(float width, float height, float near, float far, float fov)
    {
        Matrix4 result = new Matrix4();

        float aspectRatio = width / height;
        float yScale = (float)(1.0f / Math.tan(Math.toRadians(fov / 2.0f))) * aspectRatio;
        float xScale = yScale / aspectRatio;
        float frustumLength = far - near;

        result.elements[0 + 0 * 4] = xScale;
        result.elements[1 + 1 * 4] = yScale;
        result.elements[2 + 2 * 4] = -((far + near) / frustumLength);
        result.elements[2 + 3 * 4] = -1.0f;
        result.elements[3 + 2 * 4] = -((2 * near * far) / frustumLength);
        result.elements[3 + 3 * 4] = 0;

        return result;
    }

    public static Matrix4 translate(Vector3 vector)
    {
        Matrix4 result = identity();

        result.elements[0 + 3 * 4] = (float)vector.getX();
        result.elements[1 + 3 * 4] = (float)vector.getY();
        result.elements[2 + 3 * 4] = (float)vector.getZ();

        return result;
    }

    public static Matrix4 rotate(float angleX, float angleY, float angleZ)
    {
        Matrix4 resultX = identity();
        Matrix4 resultY = identity();
        Matrix4 resultZ = identity();

        float rX = (float)Math.toRadians(angleX);
        float rY = (float)Math.toRadians(angleY);
        float rZ = (float)Math.toRadians(angleZ);

        float cosX = (float)Math.cos(rX);
        float cosY = (float)Math.cos(rY);
        float cosZ = (float)Math.cos(rZ);

        float sinX = (float)Math.sin(rX);
        float sinY = (float)Math.sin(rY);
        float sinZ = (float)Math.sin(rZ);

        resultX.elements[1 + 1 * 4] = cosX;
        resultX.elements[1 + 2 * 4] = sinX;

        resultX.elements[2 + 1 * 4] = -sinX;
        resultX.elements[2 + 2 * 4] = cosX;

        resultY.elements[0 + 0 * 4] = cosY;
        resultY.elements[0 + 2 * 4] = -sinY;

        resultY.elements[2 + 0 * 4] = sinY;
        resultY.elements[2 + 2 * 4] = cosY;

        resultZ.elements[0 + 0 * 4] = cosZ;
        resultZ.elements[0 + 1 * 4] = -sinZ;

        resultZ.elements[1 + 0 * 4] = sinZ;
        resultZ.elements[1 + 1 * 4] = cosZ;

        Matrix4 resultPart = resultZ.multiply(resultX);
        Matrix4 result = resultPart.multiply(resultY);

        return result;
    }

    public static Matrix4 scale(Vector3 scale)
    {
        Matrix4 result = identity();

        result.elements[0 + 0 * 4] = (float)scale.getX();
        result.elements[1 + 1 * 4] = (float)scale.getY();
        result.elements[2 + 2 * 4] = (float)scale.getZ();

        return result;
    }

    public static Matrix4 transformation(Vector3 translation, float angleX, float angleY, float angleZ, Vector3 scale)
    {
        Matrix4 result = identity();

        result = result.multiply(translate(translation));
        result = result.multiply(rotate(angleX, angleY, angleZ));
        result = result.multiply(scale(scale));

        return result;
    }

    public static Matrix4 view(Camera camera)
    {
        Matrix4 result = identity();

        result = result.multiply(rotate(camera.getPitch(), camera.getYaw(), camera.getRoll()));
        Vector3 cameraPos = camera.getPosition();
        Vector3 negCameraPos = new Vector3(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
        result = result.multiply(translate(negCameraPos));

        return result;
    }

    public Matrix4 multiply(Matrix4 matrix)
    {
        Matrix4 result = new Matrix4();

        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                float sum = 0.0f;

                for (int e = 0; e < 4; e++)
                {
                    sum += this.elements[x + e * 4] * matrix.elements[e + y * 4];
                }

                result.elements[x + y * 4] = sum;
            }
        }

        return result;
    }

    public FloatBuffer toFloatBuffer()
    {
        return BufferUtils.createFloatBuffer(elements);
    }
}

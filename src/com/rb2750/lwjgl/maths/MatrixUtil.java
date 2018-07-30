package com.rb2750.lwjgl.maths;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.util.BufferUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.*;

import java.lang.Math;
import java.nio.FloatBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatrixUtil {
    public static Matrix4f transformation(Vector2f translation, float angleX, float angleY, float angleZ, Vector2f scale) {
        Matrix4f result = new Matrix4f().identity()
                .translate(translation.x, translation.y, 0.0f)
                .rotateAround(new Quaternionf((float) Math.toRadians(angleX), (float) Math.toRadians(angleY), (float) Math.toRadians(angleZ), 1), translation.x + scale.x / 2f, translation.y + scale.y / 2f, 0)
                .scale(scale.x, scale.y, 1f);

        return result;
    }

    public static Matrix4f transformation(Vector3f translation, float angleX, float angleY, float angleZ, Vector3f scale) {
        Matrix4f result = new Matrix4f().identity()
                .translate(translation)
                //.rotateAround(quatFromAngles((float) Math.toRadians(angleX), (float) Math.toRadians(angleY), (float) Math.toRadians(angleZ)), translation.x + scale.x / 2f, translation.y + scale.y / 2f, translation.z + scale.z / 2f)
                .rotateAround(quatFromAngles((float) Math.toRadians(angleX), (float) Math.toRadians(angleY), (float) Math.toRadians(angleZ)), 50.0f, 50.0f, 50.0f)
                .scale(scale);

        return result;
    }

    /**
     * Takes rotational angles and turns them into a quaternion.
     * @param angleX Pitch in radians.
     * @param angleY Yaw in radians.
     * @param angleZ Roll in radians.
     * @return Quaternion with angles
     */
    public static Quaternionf quatFromAngles(float angleX, float angleY, float angleZ)
    {
        Quaternionf result = new Quaternionf();

        float angle;
        float sinY, sinZ, sinX, cosY, cosZ, cosX;

        angle = angleZ * 0.5f;
        sinZ = (float)Math.sin(angle);
        cosZ = (float)Math.cos(angle);

        angle = angleY * 0.5f;
        sinY = (float)Math.sin(angle);
        cosY = (float)Math.cos(angle);

        angle = angleX * 0.5f;
        sinX = (float)Math.sin(angle);
        cosX = (float)Math.cos(angle);

        float cosYXcosZ = cosY * cosZ;
        float sinYXsinZ = sinY * sinZ;
        float cosYXsinZ = cosY * sinZ;
        float sinYXcosZ = sinY * cosZ;

        result.w = (cosYXcosZ * cosX - sinYXsinZ * sinX);
        result.x = (cosYXcosZ * sinX + sinYXsinZ * cosX);
        result.y = (sinYXcosZ * cosX + cosYXsinZ * sinX);
        result.z = (cosYXsinZ * cosX - sinYXcosZ * sinX);

        result.normalize();

        return result;
    }

    public static Matrix4f view(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f negCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Matrix4f result = new Matrix4f().identity()
                .rotate((float) Math.toRadians(camera.getPitch()), 1.0f, 0.0f, 0.0f)
                .rotate((float) Math.toRadians(camera.getYaw()), 0.0f, 1.0f, 0.0f)
                .rotate((float) Math.toRadians(camera.getRoll()), 0.0f, 0.0f, 1.0f)
                .translate(negCameraPos);

        return result;
    }

    public static FloatBuffer toFloatBuffer(Matrix4f matrix) {
        float[] elements = new float[16];

        matrix.get(elements);

        return BufferUtils.createFloatBuffer(elements);
    }
}

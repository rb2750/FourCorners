package com.rb2750.lwjgl.maths;

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
                .rotateAround(new Quaternionf((float) Math.toRadians(angleX), (float) Math.toRadians(angleY), (float) Math.toRadians(angleZ), 1), translation.x + scale.x / 2f, translation.y + scale.y / 2f, translation.z + scale.z / 2f)
                .scale(scale);

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

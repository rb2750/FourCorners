package com.rb2750.lwjgl.maths;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.*;
//import org.ode4j.math.*;

import java.lang.Math;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Conversions
{
//    public static Quaternionf odeQuatToQuatF(DQuaternionC quaternion)
//    {
//        return new Quaternionf((float)quaternion.get1(), (float)quaternion.get2(), (float)quaternion.get3(), (float)quaternion.get0());
//    }
//
//    public static Vector3f odeVec3ToVec3f(DVector3C vector)
//    {
//        return new Vector3f((float)vector.get0(), (float)vector.get1(), (float)vector.get2());
//    }

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

//    public static DMatrix3 mat4fToOdeMat3f(Matrix4f matrix)
//    {
//        return new DMatrix3(matrix.m00(), matrix.m01(), matrix.m02(), matrix.m10(), matrix.m11(), matrix.m12(), matrix.m20(), matrix.m21(), matrix.m22());
//    }
}

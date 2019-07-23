package com.sinomaps.geobookar.vr;

import android.util.Log;

import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.Vec2F;
import com.vuforia.Vec3F;
import com.vuforia.Vec4F;
import com.vuforia.VideoBackgroundConfig;

/* renamed from: com.sinomaps.geobookar.vr.SampleMath */
public class SampleMath {
    private static final String LOGTAG = "SampleMath";
    private static Vec3F mIntersection = new Vec3F();
    private static Vec3F mLineEnd = new Vec3F();
    private static Vec3F mLineStart = new Vec3F();
    private static float[] temp = new float[16];

    public static Vec2F Vec2FSub(Vec2F v1, Vec2F v2) {
        temp[0] = v1.getData()[0] - v2.getData()[0];
        temp[1] = v1.getData()[1] - v2.getData()[1];
        return new Vec2F(temp[0], temp[1]);
    }

    public static float Vec2FDist(Vec2F v1, Vec2F v2) {
        float dx = v1.getData()[0] - v2.getData()[0];
        float dy = v1.getData()[1] - v2.getData()[1];
        return (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
    }

    public static Vec3F Vec3FAdd(Vec3F v1, Vec3F v2) {
        temp[0] = v1.getData()[0] + v2.getData()[0];
        temp[1] = v1.getData()[1] + v2.getData()[1];
        temp[2] = v1.getData()[2] + v2.getData()[2];
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec3F Vec3FSub(Vec3F v1, Vec3F v2) {
        temp[0] = v1.getData()[0] - v2.getData()[0];
        temp[1] = v1.getData()[1] - v2.getData()[1];
        temp[2] = v1.getData()[2] - v2.getData()[2];
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec3F Vec3FScale(Vec3F v, float s) {
        temp[0] = v.getData()[0] * s;
        temp[1] = v.getData()[1] * s;
        temp[2] = v.getData()[2] * s;
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static float Vec3FDot(Vec3F v1, Vec3F v2) {
        return (v1.getData()[0] * v2.getData()[0]) + (v1.getData()[1] * v2.getData()[1]) + (v1.getData()[2] * v2.getData()[2]);
    }

    public static Vec3F Vec3FCross(Vec3F v1, Vec3F v2) {
        temp[0] = (v1.getData()[1] * v2.getData()[2]) - (v1.getData()[2] * v2.getData()[1]);
        temp[1] = (v1.getData()[2] * v2.getData()[0]) - (v1.getData()[0] * v2.getData()[2]);
        temp[2] = (v1.getData()[0] * v2.getData()[1]) - (v1.getData()[1] * v2.getData()[0]);
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec3F Vec3FNormalize(Vec3F v) {
        float length = (float) Math.sqrt((double) ((v.getData()[0] * v.getData()[0]) + (v.getData()[1] * v.getData()[1]) + (v.getData()[2] * v.getData()[2])));
        if (length != 0.0f) {
            length = 1.0f / length;
        }
        temp[0] = v.getData()[0] * length;
        temp[1] = v.getData()[1] * length;
        temp[2] = v.getData()[2] * length;
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec3F Vec3FTransform(Vec3F v, Matrix44F m) {
        float lambda = (m.getData()[12] * v.getData()[0]) + (m.getData()[13] * v.getData()[1]) + (m.getData()[14] * v.getData()[2]) + m.getData()[15];
        temp[0] = (m.getData()[0] * v.getData()[0]) + (m.getData()[1] * v.getData()[1]) + (m.getData()[2] * v.getData()[2]) + m.getData()[3];
        temp[1] = (m.getData()[4] * v.getData()[0]) + (m.getData()[5] * v.getData()[1]) + (m.getData()[6] * v.getData()[2]) + m.getData()[7];
        temp[2] = (m.getData()[8] * v.getData()[0]) + (m.getData()[9] * v.getData()[1]) + (m.getData()[10] * v.getData()[2]) + m.getData()[11];
        float[] fArr = temp;
        fArr[0] = fArr[0] / lambda;
        float[] fArr2 = temp;
        fArr2[1] = fArr2[1] / lambda;
        float[] fArr3 = temp;
        fArr3[2] = fArr3[2] / lambda;
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec3F Vec3FTransformNormal(Vec3F v, Matrix44F m) {
        temp[0] = (m.getData()[0] * v.getData()[0]) + (m.getData()[1] * v.getData()[1]) + (m.getData()[2] * v.getData()[2]);
        temp[1] = (m.getData()[4] * v.getData()[0]) + (m.getData()[5] * v.getData()[1]) + (m.getData()[6] * v.getData()[2]);
        temp[2] = (m.getData()[8] * v.getData()[0]) + (m.getData()[9] * v.getData()[1]) + (m.getData()[10] * v.getData()[2]);
        return new Vec3F(temp[0], temp[1], temp[2]);
    }

    public static Vec4F Vec4FTransform(Vec4F v, Matrix44F m) {
        temp[0] = (m.getData()[0] * v.getData()[0]) + (m.getData()[1] * v.getData()[1]) + (m.getData()[2] * v.getData()[2]) + (m.getData()[3] * v.getData()[3]);
        temp[1] = (m.getData()[4] * v.getData()[0]) + (m.getData()[5] * v.getData()[1]) + (m.getData()[6] * v.getData()[2]) + (m.getData()[7] * v.getData()[3]);
        temp[2] = (m.getData()[8] * v.getData()[0]) + (m.getData()[9] * v.getData()[1]) + (m.getData()[10] * v.getData()[2]) + (m.getData()[11] * v.getData()[3]);
        temp[3] = (m.getData()[12] * v.getData()[0]) + (m.getData()[13] * v.getData()[1]) + (m.getData()[14] * v.getData()[2]) + (m.getData()[15] * v.getData()[3]);
        return new Vec4F(temp[0], temp[1], temp[2], temp[3]);
    }

    public static Vec4F Vec4FDiv(Vec4F v, float s) {
        temp[0] = v.getData()[0] / s;
        temp[1] = v.getData()[1] / s;
        temp[2] = v.getData()[2] / s;
        temp[3] = v.getData()[3] / s;
        return new Vec4F(temp[0], temp[1], temp[2], temp[3]);
    }

    public static Matrix44F Matrix44FIdentity() {
        Matrix44F r = new Matrix44F();
        for (int i = 0; i < 16; i++) {
            temp[i] = 0.0f;
        }
        temp[0] = 1.0f;
        temp[5] = 1.0f;
        temp[10] = 1.0f;
        temp[15] = 1.0f;
        r.setData(temp);
        return r;
    }

    public static Matrix44F Matrix44FTranspose(Matrix44F m) {
        Matrix44F r = new Matrix44F();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                temp[(i * 4) + j] = m.getData()[(j * 4) + i];
            }
        }
        r.setData(temp);
        return r;
    }

    public static float Matrix44FDeterminate(Matrix44F m) {
        return (((((((((((((((((((((((((m.getData()[12] * m.getData()[9]) * m.getData()[6]) * m.getData()[3]) - (((m.getData()[8] * m.getData()[13]) * m.getData()[6]) * m.getData()[3])) - (((m.getData()[12] * m.getData()[5]) * m.getData()[10]) * m.getData()[3])) + (((m.getData()[4] * m.getData()[13]) * m.getData()[10]) * m.getData()[3])) + (((m.getData()[8] * m.getData()[5]) * m.getData()[14]) * m.getData()[3])) - (((m.getData()[4] * m.getData()[9]) * m.getData()[14]) * m.getData()[3])) - (((m.getData()[12] * m.getData()[9]) * m.getData()[2]) * m.getData()[7])) + (((m.getData()[8] * m.getData()[13]) * m.getData()[2]) * m.getData()[7])) + (((m.getData()[12] * m.getData()[1]) * m.getData()[10]) * m.getData()[7])) - (((m.getData()[0] * m.getData()[13]) * m.getData()[10]) * m.getData()[7])) - (((m.getData()[8] * m.getData()[1]) * m.getData()[14]) * m.getData()[7])) + (((m.getData()[0] * m.getData()[9]) * m.getData()[14]) * m.getData()[7])) + (((m.getData()[12] * m.getData()[5]) * m.getData()[2]) * m.getData()[11])) - (((m.getData()[4] * m.getData()[13]) * m.getData()[2]) * m.getData()[11])) - (((m.getData()[12] * m.getData()[1]) * m.getData()[6]) * m.getData()[11])) + (((m.getData()[0] * m.getData()[13]) * m.getData()[6]) * m.getData()[11])) + (((m.getData()[4] * m.getData()[1]) * m.getData()[14]) * m.getData()[11])) - (((m.getData()[0] * m.getData()[5]) * m.getData()[14]) * m.getData()[11])) - (((m.getData()[8] * m.getData()[5]) * m.getData()[2]) * m.getData()[15])) + (((m.getData()[4] * m.getData()[9]) * m.getData()[2]) * m.getData()[15])) + (((m.getData()[8] * m.getData()[1]) * m.getData()[6]) * m.getData()[15])) - (((m.getData()[0] * m.getData()[9]) * m.getData()[6]) * m.getData()[15])) - (((m.getData()[4] * m.getData()[1]) * m.getData()[10]) * m.getData()[15])) + (m.getData()[0] * m.getData()[5] * m.getData()[10] * m.getData()[15]);
    }

    public static Matrix44F Matrix44FInverse(Matrix44F m) {
        Matrix44F r = new Matrix44F();
        float det = 1.0f / Matrix44FDeterminate(m);
        temp[0] = ((((((m.getData()[6] * m.getData()[11]) * m.getData()[13]) - ((m.getData()[7] * m.getData()[10]) * m.getData()[13])) + ((m.getData()[7] * m.getData()[9]) * m.getData()[14])) - ((m.getData()[5] * m.getData()[11]) * m.getData()[14])) - ((m.getData()[6] * m.getData()[9]) * m.getData()[15])) + (m.getData()[5] * m.getData()[10] * m.getData()[15]);
        temp[4] = ((((((m.getData()[3] * m.getData()[10]) * m.getData()[13]) - ((m.getData()[2] * m.getData()[11]) * m.getData()[13])) - ((m.getData()[3] * m.getData()[9]) * m.getData()[14])) + ((m.getData()[1] * m.getData()[11]) * m.getData()[14])) + ((m.getData()[2] * m.getData()[9]) * m.getData()[15])) - ((m.getData()[1] * m.getData()[10]) * m.getData()[15]);
        temp[8] = ((((((m.getData()[2] * m.getData()[7]) * m.getData()[13]) - ((m.getData()[3] * m.getData()[6]) * m.getData()[13])) + ((m.getData()[3] * m.getData()[5]) * m.getData()[14])) - ((m.getData()[1] * m.getData()[7]) * m.getData()[14])) - ((m.getData()[2] * m.getData()[5]) * m.getData()[15])) + (m.getData()[1] * m.getData()[6] * m.getData()[15]);
        temp[12] = ((((((m.getData()[3] * m.getData()[6]) * m.getData()[9]) - ((m.getData()[2] * m.getData()[7]) * m.getData()[9])) - ((m.getData()[3] * m.getData()[5]) * m.getData()[10])) + ((m.getData()[1] * m.getData()[7]) * m.getData()[10])) + ((m.getData()[2] * m.getData()[5]) * m.getData()[11])) - ((m.getData()[1] * m.getData()[6]) * m.getData()[11]);
        temp[1] = ((((((m.getData()[7] * m.getData()[10]) * m.getData()[12]) - ((m.getData()[6] * m.getData()[11]) * m.getData()[12])) - ((m.getData()[7] * m.getData()[8]) * m.getData()[14])) + ((m.getData()[4] * m.getData()[11]) * m.getData()[14])) + ((m.getData()[6] * m.getData()[8]) * m.getData()[15])) - ((m.getData()[4] * m.getData()[10]) * m.getData()[15]);
        temp[5] = ((((((m.getData()[2] * m.getData()[11]) * m.getData()[12]) - ((m.getData()[3] * m.getData()[10]) * m.getData()[12])) + ((m.getData()[3] * m.getData()[8]) * m.getData()[14])) - ((m.getData()[0] * m.getData()[11]) * m.getData()[14])) - ((m.getData()[2] * m.getData()[8]) * m.getData()[15])) + (m.getData()[0] * m.getData()[10] * m.getData()[15]);
        temp[9] = ((((((m.getData()[3] * m.getData()[6]) * m.getData()[12]) - ((m.getData()[2] * m.getData()[7]) * m.getData()[12])) - ((m.getData()[3] * m.getData()[4]) * m.getData()[14])) + ((m.getData()[0] * m.getData()[7]) * m.getData()[14])) + ((m.getData()[2] * m.getData()[4]) * m.getData()[15])) - ((m.getData()[0] * m.getData()[6]) * m.getData()[15]);
        temp[13] = ((((((m.getData()[2] * m.getData()[7]) * m.getData()[8]) - ((m.getData()[3] * m.getData()[6]) * m.getData()[8])) + ((m.getData()[3] * m.getData()[4]) * m.getData()[10])) - ((m.getData()[0] * m.getData()[7]) * m.getData()[10])) - ((m.getData()[2] * m.getData()[4]) * m.getData()[11])) + (m.getData()[0] * m.getData()[6] * m.getData()[11]);
        temp[2] = ((((((m.getData()[5] * m.getData()[11]) * m.getData()[12]) - ((m.getData()[7] * m.getData()[9]) * m.getData()[12])) + ((m.getData()[7] * m.getData()[8]) * m.getData()[13])) - ((m.getData()[4] * m.getData()[11]) * m.getData()[13])) - ((m.getData()[5] * m.getData()[8]) * m.getData()[15])) + (m.getData()[4] * m.getData()[9] * m.getData()[15]);
        temp[6] = ((((((m.getData()[3] * m.getData()[9]) * m.getData()[12]) - ((m.getData()[1] * m.getData()[11]) * m.getData()[12])) - ((m.getData()[3] * m.getData()[8]) * m.getData()[13])) + ((m.getData()[0] * m.getData()[11]) * m.getData()[13])) + ((m.getData()[1] * m.getData()[8]) * m.getData()[15])) - ((m.getData()[0] * m.getData()[9]) * m.getData()[15]);
        temp[10] = ((((((m.getData()[1] * m.getData()[7]) * m.getData()[12]) - ((m.getData()[3] * m.getData()[5]) * m.getData()[12])) + ((m.getData()[3] * m.getData()[4]) * m.getData()[13])) - ((m.getData()[0] * m.getData()[7]) * m.getData()[13])) - ((m.getData()[1] * m.getData()[4]) * m.getData()[15])) + (m.getData()[0] * m.getData()[5] * m.getData()[15]);
        temp[14] = ((((((m.getData()[3] * m.getData()[5]) * m.getData()[8]) - ((m.getData()[1] * m.getData()[7]) * m.getData()[8])) - ((m.getData()[3] * m.getData()[4]) * m.getData()[9])) + ((m.getData()[0] * m.getData()[7]) * m.getData()[9])) + ((m.getData()[1] * m.getData()[4]) * m.getData()[11])) - ((m.getData()[0] * m.getData()[5]) * m.getData()[11]);
        temp[3] = ((((((m.getData()[6] * m.getData()[9]) * m.getData()[12]) - ((m.getData()[5] * m.getData()[10]) * m.getData()[12])) - ((m.getData()[6] * m.getData()[8]) * m.getData()[13])) + ((m.getData()[4] * m.getData()[10]) * m.getData()[13])) + ((m.getData()[5] * m.getData()[8]) * m.getData()[14])) - ((m.getData()[4] * m.getData()[9]) * m.getData()[14]);
        temp[7] = ((((((m.getData()[1] * m.getData()[10]) * m.getData()[12]) - ((m.getData()[2] * m.getData()[9]) * m.getData()[12])) + ((m.getData()[2] * m.getData()[8]) * m.getData()[13])) - ((m.getData()[0] * m.getData()[10]) * m.getData()[13])) - ((m.getData()[1] * m.getData()[8]) * m.getData()[14])) + (m.getData()[0] * m.getData()[9] * m.getData()[14]);
        temp[11] = ((((((m.getData()[2] * m.getData()[5]) * m.getData()[12]) - ((m.getData()[1] * m.getData()[6]) * m.getData()[12])) - ((m.getData()[2] * m.getData()[4]) * m.getData()[13])) + ((m.getData()[0] * m.getData()[6]) * m.getData()[13])) + ((m.getData()[1] * m.getData()[4]) * m.getData()[14])) - ((m.getData()[0] * m.getData()[5]) * m.getData()[14]);
        temp[15] = ((((((m.getData()[1] * m.getData()[6]) * m.getData()[8]) - ((m.getData()[2] * m.getData()[5]) * m.getData()[8])) + ((m.getData()[2] * m.getData()[4]) * m.getData()[9])) - ((m.getData()[0] * m.getData()[6]) * m.getData()[9])) - ((m.getData()[1] * m.getData()[4]) * m.getData()[10])) + (m.getData()[0] * m.getData()[5] * m.getData()[10]);
        for (int i = 0; i < 16; i++) {
            float[] fArr = temp;
            fArr[i] = fArr[i] * det;
        }
        r.setData(temp);
        return r;
    }

    public static Vec3F linePlaneIntersection(Vec3F lineStart, Vec3F lineEnd, Vec3F pointOnPlane, Vec3F planeNormal) {
        Vec3F lineDir = Vec3FNormalize(Vec3FSub(lineEnd, lineStart));
        float n = Vec3FDot(planeNormal, Vec3FSub(pointOnPlane, lineStart));
        float d = Vec3FDot(planeNormal, lineDir);
        if (((double) Math.abs(d)) < 1.0E-5d) {
            return null;
        }
        return Vec3FAdd(lineStart, Vec3FScale(lineDir, n / d));
    }

    private static void projectScreenPointToPlane(Matrix44F inverseProjMatrix, Matrix44F modelViewMatrix, float screenWidth, float screenHeight, Vec2F point, Vec3F planeCenter, Vec3F planeNormal) {
        VideoBackgroundConfig config = Renderer.getInstance().getVideoBackgroundConfig();
        float halfViewportWidth = ((float) config.getSize().getData()[0]) / 2.0f;
        float halfViewportHeight = ((float) config.getSize().getData()[1]) / 2.0f;
        float x = (point.getData()[0] - (screenWidth / 2.0f)) / halfViewportWidth;
        float y = ((point.getData()[1] - (screenHeight / 2.0f)) / halfViewportHeight) * -1.0f;
        Vec4F ndcNear = new Vec4F(x, y, -1.0f, 1.0f);
        Vec4F ndcFar = new Vec4F(x, y, 1.0f, 1.0f);
        Vec4F pointOnNearPlane = Vec4FTransform(ndcNear, inverseProjMatrix);
        Vec4F pointOnFarPlane = Vec4FTransform(ndcFar, inverseProjMatrix);
        Vec4F pointOnNearPlane2 = Vec4FDiv(pointOnNearPlane, pointOnNearPlane.getData()[3]);
        Vec4F pointOnFarPlane2 = Vec4FDiv(pointOnFarPlane, pointOnFarPlane.getData()[3]);
        Matrix44F inverseModelViewMatrix = Matrix44FInverse(modelViewMatrix);
        Vec4F nearWorld = Vec4FTransform(pointOnNearPlane2, inverseModelViewMatrix);
        Vec4F farWorld = Vec4FTransform(pointOnFarPlane2, inverseModelViewMatrix);
        mLineStart = new Vec3F(nearWorld.getData()[0], nearWorld.getData()[1], nearWorld.getData()[2]);
        mLineEnd = new Vec3F(farWorld.getData()[0], farWorld.getData()[1], farWorld.getData()[2]);
        mIntersection = linePlaneIntersection(mLineStart, mLineEnd, planeCenter, planeNormal);
        if (mIntersection == null) {
            Log.e(LOGTAG, "No intersection with the plane");
        }
    }

    public static Vec3F getPointToPlaneIntersection(Matrix44F inverseProjMatrix, Matrix44F modelViewMatrix, float screenWidth, float screenHeight, Vec2F point, Vec3F planeCenter, Vec3F planeNormal) {
        projectScreenPointToPlane(inverseProjMatrix, modelViewMatrix, screenWidth, screenHeight, point, planeCenter, planeNormal);
        return mIntersection;
    }

    public static Vec3F getPointToPlaneLineStart(Matrix44F inverseProjMatrix, Matrix44F modelViewMatrix, float screenWidth, float screenHeight, Vec2F point, Vec3F planeCenter, Vec3F planeNormal) {
        projectScreenPointToPlane(inverseProjMatrix, modelViewMatrix, screenWidth, screenHeight, point, planeCenter, planeNormal);
        return mLineStart;
    }

    public static Vec3F getPointToPlaneLineEnd(Matrix44F inverseProjMatrix, Matrix44F modelViewMatrix, float screenWidth, float screenHeight, Vec2F point, Vec3F planeCenter, Vec3F planeNormal) {
        projectScreenPointToPlane(inverseProjMatrix, modelViewMatrix, screenWidth, screenHeight, point, planeCenter, planeNormal);
        return mLineEnd;
    }
}

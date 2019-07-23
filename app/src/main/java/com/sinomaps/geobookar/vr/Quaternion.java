package com.sinomaps.geobookar.vr;

import java.util.Vector;

/* renamed from: com.sinomaps.geobookar.vr.Quaternion */
public class Quaternion {
    private final float DEG2RAD;
    private final float RAD2DEG;
    private final float ROUNDING_TOLERANCE;

    /* renamed from: pi */
    private final float f100pi;

    /* renamed from: w */
    public float f101w;

    /* renamed from: x */
    public float f102x;

    /* renamed from: y */
    public float f103y;

    /* renamed from: z */
    public float f104z;

    public Quaternion() {
        this.f100pi = 3.141592f;
        this.DEG2RAD = 0.017453289f;
        this.RAD2DEG = 57.29579f;
        this.ROUNDING_TOLERANCE = 1.0E-5f;
        this.f101w = 1.0f;
        this.f102x = 0.0f;
        this.f103y = 0.0f;
        this.f104z = 0.0f;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.f100pi = 3.141592f;
        this.DEG2RAD = 0.017453289f;
        this.RAD2DEG = 57.29579f;
        this.ROUNDING_TOLERANCE = 1.0E-5f;
        this.f101w = w;
        this.f102x = x;
        this.f103y = y;
        this.f104z = z;
    }

    public Quaternion(Vector<Float> vector, float angle) {
        this.f100pi = 3.141592f;
        this.DEG2RAD = 0.017453289f;
        this.RAD2DEG = 57.29579f;
        this.ROUNDING_TOLERANCE = 1.0E-5f;
        buildFromAxisAngle(vector, angle);
    }

    public Quaternion setEqualTo(Quaternion rhs) {
        this.f101w = rhs.f101w;
        this.f102x = rhs.f102x;
        this.f103y = rhs.f103y;
        this.f104z = rhs.f104z;
        return this;
    }

    public Quaternion multiplyWith(Quaternion rhs) {
        float qw = this.f101w;
        float qx = this.f102x;
        float qy = this.f103y;
        float qz = this.f104z;
        return new Quaternion((((rhs.f101w * qw) - (rhs.f102x * qx)) - (rhs.f103y * qy)) - (rhs.f104z * qz), (((rhs.f102x * qw) + (rhs.f101w * qx)) + (rhs.f104z * qy)) - (rhs.f103y * qz), ((rhs.f103y * qw) - (rhs.f104z * qx)) + (rhs.f101w * qy) + (rhs.f102x * qz), (((rhs.f104z * qw) + (rhs.f103y * qx)) - (rhs.f102x * qy)) + (rhs.f101w * qz));
    }

    public Quaternion multiplyThisWith(Quaternion rhs) {
        float ww = rhs.f101w;
        float xx = rhs.f102x;
        float yy = rhs.f103y;
        float zz = rhs.f104z;
        rhs.f101w = (((this.f101w * ww) - (this.f102x * xx)) - (this.f103y * yy)) - (this.f104z * zz);
        rhs.f102x = (((this.f101w * xx) + (this.f102x * ww)) + (this.f103y * zz)) - (this.f104z * yy);
        rhs.f103y = ((this.f101w * yy) - (this.f102x * zz)) + (this.f103y * ww) + (this.f104z * xx);
        rhs.f104z = (((this.f101w * zz) + (this.f102x * yy)) - (this.f103y * xx)) + (this.f104z * ww);
        return rhs;
    }

    public void normalize() {
        float magnitude = (float) Math.sqrt((double) ((this.f101w * this.f101w) + (this.f102x * this.f102x) + (this.f103y * this.f103y) + (this.f104z * this.f104z)));
        if (Math.abs(magnitude - 1.0f) > 1.0E-5f && Math.abs(magnitude) > 1.0E-5f) {
            this.f101w /= magnitude;
            this.f102x /= magnitude;
            this.f103y /= magnitude;
            this.f104z /= magnitude;
        }
    }

    public Quaternion conjugate() {
        return new Quaternion(-this.f102x, -this.f103y, -this.f104z, this.f101w);
    }

    public void buildFromAxisAngle(Vector<Float> axis, float angle) {
        float angle2 = (float) (((double) angle) * 0.5d);
        float sinAngle = (float) Math.sin((double) angle2);
        Quaternion quat = new Quaternion(((Float) axis.get(0)).floatValue(), ((Float) axis.get(1)).floatValue(), ((Float) axis.get(2)).floatValue(), 0.0f);
        quat.normalize();
        this.f102x = quat.f102x * sinAngle;
        this.f103y = quat.f103y * sinAngle;
        this.f104z = quat.f104z * sinAngle;
        this.f101w = (float) Math.cos((double) angle2);
        normalize();
    }

    public void buildFromAxisAngle(float[] axis, float angle) {
        Vector<Float> vec = new Vector<>();
        vec.add(Float.valueOf(axis[0]));
        vec.add(Float.valueOf(axis[1]));
        vec.add(Float.valueOf(axis[2]));
        buildFromAxisAngle(vec, angle);
    }

    public void buildFromEuler(float xRot, float yRot, float zRot) {
        float pitch = (xRot * 0.017453289f) / 2.0f;
        float yaw = (yRot * 0.017453289f) / 2.0f;
        float roll = (zRot * 0.017453289f) / 2.0f;
        float sinp = (float) Math.sin((double) pitch);
        float siny = (float) Math.sin((double) roll);
        float sinr = (float) Math.sin((double) yaw);
        float cosp = (float) Math.cos((double) pitch);
        float cosy = (float) Math.cos((double) roll);
        float cosr = (float) Math.cos((double) yaw);
        this.f102x = ((sinr * cosp) * cosy) - ((cosr * sinp) * siny);
        this.f103y = (cosr * sinp * cosy) + (sinr * cosp * siny);
        this.f104z = ((cosr * cosp) * siny) - ((sinr * sinp) * cosy);
        this.f101w = (cosr * cosp * cosy) + (sinr * sinp * siny);
        normalize();
    }

    public void buildFromMatrix(float[] m) {
        float[] q = {0.0f, 0.0f, 0.0f, 0.0f};
        float trace = m[0] + m[5] + m[10];
        if (trace > 0.0f) {
            float s = (float) Math.sqrt((double) (1.0f + trace));
            q[3] = 0.5f * s;
            float s2 = 0.5f / s;
            q[0] = (m[6] - m[9]) * s2;
            q[1] = (m[8] - m[2]) * s2;
            q[2] = (m[1] - m[4]) * s2;
        } else {
            int[] nxt = {1, 2, 0};
            int i = 0;
            if (m[5] > m[0]) {
                i = 1;
            }
            if (m[10] > m[i * 5]) {
                i = 2;
            }
            int j = nxt[i];
            int k = nxt[j];
            float s3 = (float) Math.sqrt((double) ((m[i * 5] - (m[j * 5] + m[k * 5])) + 1.0f));
            q[i] = 0.5f * s3;
            float s4 = 0.5f / s3;
            q[3] = (m[(j * 4) + k] - m[(k * 4) + j]) * s4;
            q[j] = (m[(i * 4) + j] + m[(j * 4) + i]) * s4;
            q[k] = (m[(i * 4) + k] + m[(k * 4) + i]) * s4;
        }
        this.f102x = q[0];
        this.f103y = q[1];
        this.f104z = q[2];
        this.f101w = q[3];
        normalize();
    }

    public float[] getMatrix() {
        float x2 = this.f102x * this.f102x;
        float y2 = this.f103y * this.f103y;
        float z2 = this.f104z * this.f104z;
        float xy = this.f102x * this.f103y;
        float xz = this.f102x * this.f104z;
        float yz = this.f103y * this.f104z;
        float wx = this.f101w * this.f102x;
        float wy = this.f101w * this.f103y;
        float wz = this.f101w * this.f104z;
        return new float[]{1.0f - ((y2 + z2) * 2.0f), (xy - wz) * 2.0f, (xz + wy) * 2.0f, 0.0f, (xy + wz) * 2.0f, 1.0f - ((x2 + z2) * 2.0f), (yz - wx) * 2.0f, 0.0f, (xz - wy) * 2.0f, (yz + wx) * 2.0f, 1.0f - ((x2 + y2) * 2.0f), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public Vector<Float> getAxisAngle() {
        Vector<Float> vector = new Vector<>();
        float magnitude = (float) Math.sqrt((double) ((this.f102x * this.f102x) + (this.f103y * this.f103y) + (this.f104z * this.f104z)));
        vector.set(0, Float.valueOf(this.f102x / magnitude));
        vector.set(1, Float.valueOf(this.f103y / magnitude));
        vector.set(2, Float.valueOf(this.f104z / magnitude));
        vector.set(3, Float.valueOf(57.29579f * ((float) Math.acos((double) this.f101w)) * 2.0f));
        return vector;
    }

    public float getMagnitude() {
        return (float) Math.sqrt((double) ((this.f102x * this.f102x) + (this.f103y * this.f103y) + (this.f104z * this.f104z) + (this.f101w * this.f101w)));
    }
}

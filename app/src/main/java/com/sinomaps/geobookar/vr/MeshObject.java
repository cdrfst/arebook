package com.sinomaps.geobookar.vr;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* renamed from: com.sinomaps.geobookar.vr.MeshObject */
public abstract class MeshObject {

    /* renamed from: com.sinomaps.geobookar.vr.MeshObject$BUFFER_TYPE */
    public enum BUFFER_TYPE {
        BUFFER_TYPE_VERTEX,
        BUFFER_TYPE_TEXTURE_COORD,
        BUFFER_TYPE_NORMALS,
        BUFFER_TYPE_INDICES
    }

    public abstract Buffer getBuffer(BUFFER_TYPE buffer_type);

    public abstract int getNumObjectIndex();

    public abstract int getNumObjectVertex();

    public Buffer getVertices() {
        return getBuffer(BUFFER_TYPE.BUFFER_TYPE_VERTEX);
    }

    public Buffer getTexCoords() {
        return getBuffer(BUFFER_TYPE.BUFFER_TYPE_TEXTURE_COORD);
    }

    public Buffer getNormals() {
        return getBuffer(BUFFER_TYPE.BUFFER_TYPE_NORMALS);
    }

    public Buffer getIndices() {
        return getBuffer(BUFFER_TYPE.BUFFER_TYPE_INDICES);
    }

    /* access modifiers changed from: protected */
    public Buffer fillBuffer(double[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (double d : array) {
            bb.putFloat((float) d);
        }
        bb.rewind();
        return bb;
    }

    /* access modifiers changed from: protected */
    public Buffer fillBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array) {
            bb.putFloat(d);
        }
        bb.rewind();
        return bb;
    }

    /* access modifiers changed from: protected */
    public Buffer fillBuffer(short[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (short s : array) {
            bb.putShort(s);
        }
        bb.rewind();
        return bb;
    }
}

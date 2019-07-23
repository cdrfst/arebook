package com.sinomaps.geobookar.vr;

import java.nio.Buffer;

/* renamed from: com.sinomaps.geobookar.vr.Plane */
public class Plane extends MeshObject {
    private static final short[] planeIndices = {0, 1, 2, 0, 2, 3};
    private static final float[] planeNormals = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final float[] planeTexcoords = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final float[] planeVertices = {-0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f};
    Buffer indices = fillBuffer(planeIndices);
    Buffer norms = fillBuffer(planeNormals);
    Buffer textCoords = fillBuffer(planeTexcoords);
    Buffer verts = fillBuffer(planeVertices);

    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                return this.verts;
            case BUFFER_TYPE_TEXTURE_COORD:
                return this.textCoords;
            case BUFFER_TYPE_INDICES:
                return this.indices;
            case BUFFER_TYPE_NORMALS:
                return this.norms;
            default:
                return null;
        }
    }

    public int getNumObjectVertex() {
        return planeVertices.length / 3;
    }

    public int getNumObjectIndex() {
        return planeIndices.length;
    }
}

package com.sinomaps.geobookar.vr;

import java.nio.Buffer;

/* renamed from: com.sinomaps.geobookar.vr.CubeObject */
public class CubeObject extends MeshObject {
    private static final short[] cubeIndices = {0, 1, 2, 0, 2, 3, 4, 6, 5, 4, 7, 6, 8, 9, 10, 8, 10, 11, 12, 14, 13, 12, 15, 14, 16, 17, 18, 16, 18, 19, 20, 22, 21, 20, 23, 22};
    private static final double[] cubeNormals = {0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, -1.0d, 0.0d};
    private static final double[] cubeTexcoords = {0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 1.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 1.0d, 1.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 1.0d, 1.0d};
    private static final double[] cubeVertices = {-1.0d, -1.0d, 1.0d, 1.0d, -1.0d, 1.0d, 1.0d, 1.0d, 1.0d, -1.0d, 1.0d, 1.0d, -1.0d, -1.0d, -1.0d, 1.0d, -1.0d, -1.0d, 1.0d, 1.0d, -1.0d, -1.0d, 1.0d, -1.0d, -1.0d, -1.0d, -1.0d, -1.0d, -1.0d, 1.0d, -1.0d, 1.0d, 1.0d, -1.0d, 1.0d, -1.0d, 1.0d, -1.0d, -1.0d, 1.0d, -1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, -1.0d, -1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, -1.0d, -1.0d, 1.0d, -1.0d, -1.0d, -1.0d, 1.0d, 1.0d, -1.0d, 1.0d, 1.0d, -1.0d, -1.0d, -1.0d, -1.0d, -1.0d};
    private Buffer mIndBuff = fillBuffer(cubeIndices);
    private Buffer mNormBuff = fillBuffer(cubeNormals);
    private Buffer mTexCoordBuff = fillBuffer(cubeTexcoords);
    private Buffer mVertBuff = fillBuffer(cubeVertices);

    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                return this.mVertBuff;
            case BUFFER_TYPE_TEXTURE_COORD:
                return this.mTexCoordBuff;
            case BUFFER_TYPE_INDICES:
                return this.mIndBuff;
            case BUFFER_TYPE_NORMALS:
                return this.mNormBuff;
            default:
                return null;
        }
    }

    public int getNumObjectVertex() {
        return cubeVertices.length / 3;
    }

    public int getNumObjectIndex() {
        return cubeIndices.length;
    }
}

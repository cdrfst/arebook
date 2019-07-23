package com.sinomaps.geobookar.vr;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* renamed from: com.sinomaps.geobookar.vr.SampleApplication3DModel */
public class SampleApplication3DModel extends MeshObject {
    private ByteBuffer norms;
    int numVerts = 0;
    private ByteBuffer textCoords;
    private ByteBuffer verts;

    public void loadModel(AssetManager assetManager, String filename) throws IOException {
        InputStream is = null;
        try {
            is = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            int floatsToRead = Integer.parseInt(reader.readLine());
            this.numVerts = floatsToRead / 3;
            this.verts = ByteBuffer.allocateDirect(floatsToRead * 4);
            this.verts.order(ByteOrder.nativeOrder());
            for (int i = 0; i < floatsToRead; i++) {
                this.verts.putFloat(Float.parseFloat(reader.readLine()));
            }
            this.verts.rewind();
            int floatsToRead2 = Integer.parseInt(reader.readLine());
            this.norms = ByteBuffer.allocateDirect(floatsToRead2 * 4);
            this.norms.order(ByteOrder.nativeOrder());
            for (int i2 = 0; i2 < floatsToRead2; i2++) {
                this.norms.putFloat(Float.parseFloat(reader.readLine()));
            }
            this.norms.rewind();
            int floatsToRead3 = Integer.parseInt(reader.readLine());
            this.textCoords = ByteBuffer.allocateDirect(floatsToRead3 * 4);
            this.textCoords.order(ByteOrder.nativeOrder());
            for (int i3 = 0; i3 < floatsToRead3; i3++) {
                this.textCoords.putFloat(Float.parseFloat(reader.readLine()));
            }
            this.textCoords.rewind();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                return this.verts;
            case BUFFER_TYPE_TEXTURE_COORD:
                return this.textCoords;
            case BUFFER_TYPE_NORMALS:
                return this.norms;
            default:
                return null;
        }
    }

    public int getNumObjectVertex() {
        return this.numVerts;
    }

    public int getNumObjectIndex() {
        return 0;
    }
}

package com.sinomaps.geobookar.vr;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* renamed from: com.sinomaps.geobookar.vr.Texture */
public class Texture {
    private static final String LOGTAG = "Vuforia_Texture";
    public int mChannels;
    public ByteBuffer mData;
    public int mHeight;
    public boolean mSuccess = false;
    public int[] mTextureID = new int[1];
    public int mWidth;

    public static Texture loadTextureFromApk(String fileName, AssetManager assets) {
        try {
            Bitmap bitMap = BitmapFactory.decodeStream(new BufferedInputStream(assets.open(fileName, 3)));
            int[] data = new int[(bitMap.getWidth() * bitMap.getHeight())];
            bitMap.getPixels(data, 0, bitMap.getWidth(), 0, 0, bitMap.getWidth(), bitMap.getHeight());
            return loadTextureFromIntBuffer(data, bitMap.getWidth(), bitMap.getHeight());
        } catch (IOException e) {
            Log.e(LOGTAG, "Failed to log texture '" + fileName + "' from APK");
            Log.i(LOGTAG, e.getMessage());
            return null;
        }
    }

    public static Texture loadTextureFromIntBuffer(int[] data, int width, int height) {
        int numPixels = width * height;
        byte[] dataBytes = new byte[(numPixels * 4)];
        for (int p = 0; p < numPixels; p++) {
            int colour = data[p];
            dataBytes[p * 4] = (byte) (colour >>> 16);
            dataBytes[(p * 4) + 1] = (byte) (colour >>> 8);
            dataBytes[(p * 4) + 2] = (byte) colour;
            dataBytes[(p * 4) + 3] = (byte) (colour >>> 24);
        }
        Texture texture = new Texture();
        texture.mWidth = width;
        texture.mHeight = height;
        texture.mChannels = 4;
        texture.mData = ByteBuffer.allocateDirect(dataBytes.length).order(ByteOrder.nativeOrder());
        int rowSize = texture.mWidth * texture.mChannels;
        for (int r = 0; r < texture.mHeight; r++) {
            texture.mData.put(dataBytes, ((texture.mHeight - 1) - r) * rowSize, rowSize);
        }
        texture.mData.rewind();
        texture.mSuccess = true;
        return texture;
    }
}

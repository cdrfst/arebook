package com.sinomaps.geobookar.opengl;

import android.opengl.GLES20;

import com.sinomaps.geobookar.utility.MyLogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class My3DItem {
    public static final boolean isChangeing = false;
    ArrayList<Float> listNorms = new ArrayList<>();
    ArrayList<Float> listTextCoords = new ArrayList<>();
    ArrayList<Float> listVerts = new ArrayList<>();
    public MtlInfo mtlInfo;
    public String name;
    public ByteBuffer norms = null;
    public int numVerts = 0;
    public ByteBuffer textCoords = null;
    public ByteBuffer verts = null;

    /* access modifiers changed from: 0000 */
    public void initData(ArrayList<Float> alvResult, ArrayList<Float> altResult, ArrayList<Float> alnResult) {
        long startTime = System.currentTimeMillis();
        int size = alvResult.size();
        this.numVerts = size / 3;
        this.verts = ByteBuffer.allocateDirect(size * 4);
        this.verts.order(ByteOrder.nativeOrder());
        for (int i = 0; i < size; i++) {
            this.verts.putFloat(((Float) alvResult.get(i)).floatValue());
        }
        this.verts.rewind();
        int size2 = alnResult.size();
        this.norms = ByteBuffer.allocateDirect(size2 * 4);
        this.norms.order(ByteOrder.nativeOrder());
        for (int i2 = 0; i2 < size2; i2++) {
            this.norms.putFloat(((Float) alnResult.get(i2)).floatValue());
        }
        this.norms.rewind();
        int size3 = altResult.size();
        if (size3 != 0) {
            this.textCoords = ByteBuffer.allocateDirect(size3 * 4);
            this.textCoords.order(ByteOrder.nativeOrder());
            for (int i3 = 0; i3 < size3; i3++) {
                this.textCoords.putFloat(((Float) altResult.get(i3)).floatValue());
            }
            this.textCoords.rewind();
        }
        MyLogger.m163v("initDataTime:" + (System.currentTimeMillis() - startTime));
    }

    /* access modifiers changed from: 0000 */
    public void draw(ShaderParam sp, float[] mMVPMatrix, float[] mModelViewMatrix, float[] mModelMatrix, float[] mViewMatrix) {
        GLES20.glUseProgram(sp.mProgram);
        if (this.verts != null) {
            GLES20.glVertexAttribPointer(sp.mPositionHandle, 3, 5126, false, 12, this.verts);
            GLES20.glEnableVertexAttribArray(sp.mPositionHandle);
        }
        if (this.norms != null) {
            GLES20.glVertexAttribPointer(sp.mNormalHandle, 3, 5126, false, 12, this.norms);
            GLES20.glEnableVertexAttribArray(sp.mNormalHandle);
        }
        if (this.textCoords != null) {
            GLES20.glVertexAttribPointer(sp.mTexCoorHandle, 3, 5126, false, 12, this.textCoords);
            GLES20.glEnableVertexAttribArray(sp.mTexCoorHandle);
        }
        if (this.mtlInfo == null || this.mtlInfo.texture == null) {
            GLES20.glUniform1f(sp.mEnableTextureHandle, 0.1f);
        } else {
            GLES20.glUniform1f(sp.mEnableTextureHandle, 1.0f);
        }
        if (Float.floatToIntBits(this.mtlInfo.alpha) != Float.floatToIntBits(1.0f)) {
            GLES20.glDepthMask(false);
        }
        GLES20.glUniform4f(sp.mColor, this.mtlInfo.diffuseColor[0], this.mtlInfo.diffuseColor[1], this.mtlInfo.diffuseColor[2], this.mtlInfo.alpha);
        if (!(this.mtlInfo == null || this.mtlInfo.texture == null)) {
            MyLogger.m163v("bind texture");
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.mtlInfo.texture.mTextureID[0]);
        }
        GLES20.glUniform1i(sp.mTextureSamplerHandle, 0);
        GLES20.glUniform1f(sp.mLightingEnabledHandle, 1.0f);
        GLES20.glUniformMatrix4fv(sp.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(sp.mModelViewMatrixHandle, 1, false, mModelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(sp.mModelMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(sp.mViewMatrixHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform4f(sp.mLighPosVecHandle, 0.0f, 1.0f, 3.0f, 0.0f);
        GLES20.glUniform3fv(sp.mVBrightnessHandle, 1, new float[]{0.5f, 0.5f, 0.5f}, 0);
        MyLogger.m163v("draw item:" + this.name);
        GLES20.glDrawArrays(4, 0, this.numVerts);
        if (Float.floatToIntBits(this.mtlInfo.alpha) != Float.floatToIntBits(1.0f)) {
            GLES20.glDepthMask(true);
        }
        MyLogger.m163v("draw item end");
    }
}

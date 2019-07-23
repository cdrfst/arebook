package com.sinomaps.geobookar.ui;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.sinomaps.geobookar.opengl.My3DObject;
import com.sinomaps.geobookar.vr.Quaternion;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* renamed from: com.sinomaps.geobookar.ui.MyGLRenderer */
public class MyGLRenderer implements Renderer {
    private ResModelActivity mActivity;
    private My3DObject mMyObject;
    private float[] mProjMatrix = new float[16];
    float mScale = 1.0f;
    private float[] mViewMatrix = new float[16];
    float xAngle = 0.0f;
    public float xDelta = 0.0f;
    float yAngle = 0.0f;
    public float yDelta = 0.0f;

    public MyGLRenderer(ResModelActivity activity) {
        this.mActivity = activity;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        initRendering();
        this.mActivity.loadingDialogHandler.sendEmptyMessage(0);
    }

    private void initRendering() {
        this.mMyObject = this.mActivity.m3DModel;
        if (this.mMyObject != null) {
            this.mMyObject.initShader(this.mActivity, "shaders/vertex.sh", "shaders/frag.sh");
            this.mMyObject.bindTextures();
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(this.mProjMatrix, 0, 45.0f, ((float) width) / ((float) height), 1.0f, 10.0f);
        Matrix.setLookAtM(this.mViewMatrix, 0, 0.0f, 0.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    public void onDrawFrame(GL10 gl) {
        if (this.mMyObject != null) {
            GLES20.glClear(16640);
            GLES20.glEnable(2929);
            GLES20.glEnable(2884);
            GLES20.glCullFace(1029);
            GLES20.glEnable(3042);
            GLES20.glBlendFunc(770, 771);
            Quaternion modelMatrixQ = new Quaternion();
            modelMatrixQ.normalize();
            float[] mModelMatrix = modelMatrixQ.getMatrix();
            float boundingRadius = this.mMyObject.getBoundingRadius();
            Matrix.scaleM(mModelMatrix, 0, 1.0f / boundingRadius, 1.0f / boundingRadius, 1.0f / boundingRadius);
            Matrix.scaleM(mModelMatrix, 0, this.mScale, this.mScale, this.mScale);
            Matrix.translateM(mModelMatrix, 0, -this.mMyObject.getCenterPoint()[0], -this.mMyObject.getCenterPoint()[1], -this.mMyObject.getCenterPoint()[2]);
            if (this.mMyObject.isbIsEarth()) {
                Matrix.translateM(mModelMatrix, 0, this.xDelta * 0.17904931f, (-this.yDelta) * 0.17904931f, 0.0f);
            } else {
                Matrix.translateM(mModelMatrix, 0, this.xDelta, -this.yDelta, 0.0f);
            }
            Matrix.rotateM(mModelMatrix, 0, this.xAngle, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(mModelMatrix, 0, this.yAngle, 0.0f, 1.0f, 0.0f);
            Matrix.rotateM(mModelMatrix, 0, this.mMyObject.getXAngle(), 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(mModelMatrix, 0, this.mMyObject.getYAngle(), 0.0f, 1.0f, 0.0f);
            float[] mMVMatrix = new float[16];
            Matrix.multiplyMM(mMVMatrix, 0, this.mViewMatrix, 0, mModelMatrix, 0);
            float[] mMVPMatrix = new float[16];
            Matrix.multiplyMM(mMVPMatrix, 0, this.mProjMatrix, 0, mMVMatrix, 0);
            this.mMyObject.draw(mMVPMatrix, mMVMatrix, mModelMatrix, this.mViewMatrix);
            GLES20.glDisable(2929);
            GLES20.glDisable(3042);
        }
    }
}

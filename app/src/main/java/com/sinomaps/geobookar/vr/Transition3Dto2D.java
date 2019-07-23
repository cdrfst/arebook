package com.sinomaps.geobookar.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.vuforia.Matrix34F;
import com.vuforia.Tool;
import com.vuforia.Vec4F;

/* renamed from: com.sinomaps.geobookar.vr.Transition3Dto2D */
public class Transition3Dto2D {
    private int animationDirection;
    private boolean animationFinished;
    private float animationLength;
    private long animationStartTime;
    private float dpiScaleIndicator;
    private float[] identityMatrix = new float[16];
    private boolean isActivityPortraitMode;
    private Plane mPlane;
    private int mvpMatrixHandle;
    private int normalHandle;
    private float[] orthoMatrix = new float[16];
    private float scaleFactor;
    private int screenHeight;
    private Vec4F screenRect;
    private int screenWidth;
    private int shaderProgramID;
    private int textureCoordHandle;
    private int vertexHandle;

    public Transition3Dto2D(int screenWidth2, int screenHeight2, boolean isPortraitMode, float dpiSIndicator, float scaleFactor2, Plane p) {
        this.identityMatrix[0] = 1.0f;
        this.identityMatrix[1] = 0.0f;
        this.identityMatrix[2] = 0.0f;
        this.identityMatrix[3] = 0.0f;
        this.identityMatrix[4] = 0.0f;
        this.identityMatrix[5] = 1.0f;
        this.identityMatrix[6] = 0.0f;
        this.identityMatrix[7] = 0.0f;
        this.identityMatrix[8] = 0.0f;
        this.identityMatrix[9] = 0.0f;
        this.identityMatrix[10] = 1.0f;
        this.identityMatrix[11] = 0.0f;
        this.identityMatrix[12] = 0.0f;
        this.identityMatrix[13] = 0.0f;
        this.identityMatrix[14] = 0.0f;
        this.identityMatrix[15] = 1.0f;
        this.dpiScaleIndicator = dpiSIndicator;
        this.scaleFactor = scaleFactor2;
        updateScreenPoperties(screenWidth2, screenHeight2, isPortraitMode);
        this.mPlane = p;
    }

    public void initializeGL(int sProgramID) {
        this.shaderProgramID = sProgramID;
        this.vertexHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexPosition");
        this.normalHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexNormal");
        this.textureCoordHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexTexCoord");
        this.mvpMatrixHandle = GLES20.glGetUniformLocation(this.shaderProgramID, "modelViewProjectionMatrix");
        SampleUtils.checkGLError("Transition3Dto2D.initializeGL");
    }

    public void updateScreenPoperties(int screenWidth2, int screenHeight2, boolean isPortraitMode) {
        this.isActivityPortraitMode = isPortraitMode;
        this.screenWidth = screenWidth2;
        this.screenHeight = screenHeight2;
        this.screenRect = new Vec4F(0.0f, 0.0f, (float) screenWidth2, (float) screenHeight2);
        for (int i = 0; i < 16; i++) {
            this.orthoMatrix[i] = 0.0f;
        }
        float nLeft = ((float) (-screenWidth2)) / 2.0f;
        float nRight = ((float) screenWidth2) / 2.0f;
        float nBottom = ((float) (-screenHeight2)) / 2.0f;
        float nTop = ((float) screenHeight2) / 2.0f;
        this.orthoMatrix[0] = 2.0f / (nRight - nLeft);
        this.orthoMatrix[5] = 2.0f / (nTop - nBottom);
        this.orthoMatrix[10] = 2.0f / (-1.0f - 1.0f);
        this.orthoMatrix[12] = (-(nRight + nLeft)) / (nRight - nLeft);
        this.orthoMatrix[13] = (-(nTop + nBottom)) / (nTop - nBottom);
        this.orthoMatrix[14] = (1.0f - 4.0f) / (1.0f - -1.0f);
        this.orthoMatrix[15] = 1.0f;
    }

    public void setScreenRect(int centerX, int centerY, int width, int height) {
        this.screenRect = new Vec4F((float) centerX, (float) centerY, (float) width, (float) height);
    }

    public void startTransition(float duration, boolean inReverse, boolean keepRendering) {
        this.animationLength = duration;
        this.animationDirection = inReverse ? -1 : 1;
        this.animationStartTime = getCurrentTimeMS();
        this.animationFinished = false;
    }

    public float stepTransition() {
        float t = (((float) (getCurrentTimeMS() - this.animationStartTime)) / 1000.0f) / this.animationLength;
        if (t >= 1.0f) {
            t = 1.0f;
            this.animationFinished = true;
        }
        if (this.animationDirection == -1) {
            return 1.0f - t;
        }
        return t;
    }

    public void render(float[] mProjectionMatrix, Matrix34F targetPose, int texture1) {
        float t = stepTransition();
        float[] modelViewProjectionTracked = new float[16];
        float[] modelViewProjectionCurrent = new float[16];
        float[] modelViewMatrix = Tool.convertPose2GLMatrix(targetPose).getData();
        float[] finalPositionMatrix = getFinalPositionMatrix();
        Matrix.scaleM(modelViewMatrix, 0, 430.0f * this.scaleFactor, 430.0f * this.scaleFactor, 1.0f);
        Matrix.multiplyMM(modelViewProjectionTracked, 0, mProjectionMatrix, 0, modelViewMatrix, 0);
        linearInterpolate(modelViewProjectionTracked, finalPositionMatrix, modelViewProjectionCurrent, deccelerate(0.8f + (0.2f * t)));
        GLES20.glUseProgram(this.shaderProgramID);
        GLES20.glVertexAttribPointer(this.vertexHandle, 3, 5126, false, 0, this.mPlane.getVertices());
        GLES20.glVertexAttribPointer(this.normalHandle, 3, 5126, false, 0, this.mPlane.getNormals());
        GLES20.glVertexAttribPointer(this.textureCoordHandle, 2, 5126, false, 0, this.mPlane.getTexCoords());
        GLES20.glEnableVertexAttribArray(this.vertexHandle);
        GLES20.glEnableVertexAttribArray(this.normalHandle);
        GLES20.glEnableVertexAttribArray(this.textureCoordHandle);
        GLES20.glEnable(3042);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, texture1);
        GLES20.glUniformMatrix4fv(this.mvpMatrixHandle, 1, false, modelViewProjectionCurrent, 0);
        GLES20.glDrawElements(4, 6, 5123, this.mPlane.getIndices());
        GLES20.glDisableVertexAttribArray(this.vertexHandle);
        GLES20.glDisableVertexAttribArray(this.normalHandle);
        GLES20.glDisableVertexAttribArray(this.textureCoordHandle);
        GLES20.glDisable(3042);
        SampleUtils.checkGLError("Transition3Dto2D.render");
    }

    public boolean transitionFinished() {
        return this.animationFinished;
    }

    public Matrix34F getFinalPositionMatrix34F() {
        float[] glFinalPositionMatrix = getFinalPositionMatrix();
        float[] vuforiaFinalPositionMatrix = new float[12];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                vuforiaFinalPositionMatrix[(i * 3) + j] = glFinalPositionMatrix[(i * 4) + j];
            }
        }
        Matrix34F finalPositionMatrix34F = new Matrix34F();
        finalPositionMatrix34F.setData(vuforiaFinalPositionMatrix);
        return finalPositionMatrix34F;
    }

    private float[] getFinalPositionMatrix() {
        float[] viewport = new float[4];
        GLES20.glGetFloatv(2978, viewport, 0);
        if (this.isActivityPortraitMode) {
            if (this.screenWidth > this.screenHeight) {
                int tempValue = this.screenWidth;
                this.screenWidth = this.screenHeight;
                this.screenHeight = tempValue;
            }
        } else if (this.screenWidth < this.screenHeight) {
            int tempValue2 = this.screenWidth;
            this.screenWidth = this.screenHeight;
            this.screenHeight = tempValue2;
        }
        float scaleFactorX = ((float) this.screenWidth) / viewport[2];
        float scaleFactorY = ((float) this.screenHeight) / viewport[3];
        float scaleMultiplierWidth = 1.3f;
        float scaleMultiplierHeight = 0.75f;
        if (this.dpiScaleIndicator == 1.0f) {
            scaleMultiplierHeight = 0.75f * 1.6f;
            scaleMultiplierWidth = 1.3f * 1.6f;
        } else if (this.dpiScaleIndicator == 1.5f) {
            scaleMultiplierHeight = 0.75f * 1.2f;
            scaleMultiplierWidth = 1.3f * 1.2f;
        } else if (this.dpiScaleIndicator == 2.0f) {
            scaleMultiplierHeight = 0.75f * 0.9f;
            scaleMultiplierWidth = 1.3f * 0.9f;
        } else if (this.dpiScaleIndicator > 2.0f) {
            scaleMultiplierHeight = 0.75f * 0.75f;
            scaleMultiplierWidth = 1.3f * 0.75f;
        }
        float scaleX = this.screenRect.getData()[2] * scaleFactorX;
        float scaleY = this.screenRect.getData()[3] * scaleFactorY;
        float[] result = (float[]) this.orthoMatrix.clone();
        Matrix.translateM(result, 0, this.screenRect.getData()[0] * scaleFactorX, this.screenRect.getData()[1] * scaleFactorY, 0.0f);
        if (this.isActivityPortraitMode) {
            Matrix.scaleM(result, 0, scaleX * scaleMultiplierWidth, scaleY * scaleMultiplierHeight, 1.0f);
        } else {
            Matrix.scaleM(result, 0, scaleX * scaleMultiplierHeight, scaleY * scaleMultiplierWidth, 1.0f);
        }
        return result;
    }

    private float deccelerate(float val) {
        return 1.0f - ((1.0f - val) * (1.0f - val));
    }

    private void linearInterpolate(float[] start, float[] end, float[] current, float elapsed) {
        if (start.length == 16 && end.length == 16 && current.length == 16) {
            for (int i = 0; i < 16; i++) {
                current[i] = ((end[i] - start[i]) * elapsed) + start[i];
            }
        }
    }

    private long getCurrentTimeMS() {
        return System.currentTimeMillis();
    }
}

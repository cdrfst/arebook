package com.sinomaps.geobookar.vr;

import android.opengl.GLES20;
import android.util.Log;


/* renamed from: com.sinomaps.geobookar.vr.SampleUtils */
public class SampleUtils {
    private static final String LOGTAG = "Vuforia_Sample";

    static int initShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            return shader;
        }
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] glStatusVar = {0};
        GLES20.glGetShaderiv(shader, 35713, glStatusVar, 0);
        if (glStatusVar[0] != 0) {
            return shader;
        }
        Log.e(LOGTAG, "Could NOT compile shader " + shaderType + " : " + GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }

    public static int createProgramFromShaderSrc(String vertexShaderSrc, String fragmentShaderSrc) {
        int vertShader = initShader(35633, vertexShaderSrc);
        int fragShader = initShader(35632, fragmentShaderSrc);
        if (vertShader == 0 || fragShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            return program;
        }
        GLES20.glAttachShader(program, vertShader);
        checkGLError("glAttchShader(vert)");
        GLES20.glAttachShader(program, fragShader);
        checkGLError("glAttchShader(frag)");
        GLES20.glLinkProgram(program);
        int[] glStatusVar = {0};
        GLES20.glGetProgramiv(program, 35714, glStatusVar, 0);
        if (glStatusVar[0] != 0) {
            return program;
        }
        Log.e(LOGTAG, "Could NOT link program : " + GLES20.glGetProgramInfoLog(program));
        GLES20.glDeleteProgram(program);
        return 0;
    }

    public static void checkGLError(String op) {
        for (int error = GLES20.glGetError(); error != 0; error = GLES20.glGetError()) {
            Log.e(LOGTAG, "After operation " + op + " got glError 0x" + Integer.toHexString(error));
        }
    }

//    public static void screenCoordToCameraCoord(int screenX, int screenY, int screenDX, int screenDY, int screenWidth, int screenHeight, int cameraWidth, int cameraHeight, int[] cameraX, int[] cameraY, int[] cameraDX, int[] cameraDY, int displayRotation, int cameraRotation) {
//        float scaledUpVideoHeight;
//        float scaledUpVideoWidth;
//        float scaledUpY;
//        float scaledUpX;
//        float videoWidth = (float) cameraWidth;
//        float videoHeight = (float) cameraHeight;
//        switch (((((displayRotation * 90) - cameraRotation) + C0834a.f589p) % C0834a.f589p) / 90) {
//            case 1:
//                int tmp = screenX;
//                screenX = screenHeight - screenY;
//                screenY = tmp;
//                int tmp2 = screenDX;
//                screenDX = screenDY;
//                screenDY = tmp2;
//                int tmp3 = screenWidth;
//                screenWidth = screenHeight;
//                screenHeight = tmp3;
//                break;
//            case 2:
//                screenX = screenWidth - screenX;
//                screenY = screenHeight - screenY;
//                break;
//            case 3:
//                int tmp4 = screenX;
//                screenX = screenY;
//                screenY = screenWidth - tmp4;
//                int tmp5 = screenDX;
//                screenDX = screenDY;
//                screenDY = tmp5;
//                int tmp6 = screenWidth;
//                screenWidth = screenHeight;
//                screenHeight = tmp6;
//                break;
//        }
//        float videoAspectRatio = videoHeight / videoWidth;
//        if (videoAspectRatio < ((float) screenHeight) / ((float) screenWidth)) {
//            scaledUpVideoWidth = ((float) screenHeight) / videoAspectRatio;
//            scaledUpVideoHeight = (float) screenHeight;
//            scaledUpX = ((float) screenX) + ((scaledUpVideoWidth - ((float) screenWidth)) / 2.0f);
//            scaledUpY = (float) screenY;
//        } else {
//            scaledUpVideoHeight = ((float) screenWidth) * videoAspectRatio;
//            scaledUpVideoWidth = (float) screenWidth;
//            scaledUpY = ((float) screenY) + ((scaledUpVideoHeight - ((float) screenHeight)) / 2.0f);
//            scaledUpX = (float) screenX;
//        }
//        if (cameraX != null && cameraX.length > 0) {
//            cameraX[0] = (int) ((scaledUpX / scaledUpVideoWidth) * videoWidth);
//        }
//        if (cameraY != null && cameraY.length > 0) {
//            cameraY[0] = (int) ((scaledUpY / scaledUpVideoHeight) * videoHeight);
//        }
//        if (cameraDX != null && cameraDX.length > 0) {
//            cameraDX[0] = (int) ((((float) screenDX) / scaledUpVideoWidth) * videoWidth);
//        }
//        if (cameraDY != null && cameraDY.length > 0) {
//            cameraDY[0] = (int) ((((float) screenDY) / scaledUpVideoHeight) * videoHeight);
//        }
//    }

    public static float[] getOrthoMatrix(float nLeft, float nRight, float nBottom, float nTop, float nNear, float nFar) {
        float[] nProjMatrix = new float[16];
        for (int i = 0; i < 16; i++) {
            nProjMatrix[i] = 0.0f;
        }
        nProjMatrix[0] = 2.0f / (nRight - nLeft);
        nProjMatrix[5] = 2.0f / (nTop - nBottom);
        nProjMatrix[10] = 2.0f / (nNear - nFar);
        nProjMatrix[12] = (-(nRight + nLeft)) / (nRight - nLeft);
        nProjMatrix[13] = (-(nTop + nBottom)) / (nTop - nBottom);
        nProjMatrix[14] = (nFar + nNear) / (nFar - nNear);
        nProjMatrix[15] = 1.0f;
        return nProjMatrix;
    }
}

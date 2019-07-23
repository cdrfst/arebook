package com.sinomaps.geobookar.ar;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.sinomaps.geobookar.vr.SampleUtils;
import com.sinomaps.geobookar.vr.VideoBackgroundShader;
import com.vuforia.CameraDevice;
import com.vuforia.Device;
import com.vuforia.GLTextureUnit;
import com.vuforia.Mesh;
import com.vuforia.Renderer;
import com.vuforia.RenderingPrimitives;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackerManager;
import com.vuforia.Vec4I;
import com.vuforia.ViewList;

/* renamed from: com.sinomaps.geobookar.ar.SampleAppRenderer */
public class SampleAppRenderer {
    private static final String LOGTAG = "SampleAppRenderer";
    static final float M_PI = 3.14159f;
    static final float VIRTUAL_FOV_Y_DEGS = 85.0f;
    private int currentView = 0;
    boolean isRenderingInitialized = false;
    float mFarPlane = 5000.0f;
    float mNearPlane = 50.0f;
    private Renderer mRenderer;
    private SampleAppRendererControl mRenderingInterface;
    private RenderingPrimitives mRenderingPrimitives;
    private int vbProjectionMatrixHandle;
    private int vbShaderProgramID;
    private int vbTexCoordHandle = 0;
    private int vbTexSampler2DHandle;
    private int vbVertexHandle = 0;
    GLTextureUnit videoBackgroundTex = new GLTextureUnit();

    SampleAppRenderer(SampleAppRendererControl renderingInterface, int deviceMode, boolean stereo) {
        this.mRenderingInterface = renderingInterface;
        this.mRenderer = Renderer.getInstance();
        if (!(deviceMode == 0 || deviceMode == 1)) {
            deviceMode = 0;
        }
        Device device = Device.getInstance();
        device.setViewerActive(stereo);
        device.setMode(deviceMode);
    }

    public void onSurfaceCreated() {
        initRendering();
    }

    /* access modifiers changed from: 0000 */
    public void onConfigurationChanged() {
        this.mRenderingPrimitives = Device.getInstance().getRenderingPrimitives();
    }

    /* access modifiers changed from: 0000 */
    public void initRendering() {
        this.vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(VideoBackgroundShader.VB_VERTEX_SHADER, VideoBackgroundShader.VB_FRAGMENT_SHADER);
        if (this.vbShaderProgramID > 0) {
            GLES20.glUseProgram(this.vbShaderProgramID);
            this.vbTexSampler2DHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "texSampler2D");
            this.vbProjectionMatrixHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "projectionMatrix");
            this.vbVertexHandle = GLES20.glGetAttribLocation(this.vbShaderProgramID, "vertexPosition");
            this.vbTexCoordHandle = GLES20.glGetAttribLocation(this.vbShaderProgramID, "vertexTexCoord");
            this.vbProjectionMatrixHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "projectionMatrix");
            this.vbTexSampler2DHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "texSampler2D");
            GLES20.glUseProgram(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void render() {
        if (!this.isRenderingInitialized) {
            initRendering();
            this.isRenderingInitialized = true;
        }
        GLES20.glClear(16640);
        State state = TrackerManager.getInstance().getStateUpdater().updateState();
        this.mRenderer.begin(state);
        ViewList viewList = this.mRenderingPrimitives.getRenderingViews();
        for (int v = 0; ((long) v) < viewList.getNumViews(); v++) {
            int viewID = viewList.getView(v);
            Vec4I viewport = this.mRenderingPrimitives.getViewport(viewID);
            GLES20.glViewport(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);
            GLES20.glScissor(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);
            float[] projectionMatrix = new float[16];
            Matrix.multiplyMM(projectionMatrix, 0, Tool.convertPerspectiveProjection2GLMatrix(this.mRenderingPrimitives.getProjectionMatrix(viewID, 1), this.mNearPlane, this.mFarPlane).getData(), 0, Tool.convert2GLMatrix(this.mRenderingPrimitives.getEyeDisplayAdjustmentMatrix(viewID)).getData(), 0);
            GLES20.glViewport(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);
            this.currentView = viewID;
            if (this.currentView != 3) {
                this.mRenderingInterface.renderFrame(state, projectionMatrix);
            }
        }
        this.mRenderer.end();
    }

    public void setNearFarPlanes(float near, float far) {
        this.mNearPlane = near;
        this.mFarPlane = far;
    }

    public void renderVideoBackground() {
        if (this.currentView != 3) {
            this.videoBackgroundTex.setTextureUnit(0);
            if (!this.mRenderer.updateVideoBackgroundTexture(this.videoBackgroundTex)) {
                Log.e(LOGTAG, "Unable to update video background texture");
                return;
            }
            float[] vbProjectionMatrix = Tool.convert2GLMatrix(this.mRenderingPrimitives.getVideoBackgroundProjectionMatrix(this.currentView, 1)).getData();
            if (Device.getInstance().isViewerActive()) {
                float sceneScaleFactor = (float) getSceneScaleFactor();
                Matrix.scaleM(vbProjectionMatrix, 0, sceneScaleFactor, sceneScaleFactor, 1.0f);
            }
            GLES20.glDisable(2929);
            GLES20.glDisable(2884);
            GLES20.glDisable(3089);
            Mesh vbMesh = this.mRenderingPrimitives.getVideoBackgroundMesh(this.currentView);
            GLES20.glUseProgram(this.vbShaderProgramID);
            GLES20.glVertexAttribPointer(this.vbVertexHandle, 3, 5126, false, 0, vbMesh.getPositions().asFloatBuffer());
            GLES20.glVertexAttribPointer(this.vbTexCoordHandle, 2, 5126, false, 0, vbMesh.getUVs().asFloatBuffer());
            GLES20.glUniform1i(this.vbTexSampler2DHandle, 0);
            GLES20.glEnableVertexAttribArray(this.vbVertexHandle);
            GLES20.glEnableVertexAttribArray(this.vbTexCoordHandle);
            GLES20.glUniformMatrix4fv(this.vbProjectionMatrixHandle, 1, false, vbProjectionMatrix, 0);
            GLES20.glDrawElements(4, vbMesh.getNumTriangles() * 3, 5123, vbMesh.getTriangles().asShortBuffer());
            GLES20.glDisableVertexAttribArray(this.vbVertexHandle);
            GLES20.glDisableVertexAttribArray(this.vbTexCoordHandle);
            SampleUtils.checkGLError("Rendering of the video background failed");
        }
    }

    /* access modifiers changed from: 0000 */
    public double getSceneScaleFactor() {
        return Math.tan((double) (CameraDevice.getInstance().getCameraCalibration().getFieldOfViewRads().getData()[1] / 2.0f)) / Math.tan((double) (1.4835286f / 2.0f));
    }
}

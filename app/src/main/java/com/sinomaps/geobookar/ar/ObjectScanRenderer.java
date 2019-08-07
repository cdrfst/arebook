package com.sinomaps.geobookar.ar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.sinomaps.geobookar.R;
import com.sinomaps.geobookar.model.ArResouceResponseBean;
import com.sinomaps.geobookar.model.ObjectInfo;
import com.sinomaps.geobookar.model.ResourceStatus;
import com.sinomaps.geobookar.opengl.My3DItem;
import com.sinomaps.geobookar.opengl.My3DObject;
import com.sinomaps.geobookar.utility.MyUtility;
import com.sinomaps.geobookar.utility.ResourceMgrTool;
import com.sinomaps.geobookar.vr.CubeShaders2;
import com.sinomaps.geobookar.vr.LineShaders;
import com.sinomaps.geobookar.vr.Plane;
import com.sinomaps.geobookar.vr.SampleApplicationSession;
import com.sinomaps.geobookar.vr.SampleMath;
import com.sinomaps.geobookar.vr.SampleUtils;
import com.sinomaps.geobookar.vr.Texture;
import com.sinomaps.geobookar.vr.Transition3Dto2D;
import com.vuforia.ImageTarget;
import com.vuforia.Matrix34F;
import com.vuforia.Matrix44F;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vec2F;
import com.vuforia.Vec3F;
import com.vuforia.Vuforia;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* renamed from: com.sinomaps.geobookar.ar.ObjectScanRenderer */
public class ObjectScanRenderer implements Renderer {
    public static final int RS_LOADING = 3;
    public static final int RS_NORMAL = 0;
    public static final int RS_SCANNING = 5;
    public static final int RS_TEXTURE_GENERATED = 4;
    public static final int RS_TRANSITION_TO_2D = 1;
    public static final int RS_TRANSITION_TO_3D = 2;
    private static final String TAG = "ImageTargetRenderer";
    boolean deleteCurrentPopupTexture = false;
    private int enableTextCoordHandle;
    private int fragColorHandle;
    private AtomicInteger framesToSkipBeforeRenderingTransition = new AtomicInteger(10);
    public boolean isStatic3DOK = false;
    private int lineColorHandle = 0;
    private int lineOpacityHandle = 0;
    private List<Matrix44F> listTargetModelViewMatrix = new ArrayList();
    private List<String> listTargetNames = new ArrayList();
    private List<Vec3F> listTargetPositiveDimension = new ArrayList();
    private ObjectScanActivity mActivity;
    private float mDPIScaleIndicator;
    public boolean mIsActive = false;
    boolean mIsShowing2DOverlay = false;
    private Texture mObjOverlayTexture;
    private Plane mPlane;
    private float mScaleFactor;
    private boolean mScanningMode = false;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean mShowAnimation3Dto2D = true;
    private boolean mStartAnimation2Dto3D = false;
    private boolean mStartAnimation3Dto2D = false;
    private boolean mTrackingStarted = false;
    private float[] modelViewMatrix3D = null;
    private int mvpMatrixButtonsHandle = 0;
    private int mvpMatrixHandle;
    private int normalHandle;
    private Matrix34F pose;
    private float[] projMatrixData3D = null;
    int renderState = 5;
    private float scaleXS = 300.0f;
    private int shaderProgramID;
    private int textureCoordHandle;
    Transition3Dto2D transition2Dto3D;
    Transition3Dto2D transition3Dto2D;
    float transitionDuration = 0.5f;
    private int vbShaderProgramID = 0;
    private int vbVertexHandle = 0;
    private int vertexHandle;
    private SampleApplicationSession vuforiaAppSession;
    public float xAngle = 0.0f;
    public float yAngle = 0.0f;

    public ObjectScanRenderer(ObjectScanActivity activity, SampleApplicationSession vuforiaAppSession2) {
        this.vuforiaAppSession = vuforiaAppSession2;
        this.mActivity = activity;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        this.vuforiaAppSession.onSurfaceCreated();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mScreenHeight = height;
        this.mScreenWidth = width;
        updateRendering(width, height);
        this.vuforiaAppSession.onSurfaceChanged(width, height);
    }

    public void updateRendering(int width, int height) {
        this.mScreenWidth = width;
        this.mScreenHeight = height;
        boolean isActivityInPortraitMode = this.mActivity.getResources().getConfiguration().orientation == 2;
        this.transition3Dto2D = new Transition3Dto2D(this.mScreenWidth, this.mScreenHeight, isActivityInPortraitMode, this.mDPIScaleIndicator, this.mScaleFactor, this.mPlane);
        this.transition3Dto2D.initializeGL(this.shaderProgramID);
        this.transition2Dto3D = new Transition3Dto2D(this.mScreenWidth, this.mScreenHeight, isActivityInPortraitMode, this.mDPIScaleIndicator, this.mScaleFactor, this.mPlane);
        this.transition2Dto3D.initializeGL(this.shaderProgramID);
    }

    public void onDrawFrame(GL10 gl) {
        if (this.mIsActive) {
            renderFrame();
        }
    }

    private void initRendering() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);
        this.shaderProgramID = SampleUtils.createProgramFromShaderSrc(CubeShaders2.CUBE_MESH_VERTEX_SHADER, CubeShaders2.CUBE_MESH_FRAGMENT_SHADER);
        this.vertexHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexPosition");
        this.normalHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexNormal");
        this.textureCoordHandle = GLES20.glGetAttribLocation(this.shaderProgramID, "vertexTexCoord");
        this.enableTextCoordHandle = GLES20.glGetUniformLocation(this.shaderProgramID, "enableTexture");
        this.fragColorHandle = GLES20.glGetUniformLocation(this.shaderProgramID, "fragColor");
        this.mvpMatrixHandle = GLES20.glGetUniformLocation(this.shaderProgramID, "modelViewProjectionMatrix");
        this.vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(LineShaders.LINE_VERTEX_SHADER, LineShaders.LINE_FRAGMENT_SHADER);
        this.mvpMatrixButtonsHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "modelViewProjectionMatrix");
        this.vbVertexHandle = GLES20.glGetAttribLocation(this.vbShaderProgramID, "vertexPosition");
        this.lineOpacityHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "opacity");
        this.lineColorHandle = GLES20.glGetUniformLocation(this.vbShaderProgramID, "color");
        this.mPlane = new Plane();
        while (!this.mActivity.bARModelIsLoad) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (Entry entry : this.mActivity.mARModels.entrySet()) {
            ((My3DObject) entry.getValue()).bindTextures();
        }
        this.mActivity.startAutoMoving();
        this.mActivity.loadingDialogHandler.sendEmptyMessage(0);
    }

    private void renderFrame() {
        GLES20.glClear(16640);
        State state = com.vuforia.Renderer.getInstance().begin();
        com.vuforia.Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(2929);
        GLES20.glEnable(2884);
        GLES20.glBlendFunc(770, 771);
        GLES20.glEnable(3042);
        this.listTargetPositiveDimension.clear();
        this.listTargetModelViewMatrix.clear();
        this.listTargetNames.clear();
        for (int i = 0; i < state.getNumTrackableResults(); i++) {
            TrackableResult trackableResult = state.getTrackableResult(i);
            this.listTargetModelViewMatrix.add(Tool.convertPose2GLMatrix(trackableResult.getPose()));
            Vec3F tempTargetPositionDimension = ((ImageTarget) trackableResult.getTrackable()).getSize();
            float[] temp = {0.0f, 0.0f, 0.0f};
            temp[0] = tempTargetPositionDimension.getData()[0] / 2.0f;
            temp[1] = tempTargetPositionDimension.getData()[1] / 2.0f;
            tempTargetPositionDimension.setData(temp);
            this.listTargetPositiveDimension.add(tempTargetPositionDimension);
            this.listTargetNames.add(trackableResult.getTrackable().getName());
        }
        if (this.deleteCurrentPopupTexture) {
            if (this.mObjOverlayTexture != null) {
                GLES20.glDeleteTextures(1, this.mObjOverlayTexture.mTextureID, 0);
                this.mObjOverlayTexture = null;
            }
            this.deleteCurrentPopupTexture = false;
        }
        if (this.renderState == 4) {
            generateProductTextureInOpenGL();
        }
        renderImageTargetRect(state);
        if (state.getNumTrackableResults() > 0) {
            this.mTrackingStarted = true;
            this.framesToSkipBeforeRenderingTransition.set(0);
            TrackableResult trackableResult2 = state.getTrackableResult(0);
            if (trackableResult2 != null) {
                this.pose = trackableResult2.getPose();
                String targetName = trackableResult2.getTrackable().getName().toLowerCase();
                if (this.mActivity.mARModels.containsKey(targetName)) {
                    renderARModel(state, targetName);
                } else if (this.mObjOverlayTexture != null) {
                    renderPopup(state);
                }
            } else {
                return;
            }
        } else {
            if (this.mActivity.bIsStatic3DModel) {
                String lastTargetName = this.mActivity.lastTargetName.toLowerCase();
                if (!lastTargetName.equals("") && this.mActivity.mARModels.containsKey(lastTargetName)) {
                    renderARModel(state, lastTargetName);
                }
            }
            if (this.mScanningMode || !this.mShowAnimation3Dto2D || this.renderState != 0 || this.framesToSkipBeforeRenderingTransition.get() == 0) {
            }
            if (this.framesToSkipBeforeRenderingTransition.get() > 0 && this.renderState == 0) {
                this.framesToSkipBeforeRenderingTransition.decrementAndGet();
            }
        }
        if (this.renderState == 1 && this.mShowAnimation3Dto2D) {
            renderTransitionTo2D();
        }
        if (this.renderState == 2) {
            renderTransitionTo3D();
        }
        GLES20.glDisable(2929);
        GLES20.glDisable(3042);
        com.vuforia.Renderer.getInstance().end();
    }

    private void renderImageTargetRect(State state) {
        if (state.getNumTrackableResults() >= 1) {
            for (int i = 0; i < state.getNumTrackableResults(); i++) {
                Vec3F targetSize = ((ImageTarget) state.getTrackableResult(i).getTrackable()).getSize();
                int imageWidth = (int) targetSize.getData()[0];
                int imageHeight = (int) targetSize.getData()[1];
                float[] vbVertices = {(float) ((-imageWidth) / 2), (float) ((-imageHeight) / 2), 0.0f, (float) (imageWidth / 2), (float) ((-imageHeight) / 2), 0.0f, (float) (imageWidth / 2), (float) ((-imageHeight) / 2), 0.0f, (float) (imageWidth / 2), (float) (imageHeight / 2), 0.0f, (float) (imageWidth / 2), (float) (imageHeight / 2), 0.0f, (float) ((-imageWidth) / 2), (float) (imageHeight / 2), 0.0f, (float) ((-imageWidth) / 2), (float) (imageHeight / 2), 0.0f, (float) ((-imageWidth) / 2), (float) ((-imageHeight) / 2), 0.0f};
                float[] targetModelViewProjection = new float[16];
                Matrix.multiplyMM(targetModelViewProjection, 0, this.vuforiaAppSession.getProjectionMatrix().getData(), 0, Tool.convertPose2GLMatrix(state.getTrackableResult(i).getPose()).getData(), 0);
                GLES20.glUseProgram(this.vbShaderProgramID);
                GLES20.glVertexAttribPointer(this.vbVertexHandle, 3, 5126, false, 0, fillBuffer(vbVertices));
                GLES20.glEnableVertexAttribArray(this.vbVertexHandle);
                GLES20.glLineWidth(2.0f);
                GLES20.glUniform1f(this.lineOpacityHandle, 0.5f);
                GLES20.glUniform3f(this.lineColorHandle, 1.0f, 1.0f, 0.0f);
                GLES20.glUniformMatrix4fv(this.mvpMatrixButtonsHandle, 1, false, targetModelViewProjection, 0);
                GLES20.glDrawArrays(1, 0, 8);
                GLES20.glDisableVertexAttribArray(this.vbVertexHandle);
            }
        }
    }

    public void resetARPose() {
        this.xAngle = 0.0f;
        this.yAngle = 0.0f;
        this.mActivity.startAutoMoving();
    }

    private void renderARModel(State state, String modelName) {
        float[] modelViewMatrix;
        My3DObject my3DObject = (My3DObject) this.mActivity.mARModels.get(modelName);
        if (my3DObject != null) {
            if (!this.mActivity.bIsStatic3DModel) {
                modelViewMatrix = Tool.convertPose2GLMatrix(state.getTrackableResult(0).getPose()).getData();
            } else {
                if (!this.isStatic3DOK) {
                    this.modelViewMatrix3D = Tool.convertPose2GLMatrix(state.getTrackableResult(0).getPose()).getData();
                }
                modelViewMatrix = (float[]) this.modelViewMatrix3D.clone();
            }
            Matrix.scaleM(modelViewMatrix, 0, this.scaleXS * this.mScaleFactor, this.scaleXS * this.mScaleFactor, this.scaleXS * this.mScaleFactor);
            float boundingRadius = my3DObject.getBoundingRadius();
            Matrix.scaleM(modelViewMatrix, 0, 1.0f / boundingRadius, 1.0f / boundingRadius, 1.0f / boundingRadius);
            Matrix.scaleM(modelViewMatrix, 0, my3DObject.getScale(), my3DObject.getScale(), my3DObject.getScale());
            Matrix.rotateM(modelViewMatrix, 0, my3DObject.getXAngle(), 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(modelViewMatrix, 0, my3DObject.getYAngle(), 0.0f, 1.0f, 0.0f);
            if (my3DObject.getXRoateIsEnable()) {
                Matrix.rotateM(modelViewMatrix, 0, this.xAngle, 1.0f, 0.0f, 0.0f);
            }
            Matrix.rotateM(modelViewMatrix, 0, this.yAngle, 0.0f, 1.0f, 0.0f);
            if (!this.mActivity.bIsStatic3DModel) {
                this.projMatrixData3D = this.vuforiaAppSession.getProjectionMatrix().getData();
            } else if (!this.isStatic3DOK) {
                this.isStatic3DOK = true;
                this.projMatrixData3D = this.vuforiaAppSession.getProjectionMatrix().getData();
            }
            float[] modelViewProjection = new float[16];
            Matrix.multiplyMM(modelViewProjection, 0, this.projMatrixData3D, 0, modelViewMatrix, 0);
            GLES20.glUseProgram(this.shaderProgramID);
            GLES20.glUniformMatrix4fv(this.mvpMatrixHandle, 1, false, modelViewProjection, 0);
            for (int i = 0; i < my3DObject.items.size(); i++) {
                My3DItem my3DItem = (My3DItem) my3DObject.items.get(i);
                if (my3DItem.verts != null) {
                    GLES20.glVertexAttribPointer(this.vertexHandle, 3, 5126, false, 12, my3DItem.verts);
                    GLES20.glEnableVertexAttribArray(this.vertexHandle);
                }
                if (my3DItem.norms != null) {
                    GLES20.glVertexAttribPointer(this.normalHandle, 3, 5126, false, 12, my3DItem.norms);
                    GLES20.glEnableVertexAttribArray(this.normalHandle);
                }
                if (my3DItem.textCoords != null) {
                    GLES20.glVertexAttribPointer(this.textureCoordHandle, 3, 5126, false, 12, my3DItem.textCoords);
                    GLES20.glEnableVertexAttribArray(this.textureCoordHandle);
                }
                if (my3DItem.mtlInfo == null || my3DItem.mtlInfo.texture == null) {
                    GLES20.glUniform1f(this.enableTextCoordHandle, 0.1f);
                } else {
                    GLES20.glUniform1f(this.enableTextCoordHandle, 1.0f);
                }
                GLES20.glUniform4f(this.fragColorHandle, my3DItem.mtlInfo.diffuseColor[0], my3DItem.mtlInfo.diffuseColor[1], my3DItem.mtlInfo.diffuseColor[2], my3DItem.mtlInfo.alpha);
                if (!(my3DItem.mtlInfo == null || my3DItem.mtlInfo.texture == null)) {
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, my3DItem.mtlInfo.texture.mTextureID[0]);
                }
                GLES20.glDrawArrays(4, 0, my3DItem.numVerts);
            }
            SampleUtils.checkGLError("Renderer model!");
        }
    }

    private void renderTransitionTo3D() {
        if (this.mStartAnimation2Dto3D) {
            this.transitionDuration = 0.5f;
            this.transition2Dto3D.startTransition(this.transitionDuration, true, true);
            this.mStartAnimation2Dto3D = false;
        } else if (this.mObjOverlayTexture != null) {
            if (this.pose == null) {
                this.pose = this.transition2Dto3D.getFinalPositionMatrix34F();
            }
            this.transition2Dto3D.render(this.vuforiaAppSession.getProjectionMatrix().getData(), this.pose, this.mObjOverlayTexture.mTextureID[0]);
            if (this.transition2Dto3D.transitionFinished()) {
                this.mIsShowing2DOverlay = false;
                this.mShowAnimation3Dto2D = true;
                this.renderState = 0;
            }
        }
    }

    private void renderTransitionTo2D() {
        if (this.mStartAnimation3Dto2D) {
            this.transition3Dto2D.startTransition(this.transitionDuration, false, true);
            this.mStartAnimation3Dto2D = false;
        } else if (this.mObjOverlayTexture != null) {
            if (this.pose == null) {
                this.pose = this.transition2Dto3D.getFinalPositionMatrix34F();
            }
            this.transition3Dto2D.render(this.vuforiaAppSession.getProjectionMatrix().getData(), this.pose, this.mObjOverlayTexture.mTextureID[0]);
            if (this.transition3Dto2D.transitionFinished()) {
                this.mIsShowing2DOverlay = true;
            }
        }
    }

    private void renderPopup(State state) {
        float[] modelViewMatrix = Tool.convertPose2GLMatrix(state.getTrackableResult(0).getPose()).getData();
        Matrix.scaleM(modelViewMatrix, 0, this.scaleXS * this.mScaleFactor, this.scaleXS * this.mScaleFactor, 1.0f);
        float[] modelViewProjection = new float[16];
        Matrix.multiplyMM(modelViewProjection, 0, this.vuforiaAppSession.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);
        GLES20.glUseProgram(this.shaderProgramID);
        if (this.renderState == 0) {
            GLES20.glVertexAttribPointer(this.vertexHandle, 3, 5126, false, 0, this.mPlane.getVertices());
            GLES20.glVertexAttribPointer(this.normalHandle, 3, 5126, false, 0, this.mPlane.getNormals());
            GLES20.glVertexAttribPointer(this.textureCoordHandle, 2, 5126, false, 0, this.mPlane.getTexCoords());
            GLES20.glEnableVertexAttribArray(this.vertexHandle);
            GLES20.glEnableVertexAttribArray(this.normalHandle);
            GLES20.glEnableVertexAttribArray(this.textureCoordHandle);
            GLES20.glUniform1f(this.enableTextCoordHandle, 1.0f);
            GLES20.glActiveTexture(33984);
            try {
                GLES20.glBindTexture(3553, this.mObjOverlayTexture.mTextureID[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            GLES20.glUniformMatrix4fv(this.mvpMatrixHandle, 1, false, modelViewProjection, 0);
            GLES20.glDrawElements(4, 6, 5123, this.mPlane.getIndices());
            GLES20.glDisableVertexAttribArray(this.vertexHandle);
            GLES20.glDisableVertexAttribArray(this.normalHandle);
            GLES20.glDisableVertexAttribArray(this.textureCoordHandle);
        } else if (this.mIsShowing2DOverlay) {
            this.mStartAnimation2Dto3D = true;
            this.mIsShowing2DOverlay = false;
            this.renderState = 2;
        }
    }

    public void generateProductTextureInOpenGL() {
        Texture textureObject = this.mActivity.getmPopupTexture();
        if (textureObject != null) {
            this.mObjOverlayTexture = textureObject;
        }
        GLES20.glGenTextures(1, this.mObjOverlayTexture.mTextureID, 0);
        GLES20.glBindTexture(3553, this.mObjOverlayTexture.mTextureID[0]);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexImage2D(3553, 0, 6408, this.mObjOverlayTexture.mWidth, this.mObjOverlayTexture.mHeight, 0, 6408, 5121, this.mObjOverlayTexture.mData);
        this.renderState = 0;
    }

    private void startTransitionTo2D() {
        if (this.renderState == 0 && this.mTrackingStarted) {
            this.transitionDuration = 0.5f;
            this.renderState = 1;
            this.mStartAnimation3Dto2D = true;
        } else if (this.renderState == 0 && !this.mTrackingStarted && this.mObjOverlayTexture != null) {
            this.transitionDuration = 0.0f;
            this.renderState = 1;
            this.mStartAnimation3Dto2D = true;
        }
    }

    public void stopTransition2Dto3D() {
        this.mStartAnimation2Dto3D = true;
    }

    public void stopTransition3Dto2D() {
        this.mStartAnimation3Dto2D = true;
    }

    public void setDPIScaleIndicator(float dpiSIndicator) {
        this.mDPIScaleIndicator = dpiSIndicator;
    }

    public void setScaleFactor(float f) {
        this.mScaleFactor = f;
    }

    public void setRenderState(int state) {
        this.renderState = state;
    }

    public void setFramesToSkipBeforeRenderingTransition(int framesToSkip) {
        this.framesToSkipBeforeRenderingTransition.set(framesToSkip);
    }

    public void deleteCurrentPopupTexture() {
        this.deleteCurrentPopupTexture = true;
    }

    public void setPopupTexture(Texture texture) {
        this.mObjOverlayTexture = texture;
    }

    public void setScanningMode(boolean scanningMode) {
        this.mScanningMode = scanningMode;
    }

    public void isShowing2DOverlay(boolean b) {
        this.mIsShowing2DOverlay = b;
    }

    public void showAnimation3Dto2D(boolean b) {
        this.mShowAnimation3Dto2D = b;
    }

    public void resetTrackingStarted() {
        this.mTrackingStarted = false;
    }

    public void dealTargetClick(String targetName, float x, float y) {
        if (!this.mActivity.mARModels.containsKey(targetName.toLowerCase())) {
            DisplayMetrics metrics = new DisplayMetrics();
            this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int i = 0;
            while (i < this.listTargetNames.size()) {
                try {
                    Vec3F intersection = SampleMath.getPointToPlaneIntersection(SampleMath.Matrix44FInverse(this.vuforiaAppSession.getProjectionMatrix()), (Matrix44F) this.listTargetModelViewMatrix.get(i), (float) metrics.widthPixels, (float) metrics.heightPixels, new Vec2F(x, y), new Vec3F(0.0f, 0.0f, 0.0f), new Vec3F(0.0f, 0.0f, 1.0f));
                    if (intersection.getData()[0] >= (-((Vec3F) this.listTargetPositiveDimension.get(i)).getData()[0]) && intersection.getData()[0] <= ((Vec3F) this.listTargetPositiveDimension.get(i)).getData()[0] && intersection.getData()[1] >= (-((Vec3F) this.listTargetPositiveDimension.get(i)).getData()[1]) && intersection.getData()[1] <= ((Vec3F) this.listTargetPositiveDimension.get(i)).getData()[1]) {
                        final ObjectInfo object = MyUtility.getObjectFromXML(this.mActivity, (String) this.listTargetNames.get(i));
                        if (object != null) {

                            ResourceMgrTool.getResourceStatus(object.ChapterID, object.ResID, new ResourceMgrTool.ResCallbackListener() {
                                @Override
                                public void resCallback(ResourceStatus status, Object data) {
                                    if (status.equals(ResourceStatus.RESOUCE_DOWNLOADED)) {
                                        if (!ObjectScanRenderer.this.mActivity.bIsGotoDetailPage && object.Type.equalsIgnoreCase("models")) {
                                            MyUtility.gotoDetailPage(ObjectScanRenderer.this.mActivity, object);
                                            ObjectScanRenderer.this.mActivity.bIsGotoDetailPage = true;
                                        } else {
                                            Toast.makeText(ObjectScanRenderer.this.mActivity, "调用播放器", Toast.LENGTH_SHORT);
                                            //region 调用打开资源接口
                                            ResourceMgrTool.playResource(object.ChapterID, object.ResID, new ResourceMgrTool.ResCallbackListener() {
                                                @Override
                                                public void resCallback(ResourceStatus status, Object data) {
                                                    if (!status.equals(ResourceStatus.RESOUCE_NOT_PAY)) {
                                                        ShowDialog(ObjectScanRenderer.this.mActivity, "提示", "资源已经打开");
                                                    } else {
                                                        ShowDialogWithStatus(ObjectScanRenderer.this.mActivity, status, object);
                                                    }
                                                }
                                            });
                                            //endregion
                                        }
                                    } else {
                                        ShowDialogWithStatus(ObjectScanRenderer.this.mActivity, status, object);
                                    }
                                }
                            });
                        }
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void ShowDialogWithStatus(final Context context, final ResourceStatus status, final ObjectInfo object) {
        String title = "提示";
        String msg = "";
        DialogInterface.OnClickListener ok = null;
        DialogInterface.OnClickListener cancel = null;
        int okBtnTxt = 0;
        int cancelBtnTxt = 0;
        switch (status) {
            case RESOUCE_DOWNLOADED:
                break;
            case RESOURCE_DOWNLOADING:
                msg = "资源下载中请稍后";
                break;
            case RESOURCE_NOT_DOWNLOAD:
                msg = "请先下载资源";
                okBtnTxt = R.string.ar_string_dialog_download_Yes;
                cancelBtnTxt = R.string.ar_string_dialog_download_No;
                cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowDialog(context, "提示", "取消下载");
                        //点击取消按钮处理
                        dialog.cancel();
                    }
                };
                ok = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResourceMgrTool.downLoadResource(object.ChapterID, object.ResID, new ResourceMgrTool.ResCallbackListener() {
                            @Override
                            public void resCallback(ResourceStatus status, Object data) {
                                ArResouceResponseBean responseBean = (ArResouceResponseBean) data;
                                ShowDialog(context, "请求下载资源的响应", responseBean.getResourceStatus().toString());
                            }
                        });

                        ResourceMgrTool.gotoResourceDownloadWindow(object.ChapterID, object.ResID, new ResourceMgrTool.ResCallbackListener() {
                            @Override
                            public void resCallback(ResourceStatus status, Object data) {
                                ArResouceResponseBean responseBean = (ArResouceResponseBean) data;
                                ShowDialog(context, "请求跳转到资源下载窗口的响应", responseBean.getResourceStatus().toString());
                            }
                        });
                        //点击确定按钮处理
                        dialog.cancel();
                    }
                };
                break;
            case RESOUCE_NOT_PAY:
                msg = "此资源需要付费";
                okBtnTxt = R.string.ar_string_dialog_pay_Yes;
                cancelBtnTxt = R.string.ar_string_dialog_pay_No;
                cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowDialog(context, "提示", "点击取消付费按钮");
                        //点击取消按钮处理
                        dialog.cancel();
                    }
                };
                ok = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResourceMgrTool.gotoResourceListWindow(object.ChapterID, object.ResID, new ResourceMgrTool.ResCallbackListener() {
                            @Override
                            public void resCallback(ResourceStatus status, Object data) {
                                ArResouceResponseBean responseBean = (ArResouceResponseBean) data;
                                ShowDialog(context, "请求跳转到资源列表的响应", responseBean.getResourceStatus().toString());
                            }
                        });
                        //点击确定按钮处理
                        dialog.cancel();
                    }
                };
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setNegativeButton(cancelBtnTxt, cancel);
        builder.setPositiveButton(okBtnTxt, ok);
        builder.show();
    }

    private static void ShowDialog(Context context, String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }

    private Buffer fillBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array) {
            bb.putFloat(d);
        }
        bb.rewind();
        return bb;
    }
}

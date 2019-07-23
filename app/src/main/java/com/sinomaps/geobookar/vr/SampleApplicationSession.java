package com.sinomaps.geobookar.vr;

import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.OrientationEventListener;

import com.sinomaps.geobookar.R;
import com.vuforia.CameraDevice;
import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Vec2I;
import com.vuforia.VideoBackgroundConfig;
import com.vuforia.VideoMode;
import com.vuforia.Vuforia;
import com.vuforia.Vuforia.UpdateCallbackInterface;

/* renamed from: com.sinomaps.geobookar.vr.SampleApplicationSession */
public class SampleApplicationSession implements UpdateCallbackInterface {
    private static final String LOGTAG = "SampleAppSession";
    /* access modifiers changed from: private */
    public Activity mActivity;
    private int mCamera = 0;
    /* access modifiers changed from: private */
    public boolean mCameraRunning = false;
    private InitVuforiaTask mInitVuforiaTask;
    private boolean mIsPortrait = false;
    /* access modifiers changed from: private */
    public LoadTrackerTask mLoadTrackerTask;
    private Matrix44F mProjectionMatrix;
    private int mScreenHeight = 0;
    private int mScreenWidth = 0;
    /* access modifiers changed from: private */
    public SampleApplicationControl mSessionControl;
    /* access modifiers changed from: private */
    public Object mShutdownLock = new Object();
    /* access modifiers changed from: private */
    public boolean mStarted = false;
    private int[] mViewport;
    /* access modifiers changed from: private */
    public int mVuforiaFlags = 0;

    /* renamed from: com.sinomaps.geobookar.vr.SampleApplicationSession$InitVuforiaTask */
    private class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean> {
        private int mProgressValue;

        private InitVuforiaTask() {
            this.mProgressValue = -1;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... params) {
            Boolean valueOf;
            boolean z = true;
            synchronized (SampleApplicationSession.this.mShutdownLock) {
                Vuforia.setInitParameters(SampleApplicationSession.this.mActivity, SampleApplicationSession.this.mVuforiaFlags, "AXZY2Ln/////AAABmdqWYWnEd0mtg0zp9/zZnFIrq20FyC6CKLvAtEEaCxdguGdIRBF/bNx/ECX7OZq9AnbI4aJOuC7M6T+KfWdP2cnNGD0VrekeOYZ7wQCK1TbdquLVLNHEi/xrFyTAZK2zSVX5M95fK9IXItbZXWflTtyERFqQ4VFkXenJqSgVu2GFlIbKwWUgy7HikTyuTRJjsEUvKO13Y7Oan9HhIkt211cwB/W9e07XRlt26504GXJ2k9B2QrzQi/ZbC+Mu/SiXlCm2EdE3IvmEmb4yIJs5xamkqmPNg7Z9RPIr+HTgePgAfOJ/mIIjd7RdFPWr1LZP7fNLF3e1G0bAJ8c9XSVZSLlf1LXRW3YdUvEEV4gTBU88");
                do {
                   this.mProgressValue = Vuforia.init();
                    publishProgress(new Integer[]{Integer.valueOf(this.mProgressValue)});
                    if (isCancelled() || this.mProgressValue < 0) {
                    }
                } while (this.mProgressValue < 100);
                if (this.mProgressValue <= 0) {
                    z = false;
                }
                valueOf = Boolean.valueOf(z);
            }
            return valueOf;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... values) {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            if (result.booleanValue()) {
                Log.d(SampleApplicationSession.LOGTAG, "InitVuforiaTask.onPostExecute: Vuforia initialization successful");
                if (SampleApplicationSession.this.mSessionControl.doInitTrackers()) {
                    try {
                        SampleApplicationSession.this.mLoadTrackerTask = new LoadTrackerTask();
                        SampleApplicationSession.this.mLoadTrackerTask.execute(new Void[0]);
                    } catch (Exception e) {
                        String logMessage = "Loading tracking data set failed";
                        SampleApplicationException vuforiaException = new SampleApplicationException(3, logMessage);
                        Log.e(SampleApplicationSession.LOGTAG, logMessage);
                        SampleApplicationSession.this.mSessionControl.onInitARDone(vuforiaException);
                    }
                } else {
                    SampleApplicationSession.this.mSessionControl.onInitARDone(new SampleApplicationException(2, "Failed to initialize trackers"));
                }
            } else {
                String logMessage2 = SampleApplicationSession.this.getInitializationErrorString(this.mProgressValue);
                Log.e(SampleApplicationSession.LOGTAG, "InitVuforiaTask.onPostExecute: " + logMessage2 + " Exiting.");
                SampleApplicationSession.this.mSessionControl.onInitARDone(new SampleApplicationException(0, logMessage2));
            }
        }
    }

    /* renamed from: com.sinomaps.geobookar.vr.SampleApplicationSession$LoadTrackerTask */
    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean> {
        private LoadTrackerTask() {
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... params) {
            Boolean valueOf;
            synchronized (SampleApplicationSession.this.mShutdownLock) {
                valueOf = Boolean.valueOf(SampleApplicationSession.this.mSessionControl.doLoadTrackersData());
            }
            return valueOf;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            SampleApplicationException vuforiaException = null;
            Log.d(SampleApplicationSession.LOGTAG, "LoadTrackerTask.onPostExecute: execution " + (result.booleanValue() ? "successful" : "failed"));
            if (!result.booleanValue()) {
                String logMessage = "Failed to load tracker data.";
                Log.e(SampleApplicationSession.LOGTAG, logMessage);
                vuforiaException = new SampleApplicationException(3, logMessage);
            } else {
                System.gc();
                Vuforia.registerCallback(SampleApplicationSession.this);
                SampleApplicationSession.this.mStarted = true;
            }
            SampleApplicationSession.this.mSessionControl.onInitARDone(vuforiaException);
        }
    }

    public SampleApplicationSession(SampleApplicationControl sessionControl) {
        this.mSessionControl = sessionControl;
    }

    public void initAR(Activity activity, int screenOrientation) {
        SampleApplicationException vuforiaException = null;
        this.mActivity = activity;
        if (screenOrientation == 4 && VERSION.SDK_INT > 8) {
            screenOrientation = 10;
        }
        OrientationEventListener orientationEventListener = new OrientationEventListener(this.mActivity) {
            int mLastRotation = -1;

            public void onOrientationChanged(int i) {
                int activityRotation = SampleApplicationSession.this.mActivity.getWindowManager().getDefaultDisplay().getRotation();
                if (this.mLastRotation != activityRotation) {
                    if (SampleApplicationSession.this.mStarted && SampleApplicationSession.this.mCameraRunning) {
                        SampleApplicationSession.this.setProjectionMatrix();
                    }
                    this.mLastRotation = activityRotation;
                }
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        }
        this.mActivity.setRequestedOrientation(screenOrientation);
        updateActivityOrientation();
        storeScreenDimensions();
        this.mActivity.getWindow().setFlags(128, 128);
        this.mVuforiaFlags = 1;
        if (this.mInitVuforiaTask != null) {
            String logMessage = "Cannot initialize SDK twice";
            vuforiaException = new SampleApplicationException(1, logMessage);
            Log.e(LOGTAG, logMessage);
        }
        if (vuforiaException == null) {
            try {
                this.mInitVuforiaTask = new InitVuforiaTask();
                this.mInitVuforiaTask.execute(new Void[0]);
            } catch (Exception e) {
                String logMessage2 = "Initializing Vuforia SDK failed";
                vuforiaException = new SampleApplicationException(0, logMessage2);
                Log.e(LOGTAG, logMessage2);
            }
        }
        if (vuforiaException != null) {
            this.mSessionControl.onInitARDone(vuforiaException);
        }
    }

    public void startAR(int camera) throws SampleApplicationException {
        if (this.mCameraRunning) {
            String error = "Camera already running, unable to open again";
            Log.e(LOGTAG, error);
            throw new SampleApplicationException(6, error);
        }
        this.mCamera = camera;
        if (!CameraDevice.getInstance().init(camera)) {
            String error2 = "Unable to open camera device: " + camera;
            Log.e(LOGTAG, error2);
            throw new SampleApplicationException(6, error2);
        } else if (!CameraDevice.getInstance().selectVideoMode(-1)) {
            String error3 = "Unable to set video mode";
            Log.e(LOGTAG, error3);
            throw new SampleApplicationException(6, error3);
        } else {
            configureVideoBackground();
            if (!CameraDevice.getInstance().start()) {
                String error4 = "Unable to start camera device: " + camera;
                Log.e(LOGTAG, error4);
                throw new SampleApplicationException(6, error4);
            }
            setProjectionMatrix();
            this.mSessionControl.doStartTrackers();
            this.mCameraRunning = true;
            if (!CameraDevice.getInstance().setFocusMode(2) && !CameraDevice.getInstance().setFocusMode(1)) {
                CameraDevice.getInstance().setFocusMode(0);
            }
        }
    }

    public void stopAR() throws SampleApplicationException {
        if (!(this.mInitVuforiaTask == null || this.mInitVuforiaTask.getStatus() == Status.FINISHED)) {
            this.mInitVuforiaTask.cancel(true);
            this.mInitVuforiaTask = null;
        }
        if (!(this.mLoadTrackerTask == null || this.mLoadTrackerTask.getStatus() == Status.FINISHED)) {
            this.mLoadTrackerTask.cancel(true);
            this.mLoadTrackerTask = null;
        }
        this.mInitVuforiaTask = null;
        this.mLoadTrackerTask = null;
        this.mStarted = false;
        stopCamera();
        synchronized (this.mShutdownLock) {
            boolean unloadTrackersResult = this.mSessionControl.doUnloadTrackersData();
            boolean deinitTrackersResult = this.mSessionControl.doDeinitTrackers();
            Vuforia.deinit();
            if (!unloadTrackersResult) {
                throw new SampleApplicationException(4, "Failed to unload trackers' data");
            } else if (!deinitTrackersResult) {
                throw new SampleApplicationException(5, "Failed to deinitialize trackers");
            }
        }
    }

    public void resumeAR() throws SampleApplicationException {
        Vuforia.onResume();
        if (this.mStarted) {
            startAR(this.mCamera);
        }
    }

    public void pauseAR() throws SampleApplicationException {
        if (this.mStarted) {
            stopCamera();
        }
        Vuforia.onPause();
    }

    public Matrix44F getProjectionMatrix() {
        return this.mProjectionMatrix;
    }

    public int[] getViewport() {
        return this.mViewport;
    }

    public void Vuforia_onUpdate(State s) {
        this.mSessionControl.onVuforiaUpdate(s);
    }

    public void onConfigurationChanged() {
        updateActivityOrientation();
        storeScreenDimensions();
        if (isARRunning()) {
            configureVideoBackground();
            setProjectionMatrix();
        }
        Device.getInstance().setConfigurationChanged();
    }

    public void onResume() {
        Vuforia.onResume();
    }

    public void onPause() {
        Vuforia.onPause();
    }

    public void onSurfaceChanged(int width, int height) {
        Vuforia.onSurfaceChanged(width, height);
    }

    public void onSurfaceCreated() {
        Vuforia.onSurfaceCreated();
    }

    /* access modifiers changed from: private */
    public String getInitializationErrorString(int code) {
//        if (code == -2) {
//            return this.mActivity.getString(R.string.INIT_ERROR_DEVICE_NOT_SUPPORTED);
//        }
//        if (code == -3) {
//            return this.mActivity.getString(R.string.INIT_ERROR_NO_CAMERA_ACCESS);
//        }
//        if (code == -4) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_MISSING_KEY);
//        }
//        if (code == -5) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_INVALID_KEY);
//        }
//        if (code == -7) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_NO_NETWORK_TRANSIENT);
//        }
//        if (code == -6) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_NO_NETWORK_PERMANENT);
//        }
//        if (code == -8) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_CANCELED_KEY);
//        }
//        if (code == -9) {
//            return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_PRODUCT_TYPE_MISMATCH);
//        }
//        return this.mActivity.getString(R.string.INIT_LICENSE_ERROR_UNKNOWN_ERROR);
        return "错错错错错";
    }

    private void storeScreenDimensions() {
        Point size = new Point();
        this.mActivity.getWindowManager().getDefaultDisplay().getRealSize(size);
        this.mScreenWidth = size.x;
        this.mScreenHeight = size.y;
    }

    private void updateActivityOrientation() {
        switch (this.mActivity.getResources().getConfiguration().orientation) {
            case 1:
                this.mIsPortrait = true;
                break;
            case 2:
                this.mIsPortrait = false;
                break;
        }
        Log.i(LOGTAG, "Activity is in " + (this.mIsPortrait ? "PORTRAIT" : "LANDSCAPE"));
    }

    public void setProjectionMatrix() {
        this.mProjectionMatrix = Tool.getProjectionGL(CameraDevice.getInstance().getCameraCalibration(), 1.0f, 5000.0f);
    }

    public void stopCamera() {
        if (this.mCameraRunning) {
            this.mSessionControl.doStopTrackers();
            this.mCameraRunning = false;
            CameraDevice.getInstance().stop();
            CameraDevice.getInstance().deinit();
        }
    }

    private void configureVideoBackground() {
        int xSize;
        int ySize;
        VideoMode vm = CameraDevice.getInstance().getVideoMode(-1);
        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setPosition(new Vec2I(0, 0));
        if (this.mIsPortrait) {
            xSize = (int) (((float) vm.getHeight()) * (((float) this.mScreenHeight) / ((float) vm.getWidth())));
            ySize = this.mScreenHeight;
            if (xSize < this.mScreenWidth) {
                xSize = this.mScreenWidth;
                ySize = (int) (((float) this.mScreenWidth) * (((float) vm.getWidth()) / ((float) vm.getHeight())));
            }
        } else {
            xSize = this.mScreenWidth;
            ySize = (int) (((float) vm.getHeight()) * (((float) this.mScreenWidth) / ((float) vm.getWidth())));
            if (ySize < this.mScreenHeight) {
                xSize = (int) (((float) this.mScreenHeight) * (((float) vm.getWidth()) / ((float) vm.getHeight())));
                ySize = this.mScreenHeight;
            }
        }
        config.setSize(new Vec2I(xSize, ySize));
        this.mViewport = new int[4];
        this.mViewport[0] = ((this.mScreenWidth - xSize) / 2) + config.getPosition().getData()[0];
        this.mViewport[1] = ((this.mScreenHeight - ySize) / 2) + config.getPosition().getData()[1];
        this.mViewport[2] = xSize;
        this.mViewport[3] = ySize;
        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth() + " , " + vm.getHeight() + "), Screen (" + this.mScreenWidth + " , " + this.mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");
        Renderer.getInstance().setVideoBackgroundConfig(config);
    }

    private boolean isARRunning() {
        return this.mStarted;
    }
}

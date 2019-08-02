package com.sinomaps.geobookar.ar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sinomaps.geobookar.R;
import com.sinomaps.geobookar.model.ObjectInfo;
import com.sinomaps.geobookar.opengl.My3DObject;
import com.sinomaps.geobookar.ui.BaseActivity;
import com.sinomaps.geobookar.ui.LoadingDialogHandler;
import com.sinomaps.geobookar.utility.MyUtility;
import com.sinomaps.geobookar.vr.SampleApplicationControl;
import com.sinomaps.geobookar.vr.SampleApplicationException;
import com.sinomaps.geobookar.vr.SampleApplicationGLView;
import com.sinomaps.geobookar.vr.SampleApplicationSession;
import com.sinomaps.geobookar.vr.Texture;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/* renamed from: com.sinomaps.geobookar.ar.ObjectScanActivity */
public class ObjectScanActivity extends BaseActivity implements SampleApplicationControl {
    static final int HIDE_2D_OVERLAY = 0;
    static final int HIDE_LOADING_DIALOG = 0;
    private static final int INVALID_POINTER_ID = -1;
    private static final int POPUP_IS_DISPLAYED = 1;
    private static final int POPUP_NOT_DISPLAYED = 0;
    static final int SHOW_2D_OVERLAY = 1;
    static final int SHOW_LOADING_DIALOG = 1;
    private static final String TAG = "ObjectScanActivity";
    private static int mTextureSize = 512;
    private final float TOUCH_SCALE_FACTOR = 0.17914012f;
    private int activePointerId = -1;
    private int activePointerId2 = -1;
    public boolean bARModelIsLoad = false;
    public boolean bIsGotoDetailPage = false;
    public boolean bIsStatic3DModel = false;
    public String lastTargetName = "";
    public LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    public HashMap<String, My3DObject> mARModels = new HashMap<>();
    /* access modifiers changed from: private */
    public Activity mActivity = null;
    public Thread mAutoMovingThead;
    public float mAutoSpeed = 10.0f;
    /* access modifiers changed from: private */
//    public Button mCloseButton;
    private DataSet mCurrentDataset;
    /* access modifiers changed from: private */
    public SampleApplicationGLView mGlView;
    /* access modifiers changed from: private */
    public boolean mIsAutoMoving = true;
    boolean mIsDroidDevice = false;
    /* access modifiers changed from: private */
    public int mPopupStatus = 0;
    private Texture mPopupTexture;
    /* access modifiers changed from: private */
    public ObjectScanRenderer mRenderer;
    private GestureDetector mRotateGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RelativeLayout mUILayout;
    private float mdpiScaleIndicator;
    private PointF mid;
    private PointF midOld;
    private Handler overlay2DHandler = new Overlay2dHandler(this);
    private PointF pointer1;
    private PointF pointer2;
    SampleApplicationSession vuforiaAppSession;
    private float xOld = 0.0f;
    private float yOld = 0.0f;

    /* renamed from: com.sinomaps.geobookar.ar.ObjectScanActivity$MyGestureListener */
    private class MyGestureListener extends SimpleOnGestureListener {
        private final Handler autofocusHandler;

        private MyGestureListener() {
            this.autofocusHandler = new Handler();
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            this.autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    if (!CameraDevice.getInstance().setFocusMode(1)) {
                        Log.e("SingleTapUp", "Unable to trigger focus");
                    }
                }
            }, 1000);
            return true;
        }
    }

    /* renamed from: com.sinomaps.geobookar.ar.ObjectScanActivity$MyRotateGestureListener */
    private class MyRotateGestureListener extends SimpleOnGestureListener {
        private MyRotateGestureListener() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            My3DObject my3DObject = ObjectScanActivity.this.getDisplayARModel(ObjectScanActivity.this.lastTargetName);
            if (my3DObject != null) {
                my3DObject.setScale(my3DObject.getScale() * 2.0f);
                ObjectScanActivity.this.mGlView.requestRender();
            }
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            ObjectScanActivity.this.mIsAutoMoving = true;
            if (velocityX < 0.0f) {
                ObjectScanActivity.this.mAutoSpeed = -Math.abs(ObjectScanActivity.this.mAutoSpeed);
            } else if (velocityX > 0.0f) {
                ObjectScanActivity.this.mAutoSpeed = Math.abs(ObjectScanActivity.this.mAutoSpeed);
            }
            return true;
        }
    }

    /* renamed from: com.sinomaps.geobookar.ar.ObjectScanActivity$MyScaleGestureListener */
    private class MyScaleGestureListener implements OnScaleGestureListener {
        private MyScaleGestureListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            My3DObject my3DObject = ObjectScanActivity.this.getDisplayARModel(ObjectScanActivity.this.lastTargetName);
            if (my3DObject != null) {
                my3DObject.setScale(my3DObject.getScale() * detector.getScaleFactor());
                ObjectScanActivity.this.mGlView.requestRender();
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    /* renamed from: com.sinomaps.geobookar.ar.ObjectScanActivity$Overlay2dHandler */
    static class Overlay2dHandler extends Handler {
        private final WeakReference<ObjectScanActivity> wrf;

        Overlay2dHandler(ObjectScanActivity objectScanActivity) {
            this.wrf = new WeakReference<>(objectScanActivity);
        }

        public void handleMessage(Message msg) {
            ObjectScanActivity activity = (ObjectScanActivity) this.wrf.get();
//            if (!(activity == null || activity.mCloseButton == null || msg.what != 1)) {
//            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("CurBookID","0a2a9b0aaf7449f4a9a9852542eea52c");//外部传入当前书的文件夹名称
        editor.commit();
        this.pointer1 = new PointF();
        this.pointer2 = new PointF();
        this.mid = new PointF();
        this.midOld = new PointF();
        this.mActivity = this;
        this.vuforiaAppSession = new SampleApplicationSession(this);
        startLoadingAnimation();
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            int mLastRotation = -1;

            public void onOrientationChanged(int orientation) {
                int activityRotaion = ObjectScanActivity.this.mActivity.getWindowManager().getDefaultDisplay().getRotation();
                if (this.mLastRotation != activityRotaion) {
                    ObjectScanActivity.this.vuforiaAppSession.setProjectionMatrix();
                    this.mLastRotation = activityRotaion;
                }
            }
        };
        int orientation = 10;
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientation = 0;
        }
        this.vuforiaAppSession.initAR(this, orientation);
        new Thread(new Runnable() {
            public void run() {
                ObjectScanActivity.this.loadARModels();
                ObjectScanActivity.this.bARModelIsLoad = true;
            }
        }).start();
        new GestureDetector(this, new MyGestureListener());
        this.mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
        this.mRotateGestureDetector = new GestureDetector(this, new MyRotateGestureListener());
        this.mdpiScaleIndicator = getApplicationContext().getResources().getDisplayMetrics().density;
        this.mIsDroidDevice = Build.MODEL.toLowerCase().startsWith("droid");
    }

    private View getLeafView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View result = getLeafView(vg.getChildAt(i));
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
        Log.v("longyuntao", "Found leaf view");
        return view;
    }

    /* access modifiers changed from: private */
    public void loadARModels() {
        try {
            String projectBaseFolder = MyUtility.getProjectBathPath(this);
            String configFile = projectBaseFolder + "Basic/ARModels.xml";
            File file = new File(configFile);
            if (!file.exists()) {
                Log.v("longyuntao", "ARModels文件未找到！");
                return;
            }
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
            FileInputStream fileInputStream = new FileInputStream(configFile);
            xmlParser.setInput(fileInputStream, "UTF-8");
            for (int eventType = xmlParser.getEventType(); eventType != 1; eventType = xmlParser.next()) {
                if (eventType == 2 && xmlParser.getName().equals("model")) {
                    String resid=xmlParser.getAttributeValue(null, "resid");
                    String name = xmlParser.getAttributeValue(null, "name");
                    String src = xmlParser.getAttributeValue(null, "src");
                    String strXAngle = xmlParser.getAttributeValue(null, "xAngle");
                    String strYAngle = xmlParser.getAttributeValue(null, "yAngle");
                    String strScale = xmlParser.getAttributeValue(null, "scale");
                    String strIsEnableXRotate = xmlParser.getAttributeValue(null, "isEnableXRotate");
                    My3DObject my3DObject = new My3DObject();
                    if (new File(projectBaseFolder + src + ".dat").exists()) {
                        my3DObject.loadDatFile(this, projectBaseFolder + src);
                    } else {
                        my3DObject.loadObjFile(this, projectBaseFolder + src);
                    }
                    if (strXAngle != null) {
                        my3DObject.setXAngle(Float.parseFloat(strXAngle));
                    }
                    if (strYAngle != null) {
                        my3DObject.setYAngle(Float.parseFloat(strYAngle));
                    }
                    if (strScale != null) {
                        my3DObject.setScale(Float.parseFloat(strScale));
                    }
                    if (strIsEnableXRotate != null && strIsEnableXRotate.equals("0")) {
                        my3DObject.setXRotateEnable(false);
                    }
                    this.mARModels.put(name.toLowerCase(), my3DObject);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public My3DObject getDisplayARModel(String targetName) {
        if (this.mARModels.containsKey(targetName.toLowerCase())) {
            return (My3DObject) this.mARModels.get(targetName.toLowerCase());
        }
        return null;
    }

    public void startAutoMoving() {
        if (this.mAutoMovingThead == null) {
            this.mAutoMovingThead = new Thread(new Runnable() {
                public void run() {
                    while (ObjectScanActivity.this.mRenderer != null) {
                        try {
                            if (ObjectScanActivity.this.mIsAutoMoving) {
                                ObjectScanActivity.this.mRenderer.yAngle += ObjectScanActivity.this.mAutoSpeed * 0.17914012f;
                                Thread.sleep(40);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });
            this.mAutoMovingThead.start();
        }
        this.mIsAutoMoving = true;
    }

    public void stopAutoMoving() {
        this.mIsAutoMoving = false;
    }

    private void initStateVariables() {
        this.mRenderer.setRenderState(5);
        this.mRenderer.setPopupTexture(null);
        this.mRenderer.setScanningMode(true);
        this.mRenderer.isShowing2DOverlay(false);
        this.mRenderer.showAnimation3Dto2D(false);
        this.mRenderer.stopTransition3Dto2D();
        this.mRenderer.stopTransition2Dto3D();
        cleanTargetTrackedId();
    }

    private void startLoadingAnimation() {
        this.mUILayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.camera_overlay, null);
        this.mUILayout.setVisibility(View.VISIBLE);
        this.mUILayout.setBackgroundColor(-16777216);
        this.loadingDialogHandler.mLoadingDialogContainer = this.mUILayout.findViewById(R.id.loading_layout);
        this.loadingDialogHandler.mLoadingDialogContainer.setVisibility(View.VISIBLE);
        addContentView(this.mUILayout, new LayoutParams(-1, -1));
        this.loadingDialogHandler.sendEmptyMessage(1);
//        this.mCloseButton = (Button) this.mUILayout.findViewById(R.id.overlay_close_button);
//        this.mCloseButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                ObjectScanActivity.this.mPopupStatus = 0;
//                ObjectScanActivity.this.loadingDialogHandler.sendEmptyMessage(0);
//                ObjectScanActivity.this.enterScanningMode();
//            }
//        });
        hide2DOverlay();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        try {
            this.vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        if (this.mGlView != null) {
            this.mGlView.setVisibility(View.VISIBLE);
            this.mGlView.onResume();
        }
        this.mPopupStatus = 0;
        hide2DOverlay();
        this.bIsGotoDetailPage = false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        this.vuforiaAppSession.onConfigurationChanged();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        try {
            this.vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        if (this.mRenderer != null) {
            this.mRenderer.deleteCurrentPopupTexture();
            initStateVariables();
        }
        if (this.mGlView != null) {
            this.mGlView.setVisibility(View.INVISIBLE);
            this.mGlView.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        try {
            this.vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(TAG, e.getString());
        }
        destroyCommunicateResource();
    }

    public boolean doInitTrackers() {
        if (TrackerManager.getInstance().initTracker(ObjectTracker.getClassType()) == null) {
            Log.e(TAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            return false;
        }
        Log.i(TAG, "Tracker successfully initialized");
        return true;
    }

    public boolean doLoadTrackersData() {
        ObjectTracker imageTracker = (ObjectTracker) TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (imageTracker == null) {
            return false;
        }
        if (this.mCurrentDataset == null) {
            this.mCurrentDataset = imageTracker.createDataSet();
        }
        if (this.mCurrentDataset == null || !this.mCurrentDataset.load(MyUtility.getProjectBathPath(this) + "Basic/QCARConfig.xml", 2) || !imageTracker.activateDataSet(this.mCurrentDataset)) {
            return false;
        }
        int numTrackables = this.mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = this.mCurrentDataset.getTrackable(count);
            trackable.setUserData("Current Dataset : " + trackable.getName());
            Log.d(TAG, "UserData:Set the following user data " + trackable.getUserData());
        }
        return true;
    }

    public boolean doStartTrackers() {
        Tracker imageTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (imageTracker != null) {
            imageTracker.start();
        }
        return true;
    }

    public boolean doStopTrackers() {
        Tracker imageTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (imageTracker != null) {
            imageTracker.stop();
        }
        return true;
    }

    public boolean doUnloadTrackersData() {
        boolean result = true;
        ObjectTracker imageTracker = (ObjectTracker) TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (imageTracker == null) {
            return false;
        }
        if (this.mCurrentDataset != null && this.mCurrentDataset.isActive()) {
            if (imageTracker.getActiveDataSet().equals(this.mCurrentDataset) && !imageTracker.deactivateDataSet(this.mCurrentDataset)) {
                result = false;
            } else if (!imageTracker.destroyDataSet(this.mCurrentDataset)) {
                result = false;
            }
            this.mCurrentDataset = null;
        }
        return result;
    }

    public boolean doDeinitTrackers() {
        TrackerManager.getInstance().deinitTracker(ObjectTracker.getClassType());
        return true;
    }

    @Override
    public void onInitARDone(SampleApplicationException exception) {
        if (exception == null) {
            boolean translucent = Vuforia.requiresAlpha();
            this.mGlView = new SampleApplicationGLView(this);
            this.mGlView.init(translucent, 16, 0);
            this.mRenderer = new ObjectScanRenderer(this, this.vuforiaAppSession);
            this.mGlView.setRenderer(this.mRenderer);
            setDeviceDPIScaleFactor(this.mdpiScaleIndicator);
            initStateVariables();
            this.mRenderer.mIsActive = true;
            addContentView(this.mGlView, new LayoutParams(-1, -1));
            this.mUILayout.bringToFront();
            this.mUILayout.setBackgroundColor(0);
            try {
                this.vuforiaAppSession.startAR(0);
            } catch (SampleApplicationException e) {
                Log.e(TAG, e.getString());
            }
            CameraDevice.getInstance().setFocusMode(2);
            return;
        }
        Log.e(TAG, exception.getString());
        finish();
    }

    public void onVuforiaUpdate(State state) {
        Vuforia.setHint(0, 10);
        if (state.getNumTrackableResults() > 0) {
            String targetName = state.getTrackableResult(0).getTrackable().getName();
            if (!targetName.equalsIgnoreCase(this.lastTargetName)) {
                this.mRenderer.resetARPose();
                this.mRenderer.deleteCurrentPopupTexture();
                this.mRenderer.setRenderState(3);
                this.mPopupTexture = createPopupTexture(targetName);
                this.lastTargetName = targetName;
                this.mRenderer.isStatic3DOK = false;
                return;
            }
            this.mRenderer.setFramesToSkipBeforeRenderingTransition(10);
            this.mRenderer.showAnimation3Dto2D(true);
            this.mRenderer.resetTrackingStarted();
            enterContentMode();
        }
    }

    public void enterContentMode() {
        this.mPopupStatus = 1;
        show2DOverlay();
        this.mRenderer.setScanningMode(false);
    }

    /* access modifiers changed from: private */
    public void enterScanningMode() {
        hide2DOverlay();
        this.mRenderer.setScanningMode(true);
        this.mRenderer.showAnimation3Dto2D(false);
        this.mRenderer.isShowing2DOverlay(false);
        this.mRenderer.setRenderState(5);
    }

    public void show2DOverlay() {
        this.overlay2DHandler.sendEmptyMessage(1);
    }

    public void hide2DOverlay() {
        this.overlay2DHandler.sendEmptyMessage(0);
    }

    public void setDeviceDPIScaleFactor(float dpiSIndicator) {
        this.mRenderer.setDPIScaleIndicator(dpiSIndicator);
        if (dpiSIndicator <= 1.0f) {
            this.mRenderer.setScaleFactor(1.6f);
        } else if (dpiSIndicator <= 1.5f) {
            this.mRenderer.setScaleFactor(1.3f);
        } else if (dpiSIndicator <= 2.0f) {
            this.mRenderer.setScaleFactor(1.0f);
        } else {
            this.mRenderer.setScaleFactor(0.6f);
        }
    }

    private Texture createPopupTexture(String name) {
        if (this.mPopupTexture != null) {
            this.mPopupTexture = null;
            System.gc();
        }
        ObjectOverlayView objectOverlayView = new ObjectOverlayView(this);
        ObjectInfo object = MyUtility.getObjectFromXML(this, name);
        objectOverlayView.setThumbImg(null);
        if (object != null) {
            objectOverlayView.setTitle(object.Name);
            String thumbPath = MyUtility.getProjectBathPath(this) + "basic/thumb/" + object.ID + ".jpg";
            if (new File(thumbPath).exists()) {
                objectOverlayView.setThumbImg(BitmapFactory.decodeFile(thumbPath));
            }
        } else {
            objectOverlayView.setTitle(name);
        }
        objectOverlayView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        objectOverlayView.measure(MeasureSpec.makeMeasureSpec(mTextureSize, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(mTextureSize, MeasureSpec.AT_MOST));
        objectOverlayView.layout(0, 0, objectOverlayView.getMeasuredWidth(), objectOverlayView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(mTextureSize, mTextureSize, Config.ARGB_8888);
        objectOverlayView.draw(new Canvas(bitmap));
        System.gc();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[(bitmap.getWidth() * bitmap.getHeight())];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();
        System.gc();
        Texture texture = Texture.loadTextureFromIntBuffer(data, width, height);
        System.gc();
        this.mRenderer.setRenderState(4);
        return texture;
    }

    private void saveBitmap(Bitmap bmp, String targetName) {
        String fileName = MyUtility.getProjectBathPath(this) + targetName + ".jpg";
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bmp.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.v("longyuntao", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Texture getmPopupTexture() {
        return this.mPopupTexture;
    }

    public void cleanTargetTrackedId() {
        synchronized (this.lastTargetName) {
            this.lastTargetName = "";
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_scan, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan_model_static) {
            this.bIsStatic3DModel = !this.bIsStatic3DModel;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onTouchEvent(MotionEvent e) {
        stopAutoMoving();
        if (this.mPopupStatus == 0) {
            if (!CameraDevice.getInstance().setFocusMode(1)) {
                Log.e(TAG, "Unable to trigger focus");
            }
        } else if (this.mPopupStatus == 1) {
            this.mRenderer.dealTargetClick(this.lastTargetName, e.getX(), e.getY());
        }
        int action = e.getAction();
        int numPointers = e.getPointerCount();
        if (numPointers == 2) {
            this.mScaleGestureDetector.onTouchEvent(e);
            return true;
        }
        if (numPointers == 1) {
            this.mRotateGestureDetector.onTouchEvent(e);
        }
        int pointerIndex = e.getActionIndex();
        int pointerId = e.getPointerId(pointerIndex);
        switch (action & 255) {
            case 0:
                PointF pointF = this.pointer1;
                float x = e.getX(pointerIndex);
                pointF.x = x;
                PointF pointF2 = this.pointer1;
                float y = e.getY(pointerIndex);
                pointF2.y = y;
                this.xOld = x;
                this.yOld = y;
                this.midOld.x = this.mid.x;
                this.midOld.y = this.mid.y;
                this.activePointerId = e.getPointerId(pointerIndex);
                break;
            case 1:
                this.activePointerId = -1;
                this.midOld.x = this.mid.x;
                this.midOld.y = this.mid.y;
                break;
            case 2:
                float x2 = this.xOld;
                float y2 = this.yOld;
                if (e.getPointerCount() < 2) {
                    x2 = e.getX(pointerIndex);
                    y2 = e.getY(pointerIndex);
                } else if (e.getPointerCount() == 2) {
                }
                if (e.getPointerCount() == 1) {
                    float dx = x2 - this.xOld;
                    float dy = y2 - this.yOld;
                    My3DObject my3DObject = getDisplayARModel(this.lastTargetName);
                    if (my3DObject != null) {
                        this.mRenderer.yAngle += ((0.17914012f * dx) / my3DObject.getScale()) % 360.0f;
                        this.mRenderer.xAngle += ((0.17914012f * dy) / my3DObject.getScale()) % 360.0f;
                    }
                    if (this.mGlView != null) {
                        this.mGlView.requestRender();
                    }
                }
                this.xOld = x2;
                this.yOld = y2;
                this.midOld.x = this.mid.x;
                this.midOld.y = this.mid.y;
                break;
            case 3:
                this.activePointerId = -1;
                this.activePointerId2 = -1;
                break;
            case 5:
                if (numPointers <= 2) {
                    this.pointer2.x = e.getX(pointerIndex);
                    this.pointer2.y = e.getY(pointerIndex);
                    this.activePointerId2 = e.getPointerId(pointerIndex);
                    this.mid.x = (this.pointer1.x + this.pointer2.x) / 2.0f;
                    this.mid.y = (this.pointer1.y + this.pointer2.y) / 2.0f;
                    break;
                }
                break;
            case 6:
                if (numPointers <= 2) {
                    if (pointerId != this.activePointerId) {
                        if (pointerId == this.activePointerId2) {
                            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            this.xOld = e.getX(newPointerIndex);
                            this.yOld = e.getY(newPointerIndex);
                            this.activePointerId = e.getPointerId(newPointerIndex);
                            this.activePointerId2 = -1;
                            break;
                        }
                    } else {
                        int newPointerIndex2 = pointerIndex == 0 ? 1 : 0;
                        this.xOld = e.getX(newPointerIndex2);
                        this.yOld = e.getY(newPointerIndex2);
                        this.activePointerId = e.getPointerId(newPointerIndex2);
                        this.activePointerId2 = -1;
                        break;
                    }
                }
                break;
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        confirmExit(this);
        return true;
    }

    public static void confirmExit(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.yt_string_dialog_exit_msg));
        builder.setTitle(activity.getResources().getString(R.string.yt_string_dialog_exit_title));
        builder.setPositiveButton(activity.getResources().getString(R.string.yt_string_dialog_exit_Yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.yt_string_dialog_exit_No), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

//region 与资源模块通信
    private static final int MSG_GET_RESOUCE_FILE = 0x1100;
    private static final int MSG_GET_RESOUCE_TYPE = 0x1101;//下载还是未下载
    private Messenger mService;
    private boolean isConn;
    private void bindServiceInvoked()
    {
        Intent intent = new Intent();
        intent.setAction("com.mainbo.ztec.resouce");
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }


    private void destroyCommunicateResource()
    {
//        unbindService(mConn);
    }
    private Messenger mMessenger = new Messenger(new Handler()
    {
        @Override
        public void handleMessage(Message msgFromServer)
        {
            switch (msgFromServer.what)
            {
//                case MSG_SUM:
//                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });


    private ServiceConnection mConn = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = new Messenger(service);
            isConn = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
            isConn = false;

        }
    };

    private void send2ebook(){
//        Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
//        msgFromClient.replyTo = mMessenger;
//        if (isConn)
//        {
//            //往服务端发送消息
//            mService.send(msgFromClient);
//        }
    }

    //endregion
}

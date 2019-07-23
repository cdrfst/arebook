package com.sinomaps.geobookar.ui;

import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

/* renamed from: com.sinomaps.geobookar.ui.MyGLSurfaceView */
public class MyGLSurfaceView extends GLSurfaceView {
    public static final double TOUCH_SCALE_FACTOR = 0.17904931097838225d;
    private PointF lastPt = new PointF();
    /* access modifiers changed from: private */
    public MyGLRenderer mRenderer;
    private ScaleGestureDetector mScaleGestureDetector;
    private int pointerId;

    public MyGLSurfaceView(ResModelActivity activity) {
        super(activity);
        initSurfaceView(activity);
    }

    private void initSurfaceView(ResModelActivity activity) {
        setEGLContextClientVersion(2);
        this.mRenderer = new MyGLRenderer(activity);
        setRenderer(this.mRenderer);
        setRenderMode(0);
        this.mScaleGestureDetector = new ScaleGestureDetector(getContext(), new OnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor();
                MyGLRenderer access$000 = MyGLSurfaceView.this.mRenderer;
                access$000.mScale *= scale;
                MyGLSurfaceView.this.requestRender();
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        int numPointers = e.getPointerCount();
        if (numPointers == 2) {
            this.mScaleGestureDetector.onTouchEvent(e);
        }
        switch (action & 255) {
            case 0:
                this.pointerId = e.getPointerId(0);
                this.lastPt.set(e.getX(), e.getY());
                break;
            case 2:
                if (numPointers == 1 && e.getPointerId(0) == this.pointerId) {
                    float dx = e.getX() - this.lastPt.x;
                    float dy = e.getY() - this.lastPt.y;
                    MyGLRenderer myGLRenderer = this.mRenderer;
                    myGLRenderer.xAngle = (float) (((double) myGLRenderer.xAngle) + (((((double) dy) * 0.17904931097838225d) / ((double) this.mRenderer.mScale)) % 360.0d));
                    if (this.mRenderer.xAngle > 90.0f) {
                        this.mRenderer.xAngle = 90.0f;
                    }
                    if (this.mRenderer.xAngle < -90.0f) {
                        this.mRenderer.xAngle = -90.0f;
                    }
                    MyGLRenderer myGLRenderer2 = this.mRenderer;
                    myGLRenderer2.yAngle = (float) (((double) myGLRenderer2.yAngle) + (((((double) dx) * 0.17904931097838225d) / ((double) this.mRenderer.mScale)) % 360.0d));
                    requestRender();
                } else if (numPointers == 2 && e.getPointerId(0) == this.pointerId) {
                    float dx2 = e.getX() - this.lastPt.x;
                    float dy2 = e.getY() - this.lastPt.y;
                    this.mRenderer.xDelta += dx2 / this.mRenderer.mScale;
                    this.mRenderer.yDelta += dy2 / this.mRenderer.mScale;
                    requestRender();
                }
                this.lastPt.set(e.getX(), e.getY());
                break;
        }
        return true;
    }
}

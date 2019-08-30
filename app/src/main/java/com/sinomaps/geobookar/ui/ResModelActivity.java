package com.sinomaps.geobookar.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;

import com.sinomaps.geobookar.R;
import com.sinomaps.geobookar.model.ModelInfo;
import com.sinomaps.geobookar.model.ObjectInfo;
import com.sinomaps.geobookar.opengl.My3DObject;
import com.sinomaps.geobookar.utility.MyUtility;

/* renamed from: com.sinomaps.geobookar.ui.ResModelActivity */
public class ResModelActivity extends BaseActivity {
    public final LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    public My3DObject m3DModel = null;
    /* access modifiers changed from: private */
    public FrameLayout mContainer;
    public MyGLSurfaceView mModelView;
    /* access modifiers changed from: private */
    public ObjectInfo mObject;
    private RadioGroup mRadioGroupModels;
    /* access modifiers changed from: private */
    public HorizontalScrollView mScrollViewSegment;
    private RelativeLayout mUILayout;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_model);
        this.mObject = (ObjectInfo) getIntent().getSerializableExtra("Object");
        if (this.mObject != null && this.mObject.models.size() != 0) {
            String firstModelUri = MyUtility.getDataBathPath(this) + ((ModelInfo) this.mObject.models.get(0)).Src;
            if (firstModelUri.endsWith("\\")) {
                firstModelUri = firstModelUri + ((ModelInfo) this.mObject.models.get(0)).Name;
            }
            if (!MyUtility.checkResourceIsExist(this, firstModelUri + ".dat")) {
                finish();
                return;
            }
            this.mContainer = (FrameLayout) findViewById(R.id.container);
            this.mScrollViewSegment = (HorizontalScrollView) findViewById(R.id.hsv_segment);
            this.mRadioGroupModels = (RadioGroup) findViewById(R.id.rg_models);
            initRadioGroups();
            initEvents();
            displayModel(0);
        }
    }

    private void initRadioGroups() {
        for (int i = 0; i < this.mObject.models.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(((ModelInfo) this.mObject.models.get(i)).Name);
            rb.setTag(Integer.valueOf(i));
            if (i == 0) {
                rb.setChecked(true);
//                rb.setBackgroundResource(R.drawable.dragonmap_selector_bg_scene_groupitem_first);
            } else if (i == this.mObject.models.size() - 1) {
//                rb.setBackgroundResource(R.drawable.dragonmap_selector_bg_scene_groupitem_last);
            } else {
//                rb.setBackgroundResource(R.drawable.dragonmap_selector_bg_scene_groupitem);
            }
            if (this.mObject.models.size() == 1) {
//                rb.setBackgroundResource(R.drawable.dragonmap_selector_bg_scene_groupitem_single);
            }
//            rb.setTextSize(2, getResources().getDimension(R.dimen.res_model_tab_text_size));
//            rb.setTextColor(getResources().getColorStateList(R.color.segment_color));
            rb.setEllipsize(TruncateAt.MARQUEE);
            rb.setGravity(17);
            rb.setPadding(30, 10, 30, 10);
            rb.setLayoutParams(new LayoutParams(100, -2));
            rb.setButtonDrawable(new ColorDrawable(0));
            rb.setSingleLine(true);
            this.mRadioGroupModels.addView(rb);
        }
        this.mRadioGroupModels.check(this.mRadioGroupModels.getChildAt(0).getId());
    }

    private void initEvents() {
        Display d = getWindowManager().getDefaultDisplay();
        d.getMetrics(new DisplayMetrics());
        final int screenHalf = d.getWidth() / 2;
        this.mRadioGroupModels.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) ResModelActivity.this.findViewById(checkedId);
                ResModelActivity.this.mScrollViewSegment.smoothScrollBy((rb.getLeft() - ResModelActivity.this.mScrollViewSegment.getScrollX()) - screenHalf, 0);
                ResModelActivity.this.displayModel(Integer.parseInt(rb.getTag().toString()));
            }
        });
    }

    /* access modifiers changed from: private */
    public void displayModel(final int position) {
        if (this.mModelView != null) {
            this.mContainer.removeView(this.mModelView);
            this.mModelView = null;
        }
        if (this.mUILayout != null) {
//            ((ViewGroup) findViewById(16908290)).removeView(this.mUILayout);
        }
        startLoadingAnimation();
        this.m3DModel = new My3DObject();
        new Thread(new Runnable() {
            public void run() {
                try {
                    ModelInfo curModel = (ModelInfo) ResModelActivity.this.mObject.models.get(position);
                    String modelPath = MyUtility.getDataBathPath(ResModelActivity.this) + curModel.Src;
                    if (curModel.Src.endsWith("\\")) {
                        modelPath = modelPath + curModel.Name;
                    }
                    ResModelActivity.this.m3DModel.loadDatFile(ResModelActivity.this, modelPath);
                    ResModelActivity.this.m3DModel.setXAngle(curModel.XAngle);
                    ResModelActivity.this.m3DModel.setYAngle(curModel.YAngle);
                    ResModelActivity.this.m3DModel.setbIsEarth(curModel.IsEarth);
                    ResModelActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            ResModelActivity.this.mModelView = new MyGLSurfaceView(ResModelActivity.this);
                            ResModelActivity.this.mModelView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                            ResModelActivity.this.mContainer.addView(ResModelActivity.this.mModelView);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        this.mUILayout.bringToFront();
        this.mUILayout.setBackgroundColor(0);
    }

    private void startLoadingAnimation() {
        this.mUILayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.camera_overlay, null, false);
        this.mUILayout.setVisibility(View.VISIBLE);
        this.loadingDialogHandler.mLoadingDialogContainer = this.mUILayout.findViewById(R.id.loading_layout);
        this.loadingDialogHandler.sendEmptyMessage(1);
        addContentView(this.mUILayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mModelView != null) {
            this.mModelView.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mModelView != null) {
            this.mModelView.onResume();
        }
    }
}

package com.sinomaps.geobookar.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

/* renamed from: com.sinomaps.geobookar.ui.LoadingDialogHandler */
public class LoadingDialogHandler extends Handler {
    public static final int HIDE_LOADING_DIALOG = 0;
    public static final int SHOW_LOADING_DIALOG = 1;
    private final WeakReference<Activity> mActivity;
    public View mLoadingDialogContainer;

    public LoadingDialogHandler(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
    }

    public void handleMessage(Message msg) {
        if (((Activity) this.mActivity.get()) != null) {
            if (msg.what == 1) {
                this.mLoadingDialogContainer.setVisibility(View.VISIBLE);
            } else if (msg.what == 0) {
                this.mLoadingDialogContainer.setVisibility(View.GONE);
            }
        }
    }
}

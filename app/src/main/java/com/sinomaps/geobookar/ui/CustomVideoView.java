package com.sinomaps.geobookar.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/* renamed from: com.sinomaps.geobookar.ui.CustomVideoView */
public class CustomVideoView extends VideoView {
    private PlayPauseListener mListener;

    /* renamed from: com.sinomaps.geobookar.ui.CustomVideoView$PlayPauseListener */
    public interface PlayPauseListener {
        void onPause();

        void onPlay();
    }

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(PlayPauseListener listener) {
        this.mListener = listener;
    }

    public void pause() {
        super.pause();
        if (this.mListener != null) {
            this.mListener.onPause();
        }
    }

    public void start() {
        super.start();
        if (this.mListener != null) {
            this.mListener.onPlay();
        }
    }
}

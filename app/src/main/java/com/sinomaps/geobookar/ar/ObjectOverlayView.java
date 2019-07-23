package com.sinomaps.geobookar.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sinomaps.geobookar.R;

/* renamed from: com.sinomaps.geobookar.ar.ObjectOverlayView */
public class ObjectOverlayView extends RelativeLayout {
    public ObjectOverlayView(Context context) {
        this(context, null);
    }

    public ObjectOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ObjectOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflateLayout(context);
    }

    private void inflateLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.camera_overlay_popup, this, true);
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.textViewTitle)).setText(title);
    }

    public void setThumbImg(Bitmap bmp) {
        ((ImageView) findViewById(R.id.thumbImg)).setImageBitmap(bmp);
    }
}

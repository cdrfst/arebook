package com.sinomaps.geobookar.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {
    private Context context;
    private View mConvertView;
    private int mPosition;
    private SparseArray<View> mViews = new SparseArray();

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.context = context;
        this.mPosition = position;
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.mPosition = position;
        return holder;
    }

    public <T extends View> T getView(int viewId) {
        View view = (View) this.mViews.get(viewId);
        if (view != null) {
            return (T)view;
        }
        view = this.mConvertView.findViewById(viewId);
        this.mViews.put(viewId, view);
        return (T)view;
    }

    public View getConvertView() {
        return this.mConvertView;
    }

    public ViewHolder setText(int viewId, String text) {
        ((TextView) getView(viewId)).setText(text);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ((ImageView) getView(viewId)).setImageResource(resId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ((ImageView) getView(viewId)).setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, String imagePath) {
//        Glide.with(this.context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).transform(new GlideRoundTransform(this.context, 5)).crossFade().into((ImageView) getView(viewId));
        return this;
    }

    public ViewHolder setProgress(int viewId, int value) {
        ((ProgressBar) getView(viewId)).setProgress(value);
        return this;
    }

    public void setVisible(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }
}

package com.example.velveteyebrows.network;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.database.DbAdapter;
import com.velveteyebrows.dto.ServiceDTO;

import java.io.ByteArrayOutputStream;

public class AnimatedNetworkImageView extends NetworkImageView {

    private static final int ANIM_DURATION = 500;
    private boolean shouldAnimate = false;
    private ServiceDTO service;
    private Bitmap  mLocalBitmap;
    private boolean mShowLocal;


    public AnimatedNetworkImageView(Context context) {
        super(context);
    }

    public AnimatedNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {

        if(service.getImage() == null && bm != null) {

            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, imageBytes);

            try (DbAdapter dbAdapter = new DbAdapter(getContext())) {
                dbAdapter.setImage(service, imageBytes.toByteArray());
            }
        }

        super.setImageBitmap(bm);

        if(shouldAnimate || AppData.isLocal) {
            ObjectAnimator.ofFloat(this, "alpha", 0, 1)
                    .setDuration(ANIM_DURATION)
                    .start();
        }
    }


    public void setImageBitmapLocal(Bitmap bm) {
        mShowLocal = bm != null;
        mLocalBitmap = bm;
        requestLayout();
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        shouldAnimate = !imageLoader.isCached(url, 0, 0);
        super.setImageUrl(url, imageLoader);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        if (mShowLocal) {
            setImageBitmap(mLocalBitmap);
        }
    }
}
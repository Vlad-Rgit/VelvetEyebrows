package com.example.velveteyebrows.animations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import com.example.velveteyebrows.R;

public class BackdropRevealAnimator {

    private final AnimatorSet _animatorSet = new AnimatorSet();
    private final Context _context;
    private final View _frontLayer;
    private Interpolator _interpolator;
    private boolean _isBackdropShown = false;
    private final int _screenHeight;
    private int _openIconResource = -1;
    private int _closeIconResource = -1;


    public BackdropRevealAnimator(Context context, View frontLayer){
        _context = context;
        _frontLayer = frontLayer;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        _screenHeight = displayMetrics.heightPixels;
    }

    public BackdropRevealAnimator(Context context, View frontLayer,
                                  int openIconResource, int closeIconResource){
        this(context, frontLayer);
        _openIconResource = openIconResource;
        _closeIconResource = closeIconResource;
    }

    public BackdropRevealAnimator(Context context, View frontLayer,
                                  int openIconResource, int closeIconResource,
                                  Interpolator interpolator){
        this(context,frontLayer,openIconResource,closeIconResource);
        _interpolator = interpolator;
    }

    public void animate() {

        _isBackdropShown = !_isBackdropShown;

        //Clear existing animations
        _animatorSet.removeAllListeners();
        _animatorSet.end();
        _animatorSet.cancel();

        //updateIcon(v);

        Resources appResources = _context.getResources();

        float translateY = _screenHeight - 355;

        ObjectAnimator animator = ObjectAnimator.ofFloat(_frontLayer,
                "translationY",
                _frontLayer.getTranslationY(),
                _isBackdropShown ? translateY : 0);


        animator.setDuration(700);

        if(_interpolator != null){
            _animatorSet.setInterpolator(_interpolator);
        }

        _animatorSet.play(animator);
        _animatorSet.start();
    }

    private void updateIcon(View v){

        if(_openIconResource == -1){
            return;
        }

        if(v instanceof ImageView){

            ((ImageView)v).setImageResource(_isBackdropShown ?
                    _closeIconResource : _openIconResource);

        }
    }

    public boolean isBackdropShown() {
        return _isBackdropShown;
    }
}

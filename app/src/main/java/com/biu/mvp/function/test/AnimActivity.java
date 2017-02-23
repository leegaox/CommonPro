package com.biu.mvp.function.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.biu.mvp.R;


public class AnimActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        ObjectAnimator mAnimator;
        Path path = new Path();
        path.lineTo(800, 800);
        mAnimator = ObjectAnimator.ofFloat(frame, frame.X, frame.Y,path);
        mAnimator.setInterpolator(new AnimationUtils().loadInterpolator(this,android.R.interpolator.fast_out_linear_in));
        mAnimator.setDuration(1000);
        mAnimator.start();
    }

}

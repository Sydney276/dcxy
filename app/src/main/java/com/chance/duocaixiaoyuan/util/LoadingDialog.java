package com.chance.duocaixiaoyuan.util;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.chance.duocaixiaoyuan.R;

public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context, R.style.loading_dialog);
        initView();
    }



    private void initView(){
        setContentView(R.layout.loading_dialog);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_animation);
        animation.setInterpolator(new LinearInterpolator());
        findViewById(R.id.loading_dialog_img).startAnimation(animation);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=0.8f;
        getWindow().setAttributes(attributes);
        setCancelable(false);
    }
}
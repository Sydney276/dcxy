package com.chance.duocaixiaoyuan.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.chance.duocaixiaoyuan.R;

public class BeiZhuDialog extends Dialog implements View.OnClickListener {
    EditText et;
    Button cancelBtn, ensureBtn;
    OnEnsureListener onEnsureListener;

    // 设置回调接口方法
    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public BeiZhuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit); // 设置对话框显示布局
        et = findViewById(R.id.dialog_beizhu_et);
        ensureBtn = findViewById(R.id.dialog_beizhu_btn_ensure);
        cancelBtn = findViewById(R.id.dialog_beizhu_btn_cancel);
        cancelBtn.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);
    }

    public interface OnEnsureListener {
        public void onEnsure();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_beizhu_btn_cancel:
                cancel();
                break;
            case R.id.dialog_beizhu_btn_ensure:
                if(onEnsureListener!=null) {
                    onEnsureListener.onEnsure();
                }
                break;
        }
    }

    // 获取输入数据的方法
    public String getEditText() {
        return et.getText().toString().trim();
    }

    // 设置Dialog的尺寸和屏幕尺寸一致
    public void setDialogSize() {
        // 获取当前窗口对象
        Window win = getWindow();
        // 获取窗口对象的参数
        WindowManager.LayoutParams wlp = win.getAttributes();
        // 获取屏幕宽度
        Display d = win.getWindowManager().getDefaultDisplay();
        wlp.width = (int) d.getWidth(); // 对话框窗口为屏幕窗口
        wlp.gravity = Gravity.BOTTOM;
        win.setBackgroundDrawableResource(android.R.color.transparent);
        win.setAttributes(wlp);
        handler.sendEmptyMessageDelayed(1, 100);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 自动弹出软键盘
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };
}

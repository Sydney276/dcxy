package com.chance.duocaixiaoyuan;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chance.duocaixiaoyuan.util.Account;
import com.chance.duocaixiaoyuan.util.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneET;
    private EditText pwdET;
    //全局化
    private LoadingDialog dialog;
    // 本地存储
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("dcxy", Activity.MODE_PRIVATE);
        init();
    }

    public void init() {

        // 检查上一次是否登录成功，如果登录成功，直接跳转主界面，否则进行登录
        // 读取存储内容
        preferences = getSharedPreferences("dcxy", Activity.MODE_PRIVATE);
        String phone = "";
        if (preferences != null) {
            phone = preferences.getString("phone", "");
        }
        // 存储有数据进行主界面跳转
        if (!phone.equals("")) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            setStatusBar();
            setContentView(R.layout.activity_login);
            phoneET = findViewById(R.id.login_phone_et);
            pwdET = findViewById(R.id.login_pwd_et);
        }
    }


    /* 显示加载对话框 */
    private void loadingDialog() {
        dialog = new LoadingDialog(this);
        dialog.show();
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));//设置状态栏颜色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }

    public void click(View view) {
        // 显示加载界面
        loadingDialog();
        new Thread(new Runnable() {
            boolean flag = false;
            @Override
            public void run() {
                try {
                    flag = Account.doLogin(phoneET.getText().toString(), pwdET.getText().toString(), LoginActivity.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 取消加载界面
                        dialog.dismiss();
                        if(flag) {
                            // 跳转到主界面
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, Account.msg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void rjsm(View view) {
        Intent intent = new Intent();
        intent.setClass(this, InstructionsActivity.class);
        startActivity(intent);
    }

//    public void onBackPressed() {
//        finishActivity(0);
//    }
}
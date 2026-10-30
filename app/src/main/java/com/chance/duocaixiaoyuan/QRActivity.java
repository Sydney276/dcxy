package com.chance.duocaixiaoyuan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chance.duocaixiaoyuan.bean.User;
import com.chance.duocaixiaoyuan.util.Account;
import com.chance.duocaixiaoyuan.util.Tool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {
    ImageView ivOutput;
    String sText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);
        ivOutput = findViewById(R.id.qr_output);

        // 判断是否有网络
        if (!Tool.checkNetworkAvailable(this)) {
            Tool.showAlertDialog(this, "温馨提示", "当前无网络连接!").show();
        }else {
            initQR();
        }



    }

    public void initQR(){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 信息
                    sText = Account.getIdBar();
                    if (Account.idBarCode != 1000) {
                        Account.doLogin(User.phone, User.pwd, QRActivity.this);
                        sText = Account.getIdBar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writer.encode(sText.substring(0, sText.length() - 1) + "3", BarcodeFormat.CODE_128, width, 350);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);
                            ivOutput.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }

    public void click(View view) {
        finish();
    }


}
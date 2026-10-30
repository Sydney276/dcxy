package com.chance.duocaixiaoyuan.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.chance.duocaixiaoyuan.bean.User;

public class Tool {
    public static LoadingDialog dialog;
    public static void displayToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void setValuesToUser(SharedPreferences preferences) {
        String customerName = preferences.getString("customerName", "");
        String hardwareAccount = preferences.getString("hardwareAccount", "");
        String phone = preferences.getString("phone", "");
        String pwd = preferences.getString("pwd", "");
        String token = preferences.getString("token", "");
        String areaId = preferences.getString("areaId", "");
        String customerId = preferences.getString("customerId", "");
        String commonDeviceId = preferences.getString("commonDeviceId", "");
        String commonDeviceIdBeiZhu = preferences.getString("commonDeviceIdBeiZhu", "");

        User.setValues(phone, pwd, token, customerId, customerName, hardwareAccount, areaId);
        User.commonDeviceId = commonDeviceId;
        User.commonDeviceIdBeiZhu = commonDeviceIdBeiZhu;
    }

    public static void clearStorage(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static AlertDialog.Builder showAlertDialog(Context context, String title, String msg) {
       return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg);

    }

    public static void loadingDialog(Context context) {
        dialog = new LoadingDialog(context);
        dialog.show();
    }

    /*判断是否有网络*/
    //判断网络连接是否可用（返回true表示网络可用，false为不可用）
    public static boolean checkNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        //获取手机所有链接管理对象（包括对Wi-Fi，net等连接的管理）
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        } else {
            //获取NetworkInfo对象
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null && info.length > 0) {
                for (int i = 0; i < info.length; i++) {
                    System.out.println(i + "状态" + info[i].getState());
                    System.out.println(i + "类型" + info[i].getTypeName());

                    // 判断当前网络状态是否为连接状态
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void openWeblink(Context context, String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));//Url 要打开的网址
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent); //启动浏览器
    }

}

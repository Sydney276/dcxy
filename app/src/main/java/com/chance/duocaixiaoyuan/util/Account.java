package com.chance.duocaixiaoyuan.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.chance.duocaixiaoyuan.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class Account {
    public static String loginUrl = "https://dcxy-customer-app.dcrym.com/app/customer/login";
    public static String deviceUrl = "https://gx-app-server.dcrym.com/dcxy/api/gx/devices/";
    public static String idBarUrl = "https://dcxy-customer-app.dcrym.com/app/customer/flush/idbar";
    public static String balanceUrl = "https://dcxy-customer-app.dcrym.com/account/current/all";
    public static String msg; // 登录提示信息
    public static String result; // 启动设备结果信息
    public static int code; // 启动设备返回code信息
    public static int idBarCode; // 获取一维码返回code信息

    public static boolean doLogin(String phone, String pwd, Context context) throws JSONException {
        boolean flag = false;
        // 请求体
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("loginAccount", phone);
        jsonObject.put("password", pwd);

        String param = jsonObject.toString();
        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("clientSource", "{\"areaId\":\"\",\"customerId\":\"\",\"uuid\":\"\",\"sourceType\":\"Android\",\"appVersion\":\"4.3.98\",\"platformCode\":\"00001\",\"systemVersion\":\"29\",\"deviceInfo\":\"HUAWEI GLK-AL00\",\"networkInfo\":\" WIFI\"}");

        // 响应结果
        String result = HttpUtil.sendPost(loginUrl, param, headers);
//        System.out.println(result);
        // 字符串转json格式解析
        JSONObject jsonObj = new JSONObject(result);
        int code = jsonObj.getInt("code");
        if (code==1000) {
            flag = true;
            JSONObject data = new JSONObject(jsonObj.get("data").toString());
            String token = data.get("token").toString();
            String customerId = data.get("customerId").toString();
            String areaId = data.get("areaId").toString();
            String customerName = data.get("customerName").toString();
            String hardwareAccount = data.get("hardwareAccount").toString();

            SharedPreferences preferences = context.getSharedPreferences("dcxy", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("token", token);
            editor.putString("phone", phone);
            editor.putString("pwd", pwd);
            editor.putString("customerId", customerId);
            editor.putString("areaId", areaId);
            editor.putString("customerName", customerName);
            editor.putString("hardwareAccount", hardwareAccount);
            editor.commit();

            User.setValues(phone, pwd, token, customerId, customerName, hardwareAccount, areaId);
        }
        msg = jsonObj.get("msg").toString();

        return flag;
    }

    public  static void startDevice(String deviceId) throws JSONException {
        String url = deviceUrl + deviceId + "/beginning";
        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("reqSource", "app");
        headers.put("Content-Type", "application/json");
        headers.put("token", User.token);
        headers.put("clientSource", "{\"areaId\":\""+ User.areaId +"\",\"customerId\":\""+ User.customerId +"\",\"uuid\":\"\",\"sourceType\":\"Android\",\"appVersion\":\"4.3.98\",\"platformCode\":\"00001\",\"systemVersion\":\"29\",\"deviceInfo\":\"HUAWEI GLK-AL00\",\"networkInfo\":\" WIFI\"}");
        // 请求体
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("customerId", User.customerId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String param = jsonObject.toString();
        // 响应结果
        result = HttpUtil.sendPost(url, param, headers);

        JSONObject jsonObj = new JSONObject(result);

        code = jsonObj.getInt("code");

    }


    public static String getIdBar() throws JSONException {
        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("reqSource", "app");
        headers.put("Content-Type", "application/json");
        headers.put("token", User.token);
        headers.put("clientSource", "{\"areaId\":\""+ User.areaId +"\",\"customerId\":\""+ User.customerId +"\",\"uuid\":\"\",\"sourceType\":\"Android\",\"appVersion\":\"4.3.98\",\"platformCode\":\"00001\",\"systemVersion\":\"29\",\"deviceInfo\":\"HUAWEI GLK-AL00\",\"networkInfo\":\" WIFI\"}");

        String result = HttpUtil.sendGet(idBarUrl, headers);
        JSONObject jsonObj = new JSONObject(result);
        idBarCode = jsonObj.getInt("code");
        if(idBarCode != 1000) {
            return "";
        }
        return jsonObj.getString("data");
    }

    public static String getBalance() {
        String balance = "";
        // 请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("reqSource", "app");
        headers.put("Content-Type", "application/json");
        headers.put("token", User.token);
        headers.put("clientSource", "{\"areaId\":\""+ User.areaId +"\",\"customerId\":\""+ User.customerId +"\",\"uuid\":\"\",\"sourceType\":\"Android\",\"appVersion\":\"4.3.98\",\"platformCode\":\"00001\",\"systemVersion\":\"29\",\"deviceInfo\":\"HUAWEI GLK-AL00\",\"networkInfo\":\" WIFI\"}");

        try {
            String result = HttpUtil.sendGet(balanceUrl, headers);
            JSONObject jsonObj = new JSONObject(result);
            JSONObject data = jsonObj.getJSONObject("data");
            JSONArray accountNewVos = data.getJSONArray("accountNewVos");
            JSONObject d = accountNewVos.getJSONObject(0);
            balance = d.getString("money");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balance;
    }

}

package com.chance.duocaixiaoyuan.bean;

public class User {
    public static String customerName; // 用户名称
    public static String hardwareAccount; // 设备账号
    public static String phone;
    public static String pwd;
    public static String token; // 登录凭证
    public static String areaId; // 地区id
    public static String customerId; // 用户id
    public static String commonDeviceId; // 常用设备id
    public static String commonDeviceIdBeiZhu;

    public static void setValues(String phone, String pwd, String token,
                          String customerId, String customerName, String hardwareAccount,
                          String areaId) {
        User.phone = phone;
        User.pwd = pwd;
        User.token = token;
        User.customerId = customerId;
        User.customerName = customerName;
        User.hardwareAccount = hardwareAccount;
        User.areaId = areaId;
    }

}

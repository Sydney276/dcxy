package com.chance.duocaixiaoyuan;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.chance.duocaixiaoyuan.bean.User;
import com.chance.duocaixiaoyuan.util.Account;
import com.chance.duocaixiaoyuan.util.BeiZhuDialog;
import com.chance.duocaixiaoyuan.util.Tool;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    SharedPreferences preferences;
    TextView tv_name, tv_deviceId, tv_deviceName, tv_money;
    PopupMenu popupMenu;
    int kind;
    String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("dcxy", Activity.MODE_PRIVATE);
        // 把数据设置到User中
        Tool.setValuesToUser(preferences);
        init();

        // 启动后直接进入“被扫码”界面；保留主界面，关闭二维码页后可返回
        startActivity(new Intent(MainActivity.this, QRActivity.class));
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        tv_name = findViewById(R.id.main_tv_name);
        tv_deviceId = findViewById(R.id.main_tv_deviceId);
        tv_deviceName = findViewById(R.id.main_tv_deviceName);
        tv_money = findViewById(R.id.main_tv_money);

        tv_name.setText(User.customerName);
        tv_deviceId.setText("(设备账号：" + User.hardwareAccount + ")");
        if(!User.commonDeviceId.equals("")) {
            tv_deviceName.setText("常用备注:"+User.commonDeviceIdBeiZhu);
        }

        // 判断是否有网络
        if (!Tool.checkNetworkAvailable(this)) {
            Tool.showAlertDialog(this, "温馨提示", "当前无网络连接!").show();
            return;
        }

        // 获取余额
        new Thread(new Runnable() {
            @Override
            public void run() {
                money = Account.getBalance();
                // ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(money)) {
                            tv_money.setText("("+money+")");
                        }
                    }
                });
            }
        }).start();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //第二个参数表示此menu的id值，在onOptionsItemSelected方法中通过id值判断是哪个menu被点击了
        menu.add(Menu.NONE, 1, 1, "菜单1");
        return true;
    }
    //点击实现的操作
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:
                Toast.makeText(getApplicationContext(),"菜单1",Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressLint("NonConstantResourceId")
    public void click(View view) {
        switch (view.getId()) {
            // 修改备注
            case R.id.main_tv_deviceName:
                updateBeiZhu();
                break;
                // 右上角更多按钮
            case R.id.main_iv_more:
                Tool.displayToast(this, "测试！");
                break;
                // 被扫码
            case R.id.main_bt_bsm:
                Intent intent = new Intent();
                intent.setClass(this, QRActivity.class);
                startActivity(intent);
                break;
                // 扫码
            case R.id.main_bt_sm:
                kind = 0;
                scanQR();
                break;
                // 添加常用设备
            case R.id.main_bt_add_common:
                kind = 1;
                scanQR();
                break;
                // 启动常用
            case R.id.main_bt_start_common:
                startCommonDevice();
                break;
            case R.id.main_bt_enter_group:
                boolean result = joinQQGroup("hfq0EiUWGPQUfoCzcAVg04QkmzRHzLZ8");

                if(!result) {
                    // 将群号复制到粘贴板
                    setClipboard("644975951");
                    // 提示信息
                    Tool.displayToast(this, "已复制群号到粘贴板！");
                }
                break;
            case R.id.main_bt_share_app:
                setClipboard("https://chance.lanzout.com/b03q5yd0h\n" + "密码:1234");
                Tool.displayToast(this, "已复制分享链接到粘贴板！\n发送给他人即可~");
        }
    }

    public void setClipboard(String text){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }
    private void updateBeiZhu() {
        if (!TextUtils.isEmpty(User.commonDeviceId)) {
            // 弹出修改对话框
            showBZDialog(User.commonDeviceId);
        }
    }


    private void startCommonDevice() {
        // 判断是否设置了常用
        if (TextUtils.isEmpty(User.commonDeviceId)) {
            Tool.showAlertDialog(this, "温馨提示", "您还未添加常用！").show();
            return;
        }
        Tool.showAlertDialog(this, "温馨提示", "确定要启动嘛？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toStartDevice(User.commonDeviceId);
                    }
                }).setNegativeButton("取消", null).show();
    }

    // 启动常用


    private void scanQR() {

        // 判断是否有网络
        if (!Tool.checkNetworkAvailable(this)) {
            Tool.showAlertDialog(this, "温馨提示", "当前无网络连接!").show();
            return;
        }

        IntentIntegrator intentIntegrator = new IntentIntegrator(
                MainActivity.this
        );
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();
    }



    public void show(View v){
        //实例化一个弹出式菜单，传入上下文和控件
        popupMenu = new PopupMenu(this,v);
        //根据菜单填充器获得菜单的布局
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
        //设置菜单的点击事件
        popupMenu.setOnMenuItemClickListener(this);
        //显示菜单
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()){
            case R.id.item_popup_1:
                intent.setClass(this, InstructionsActivity.class);
                startActivity(intent);
                break;
            case R.id.item_popup_2:
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.item_popup_3:
                Tool.clearStorage(preferences);
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );
        if (intentResult.getContents() != null) {
            if(kind==0) {
                saoMa(intentResult);
            }else {
                addCommonDevice(intentResult);
            }
        }

    }

    public void saoMa(IntentResult intentResult) {
        // 判断扫描的二维码是否为多彩校园设备二维码
        String qrResult = intentResult.getContents();
        // 按问号分割
        String[] arr = qrResult.split("\\?");
        if (!arr[0].equals("https://www.dcrym.com")) {
            Tool.showAlertDialog(this, "扫描结果", "请扫码多彩校园饮水设备二维码").show();
            return;
        }
        String deviceId = arr[1].split("=")[1].substring(2);

        Tool.showAlertDialog(this, "扫描成功", "当前设备：" + deviceId)
                .setPositiveButton("启动", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toStartDevice(deviceId);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void addCommonDevice(IntentResult intentResult) {
        // 判断扫描的二维码是否为多彩校园设备二维码
        String qrResult = intentResult.getContents();
        // 按问号分割
        String[] arr = qrResult.split("\\?");
        if (!arr[0].equals("https://www.dcrym.com")) {
            Tool.showAlertDialog(this, "扫描结果", "请扫码多彩校园饮水设备二维码").show();
            return;
        }
        String deviceId = arr[1].split("=")[1].substring(2);

        Tool.showAlertDialog(this, "扫描成功", "当前设备：" + deviceId)
                .setPositiveButton("添加常用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 输入备注
                        showBZDialog(deviceId);
                    }
                }).show();
    }

    public void showBZDialog(String deviceId) {
        BeiZhuDialog dialog = new BeiZhuDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BeiZhuDialog.OnEnsureListener() {
            @Override
            public void onEnsure() {
                User.commonDeviceId = deviceId;
                String msg = dialog.getEditText();

                if(!TextUtils.isEmpty(msg)) {
                    User.commonDeviceIdBeiZhu  = msg;
                }else {
                    User.commonDeviceIdBeiZhu = deviceId;
                }
                // 存储到应用中
                SharedPreferences preferences = getSharedPreferences("dcxy", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("commonDeviceId", deviceId);
                editor.putString("commonDeviceIdBeiZhu", User.commonDeviceIdBeiZhu);
                editor.commit();
                // 更新界面
                tv_deviceName.setText("常用备注:" + User.commonDeviceIdBeiZhu);
                dialog.cancel();
            }
        });
    }

    // 开启设备
    public  void toStartDevice(String deviceId) {
        // 显示加载界面
        Tool.loadingDialog(MainActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Account.startDevice(deviceId);
                    if(!(Account.code == 1000)) {
                        boolean flag = Account.doLogin(User.phone, User.pwd, MainActivity.this);
                        // 第二次尝试登录失败，说明密码已经更改，跳转到登录界面
                        if(!flag) {
                            Tool.clearStorage(preferences);
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        Account.startDevice(deviceId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 取消加载界面
                        Tool.dialog.dismiss();
                        Tool.displayToast(MainActivity.this, Account.result);
                    }
                });
            }
        }).start();
    }

    /****************
     *
     * 发起添加群流程。群号：交流群(644975951) 的 key 为： hfq0EiUWGPQUfoCzcAVg04QkmzRHzLZ8
     * 调用 joinQQGroup(hfq0EiUWGPQUfoCzcAVg04QkmzRHzLZ8) 即可发起手Q客户端申请加群 交流群(644975951)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void getBalance(View view) {
        // 判断是否有网络
        if (!Tool.checkNetworkAvailable(this)) {
            Tool.showAlertDialog(this, "温馨提示", "当前无网络连接!").show();
            return;
        }

        // 获取余额
        new Thread(new Runnable() {
            @Override
            public void run() {
                money = Account.getBalance();
                // ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(money)) {
                            tv_money.setText("("+money+")");
                        }
                    }
                });
            }
        }).start();
    }
}


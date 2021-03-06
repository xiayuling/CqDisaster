package com.nandi.cqdisaster.AnyChat.activity;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.nandi.cqdisaster.AnyChat.bussinesscenter.BussinessCenter;
import com.nandi.cqdisaster.AnyChat.util.BaseConst;
import com.nandi.cqdisaster.AnyChat.util.BaseMethod;
import com.nandi.cqdisaster.AnyChat.util.ConfigEntity;
import com.nandi.cqdisaster.AnyChat.util.ConfigService;
import com.nandi.cqdisaster.AnyChat.util.DialogFactory;
import com.nandi.cqdisaster.R;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends Activity implements AnyChatBaseEvent, OnClickListener {
    private Button configBtn;
    private Button loginBtn;
    private ImageView ivBack;
    private ConfigEntity configEntity;
    private EditText mEditAccount;
    private ProgressDialog mProgressLogin;
    private Dialog dialog;
    private AnyChatCoreSDK anychat;
    private boolean bNeedRelease = false;
    private String strUserName;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        initSdk();
        intParams();
        strUserName=getIntent().getStringExtra("NAME");
        initView();
        initLoginProgress();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAPTURE_AUDIO_OUTPUT};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    protected void intParams() {
        configEntity = ConfigService.LoadConfig(this);
        BussinessCenter.getBussinessCenter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tag = intent.getIntExtra("INTENT", BaseConst.AGAIGN_LOGIN);
        if (tag == BaseConst.AGAIGN_LOGIN) {
            if (anychat != null) {
                anychat.Logout();
                anychat.SetBaseEvent(this);
            }
        } else if (tag == BaseConst.APP_EXIT) {
            this.finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    private void initSdk() {
        if (anychat == null) {
            anychat = AnyChatCoreSDK.getInstance(LoginActivity.this);
            anychat.SetBaseEvent(this);
            anychat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
            bNeedRelease = true;
        }
    }

    private void initView() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.setContentView(R.layout.login_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        mEditAccount = (EditText) findViewById(R.id.edit_account);
        mEditAccount.setText(strUserName);

        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);

        configBtn = (Button) findViewById(R.id.btn_setting);
        configBtn.setOnClickListener(this);

    }

    private void initLoginProgress() {
        mProgressLogin = new ProgressDialog(this);
        mProgressLogin.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                loginBtn.setClickable(true);
            }
        });
        mProgressLogin.setMessage(this.getString(R.string.login_progress));
    }

    private void Login() {

        ConfigService.SaveConfig(this, configEntity);
        if (mEditAccount.getText().length() == 0) {
            BaseMethod.showToast(this.getString(R.string.str_account_input_hint), this);
            return;
        }

        /**
         * AnyChat可以连接自主部署的服务器、也可以连接AnyChat视频云平台； 连接自主部署服务器的地址为自设的服务器IP地址或域名、端口；
         * 连接AnyChat视频云平台的服务器地址为：cloud.anychat.cn；端口为：8906
         */
        this.anychat.Connect(configEntity.ip, configEntity.port);
        /***
         * AnyChat支持多种用户身份验证方式，包括更安全的签名登录，
         * 详情请参考：http://bbs.anychat.cn/forum.php?mod=viewthread&tid=2211&highlight=%C7%A9%C3%FB
         */
        this.anychat.Login(strUserName, "123");
        loginBtn.setClickable(false);
        mProgressLogin.show();
    }

//    protected void onDestroy() {
//        super.onDestroy();
//        if (bNeedRelease) {
//            anychat.Logout();
//            anychat.removeEvent(this);
//            anychat.Release();
//        }
//        BussinessCenter.getBussinessCenter().realseData();
//    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if (!bSuccess) {
            BaseMethod.showToast(getString(R.string.server_connect_error), this);
            mProgressLogin.dismiss();
        } else {
        }
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            BussinessCenter.selfUserId = dwUserId;
            BussinessCenter.selfUserName = mEditAccount.getText().toString();
            Intent intent = new Intent();
            intent.setClass(this, HallActivity.class);
            this.startActivity(intent);
            finish();
        } else if (dwErrorCode == 200) {
            BaseMethod.showToast(getString(R.string.str_lggin_failed), this);
        }
        mProgressLogin.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Login();
                break;
            case R.id.btn_setting:
                dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_CONFIG, configEntity, this);
                dialog.show();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

}
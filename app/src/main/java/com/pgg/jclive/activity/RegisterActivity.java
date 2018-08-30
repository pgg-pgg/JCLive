package com.pgg.jclive.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pgg.jclive.R;
import com.pgg.jclive.base.JCBaseApplication;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.titlebar)
    Toolbar titlebar;

    @BindView(R.id.account)
    EditText account;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.confirm_password)
    EditText confirm_password;

    @BindView(R.id.register)
    Button register;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }


    private void setTitleBar() {
        titlebar.setTitle("注册新用户");
        titlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(titlebar);
    }

    @OnClick(R.id.register)
    public void onClick(){
        register();
    }

    private void register() {
        String accountStr = account.getText().toString();
        String passwordStr = password.getText().toString();
        String confirmPswStr = confirm_password.getText().toString();

        if (TextUtils.isEmpty(accountStr) ||
                TextUtils.isEmpty(passwordStr) ||
                TextUtils.isEmpty(confirmPswStr)) {
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordStr.equals(confirmPswStr)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        ILiveLoginManager.getInstance().tlsRegister(accountStr, passwordStr, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //注册成功
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                //登录一下
                login();

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //注册失败
                Toast.makeText(RegisterActivity.this, "注册失败：" + errMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void login() {
        final String accountStr = account.getText().toString();
        String passwordStr = password.getText().toString();

        //调用腾讯IM登录
        ILiveLoginManager.getInstance().tlsLogin(accountStr, passwordStr, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                //登陆成功。
                loginLive(accountStr, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(RegisterActivity.this, "tls登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginLive(String accountStr, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                //跳转到修改用户信息界面。
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,EditProfileActivity.class);
                startActivity(intent);

                getSelfInfo();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(RegisterActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(RegisterActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                JCBaseApplication.getApplication().setSelfProfile(timUserProfile);
            }
        });
    }


}

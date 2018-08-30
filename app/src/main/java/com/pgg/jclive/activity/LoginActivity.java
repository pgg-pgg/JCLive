package com.pgg.jclive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pgg.jclive.MainActivity;
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

public class LoginActivity extends AppCompatActivity{

    @BindView(R.id.account)
    EditText account;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.login)
    Button login;

    @BindView(R.id.register)
    Button register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.login,R.id.register})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login:
               //登录操作
                login();
                break;
            case R.id.register:
                //注册的操作
                register();
                break;
        }
    }

    private void register() {
        //注册新用户，跳转到注册页面。
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
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
                Toast.makeText(LoginActivity.this, "tls登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginLive(String accountStr, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                getSelfInfo();

                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(LoginActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(LoginActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                JCBaseApplication.getApplication().setSelfProfile(timUserProfile);
            }
        });
    }


}

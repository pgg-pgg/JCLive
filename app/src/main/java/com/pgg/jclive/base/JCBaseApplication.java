package com.pgg.jclive.base;

import android.app.Application;
import android.content.Context;

import com.pgg.jclive.editprofile.CustomProfile;
import com.pgg.jclive.utils.QnUploadHelper;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class JCBaseApplication extends Application {

    private static JCBaseApplication app;
    private static Context appContext;
    private ILVLiveConfig mLiveConfig;

    private TIMUserProfile mSelfProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appContext = getApplicationContext();
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400132856, 37047);
        List<String> customInfos = new ArrayList<String>();
        customInfos.add(CustomProfile.CUSTOM_GET);
        customInfos.add(CustomProfile.CUSTOM_SEND);
        customInfos.add(CustomProfile.CUSTOM_LEVEL);
        customInfos.add(CustomProfile.CUSTOM_RENZHENG);
        TIMManager.getInstance().initFriendshipSettings(CustomProfile.allBaseInfo, customInfos);

        //初始化直播场景
        mLiveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(mLiveConfig);

        //七牛云初始化
        QnUploadHelper.init("fywLTKHt3JUahQrTPSFrKRt27FjWTBV6Yn8CQFWe",
                "00nzSVpO5yURyMxpPkOP_9shEtnGYDbGJxMavzdL",
                "http://oe0i3jf0i.bkt.clouddn.com/",
                "imooc");

        LeakCanary.install(this);
    }


    public static Context getContext() {
        return appContext;
    }

    public static JCBaseApplication getApplication() {
        return app;
    }

    public void setSelfProfile(TIMUserProfile userProfile) {
        mSelfProfile = userProfile;
    }

    public TIMUserProfile getSelfProfile() {
        return mSelfProfile;
    }

    public ILVLiveConfig getLiveConfig() {
        return mLiveConfig;
    }
}

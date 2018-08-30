package com.pgg.jclive.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgg.jclive.R;
import com.pgg.jclive.adapter.WatcherAdapter;
import com.pgg.jclive.utils.ImgUtils;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class TitleView extends LinearLayout {

    private ImageView hostAvatarImgView;//主播头像
    private TextView watcherNumView;//观看人数
    private int watcherNum=0;

    private RecyclerView watcherListView;//观众列表
    private WatcherAdapter adapter;
    private String hostId; //主播id

    public TitleView(Context context) {
        this(context,null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_title, this, true);

        findAllViews();
    }

    private void findAllViews() {
        hostAvatarImgView = (ImageView) findViewById(R.id.host_avatar);
        watcherNumView = (TextView) findViewById(R.id.watchers_num);
        hostAvatarImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击头像，显示详情对话框
                showUserInfoDialog(hostId);
            }
        });

        watcherListView =  findViewById(R.id.watch_list);
        watcherListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        watcherListView.setLayoutManager(layoutManager);
        //设置adapter
        adapter = new WatcherAdapter(getContext());
        watcherListView.setAdapter(adapter);
        adapter.setOnClickAvaListener(new WatcherAdapter.OnClickAvaListener() {
            @Override
            public void onClick(TIMUserProfile userProfile) {
                showUserInfoDialog(userProfile.getIdentifier());
            }
        });
    }


    /**
     * 显示用户信息
     * @param senderId
     */
    private void showUserInfoDialog(String senderId) {
        List<String> ids = new ArrayList<String>();
        ids.add(senderId);
        TIMFriendshipManager.getInstance().getUsersProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(TitleView.this.getContext(), "请求用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                Context context = TitleView.this.getContext();
                if(context instanceof Activity) {
                    UserInfoDialog userInfoDialog = new UserInfoDialog((Activity) context, timUserProfiles.get(0));
                    userInfoDialog.show();
                }
            }
        });
    }


    /**
     * 设置主播头像
     * @param userProfile
     */
    public void setHost(TIMUserProfile userProfile) {
        if(userProfile == null){
            ImgUtils.loadRound(R.drawable.default_avatar, hostAvatarImgView);
        }else {
            hostId = userProfile.getIdentifier();
            String avatarUrl = userProfile.getFaceUrl();
            if (TextUtils.isEmpty(avatarUrl)) {
                ImgUtils.loadRound(R.drawable.default_avatar, hostAvatarImgView);
            } else {
                ImgUtils.loadRound(avatarUrl, hostAvatarImgView);
            }
        }
    }


    /**
     * 加入一个观众
     * @param userProfile
     */
    public void addWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            adapter.addWatcher(userProfile);
            watcherNum++;
            watcherNumView.setText("观众:" + watcherNum);
        }
    }

    /**
     * 加入一组观众
     * @param userProfileList
     */
    public void addWatchers(List<TIMUserProfile> userProfileList){
        if(userProfileList != null){
            adapter.addWatchers(userProfileList);
            watcherNum+= userProfileList.size();
            watcherNumView.setText("观众:" + watcherNum);
        }
    }

    /**
     * 一处一个观众
     * @param userProfile
     */
    public void removeWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            adapter.removeWatcher(userProfile);
            watcherNum--;
            watcherNumView.setText("观众:" + watcherNum);
        }
    }
}

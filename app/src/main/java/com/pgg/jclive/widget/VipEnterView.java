package com.pgg.jclive.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgg.jclive.R;
import com.tencent.TIMUserProfile;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class VipEnterView extends RelativeLayout {


    private TextView userNameView;
    private ImageView splashView;

    private Animation viewAnimation;
    private Animation nameAnimation;
    private Animation splashAnimation;

    private List<TIMUserProfile> vipProfile = new LinkedList<TIMUserProfile>();
    private boolean isAvaliable = true;

    public VipEnterView(Context context) {
        this(context,null);
    }

    public VipEnterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VipEnterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setBackgroundResource(R.drawable.vip_enter_bkg);
        LayoutInflater.from(getContext()).inflate(R.layout.view_vip_enter, this, true);
        findAllViews();
        loadAnim();
        setVisibility(INVISIBLE);
    }

    private void loadAnim() {
        viewAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.vip_enter_view);
        nameAnimation=AnimationUtils.loadAnimation(getContext(),R.anim.vip_enter_name);
        splashAnimation=AnimationUtils.loadAnimation(getContext(),R.anim.vip_enter_splash);

        viewAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                VipEnterView.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                userNameView.post(new Runnable() {
                    @Override
                    public void run() {
                        userNameView.startAnimation(nameAnimation);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        nameAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                userNameView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashView.post(new Runnable() {
                    @Override
                    public void run() {
                        splashView.startAnimation(splashAnimation);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        splashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                splashView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 判断有无下一个vip用户进入。
                isAvaliable = true;
                if (vipProfile.size() > 0) {
                    showVipEnter(vipProfile.remove(0));
                } else {
                    VipEnterView.this.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showVipEnter(TIMUserProfile userProfile) {

    }

    private void findAllViews() {
        userNameView = findViewById(R.id.user_name);
        splashView = findViewById(R.id.splash);
    }
}

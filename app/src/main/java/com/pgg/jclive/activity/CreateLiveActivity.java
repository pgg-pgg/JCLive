package com.pgg.jclive.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pgg.jclive.R;
import com.pgg.jclive.base.JCBaseApplication;
import com.pgg.jclive.domain.RoomInfo;
import com.pgg.jclive.network.BaseRequest;
import com.pgg.jclive.network.CreateRoomRequest;
import com.pgg.jclive.utils.ImgUtils;
import com.pgg.jclive.utils.PicChooserHelper;
import com.tencent.TIMUserProfile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class CreateLiveActivity extends AppCompatActivity {

    @BindView(R.id.titlebar)
    Toolbar titlebar;

    @BindView(R.id.set_cover)
    FrameLayout set_cover;

    @BindView(R.id.cover)
    ImageView mCoverImg;

    @BindView(R.id.tv_pic_tip)
    TextView mCoverTipTxt;

    @BindView(R.id.room_no)
    TextView room_no;

    @BindView(R.id.title)
    TextView mTitleEt;

    @BindView(R.id.create)
    TextView mCreateRoomBtn;

    private String coverUrl = null;
    private PicChooserHelper mPicChooserHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);
        setupTitlebar();
    }

    private void setupTitlebar() {
        titlebar.setTitle("开始我的直播");
        titlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(titlebar);
    }

    @OnClick({R.id.create,R.id.set_cover})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.create:
                //创建直播
                requestCreateRoom();
                break;

            case R.id.set_cover:
                //选择图片
                choosePic();
                break;
        }
    }

    /**
     * 选择图片
     */
    private void choosePic() {
        if (mPicChooserHelper == null) {
            mPicChooserHelper = new PicChooserHelper(this, PicChooserHelper.PicType.Cover);
            mPicChooserHelper.setOnChooseResultListener(new PicChooserHelper.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    //获取图片成功
                    updateCover(url);
                }

                @Override
                public void onFail(String msg) {
                    //获取图片失败
                    Toast.makeText(CreateLiveActivity.this, "选择失败：" + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        mPicChooserHelper.showPicChooseDialog();
    }

    private void updateCover(String url) {
        coverUrl = url;
        ImgUtils.load(url, mCoverImg);
        mCoverTipTxt.setVisibility(View.GONE);
    }

    /**
     * 创建直播
     */
    private void requestCreateRoom() {
        CreateRoomRequest.CreateRoomParam param = new CreateRoomRequest.CreateRoomParam();
        TIMUserProfile selfProfile= JCBaseApplication.getApplication().getSelfProfile();
        param.userId = selfProfile.getIdentifier();
        param.userAvatar = selfProfile.getFaceUrl();
        String nickName = selfProfile.getNickName();
        param.userName = TextUtils.isEmpty(nickName) ? selfProfile.getIdentifier() : nickName;
        param.liveTitle = mTitleEt.getText().toString();
        param.liveCover = coverUrl;
        //创建房间
        CreateRoomRequest request = new CreateRoomRequest();
        request.setOnResultListener(new BaseRequest.OnResultListener<RoomInfo>() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(CreateLiveActivity.this, "请求失败：" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(RoomInfo roomInfo) {
                Toast.makeText(CreateLiveActivity.this, "请求成功：" + roomInfo.roomId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(CreateLiveActivity.this, HostLiveActivity.class);
                intent.putExtra("roomId", roomInfo.roomId);
                startActivity(intent);

                finish();
            }
        });

        String requestUrl = request.getUrl(param);
        request.request(requestUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPicChooserHelper != null) {
            mPicChooserHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

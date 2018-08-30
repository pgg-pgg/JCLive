package com.pgg.jclive.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pgg.jclive.MainActivity;
import com.pgg.jclive.R;
import com.pgg.jclive.editprofile.CustomProfile;
import com.pgg.jclive.utils.ImgUtils;
import com.pgg.jclive.utils.PicChooserHelper;
import com.pgg.jclive.widget.EditGenderDialog;
import com.pgg.jclive.widget.EditStrProfileDialog;
import com.pgg.jclive.widget.ProfileEdit;
import com.pgg.jclive.widget.ProfileTextView;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pengganggui on 2018/8/30.
 * 个人信息Fragment
 */

public class EditProfileFragment extends Fragment {

    @BindView(R.id.title_bar)
    Toolbar title_bar;

    @BindView(R.id.avatar)
    LinearLayout avatar;

    @BindView(R.id.avatar_img)
    ImageView avatar_img;

    @BindView(R.id.nick_name)
    ProfileEdit nick_name;

    @BindView(R.id.gender)
    ProfileEdit gender;

    @BindView(R.id.sign)
    ProfileEdit sign;

    @BindView(R.id.renzheng)
    ProfileEdit renzheng;

    @BindView(R.id.location)
    ProfileEdit location;

    @BindView(R.id.id)
    ProfileTextView id;

    @BindView(R.id.level)
    ProfileTextView level;

    @BindView(R.id.get_nums)
    ProfileTextView get_nums;

    @BindView(R.id.send_nums)
    ProfileTextView send_nums;

    @BindView(R.id.complete)
    Button complete;

    private TIMUserProfile mUserProfile;
    private PicChooserHelper mPicChooserHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this,mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitleBar();
        setIconKey();
        getSelfInfo();
    }


    private void setTitleBar() {
        title_bar.setTitle("编辑个人信息");
        title_bar.setTitleTextColor(Color.WHITE);
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(title_bar);
        }
    }

    private void setIconKey() {
        nick_name.set(R.drawable.ic_info_nickname, "昵称", "");
        gender.set(R.drawable.ic_info_gender, "性别", "");
        sign.set(R.drawable.ic_info_sign, "签名", "无");
        renzheng.set(R.drawable.ic_info_renzhen, "认证", "未知");
        location.set(R.drawable.ic_info_location, "地区", "未知");
        id.set(R.drawable.ic_info_id, "ID", "");
        level.set(R.drawable.ic_info_level, "等级", "0");
        get_nums.set(R.drawable.ic_info_get, "获得票数", "0");
        send_nums.set(R.drawable.ic_info_send, "送出票数", "0");
    }


    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                mUserProfile = timUserProfile;
                updateViews(timUserProfile);
            }
        });
    }

    private void updateViews(TIMUserProfile timUserProfile) {
        //更新界面
        String faceUrl = timUserProfile.getFaceUrl();
        if (TextUtils.isEmpty(faceUrl)) {
            ImgUtils.loadRound(R.drawable.default_avatar, avatar_img);
        } else {
            ImgUtils.loadRound(faceUrl, avatar_img);
        }
        nick_name.updateValue(timUserProfile.getNickName());
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        gender.updateValue(genderStr);
        sign.updateValue(timUserProfile.getSelfSignature());
        location.updateValue(timUserProfile.getLocation());
        id.updateValue(timUserProfile.getIdentifier());

        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        renzheng.updateValue(getValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知"));
        level.updateValue(getValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0"));
        get_nums.updateValue(getValue(customInfo, CustomProfile.CUSTOM_GET, "0"));
        send_nums.updateValue(getValue(customInfo, CustomProfile.CUSTOM_SEND, "0"));
    }

    private String getValue(Map<String, byte[]> customInfo, String key, String defaultValue) {
        if (customInfo != null) {
            byte[] valueBytes = customInfo.get(key);
            if (valueBytes != null) {
                return new String(valueBytes);
            }
        }
        return defaultValue;
    }

    @OnClick({R.id.avatar,R.id.nick_name,R.id.gender,R.id.sign,R.id.renzheng,R.id.location,R.id.complete})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.avatar:
                //修改头像
                choosePic();
                break;
            case R.id.nick_name:
                //修改昵称
                showEditNickNameDialog();
                break;
            case R.id.gender:
                //修改性别
                showEditGenderDialog();
                break;
            case R.id.sign:
                //修改签名
                showEditSignDialog();
                break;
            case R.id.renzheng:
                //修改认证
                showEditRenzhengDialog();
                break;
            case R.id.location:
                //修改位置
                showEditLocationDialog();
                break;
            case R.id.complete:
                //完成，点击跳转到主界面
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 修改位置
     */
    private void showEditLocationDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新地区失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("地区", R.drawable.ic_info_location, location.getValue());
    }

    /**
     * 修改认证
     */
    private void showEditRenzhengDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setCustomInfo(CustomProfile.CUSTOM_RENZHENG, content.getBytes(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新认证失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("认证", R.drawable.ic_info_renzhen, renzheng.getValue());
    }

    /**
     * 修改签名
     */
    private void showEditSignDialog() {
        EditStrProfileDialog dialog = new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, final String content) {
                TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新签名失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("签名", R.drawable.ic_info_sign, sign.getValue());
    }

    /**
     * 修改性别
     */
    private void showEditGenderDialog() {
        EditGenderDialog dialog = new EditGenderDialog(getActivity());
        dialog.setOnChangeGenderListener(new EditGenderDialog.OnChangeGenderListener() {
            @Override
            public void onChangeGender(boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新性别失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show(gender.getValue().equals("男"));
    }

    /**
     * 修改昵称
     */
    private void showEditNickNameDialog() {
        EditStrProfileDialog dialog=new EditStrProfileDialog(getActivity());
        dialog.setOnOKListener(new EditStrProfileDialog.OnOKListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "更新昵称失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfInfo();
                    }
                });
            }
        });
        dialog.show("昵称", R.drawable.ic_info_nickname, nick_name.getValue());
    }

    /**
     * 修改头像
     */
    private void choosePic() {
        if (mPicChooserHelper==null){
            mPicChooserHelper=new PicChooserHelper(this,PicChooserHelper.PicType.Avatar);
            mPicChooserHelper.setOnChooseResultListener(new PicChooserHelper.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    updateAvatar(url);
                }

                @Override
                public void onFail(String msg) {
                    Toast.makeText(getActivity(), "选择失败：" + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        mPicChooserHelper.showPicChooseDialog();
    }

    /**
     * 更新头像
     * @param url
     */
    private void updateAvatar(String url) {
        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "头像更新失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                //更新头像成功
                getSelfInfo();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mPicChooserHelper != null) {
            mPicChooserHelper.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}

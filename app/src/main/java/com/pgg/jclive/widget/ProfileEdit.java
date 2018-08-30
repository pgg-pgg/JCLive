package com.pgg.jclive.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pgg.jclive.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class ProfileEdit extends LinearLayout {

    @BindView(R.id.profile_icon)
    ImageView profile_icon;

    @BindView(R.id.profile_key)
    TextView profile_key;

    @BindView(R.id.profile_value)
    TextView profile_value;

    @BindView(R.id.right_arrow)
    ImageView right_arrow;

    public ProfileEdit(Context context) {
        this(context,null);
    }

    public ProfileEdit(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProfileEdit(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_profile_edit, this, true);
        ButterKnife.bind(this,view);
    }

    public void set(int iconResId,String key,String value){
        profile_icon.setImageResource(iconResId);
        profile_key.setText(key);
        profile_value.setText(value);
    }

    public void updateValue(String value) {
        profile_value.setText(value);
    }

    public String getValue() {
        return profile_value.getText().toString();
    }

    protected void disableEdit() {
        right_arrow.setVisibility(GONE);
    }

}

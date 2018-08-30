package com.pgg.jclive.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class ProfileTextView extends ProfileEdit {

    public ProfileTextView(Context context) {
        this(context,null);
    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableEdit();
    }
}

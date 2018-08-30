package com.pgg.jclive.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pgg.jclive.R;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class TransParentDialog {

    protected Activity activity;
    protected Dialog dialog;

    public TransParentDialog(Activity activity) {
        this.activity=activity;
        this.dialog=new Dialog(activity, R.style.dialog);
    }

    protected void setContentView(View view){
        dialog.setContentView(view);
    }

    public void setWidthAndHeight(int width,int height){
        Window win=dialog.getWindow();
        WindowManager.LayoutParams attributes = win.getAttributes();
        if (attributes!=null){
            attributes.width=width;//设置宽度
            attributes.height=height;//设置高度
            win.setAttributes(attributes);
        }
    }

    public void show(){
        dialog.show();
    }

    public void hide(){
        dialog.hide();
    }
}

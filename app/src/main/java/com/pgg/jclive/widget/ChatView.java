package com.pgg.jclive.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pgg.jclive.R;
import com.pgg.jclive.constants.Constants;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class ChatView extends LinearLayout {

    private CheckBox mSwitchChatType;
    private EditText mChatContent;
    private TextView mSend;

    public ChatView(Context context) {
        this(context,null);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int paddingPx = (int) (getResources().getDisplayMetrics().density * 10 + 0.5f);
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        setBackgroundColor(Color.parseColor("#ccffffff"));
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat, this, true);

        findAllViews();
    }

    private void findAllViews() {
        mSwitchChatType = findViewById(R.id.switch_chat_type);
        mChatContent = findViewById(R.id.chat_content_edit);
        mSend = findViewById(R.id.chat_send);

        mSwitchChatType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mChatContent.setHint("发送弹幕聊天消息");
                } else {
                    mChatContent.setHint("和大家聊点什么吧");
                }
            }
        });
        mSwitchChatType.setChecked(false);
        mChatContent.setHint("和大家聊点什么吧");
        mSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 发送聊天消息。
                sendChatMsg();
            }
        });
    }

    private void sendChatMsg() {
        if (mOnChatSendListener!=null){
            ILVCustomCmd customCmd=new ILVCustomCmd();
            customCmd.setType(ILVText.ILVTextType.eGroupMsg);
            boolean isDanmu=mSwitchChatType.isChecked();
            if (isDanmu){
                customCmd.setCmd(Constants.CMD_CHAT_MSG_DANMU);
            }else {
                customCmd.setCmd(Constants.CMD_CHAT_MSG_LIST);
            }
            customCmd.setParam(mChatContent.getText().toString());
            mOnChatSendListener.onChatSend(customCmd);//设置消息内容
        }
    }

    private OnChatSendListener mOnChatSendListener;

    public void setOnChatSendListener(OnChatSendListener l) {
        mOnChatSendListener = l;
    }

    public interface OnChatSendListener {
        public void onChatSend(ILVCustomCmd msg);
    }
}

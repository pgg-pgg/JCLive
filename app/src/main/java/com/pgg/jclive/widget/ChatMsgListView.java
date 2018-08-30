package com.pgg.jclive.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.pgg.jclive.R;
import com.pgg.jclive.adapter.ChatMsgAdapter;
import com.pgg.jclive.domain.ChatMsgInfo;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class ChatMsgListView extends RelativeLayout {

    private ListView mChatMsgListView;
    private ChatMsgAdapter mChatMsgAdapter;

    public ChatMsgListView(Context context) {
        this(context,null);
    }

    public ChatMsgListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChatMsgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_msg_list, this, true);

        findAllViews();
    }

    private void findAllViews() {
        mChatMsgListView = findViewById(R.id.chat_msg_list);
        mChatMsgAdapter = new ChatMsgAdapter(getContext());
        mChatMsgListView.setAdapter(mChatMsgAdapter);
        mChatMsgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMsgInfo msgInfo = mChatMsgAdapter.getItem(position);
                showUserInfoDialog(msgInfo.getSenderId());
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
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                Context context = ChatMsgListView.this.getContext();
                if(context instanceof Activity) {
                    UserInfoDialog userInfoDialog = new UserInfoDialog((Activity) context, timUserProfiles.get(0));
                    userInfoDialog.show();
                }
            }
        });
    }

    public void addMsgInfo(ChatMsgInfo info) {
        if (info != null) {
            mChatMsgAdapter.addMsgInfo(info);
            mChatMsgListView.smoothScrollToPosition(mChatMsgAdapter.getCount());
        }
    }

    public void addMsgInfos(List<ChatMsgInfo> infos) {
        if (infos != null) {
            mChatMsgAdapter.addMsgInfos(infos);
            mChatMsgListView.smoothScrollToPosition(mChatMsgAdapter.getCount());
        }
    }
}

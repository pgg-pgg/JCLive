package com.pgg.jclive.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgg.jclive.R;
import com.pgg.jclive.domain.ChatMsgInfo;
import com.pgg.jclive.utils.ImgUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class ChatMsgAdapter extends BaseAdapter{
    private List<ChatMsgInfo> mChatMsgInfos = new ArrayList<>();
    private Context context;
    public ChatMsgAdapter(Context context) {
        this.context=context;
    }

    /**
     * 添加一个消息
     * @param info
     */
    public void addMsgInfo(ChatMsgInfo info){
        if (info!=null){
            mChatMsgInfos.add(info);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加一组消息
     * @param infos
     */
    public void addMsgInfos(List<ChatMsgInfo> infos){
        if (infos!=null){
            mChatMsgInfos.addAll(infos);
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return mChatMsgInfos.size();
    }

    @Override
    public ChatMsgInfo getItem(int i) {
        return mChatMsgInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatMsgHolder holder=null;
        if (view==null){
            view= LayoutInflater.from(context).inflate(R.layout.view_chat_msg_list_item,null);
            holder=new ChatMsgHolder(view);
            view.setTag(holder);
        }else {
            holder= (ChatMsgHolder) view.getTag();
        }
        holder.bindData(mChatMsgInfos.get(i));
        return view;
    }

    private class ChatMsgHolder {

        private ImageView avatar;
        private TextView content;

        private ChatMsgInfo chatMsgInfo;

        public ChatMsgHolder(View itemView) {

            avatar = (ImageView) itemView.findViewById(R.id.sender_avatar);
            content = (TextView) itemView.findViewById(R.id.chat_content);
        }

        public void bindData(ChatMsgInfo info) {
            chatMsgInfo = info;

            String avatarUrl = info.getAvatar();
            if (TextUtils.isEmpty(avatarUrl)) {
                ImgUtils.loadRound(R.drawable.default_avatar, avatar);
            } else {
                ImgUtils.loadRound(avatarUrl, avatar);
            }
            content.setText(info.getContent());
        }
    }
}

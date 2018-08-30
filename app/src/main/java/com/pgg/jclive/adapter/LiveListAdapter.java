package com.pgg.jclive.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgg.jclive.R;
import com.pgg.jclive.activity.WatcherActivity;
import com.pgg.jclive.domain.RoomInfo;
import com.pgg.jclive.utils.ImgUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class LiveListAdapter extends BaseAdapter {
    private Context mContext;
    private List<RoomInfo> liveRooms = new ArrayList<RoomInfo>();

    public LiveListAdapter(Context context) {
        this.mContext=context;
    }

    /**
     * 移除所有item
     */
    public void removeAllRoomInfos(){
        liveRooms.clear();
    }

    public void addRoomInfos(List<RoomInfo> roomInfos){
        if (roomInfos!=null){
            liveRooms.clear();
            liveRooms.addAll(roomInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return liveRooms.size();
    }

    @Override
    public RoomInfo getItem(int position) {
        return liveRooms.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_live_list,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.bindData(liveRooms.get(position));
        return convertView;
    }

    private class ViewHolder{
        View itemView;
        TextView liveTitle;
        ImageView liveCover;
        ImageView hostAvatar;
        TextView hostName;
        TextView watchNums;

        public ViewHolder(View view) {
            itemView = view;
            liveTitle =  view.findViewById(R.id.live_title);
            liveCover =  view.findViewById(R.id.live_cover);
            hostName =  view.findViewById(R.id.host_name);
            hostAvatar =  view.findViewById(R.id.host_avatar);
            watchNums =  view.findViewById(R.id.watch_nums);
        }

        public void bindData(final RoomInfo roomInfo) {

            String userName = roomInfo.userName;
            if (TextUtils.isEmpty(userName)) {
                userName = roomInfo.userId;
            }
            hostName.setText(userName);

            String liveTitleStr = roomInfo.liveTitle;
            if (TextUtils.isEmpty(liveTitleStr)) {
                this.liveTitle.setText(userName + "的直播");
            } else {
                this.liveTitle.setText(liveTitleStr);
            }
            String url = roomInfo.liveCover;
            if (TextUtils.isEmpty(url)) {
                ImgUtils.load(R.drawable.default_cover, liveCover);
            } else {
                ImgUtils.load(url, liveCover);
            }

            String avatar = roomInfo.userAvatar;
            if (TextUtils.isEmpty(avatar)) {
                ImgUtils.loadRound(R.drawable.default_avatar, hostAvatar);
            } else {
                ImgUtils.loadRound(avatar, hostAvatar);
            }

            int watchers = roomInfo.watcherNums;
            String watchText = watchers + "人\r\n正在看";
            watchNums.setText(watchText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, WatcherActivity.class);
                    intent.putExtra("roomId", roomInfo.roomId);
                    intent.putExtra("hostId", roomInfo.userId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

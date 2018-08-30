package com.pgg.jclive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pgg.jclive.R;
import com.pgg.jclive.utils.ImgUtils;
import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class WatcherAdapter extends RecyclerView.Adapter<WatcherAdapter.WatcherHolder> {

    private Context mContext;
    private List<TIMUserProfile> watcherList = new ArrayList<TIMUserProfile>();
    private Map<String , TIMUserProfile> watcherMap = new HashMap<String , TIMUserProfile>();

    public WatcherAdapter(Context context){
        mContext=context;
    }

    public void addWatchers(List<TIMUserProfile> userProfiles){
        if (userProfiles==null){
            return;
        }
        for (TIMUserProfile userProfile:userProfiles){
            if (userProfile!=null){
                boolean inWatcher=watcherMap.containsKey(userProfile.getIdentifier());
                if (!inWatcher){
                    watcherList.add(userProfile);
                    watcherMap.put(userProfile.getIdentifier(),userProfile);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addWatcher(TIMUserProfile userProfile){
        if(userProfile!=null){
            boolean inWatcher=watcherMap.containsKey(userProfile.getIdentifier());
            if (!inWatcher){
                watcherList.add(userProfile);
                watcherMap.put(userProfile.getIdentifier(),userProfile);
                notifyDataSetChanged();
            }
        }
    }

    public void removeWatcher(TIMUserProfile userProfile) {
        if (userProfile == null) {
            return;
        }

        TIMUserProfile targetUser = watcherMap.get(userProfile.getIdentifier());
        if(targetUser != null) {
            watcherList.remove(targetUser);
            watcherMap.remove(targetUser.getIdentifier());
            notifyDataSetChanged();
        }
    }

    @Override
    public WatcherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_watcher,parent,false);
        WatcherHolder holder = new WatcherHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WatcherHolder holder, int position) {
        if (holder instanceof WatcherHolder) {
            holder.bindData(watcherList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return watcherList.size();
    }


    class WatcherHolder extends RecyclerView.ViewHolder {

        private ImageView avatarImg;

        public WatcherHolder(View itemView) {
            super(itemView);
            avatarImg =  itemView.findViewById(R.id.user_avatar);
        }

        public void bindData(final TIMUserProfile userProfile) {
            String avatarUrl = userProfile.getFaceUrl();
            if (TextUtils.isEmpty(avatarUrl)) {
                ImgUtils.loadRound(R.drawable.default_avatar, avatarImg);
            } else {
                ImgUtils.loadRound(avatarUrl, avatarImg);
            }
            avatarImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onClick(userProfile);
                    }
                }
            });
        }
    }
    private OnClickAvaListener listener;
    public void setOnClickAvaListener(OnClickAvaListener listener){
        this.listener=listener;
    }
    public interface OnClickAvaListener{
        void onClick(TIMUserProfile userProfile);
    }
}

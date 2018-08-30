package com.pgg.jclive.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pgg.jclive.R;
import com.pgg.jclive.adapter.LiveListAdapter;
import com.pgg.jclive.domain.RoomInfo;
import com.pgg.jclive.network.BaseRequest;
import com.pgg.jclive.network.GetLiveListRequest;

import java.util.List;

import butterknife.BindView;

/**
 * Created by pengganggui on 2018/8/30.
 */

public class LiveListFragment extends Fragment {

    @BindView(R.id.titlebar)
    Toolbar titlebar;

    @BindView(R.id.swipe_refresh_layout_list)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.live_list)
    ListView live_list;

    @BindView(R.id.activity_live_list)
    RelativeLayout activity_live_list;

    private LiveListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_live_list, container, false);
        requestLiveList();
        return view;
    }

    /**
     * 请求数据
     */
    private void requestLiveList() {
        GetLiveListRequest listRequest=new GetLiveListRequest();
        listRequest.setOnResultListener(new BaseRequest.OnResultListener<List<RoomInfo>>() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(getActivity(), "请求列表失败：" + msg, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(List<RoomInfo> roomInfos) {
                adapter.removeAllRoomInfos();//下拉刷新
                adapter.addRoomInfos(roomInfos);//添加数据
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        GetLiveListRequest.LiveListParam param=new GetLiveListRequest.LiveListParam();
        param.pageIndex=0;//从0开始
        String url=listRequest.getUrl(param);
        listRequest.request(url);
    }
}

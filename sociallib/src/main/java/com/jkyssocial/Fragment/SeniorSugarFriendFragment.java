package com.jkyssocial.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.DeviceUtil;
import com.jkyssocial.adapter.SeniorSugarFriendFragmentAdapter;
import com.jkyssocial.event.FollowUserEvent;
import com.mintcode.util.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeniorSugarFriendFragment extends AllCircleBaseFragment {

    private MyListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SeniorSugarFriendFragmentAdapter adapter;

    public static SeniorSugarFriendFragment newInstance() {
        SeniorSugarFriendFragment f = new SeniorSugarFriendFragment();
        return f;
    }

    public SeniorSugarFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_senior_sugar_friend, container, false);
        listView = (MyListView) view.findViewById(R.id.zsty_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.zsty_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.social_primary,
                R.color.social_primary,
                R.color.social_primary,
                R.color.social_primary);
        swipeRefreshLayout.setProgressViewOffset(true, 190 * DeviceUtil.getDensity(), 240 * DeviceUtil.getDensity());
        swipeRefreshLayout.setRefreshing(true);
        // 抽象适配器的实例化
        adapter = new SeniorSugarFriendFragmentAdapter(getActivity(), listView, swipeRefreshLayout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        swipeRefreshLayout.setOnRefreshListener(adapter);
        listView.setOnLoadListener(adapter);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        LogUtil.addLog(getContext(), "page-forum-expert-list");
        //访问网络 数据源变化
        return view;
    }

    // Fragment的标题
    private String title;

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getFragmentTitle() {
        if (title != null) {
            return title;
        }
        return "资深糖友";
    }

    public void onEventMainThread(FollowUserEvent event) {
        if (adapter != null)
            adapter.followUser(event.buddyId, event.follow);
    }

}

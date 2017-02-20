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

import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.DeviceUtil;
import com.jkyssocial.adapter.ExperDoctorFragmentAdapter;
import com.jkyssocial.event.FollowUserEvent;
import com.mintcode.util.LogUtil;

import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpertDoctorFragment extends AllCircleBaseFragment{
    private MyListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    //重新写了适配器,方便功能扩展
    private ExperDoctorFragmentAdapter adapter;

    public static ExpertDoctorFragment newInstance() {
        ExpertDoctorFragment expertDoctorFragment = new ExpertDoctorFragment();
        return expertDoctorFragment;
    }

    public ExpertDoctorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_expert_doctor, container, false);
        listView = (MyListView) view.findViewById(R.id.zjys_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.zjys_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setProgressViewOffset(true, 190 * DeviceUtil.getDensity(), 240 * DeviceUtil.getDensity());
        swipeRefreshLayout.setRefreshing(true);
        adapter = new ExperDoctorFragmentAdapter(getActivity(), listView, swipeRefreshLayout);
        listView.setAdapter(adapter);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnLoadListener(adapter);
        listView.setOnItemClickListener(adapter);
        swipeRefreshLayout.setOnRefreshListener(adapter);
        LogUtil.addLog(getContext(), "page-forum-doctor-list");
//        getData(null);
        return view;
    }

    // Fragment标题
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
        return "生活";
    }

    public void onEventMainThread(FollowUserEvent event) {
        if(adapter != null)
            adapter.followUser(event.buddyId, event.follow);
    }

}

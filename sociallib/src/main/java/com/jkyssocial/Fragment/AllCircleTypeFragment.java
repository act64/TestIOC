package com.jkyssocial.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.DeviceUtil;
import com.jkyssocial.adapter.AllCircleTypeAdapter;
import com.jkyssocial.event.FollowCircleEvent;

import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllCircleTypeFragment extends AllCircleBaseFragment{

    private MyListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AllCircleTypeAdapter adapter;

    public AllCircleTypeFragment() {
        // Required empty public constructor
    }

    public static AllCircleTypeFragment newInstance(String code, String name) {
        AllCircleTypeFragment f = new AllCircleTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        f.setArguments(bundle);
        f.setTitle(name);
        Log.i("Type--newInstance", "--newInstance--" + code) ;
        return f;
    }

    private String code;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("Type--onAttach", "--onAttach--" + code) ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        code = bundle.getString("code");
        EventBus.getDefault().register(this);
        Log.i("Type--onCreate", "--onCreate--" + code) ;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_circle_type, container, false);
//        Bundle bundle = getArguments();
//        code = bundle.getString("code");

        listView = (MyListView) view.findViewById(R.id.all_circle_type_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.all_circle_type_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setProgressViewOffset(true, 190 * DeviceUtil.getDensity(), 240 * DeviceUtil.getDensity());
        swipeRefreshLayout.setRefreshing(true);
        // 适配器的实例化
        adapter = new AllCircleTypeAdapter(getActivity(), listView, swipeRefreshLayout, code);
        listView.setAdapter(adapter);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnLoadListener(adapter);
        listView.setOnItemClickListener(adapter);
        swipeRefreshLayout.setOnRefreshListener(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("Type--onActivityCreated", "--onActivityCreated--" + code) ;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Type--onStart", "--onStart--" + code) ;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Type--onPause", "--onPause--" + code) ;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Type--onStop", "--onStop--" + code) ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("Type--onDestroyView", "--onDestroyView--" + code) ;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Log.i("Type--onDestroy", "--onDestroy--" + code) ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("Type--onDetach", "--onDetach--" + code) ;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    // Fragment的标题
    private String title;

    @Override
    public String getFragmentTitle() {
        return title;
    }

    public void onEventMainThread(FollowCircleEvent event) {
        if(adapter != null)
            adapter.followCircle(event.circleId, event.follow);
    }

}






























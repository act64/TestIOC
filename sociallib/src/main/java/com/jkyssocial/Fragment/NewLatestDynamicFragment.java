package com.jkyssocial.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkyssocial.adapter.NewLatestDynamicListAdapter;
import com.jkyssocial.event.ChangSocialLatestDynamicEvent;
import com.jkyssocial.event.DeleteDynamicEvent;
import com.jkyssocial.event.PublishDynamicEvent;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.listener.ListUIListener;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * 糖友动态
 */
public class NewLatestDynamicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    NewLatestDynamicListAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<NewLatestDynamicFragment> fragmentWR;

        public ListUIListenerImpl(NewLatestDynamicFragment fragment) {
            fragmentWR = new WeakReference<NewLatestDynamicFragment>(fragment);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            NewLatestDynamicFragment fragment = fragmentWR.get();
            fragment.swipeRefreshLayout.setEnabled(true);
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            NewLatestDynamicFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.endlessRecyclerOnScrollListener.reset();
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {

        }

    }

    public static NewLatestDynamicFragment newInstance() {
        NewLatestDynamicFragment pageFragment = new NewLatestDynamicFragment();
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment_sugar_friend_dynamic, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        dynamicListAdapter = new NewLatestDynamicListAdapter(getActivity(), new ListUIListenerImpl(this));
        recyclerView.setAdapter(dynamicListAdapter);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                dynamicListAdapter.loadmore();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        swipeRefreshLayout.setColorSchemeResources(R.color.social_primary, R.color.social_primary, R.color.social_primary, R.color.social_primary);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

        dynamicListAdapter.create();
//        LogUtil.addLog(getContext(), "page-forum-recent-topic");

        return view;
    }

    @Override
    public void onRefresh() {
        dynamicListAdapter.refresh();
        EventBus.getDefault().post(new ChangSocialLatestDynamicEvent(-1));
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        dynamicListAdapter = null;
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    public void onEventMainThread(ChangSocialLatestDynamicEvent event) {
//        if(event.getNum() > 0 && dynamicListAdapter != null)
//            dynamicListAdapter.refresh();
//    }

    public void onEventMainThread(DeleteDynamicEvent event) {
        if (dynamicListAdapter != null)
            dynamicListAdapter.removeItem(event.getDynamic().getDynamicId());
    }

    public void onEventMainThread(PublishDynamicEvent event) {
        if(dynamicListAdapter != null)
            dynamicListAdapter.refresh();
    }
}
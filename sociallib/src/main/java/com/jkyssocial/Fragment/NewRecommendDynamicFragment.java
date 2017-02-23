package com.jkyssocial.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkys.sociallib.R2;
import com.jkyssocial.adapter.NewRecommendDynamicListAdapter;
import com.jkyssocial.event.ChangSocialLatestDynamicEvent;
import com.jkyssocial.event.DeleteDynamicEvent;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.listener.ListUIListener;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * 糖友动态
 */
public class NewRecommendDynamicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    NewRecommendDynamicListAdapter dynamicListAdapter;

    LinearLayoutManager linearLayoutManager;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private Unbinder unbinder;

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<NewRecommendDynamicFragment> fragmentWR;

        public ListUIListenerImpl(NewRecommendDynamicFragment fragment) {
            fragmentWR = new WeakReference<NewRecommendDynamicFragment>(fragment);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            NewRecommendDynamicFragment fragment = fragmentWR.get();
            fragment.swipeRefreshLayout.setEnabled(true);
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            NewRecommendDynamicFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.endlessRecyclerOnScrollListener.reset();
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {

        }

    }

    public static NewRecommendDynamicFragment newInstance() {
        NewRecommendDynamicFragment pageFragment = new NewRecommendDynamicFragment();
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment_sugar_friend_dynamic, container, false);
        unbinder=  ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        dynamicListAdapter = new NewRecommendDynamicListAdapter(getActivity(), new ListUIListenerImpl(this));
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
//        AppImpl.getAppRroxy().addLog(getContext(), "page-forum-recent-topic");

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
        unbinder.unbind();
    }

//    public void onEventMainThread(ChangSocialLatestDynamicEvent event) {
//        if(event.getNum() > 0 && dynamicListAdapter != null)
//            dynamicListAdapter.refresh();
//    }

    public void onEventMainThread(DeleteDynamicEvent event) {
        if (dynamicListAdapter != null)
            dynamicListAdapter.removeItem(event.getDynamic().getDynamicId());
    }
}
package com.jkyssocial.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.adapter.PersonalSpaceDynamicListAdapter;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.event.DeleteDynamicEvent;
import com.jkyssocial.event.PrePublishDynamicEvent;
import com.jkyssocial.event.PublishDynamicEvent;
import com.jkyssocial.listener.EndlessRecyclerOnScrollListener;
import com.jkyssocial.listener.ListUIListener;
import com.jkyssocial.listener.ScrollToTopListener;


import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/6/3
 * Time: 17:35
 * Email:AndroidZern@163.com
 */
public class MyDynamicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.tv_bg_message)
    TextView emptyMessage;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.scrollToTop)
    ImageView scrollToTop;

    Buddy otherBuddy;
    Buddy myBuddy;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    PersonalSpaceDynamicListAdapter dynamicListAdapter;

    static class ListUIListenerImpl implements ListUIListener {
        private WeakReference<MyDynamicFragment> fragmentWR;

        public ListUIListenerImpl(MyDynamicFragment fragment) {
            fragmentWR = new WeakReference<MyDynamicFragment>(fragment);
        }

        @Override
        public void afterCreateUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            MyDynamicFragment fragment = fragmentWR.get();
            fragment.swipeRefreshLayout.setEnabled(true);
            switch (resultCode) {
                case ListUIListener.SUCCESS_CODE:
                    fragment.emptyMessage.setVisibility(View.GONE);
                    break;
                case ListUIListener.NETWORK_NULL_LIST_CODE:
                    break;
                case ListUIListener.NULL_LIST_CODE:
                    fragment.emptyMessage.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void afterRefreshUI(int resultCode) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            MyDynamicFragment fragment = fragmentWR.get();
            if (fragment.swipeRefreshLayout != null)
                fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.endlessRecyclerOnScrollListener.reset();
            switch (resultCode) {
                case ListUIListener.SUCCESS_CODE:
                    fragment.emptyMessage.setVisibility(View.GONE);
                    break;
                case ListUIListener.NETWORK_NULL_LIST_CODE:
                    break;
                case ListUIListener.NULL_LIST_CODE:
                    fragment.emptyMessage.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void afterLoadmoreUI(int resultCode) {
        }

    }

    public static MyDynamicFragment newInstance() {
        MyDynamicFragment myDynamicFragment = new MyDynamicFragment();
        return myDynamicFragment;
    }

    @Override
    public void onDestroyView() {
        dynamicListAdapter = null;
//        Log.e("内存","dynamicListAdapter = null");
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_dynamic, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    private void initAdapter() {
        myBuddy = CommonInfoManager.getInstance().getUserInfo(getActivity());
        NewPersonalSpaceActivity activity = (NewPersonalSpaceActivity) getActivity();
        otherBuddy = activity.getOtherBuddy();
        // 如果是别人的空间
        if (otherBuddy != null) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_WITHOUT_CACHE, new RequestManager.RequestListener<GetUserInfoResult>() {
                @Override
                public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
                    if (data != null && data.getBuddy() != null) {
                        otherBuddy = data.getBuddy();
                    }
                }
            }, 1, getActivity(), otherBuddy.getBuddyId());

            dynamicListAdapter = new PersonalSpaceDynamicListAdapter(getActivity(),
                    new ListUIListenerImpl(this), otherBuddy);
        } else { // 是自己的空间
            dynamicListAdapter = new PersonalSpaceDynamicListAdapter(getActivity(),
                    new ListUIListenerImpl(this), myBuddy);
        }
    }

    private void initView() {
        initAdapter();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
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

        ScrollToTopListener scrollToTopListener = new ScrollToTopListener(linearLayoutManager) {
            @Override
            public void onHide() {
                scrollToTop.setVisibility(View.GONE);
            }

            @Override
            public void onShow() {
                scrollToTop.setVisibility(View.VISIBLE);
            }
        };
        recyclerView.addOnScrollListener(scrollToTopListener);
        dynamicListAdapter.create();

    }

   @OnClick(R2.id.scrollToTop)
    void onScrollToTop(View view) {
        recyclerView.smoothScrollToPosition(0);
    }


    @Override
    public void onRefresh() {
        dynamicListAdapter.refresh();
    }

    public void onEventMainThread(PublishDynamicEvent event) {
        if (dynamicListAdapter != null)
            dynamicListAdapter.replaceItem(event.getDynamic(), event.getSendingDynamic());
    }

    public void onEventMainThread(PrePublishDynamicEvent event) {
        Dynamic sendingDynamic = event.getDynamic();
        dynamicListAdapter.addItem(sendingDynamic);
        recyclerView.scrollToPosition(0);
        emptyMessage.setVisibility(View.GONE);
    }

    public void onEventMainThread(DeleteDynamicEvent event) {
        if (dynamicListAdapter != null && (otherBuddy == null || myBuddy.getBuddyId().equals(otherBuddy.getBuddyId()))) {
            dynamicListAdapter.removeItem(event.getDynamic().getDynamicId());
        }
        if (dynamicListAdapter.getItemCount() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }
}

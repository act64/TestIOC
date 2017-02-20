package com.jkyssocial.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkys.tools.MainSelector;
import com.jkysshop.model.ShopLoginStatus;
import com.jkyssocial.activity.MessageCenterActivity;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.activity.PublishDynamicActivity;
import com.jkyssocial.activity.NewSocialMainActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.GetUserInfoResult;
import com.jkyssocial.event.ChangSocialMessageEvent;
import com.jkyssocial.event.ChangeUserInfoEvent;
import com.jkyssocial.pageradapter.CircleFragmentPagerAdapter;
import com.mintcode.util.DensityUtils;
import com.mintcode.util.ImageManager;
import com.mintcode.util.Keys;
import com.mintcode.util.LogUtil;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;
import de.greenrobot.event.EventBus;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 糖友圈社区首页
 *
 * @author yangxiaolong
 */
public class SocialMainFragment extends SocialBaseFragment {

    private static final int MESSAGE_READ = 1;
    private static final int LATEST_DYNAMIC_READ = 2;
    private static final int DYNAMIC_DETAIL = 3;

    @Bind(R.id.main_content)
    View rootView;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    /**
     * 小圆点的父控件
     */
//    @Bind(R.id.ll_dot)
//    LinearLayout llDot;

    @Bind(R.id.my_avatar)
    ImageView myAvatar;

    @Bind(R.id.message_unread_num)
    TextView messageUnreadNum;

    @Bind(R.id.message_unread_Layout)
    RelativeLayout messageUnreadLayout;

    @Bind(R.id.guidance)
    ViewStub guidance;

//    @Bind(R.id.social_main_swipeRefreshLayout)
//    SwipeRefreshLayout swipeRefreshLayout;

//    @Bind(R.id.send_dynamic)
//    TextView sendDynamicTv;

    CircleFragmentPagerAdapter pagerAdapter;

    private Buddy myBuddy;

    static class GetUserInfoRequestListener implements RequestManager.RequestListener<GetUserInfoResult> {
        WeakReference<SocialMainFragment> fragmentWR;

        public GetUserInfoRequestListener(SocialMainFragment fragment) {
            fragmentWR = new WeakReference<SocialMainFragment>(fragment);
        }

        @Override
        public void processResult(int requestCode, int resultCode, GetUserInfoResult data) {
            if (fragmentWR == null || fragmentWR.get() == null)
                return;
            SocialMainFragment fragment = fragmentWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data != null && data.getBuddy() != null) {
                    fragment.myBuddy = data.getBuddy();
                    if (fragment.myAvatar != null && !TextUtils.isEmpty(fragment.myBuddy.getImgUrl())) {
                        ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + fragment.myBuddy.getImgUrl(),
                                fragment.getContext(), fragment.myAvatar, ImageManager.avatarOptions);
                    }
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerAdapter = new CircleFragmentPagerAdapter(getChildFragmentManager(), getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.social_fragment_social_main, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        if (CommonInfoManager.showGuidance(getContext(), getClass().getName())) {
            final RelativeLayout guidanceLayout = (RelativeLayout) guidance.inflate();
            final ImageView imageView = (ImageView) guidanceLayout.findViewById(R.id.image_shower);
            final FancyButton fancyButton = (FancyButton) guidanceLayout.findViewById(R.id.i_know);
//            fancyButton.setVisibility(View.GONE);
            fancyButton.setVisibility(View.VISIBLE);
            imageView.setPadding(0, 0, DensityUtils.dipTopx(getContext(), 15), 0);
            imageView.setImageResource(R.drawable.social_main_guidance_see_personal);
            guidanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    imageView.setPadding(DensityUtils.dipTopx(getContext(), 10), DensityUtils.dipTopx(getContext(), 7), 0, 0);
//                    imageView.setImageResource(R.drawable.social_main_guidance_add_circle);
//                    fancyButton.setVisibility(View.VISIBLE);
                    guidanceLayout.setVisibility(View.GONE);
                }
            });
            fancyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guidanceLayout.setVisibility(View.GONE);
                }
            });
        }
//        if("Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)){
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
//            layoutParams.height = DensityUtils.dipTopx(getContext(), 70);
//            toolbar.setLayoutParams(layoutParams);
//        }
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setTabTextColors(Color.parseColor("#90FFFFFF"), Color.parseColor("#FFFFFF"));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        String uid = mValueDBService.findValue(Keys.UID);
        if (uid != null && !"-1000".equals(uid)) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE,
                    new GetUserInfoRequestListener(this), 1, getActivity().getApplicationContext(), null);
        }
        myBuddy = CommonInfoManager.getInstance().getUserInfo(getContext());
//        initSwipeRefreshLayout();
        return view;
    }

    @OnClick(R.id.avatarArea)
    public void goToPersonalSpace(View view) {
        if (MainSelector.isNeedNewMain())
            return;
        if (showLoginDialog())
            return;
        if (myBuddy != null) {
            //TODO 测试
            Intent intent = new Intent(getActivity(), NewPersonalSpaceActivity.class);
//            Intent intent = new Intent(getActivity(), NewSocialMainActivity.class);
            intent.putExtra("myBuddy", myBuddy);
            startActivity(intent);
        }
    }

    public final static String from = "SocialMain";

    @OnClick(R.id.send_dynamic)
    void publishDynamic(View view) {
        if (showLoginDialog())
            return;
        if (myBuddy != null) {
            Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
            intent.putExtra("from", from);
            startActivity(intent);
        }
    }

    @OnClick(R.id.message_unread_num)
    public void goToMessageCenter(View view) {
        if (showLoginDialog())
            return;
        Intent intent = new Intent(getActivity(), MessageCenterActivity.class);
        startActivity(intent);
    }

    @OnPageChange(value = R.id.viewPager, callback = OnPageChange.Callback.PAGE_SELECTED)
    void onPageSelected(int position) {
        if (position == 1) {
            LogUtil.addLog(getContext(), "event-forum-circle-tab");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(ChangSocialMessageEvent event) {
        if (messageUnreadLayout != null) {
            messageUnreadLayout.setVisibility(event.getNum() > 0 ? View.VISIBLE : View.GONE);
            messageUnreadNum.setText("新消息 (" + event.getNum() + ")");
        }
    }

    public void onEventMainThread(ChangeUserInfoEvent event) {
        String uid = mValueDBService.findValue(Keys.UID);
        if (uid != null && !"-1000".equals(uid)) {
            ApiManager.getUserInfo(ApiManager.TYPE_REQUEST_UPDATE_CACHE,
                    new GetUserInfoRequestListener(this), 1, getActivity().getApplicationContext(), null);
        }
    }


    // LoginHelper在登出的时候发出一个通知用来更新社区用户头像
    public void onEventMainThread(ShopLoginStatus event) {
        myAvatar.setImageResource(R.drawable.social_new_avatar);
    }

    public void onEventMainThread(Buddy buddy) {
        if (buddy != null) {
            myBuddy = buddy;
            if (myAvatar != null && !TextUtils.isEmpty(myBuddy.getImgUrl())) {
                ImageManager.loadImage(BuildConfig.STATIC_PIC_PATH + myBuddy.getImgUrl(), getContext(),
                        myAvatar, ImageManager.avatarOptions);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}

package com.jkyssocial.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jkys.common.listeners.SocailTaskListener;
import com.jkys.jkysbase.BaseCommonUtil;
import com.jkys.jkysbase.TimeUtil;
import com.jkys.jkyswidget.CustomSpinner;
import com.jkys.jkyswidget.MyListView;
import com.jkys.proxy.AppImpl;
import com.jkys.sociallib.R;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.CommonInfoManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.AddReplyResult;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.ListMessageResult;
import com.jkyssocial.data.Message;
import com.jkyssocial.event.ChangSocialMessageEvent;
import com.mintcode.base.BaseActivity;
import com.mintcode.util.ImageManager;
import com.mintcode.util.Keys;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 糖友圈消息中心页
 *
 * @author yangxiaolong
 */
public class MessageCenterActivity extends BaseActivity implements View.OnTouchListener {

    public static final int SUCCESS = 10000;
    View emptyView;

    MyListView mListView;

    MessageCenterAdapter mAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    Buddy myBuddy;

    boolean firstReadSuccess = false;

    LinearLayout editLinear;

    EditText editText;

    TextView sendComment;

    /*
    0:全部消息；1:回复消息；2:点赞消息
     */
    private int qType;

    private static final String[] messageTypes = {"全部消息", "回复消息", "点赞消息"};
    private CustomSpinner spinner;
    private ArrayAdapter<String> adapter;
    private ImageView imgTip;

    static class AddReplyRequestListener implements RequestManager.RequestListener<AddReplyResult> {
        private WeakReference<MessageCenterActivity> activityWR;

        private AddReplyRequestListener(MessageCenterActivity activity) {
            activityWR = new WeakReference<MessageCenterActivity>(activity);
        }

        @Override
        public void processResult(int requestCode, int resultCode, AddReplyResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            MessageCenterActivity activity = activityWR.get();
            if (data != null && data.getReply() != null) {
                activity.editLinear.setVisibility(View.GONE);
                BaseCommonUtil.hideKeyBoard(activity);
                activity.editText.setText(null);
                AppImpl.getAppRroxy().getTaskReward(new SocailTaskListener(activity, "每天第一次回复"), "article/reply");
                Toast.makeText(activity.getApplicationContext(), "回复成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class MsgListRequestListener implements RequestManager.RequestListener<ListMessageResult> {
        private WeakReference<MessageCenterActivity> activityWR;
        private MessageCenterAdapter mAdapter;

        private MsgListRequestListener(MessageCenterActivity activity, MessageCenterAdapter adapter) {
            activityWR = new WeakReference<MessageCenterActivity>(activity);
            mAdapter = adapter;
        }

        @Override
        public void processResult(int requestCode, int resultCode, final ListMessageResult data) {
            if (activityWR == null || activityWR.get() == null)
                return;
            final MessageCenterActivity activity = activityWR.get();
            if (resultCode == RequestManager.RESULT_SUCCESS_CODE) {
                if (data.getReturnCode().equals("0000")) {
                    if (data.getMsgList() != null && !data.getMsgList().isEmpty()) {
                        if (requestCode == mAdapter.REFRESH_CODE) {
                            mAdapter.curList.clear();
                            activity.emptyView.setVisibility(View.GONE);
                            activity.mListView.resumeLoad();
                            mAdapter.curList.addAll(data.getMsgList());
                            mAdapter.notifyDataSetChanged();
                            activity.mListView.requestFocusFromTouch();
                            activity.mListView.setSelection(0);
                            activity.mListView.requestFocus();
                        } else {
                            mAdapter.curList.addAll(data.getMsgList());
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (requestCode == mAdapter.REFRESH_CODE) {
                            mAdapter.curList.clear();
                            activity.emptyView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        } else
                            activity.mListView.forbidLoad("已经全部加载完毕", true);
                    }
                    activity.firstReadSuccess = true;
                    EventBus.getDefault().post(new ChangSocialMessageEvent(0));
                }
            } else {
                //TODO
            }
            activity.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    activity.swipeRefreshLayout.setRefreshing(false);
                }
            });
            activity.mListView.endLoad();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_activity_message_center);
        myBuddy = CommonInfoManager.getInstance().getUserInfo(this);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinner = (CustomSpinner) findViewById(R.id.spinner);
        imgTip = (ImageView) findViewById(R.id.img_tip);
        imgTip.setColorFilter(Color.argb(255, 255, 255, 255));
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_message_item, messageTypes);
        spinner.setAdapter(adapter);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(imgTip, "rotation", 0f, 180f).setDuration(350);
                animator.start();
            }

            @Override
            public void onSpinnerClosed() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(imgTip, "rotation", 180f, 0f).setDuration(350);
                animator.start();
            }
        });
        //设置默认值
        spinner.setVisibility(View.VISIBLE);

        // Set the adapter
        mListView = (MyListView) findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        emptyView = findViewById(R.id.empty);
        mAdapter = new MessageCenterAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnLoadListener(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(mAdapter);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        editLinear = (LinearLayout) findViewById(R.id.editLinear);
        editText = (EditText) editLinear.findViewById(R.id.commentEdit);
        sendComment = (TextView) editLinear.findViewById(R.id.sendComment);

        AppImpl.getAppRroxy().addLog(this, "page-forum-topic-list");


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // mSpin is our custom Spinner
        if (spinner.hasBeenOpened() && hasFocus) {
            spinner.performClosedEvent();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (firstReadSuccess) {
           AppImpl.getAppRroxy().deleteCashDBServiceKey(Keys.SOCIAL_MESSAGE_UNREAD_NUM);
            setResult(SUCCESS);
        }
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            if (qType != arg2) {
                qType = arg2;
                mAdapter.switchType();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        editLinear.setVisibility(View.GONE);
        BaseCommonUtil.hideKeyBoard(this);
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        editLinear.setVisibility(View.GONE);
        BaseCommonUtil.hideKeyBoard(this);
        return false;
    }

    class MessageCenterAdapter extends BaseAdapter implements OnRefreshListener, MyListView.OnLoadListener {

        private static final int COUNT = 20;

        private int REFRESH_CODE = 1;

        private int LOAD_MORE_CODE = 2;

        List<Message> listAll;
        List<Message> listComment;
        List<Message> listZan;
        List<Message> curList;

        DynamicDetailClickListener dynamicDetailClickListener;
        GoPersonalSpaceClickListener goPersonalSpaceClickListener;
        OpenCommentLinearListener openCommentLinearListener;
        private boolean switching = false;

        public MessageCenterAdapter() {
            listAll = new ArrayList<Message>();
            listComment = new ArrayList<Message>();
            listZan = new ArrayList<Message>();
            curList = listAll;
            dynamicDetailClickListener = new DynamicDetailClickListener();
            goPersonalSpaceClickListener = new GoPersonalSpaceClickListener();
            openCommentLinearListener = new OpenCommentLinearListener();
            getData(null);
        }

        private void getData(final Long baseTime) {
            int requestCode = baseTime == null ? REFRESH_CODE : LOAD_MORE_CODE;
            if (requestCode == REFRESH_CODE)
                mListView.forbidLoad("", true);
            ApiManager.listMsg(new MsgListRequestListener(MessageCenterActivity.this, this), requestCode, MessageCenterActivity.this, qType, baseTime, COUNT);
        }

        @Override
        public int getCount() {
            return curList.size();
        }

        @Override
        public Object getItem(int position) {
            return curList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            Message message = curList.get(position);
            if (message.getType() == 3)
                return 1;
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message message = curList.get(position);
            int type = getItemViewType(position);
            if (type == 0)
                return getCommentView(position, message, convertView, parent);
            else if (type == 1)
                return getZanView(position, message, convertView, parent);
            return null;
        }

        private void setSameView(ViewHolder holder, View convertView) {
            holder.headerView = convertView.findViewById(R.id.header);
            holder.avatar = (ImageView) convertView
                    .findViewById(R.id.avatar);
            holder.vFlag = (ImageView) convertView
                    .findViewById(R.id.vFlag);
            holder.nickname = (TextView) convertView
                    .findViewById(R.id.nickname);
            holder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            holder.footer = convertView.findViewById(R.id.footer);
            holder.contentImage = (ImageView) convertView
                    .findViewById(R.id.contentImage);
            holder.contentText = (TextView) convertView
                    .findViewById(R.id.contentText);

            holder.headerView.setOnClickListener(goPersonalSpaceClickListener);
            holder.footer.setOnClickListener(dynamicDetailClickListener);
        }

        protected void processSameView(int position, ViewHolder holder, Message message) {
            holder.position = position;
            if (message.getCreator() != null) {
                if (!TextUtils.isEmpty(message.getCreator().getImgUrl())) {
                    ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH()
                                    + message.getCreator().getImgUrl(), null,
                            holder.avatar, R.drawable.social_new_avatar);
                } else {
                    holder.avatar.setImageResource(R.drawable.social_new_avatar);
                }

                ImageManager.setVFlag(holder.vFlag, message.getCreator());
                holder.nickname.setText(message.getCreator().getUserName());
            }
            holder.headerView.setTag(holder);
            holder.footer.setTag(holder);
            holder.createTime.setText(TimeUtil.getInterval(message.getCreatedTime()));
            if (message.getDynamic() != null) {
                if (message.getDynamic().getImages() != null && message.getDynamic().getImages().size() > 0) {
                    String url = message.getDynamic().getImages().get(0);
                    if (!TextUtils.isEmpty(url)) {
                        ImageManager.loadImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + url, null, holder.contentImage);
                    }

                    holder.contentImage.setVisibility(View.VISIBLE);
                    holder.contentText.setVisibility(View.GONE);
                } else {
                    holder.contentImage.setVisibility(View.GONE);
                    holder.contentText.setVisibility(View.VISIBLE);
                    holder.contentText.setText(message.getDynamic().getContent());
                }
            } else {
                holder.contentImage.setVisibility(View.INVISIBLE);
                holder.contentText.setText("该内容已被删除");
            }
        }

        private View getCommentView(int position, Message message, View convertView, ViewGroup parent) {
            CommentViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.social_item_list_message_comment, parent, false);
                holder = new CommentViewHolder();
                setSameView(holder, convertView);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (CommentViewHolder) convertView.getTag();
            }
            processSameView(position, holder, message);
            holder.content.setText(message.getContent());
            holder.content.setTag(holder);
            holder.content.setOnClickListener(openCommentLinearListener);
            return convertView;
        }

        private View getZanView(int position, Message message, View convertView, ViewGroup parent) {
            ZanViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(
                        R.layout.social_item_list_message_zan, parent, false);
                holder = new ZanViewHolder();
                setSameView(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ZanViewHolder) convertView.getTag();
            }
            processSameView(position, holder, message);
            return convertView;
        }

        public void switchType() {
            if (qType == 0)
                curList = listAll;
            else if (qType == 1)
                curList = listComment;
            else
                curList = listZan;
            if (curList.isEmpty()) {
                switching = true;
                getData(null);
            } else {
                notifyDataSetChanged();
                mListView.requestFocusFromTouch();
                mListView.setSelection(0);
                mListView.requestFocus();
            }
        }

        private void openCommentLinear(Message message) {
            editLinear.setVisibility(View.VISIBLE);
            editText.setHint("回复 " + message.getCreator().getUserName());
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusable(true);
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 300);
//        editText.postDelayed(new Runnable(){
//            @Override
//            public void run()
//            {
//                Rect r = new Rect();
//                View rootview = getWindow().getDecorView(); // this = activity
//                rootview.getWindowVisibleDisplayFrame(r);
//                DisplayMetrics metrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                int screenHeight = metrics.heightPixels - (r.bottom - r.top);
//                int heightDifference = screenHeight - (r.bottom - r.top);
//                rootview.getLocationOnScreen();
//
//            }
//        }, 600);
            sendComment.setTag(message);
            sendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = (Message) v.getTag();
                    String content = editText.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        ApiManager.addReply(new AddReplyRequestListener(MessageCenterActivity.this), 1, MessageCenterActivity.this, message.getArg().getDynamicId(),
                                message.getArg().getCommentId(), message.getCreator().getBuddyId(), content);
                        v.setTag(null);
                    }
                }
            });
        }

        class DynamicDetailClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                int position = holder.position;
                Dynamic dynamic = curList.get(position).getDynamic();
                if (null != dynamic) {
                    if (dynamic.getOwner() == null) {
                        dynamic.setOwner(myBuddy);
                    }
                    Intent intent = new Intent(MessageCenterActivity.this, DynamicDetailActivity.class);
                    intent.putExtra("dynamic", curList.get(position).getDynamic());
                    startActivity(intent);
                }
            }
        }

        class GoPersonalSpaceClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                if (AppImpl.getAppRroxy().isNeedNewMain() )
                    return;
                ViewHolder holder = (ViewHolder) v.getTag();
                int position = holder.position;
                if (curList.get(position).getCreator() != null) {
                    Intent intent = new Intent(MessageCenterActivity.this, NewPersonalSpaceActivity.class);
                    intent.putExtra("otherBuddy", curList.get(position).getCreator());
                    startActivity(intent);
                }
            }
        }

        class OpenCommentLinearListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                int position = holder.position;
                if (curList.get(position).getCreator() != null) {
                    openCommentLinear(curList.get(position));
                }
            }
        }

        public class ViewHolder {
            public View headerView;
            public ImageView avatar;
            public ImageView vFlag;
            public TextView nickname;
            public TextView createTime;
            public View footer;
            public ImageView contentImage;
            public TextView contentText;
            public int position;
        }

        class CommentViewHolder extends ViewHolder {
            public TextView content;
        }

        class ZanViewHolder extends ViewHolder {
            public TextView zan;
        }

        @Override
        public void onRefresh() {
            getData(null);
        }

        @Override
        public void onLoad() {
            if (curList.size() > 0)
                getData(curList.get(curList.size() - 1).getCreatedTime());
        }

    }

}

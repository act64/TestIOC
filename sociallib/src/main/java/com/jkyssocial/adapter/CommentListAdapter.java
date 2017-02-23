package com.jkyssocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jkys.jkysbase.TimeUtil;
import com.jkys.jkysbase.util.TouchListenerForLinkMoncementMethod;
import com.jkys.jkyswidget.MyListView;
import com.jkys.tools.MainSelector;
import com.jkyssocial.activity.DynamicDetailActivity;
import com.jkyssocial.activity.NewPersonalSpaceActivity;
import com.jkyssocial.common.manager.ApiManager;
import com.jkyssocial.common.manager.RequestManager;
import com.jkyssocial.data.Buddy;
import com.jkyssocial.data.Comment;
import com.jkyssocial.data.CommentListResult;
import com.jkyssocial.data.Dynamic;
import com.jkyssocial.data.Reply;
import com.mintcode.util.ImageManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dreamplus.wentang.BuildConfig;
import cn.dreamplus.wentang.R;

/**
 * Created by yangxiaolong on 15/9/2.
 */
public class CommentListAdapter extends BaseAdapter implements SwipeRefreshLayout.OnRefreshListener,
        MyListView.OnLoadListener, Response.ErrorListener,
        RequestManager.RequestListener<CommentListResult> {

    protected int REFRESH_CODE = 1;

    protected int LOAD_MORE_CODE = 2;
    protected static final int COUNT = 20;

    //    private Activity activity;
    private Context context;
    private WeakReference<Activity> activityWR;

    private RefreshListener refreshListener;

    private MyListView myListView;

    private Dynamic dynamic;

    private OpenReplyListener openReplyListener;

    GoPersonalClickListener goPersonalClickListener;

    ReplyClickListener replyClickListener;

    CommentClickListener commentClickListener;

    // 最大显示多少行
    private int maxImageRowCount = 3;

    // 最大显示多少列
    private int maxImageColumnCount = 3;

    protected int maxImageCount = maxImageRowCount * maxImageColumnCount;

    /**
     * 是否正在加载
     */
    private boolean isLoadingData = false;

    private boolean setSelectionAfterHeaderView = false;

    List<Comment> list;

    private View emptyView;

    public CommentListAdapter(Activity activity, MyListView myListView, Dynamic dynamic,
                              RefreshListener refreshListener, OpenReplyListener openReplyListener, View emptyView,
                              DynamicDetailActivity.CommentListener commentListener) {
//        this.activity = activity;
        activityWR = new WeakReference<Activity>(activity);
        context = activity.getApplicationContext();
        this.myListView = myListView;
        this.dynamic = dynamic;
        this.emptyView = emptyView;
        this.refreshListener = refreshListener;
        this.openReplyListener = openReplyListener;
        list = new ArrayList<Comment>();
        goPersonalClickListener = new GoPersonalClickListener();
        replyClickListener = new ReplyClickListener();
        commentClickListener = new CommentClickListener();
        getData(null);
    }


    protected void getData(Long baseTime) {
        int requestCode = baseTime == null ? REFRESH_CODE : LOAD_MORE_CODE;
        ApiManager.listComment(this, requestCode, context, dynamic.getDynamicId(), baseTime, COUNT);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.social_item_list_text_comment, parent, false);
            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView
                    .findViewById(R.id.avatar);
            holder.vFlag = (ImageView) convertView
                    .findViewById(R.id.vFlag);
            holder.commentArea = convertView.findViewById(R.id.commentArea);
            holder.username = (TextView) convertView
                    .findViewById(R.id.username);
            holder.comment = (TextView) convertView
                    .findViewById(R.id.comment);
            holder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            holder.commentLinear = (LinearLayout) convertView
                    .findViewById(R.id.commentLinear);
//            holder.replyViewList = new ArrayList<>();
            holder.replyViewList1 = new ArrayList<>();
            holder.avatar.setOnClickListener(goPersonalClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(comment.getOwner().getImgUrl())) {
            ImageManager.loadImageByDefaultImage( AppImpl.getAppRroxy().getSTATIC_PIC_PATH() + comment.getOwner().getImgUrl(),
                    null, holder.avatar, R.drawable.social_new_avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.social_new_avatar);
        }

        holder.avatar.setTag(comment.getOwner());
        holder.username.setText(comment.getOwner().getUserName());
        holder.username.setTag(comment.getOwner());
        holder.username.setOnClickListener(goPersonalClickListener);
        ImageManager.setVFlag(holder.vFlag, comment.getOwner());
        holder.commentArea.setTag(comment);
        addCommentLongClick(holder.commentArea);
        holder.commentArea.setOnClickListener(commentClickListener);

        holder.createTime.setText(TimeUtil.getInterval(comment.getCreatedTime()));
        holder.comment.setTag(comment);
        holder.comment.setTag(R.id.tag_third, null);
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(comment.getOwner().getUserName());
//        ClickableSpan clickableSpan = new GoPersonalClickSpan(comment.getOwner());
//        spannableStringBuilder.setSpan(clickableSpan, 0, spannableStringBuilder.length(), 0);
//        spannableStringBuilder.append(" : " + comment.getContent());
//        holder.comment.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
        holder.comment.setText(comment.getContent());
        holder.comment.setOnTouchListener(new TouchListenerForLinkMoncementMethod());

        List<Reply> replyList = comment.getReplyList();
        if (replyList == null || replyList.isEmpty()) {
            holder.commentLinear.setVisibility(View.GONE);
        } else {
            holder.commentLinear.setVisibility(View.VISIBLE);
            for (int i = 0; i < replyList.size(); ++i) {
                if (i >= holder.replyViewList1.size()) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout replyLinear = (LinearLayout) inflater.inflate(R.layout.social_item_list_text_comment_reply, parent, false);
                    TextView replyName = (TextView) replyLinear.findViewById(R.id.replyName);
                    TextView replyContent = (TextView) replyLinear.findViewById(R.id.replyContent);
                    TextView replyTime = (TextView) replyLinear.findViewById(R.id.replyTime);
                    ReplyView replyView = new ReplyView(replyLinear, replyName, replyContent, replyTime);
                    holder.replyViewList1.add(replyView);
                    holder.commentLinear.addView(replyLinear);
                }
                Reply reply = replyList.get(i);
                holder.replyViewList1.get(i).replyLinear.setOnClickListener(replyClickListener);
                holder.replyViewList1.get(i).replyLinear.setTag(reply);
                addReplyLongClick(holder.replyViewList1.get(i).replyLinear);

                String ownerName = reply.getOwner() == null ? " " : reply.getOwner().getUserName();
                String targetName = reply.getTargetBuddy() == null ? " " : reply.getTargetBuddy().getUserName();
                SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(ownerName);
                ClickableSpan clickableSpanOwner = new GoPersonalClickSpan(reply.getOwner());
                spannableStringBuilder1.setSpan(clickableSpanOwner, 0, spannableStringBuilder1.length(), 0);
                spannableStringBuilder1.append(" 回复 ");
                ClickableSpan clickableSpanTargetBuddy = new GoPersonalClickSpan(reply.getTargetBuddy());
                int len1 = spannableStringBuilder1.length();
                spannableStringBuilder1.append(targetName);
                spannableStringBuilder1.setSpan(clickableSpanTargetBuddy, len1,
                        spannableStringBuilder1.length(), 0);

                holder.replyViewList1.get(i).replyName.setText(spannableStringBuilder1, TextView.BufferType.SPANNABLE);
                holder.replyViewList1.get(i).replyName.setOnTouchListener(new TouchListenerForLinkMoncementMethod());
                holder.replyViewList1.get(i).replyContent.setText(reply.getContent());
                holder.replyViewList1.get(i).replyTime.setText(TimeUtil.getInterval(reply.getCreatedTime()));
                holder.replyViewList1.get(i).replyLinear.setVisibility(View.VISIBLE);
            }
            for (int i = replyList.size(); i < holder.replyViewList1.size(); ++i) {
                holder.replyViewList1.get(i).replyLinear.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    protected void addCommentLongClick(View commentView) {
    }

    protected void addReplyLongClick(View replyView) {
    }

    public void addItem(Comment comment) {
        list.add(0, comment);
        notifyDataSetChanged();
    }

    public void addReply(Reply reply) {
        for (Comment comment : list) {
            if (comment.getCommentId().equals(reply.getCommentId())) {
                List<Reply> replyList = comment.getReplyList();
                if (replyList == null) {
                    replyList = new ArrayList<>();
                    comment.setReplyList(replyList);
                }
                replyList.add(reply);
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(String commentId) {
        int i = 0;
        for (Comment comment1 : list) {
            if (comment1.getCommentId().equals(commentId)) {
                list.remove(i);
                break;
            }
            i++;
        }
        notifyDataSetChanged();
    }

    public void removeReply(String commentId, String replyId) {
        for (Comment comment : list) {
            if (comment.getCommentId().equals(commentId)) {
                List<Reply> replyList = comment.getReplyList();
                int i = 0;
                for (Reply reply1 : replyList) {
                    if (reply1.getReplyId().equals(replyId)) {
                        replyList.remove(i);
                        break;
                    }
                    i++;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSetSelectionAfterHeaderView() {
        this.setSelectionAfterHeaderView = true;
    }

    @Override
    public void processResult(int requestCode, int resultCode, CommentListResult data) {
        if (data != null && data.getCommentList() != null && !data.getCommentList().isEmpty()) {
            if (requestCode == REFRESH_CODE) {
                list = new ArrayList<Comment>();
                emptyView.setVisibility(View.GONE);
                myListView.resumeLoad();
            }
//            if(data.getCommentList().size() < COUNT)
//                myListView.forbidLoad("已经全部加载完毕", true);
            list.addAll(data.getCommentList());
            notifyDataSetChanged();
            if (setSelectionAfterHeaderView) {
                setSelectionAfterHeaderView = false;
                myListView.requestFocusFromTouch();
                myListView.setSelection(1);
                myListView.requestFocus();
            }
        } else {
            if (requestCode == REFRESH_CODE) {
                list = new ArrayList<Comment>();
                emptyView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            } else
                myListView.forbidLoad("已经全部加载完毕", true);
        }
        isLoadingData = false;
        myListView.endLoad();
        if (refreshListener != null)
            refreshListener.stopRefresh();
    }

    public static class ViewHolder {
        public ImageView avatar;
        public ImageView vFlag;

        public View commentArea;
        public TextView username;
        public TextView comment;
        public TextView createTime;

        public LinearLayout commentLinear;
        //        public List<TextView> replyViewList;
        public List<ReplyView> replyViewList1;
    }

    public static class ReplyView {
        public LinearLayout replyLinear;
        public TextView replyName;
        public TextView replyContent;
        public TextView replyTime;

        public ReplyView(LinearLayout replyLinear, TextView replyName, TextView replyContent, TextView replyTime) {
            this.replyLinear = replyLinear;
            this.replyName = replyName;
            this.replyContent = replyContent;
            this.replyTime = replyTime;
        }
    }

    @Override
    public void onRefresh() {
        getData(null);
    }

    @Override
    public void onLoad() {
        if (list.size() > 0) {
            getData(list.get(list.size() - 1).getCreatedTime());
        } else {
            myListView.forbidLoad("已经全部加载完毕", true);
        }
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        isLoadingData = false;
//        swipeRefreshLayout.setRefreshing(false);
        if (refreshListener != null) {
            refreshListener.stopRefresh();
        }
        myListView.endLoad();
    }

    public interface RefreshListener {
        public void stopRefresh();
    }

    public interface OpenReplyListener {
        public void openReply(AddCommentParam param);
    }

    public static class AddCommentParam {
        public String commentId;
        public String targetBuddyId;
        public String targetUserName;

        public AddCommentParam(String commentId, String targetBuddyId, String targetUserName) {
            this.commentId = commentId;
            this.targetBuddyId = targetBuddyId;
            this.targetUserName = targetUserName;
        }
    }

    class GoPersonalClickSpan extends ClickableSpan {

        private Buddy buddy;

        GoPersonalClickSpan(Buddy buddy) {
            this.buddy = buddy;
        }

        @Override
        public void onClick(View v) {
            if (AppImpl.getAppRroxy().isNeedNewMain() )
                return;
            if (v.getTag(R.id.tag_third) != null) {
                v.setTag(R.id.tag_third, null);
                return;
            }
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            Intent intent = new Intent(activity, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", buddy);
            activity.startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.social_primary));
        }
    }

    class GoPersonalClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (AppImpl.getAppRroxy().isNeedNewMain() )
                return;
            if (activityWR == null || activityWR.get() == null)
                return;
            Activity activity = activityWR.get();
            Intent intent = new Intent(activity, NewPersonalSpaceActivity.class);
            intent.putExtra("otherBuddy", (Buddy) v.getTag());
            activity.startActivity(intent);
        }
    }

    class ReplyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Reply reply = (Reply) v.getTag();
            AddCommentParam param = new AddCommentParam(reply.getCommentId(), reply.getOwner().getBuddyId(), reply.getOwner().getUserName());
            openReplyListener.openReply(param);
        }
    }

    class CommentClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getTag(R.id.tag_third) != null) {  //长按冲突处理
                v.setTag(R.id.tag_third, null);
                return;
            }
            Comment comment = (Comment) v.getTag();
//                myListView.setSelection(comment);
            AddCommentParam param = new AddCommentParam(comment.getCommentId(), comment.getOwner().getBuddyId(), comment.getOwner().getUserName());
            openReplyListener.openReply(param);
        }
    }

    ;

}
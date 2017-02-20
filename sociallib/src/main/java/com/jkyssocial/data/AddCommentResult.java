package com.jkyssocial.data;

import com.jkys.jkysbase.data.NetWorkResult;

/**
 * Created by yangxiaolong on 15/8/31.
 */
public class AddCommentResult extends NetWorkResult {
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    private Comment comment;

}

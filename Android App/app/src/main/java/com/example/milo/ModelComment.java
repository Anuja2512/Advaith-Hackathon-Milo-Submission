package com.example.milo;

public class ModelComment {

    String cmt_userPic,cmt_username,comment,frame,token;

    public ModelComment() {
    }
    public ModelComment(String cmt_userPic, String cmt_username, String comment, String frame, String token) {
        this.cmt_userPic = cmt_userPic;
        this.cmt_username = cmt_username;
        this.comment = comment;
        this.frame = frame;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getCmt_userPic() {
        return cmt_userPic;
    }

    public void setCmt_userPic(String cmt_userPic) {
        this.cmt_userPic = cmt_userPic;
    }

    public String getCmt_username() {
        return cmt_username;
    }

    public void setCmt_username(String cmt_username) {
        this.cmt_username = cmt_username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}

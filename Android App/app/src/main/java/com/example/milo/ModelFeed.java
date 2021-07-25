package com.example.milo;

public class ModelFeed {
    String username,time,description,postPic,comments, hashtags, token, key;

    public ModelFeed(String username, String time, String description, String postPic, String comments, String hashtags, String token, String key) {
        this.username = username;
        this.time = time;
        this.description = description;
        this.postPic = postPic;
        this.comments = comments;
        this.hashtags = hashtags;
        this.token=token;
        this.key=key;

    }

    public ModelFeed() {
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostPic() {
        return postPic;
    }

    public void setPostPic(String postPic) {
        this.postPic = postPic;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

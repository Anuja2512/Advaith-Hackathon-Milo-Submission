package com.example.milo;

public class ModelBlurb {
    String username,hashtags,radius,title,profession,userPic,blurbPic,description, token, key;

    public ModelBlurb() {
    }

    public ModelBlurb(String username, String hashtags, String radius, String title, String profession, String userPic, String blurbPic, String description, String token, String key) {
        this.username = username;
        this.hashtags = hashtags;
        this.radius = radius;
        this.title = title;
        this.profession = profession;
        this.userPic = userPic;
        this.blurbPic = blurbPic;
        this.description = description;
        this.token = token;
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getBlurbPic() {
        return blurbPic;
    }

    public void setBlurbPic(String blurbPic) {
        this.blurbPic = blurbPic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

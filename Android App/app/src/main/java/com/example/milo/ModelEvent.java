package com.example.milo;

public class ModelEvent {
    String username,title,radius,description,date,eventPic,userPic, token, key;

    public ModelEvent(String username, String title, String radius, String description, String date, String eventPic, String userPic, String token, String key) {
        this.username = username;
        this.title = title;
        this.radius = radius;
        this.description = description;
        this.date = date;
        this.eventPic = eventPic;
        this.userPic = userPic;
        this.token = token;
        this.key = key;
    }

    public ModelEvent() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventPic() {
        return eventPic;
    }

    public void setEventPic(String eventPic) {
        this.eventPic = eventPic;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}

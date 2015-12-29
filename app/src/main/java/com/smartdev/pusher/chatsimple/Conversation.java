package com.smartdev.pusher.chatsimple;

import java.util.Date;

/**
 * Created by nguyenbon on 11/27/15.
 */
public class Conversation {

    private String username;
    private String message;
    private String date;

    public Conversation(){

    }

    public Conversation(String username, String message, String date) {
        this.setUsername(username);
        this.setMessage(message);
        this.setDate(date);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSent()
    {
        return FakeUser.username.equals(username);
    }

}

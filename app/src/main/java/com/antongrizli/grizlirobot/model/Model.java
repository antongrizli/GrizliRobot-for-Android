package com.antongrizli.grizlirobot.model;

import twitter4j.Status;

/**
 * Created by Антон on 12.11.2015.
 */
public class Model {
    private String tweet_text;
    private String user_name;
    private String date_tweet;
    private boolean selected;
    private Long tweetID;
    private Status status;

    public Model(String tweet_text, String user_name, String date_tweet, Long tweetID, Status status) {
        this.tweet_text = tweet_text;
        this.user_name = user_name;
        this.selected = false;
        this.date_tweet = date_tweet;
        this.tweetID = tweetID;
        this.status = status;
    }

    public String getTweetText() {
        return tweet_text;
    }

    public void setTweetText(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getDate_tweet() {
        return date_tweet;
    }

    public void setDate_tweet(String date_tweet) {
        this.date_tweet = date_tweet;
    }

    public Long getTweetID() {
        return tweetID;
    }

    public Status getStatus() {
        return status;
    }
}

package com.antongrizli.grizlirobot.model;

import java.util.Date;

/**
 * Created by Антон on 02.12.2015.
 */
public class DeleteFriendsAndTweets {
    private boolean friend;
    private boolean tweet;
    private Date date;

    public DeleteFriendsAndTweets(boolean friend, boolean tweet, Date date) {
        this.friend = friend;
        this.tweet = tweet;
        this.date = date;
    }

    public boolean isFriend() {
        return friend;
    }

    public boolean isTweet() {
        return tweet;
    }

    public Date getDate() {
        return date;
    }
}

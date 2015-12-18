package com.antongrizli.grizlirobot.model;

import twitter4j.Query;

/**
 * Created by Антон on 16.11.2015.
 */
public class TransferModel {
    private Query query;
    private Boolean isCheckAddFriends;
    private Boolean isCheckRetweet;

    public TransferModel(Query query, Boolean isCheckAddFriends, Boolean isCheckRetweet) {
        this.isCheckAddFriends = isCheckAddFriends;
        this.isCheckRetweet = isCheckRetweet;
        this.query = query;
    }

    public TransferModel getTransferModel(){
        return this;
    }

    public Boolean getIsCheckAddFriends() {
        return isCheckAddFriends;
    }

    public Boolean getIsCheckRetweet() {
        return isCheckRetweet;
    }

    public Query getQuery() {
        return query;
    }
}

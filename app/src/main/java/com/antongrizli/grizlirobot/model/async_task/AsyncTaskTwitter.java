package com.antongrizli.grizlirobot.model.async_task;

import android.os.AsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Антон on 03.11.2015.
 */
public class AsyncTaskTwitter extends AsyncTask<Twitter, Integer, String> {
    @Override
    protected String doInBackground(Twitter... params) {
        Twitter twitter = params[0];

        try {
            return twitter.getScreenName().toString();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }
}

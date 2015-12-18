package com.antongrizli.grizlirobot.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAuth {
    private Twitter tw = null;
    private final static String CONSUMER_KEY = "wJju6Grakyayi2uoCk8uFm8gj";
    private final static String CONSUMER_SECRET = "WBlIMF5mfbasFUqn7Cxu2UkXcCt7hZPcMf02Mfjltzzyn2BVFn";

    private final String MAIN_ACTIVITY = "MAIN_ACTIVITY";
    private final static String PREFS_NAME = "config";

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    private final static String TWAUTH = "TwitterAuth.class";

    private String token = null;
    private String token_secret = null;
    private boolean connect = false;

    private static TwitterAuth INSTANCE = new TwitterAuth();

    public TwitterAuth() {
    }

    public static TwitterAuth getInstance() {
        return INSTANCE;
    }

    public final static long OWNER_ID = 2284447377l;


    public Twitter getTwitter() {
        Twitter twitter = null;
        loadToken();
        if ((token != null) && (token_secret != null) && (token.length() > 0) && (token_secret.length() > 0)) {
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            AccessToken accessToken = new AccessToken(token, token_secret);
            twitter.setOAuthAccessToken(accessToken);
            connect = true;
        }
        Log.d(MAIN_ACTIVITY, "Connection is " + connect);
        this.tw = twitter;
        return twitter;
    }

    public Twitter getTwitterL(){
        return tw;
    }

    public Twitter getTwitter(String login, String password, Boolean save) throws TwitterException {
        Twitter twitter = null;
        TwitterComLoginModel loginModel = new TwitterComLoginModel();
        Log.i(TWAUTH, "Run builder");
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(null)
                .setOAuthAccessTokenSecret(null);
        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        while (null == accessToken) {
            String pin = loginModel.loginToTwitter(requestToken.getAuthorizationURL(), login, password);
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                Log.i(TWAUTH, "Success connection");
                connect = true;
                saveToken(accessToken.getToken(), accessToken.getTokenSecret(), save);
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    Log.i(TWAUTH, "Unable to get the access token.", te);
                } else {
                    Log.e(TWAUTH, "Twitter Exeption", te);
                }
            }
        }
        this.tw = twitter;
        return twitter;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect;
    }

    public void saveToken(String token, String secretToken, Boolean save) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("token", token);
        editor.putString("secret_token", secretToken);
        editor.putBoolean("save", save);
        editor.apply();
    }

    private void loadToken() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        token = settings.getString("token", null);
        token_secret = settings.getString("secret_token", null);
        Log.d(MAIN_ACTIVITY, "Token: " + token + "\nSecret Token: " + token_secret);
    }
}

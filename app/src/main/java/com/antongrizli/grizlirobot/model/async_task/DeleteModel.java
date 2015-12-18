package com.antongrizli.grizlirobot.model.async_task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.antongrizli.grizlirobot.model.DeleteFriendsAndTweets;

import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * Created by Антон on 02.12.2015.
 */
public class DeleteModel extends AsyncTask<DeleteFriendsAndTweets, Integer, Void> {
    private Twitter twitter = TwitterFactory.getSingleton();
    private ProgressDialog bar;

    public void setContext(Context context) {
        this.bar = new ProgressDialog(context);
    }

    private void deleteTweets() {
        int pageNumber = 1;
        try {
            int barSize = 0;
            while (true) {
                Paging paging = new Paging();
                paging.setPage(pageNumber++);
                List<twitter4j.Status> bufferList = twitter.getUserTimeline(paging);
                bar.setMax(barSize+bufferList.size());
                if (bufferList.size() == 0) break;
                for (twitter4j.Status loop : bufferList) {
                    twitter.destroyStatus(loop.getId());
                    publishProgress(1);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private void deleteFriends() {
        long followerCursor = -1;
        try {
            PagableResponseList<User> users = null;
            int barSize = 100;
            do {
                users = twitter.friendsFollowers().getFollowersList(twitter.getId(), followerCursor);
                followerCursor = users.getNextCursor();
                bar.setMax(barSize+users.size());
                for (User loop : users) {
                    twitter.destroyFriendship(loop.getId());
                    publishProgress(1);
                }
            } while (users.hasNext());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        bar.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        bar.incrementProgressBy(values[0]);
    }

    @Override
    protected Void doInBackground(DeleteFriendsAndTweets... params) {
        if (params[0].isFriend()) {
            deleteFriends();
        }
        if (params[0].isTweet()) {
            deleteTweets();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setTitle("Unsubscribe");
        bar.setMessage("Unsubscribe tweets...");
        bar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        bar.setIndeterminate(false);
        bar.show();
    }
}

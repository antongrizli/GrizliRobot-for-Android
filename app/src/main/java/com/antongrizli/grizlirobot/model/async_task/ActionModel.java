package com.antongrizli.grizlirobot.model.async_task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.antongrizli.grizlirobot.model.Model;
import com.antongrizli.grizlirobot.model.TransferModel;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class ActionModel extends AsyncTask<TransferModel, Integer, List<Model>> {
    private Context context;

    private ProgressDialog pd;


    public void setContext(Context context) {
        this.context = context;
        Toast.makeText(context, "Run search", Toast.LENGTH_SHORT).show();
        pd = new ProgressDialog(context);
    }

    public List<twitter4j.Status> searchQuery(Query query, Boolean isCreateFriend, Boolean isRetweet) {
        List<twitter4j.Status> tweets = new ArrayList<>();
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            QueryResult result;
            int count = 0;
//            while (query != null) {
//
//                result = twitter.search(query);
//                System.out.println(result.getCount());
//                tweets.addAll(result.getTweets());
//
//                if (isCreateFriend || isRetweet)
//                    for (twitter4j.Status tweet : tweets) {
//                        publishProgress(1);
//                        System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//                        if (isCreateFriend) {
//                            Thread.sleep(1000);//1 second
//                            twitter.createFriendship(tweet.getUser().getScreenName());
//                        }
//                        if (isRetweet) {
//                            Thread.sleep(2000);//2 secunds
//                            twitter.retweetStatus(tweet.getId());
//                        }
//                    }//all time 3 minutes
//                query = result.nextQuery();
//                //Thread.sleep(5 * 1000 * 60);//5 minutes
//                count++;
//                publishProgress(60);
//                if (count * 60 > 960)
//                    break;
//            }


            result = twitter.search(query);
            System.out.println(result.getCount());
            tweets.addAll(result.getTweets());

            if (isCreateFriend || isRetweet)
                for (twitter4j.Status tweet : tweets) {
                    publishProgress(1);
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                    if (isCreateFriend) {
                        Thread.sleep(1000);//1 second
                        twitter.createFriendship(tweet.getUser().getScreenName());
                    }
                    if (isRetweet) {
                        Thread.sleep(2000);//2 secunds
                        twitter.retweetStatus(tweet.getId());
                    }
                }//all time 3 minutes

            //Если в файле уже много записей, тогда нужно перейти на проверку актуальности
            //Не актульным считается твит который добавлен в XML больше 7 дней
            //Если такой найден, то нужно отписаться от человека и от твита

        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public List<Model> getDataToModel(List<twitter4j.Status> tweets) {
        List<Model> transfered = new ArrayList<>();
        for (twitter4j.Status tweet : tweets) {
            transfered.add(new Model(tweet.getText(), tweet.getUser().getScreenName(), tweet.getCreatedAt().toString(),tweet.getId(), tweet));
        }
        return transfered;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pd.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(List<Model> models) {
        pd.dismiss();
        EventBus.getDefault().post(models);
    }

    @Override
    protected List<Model> doInBackground(TransferModel... params) {
        TransferModel transferModel = params[0];
        pd.setMax(transferModel.getQuery().getCount());
        return getDataToModel(searchQuery(transferModel.getQuery(), transferModel.getIsCheckAddFriends(), transferModel.getIsCheckRetweet()));
    }

    @Override
    protected void onPreExecute() {
        pd.setTitle("Run search");
        pd.setMessage("Searching tweets...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(false);
        pd.show();
    }
}

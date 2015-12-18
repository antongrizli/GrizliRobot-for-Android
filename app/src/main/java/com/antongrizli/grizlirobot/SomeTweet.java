package com.antongrizli.grizlirobot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class SomeTweet extends AppCompatActivity {
    private TextView twitterText;
    private ImageView tweetImage;
    private TextView tweetDate;
    private TextView retweetCount;
    private TextView favoriteCount;
    private ImageButton btnAddUser;
    private ImageButton btnRetweet;
    private ImageButton btnFavorite;

    private Status status;

    private static final String LOG = "SOME_TWEET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        status = (Status) bundle.getSerializable("status");


        twitterText = (TextView) findViewById(R.id.twitterText);
        tweetImage = (ImageView) findViewById(R.id.tweetImage);
        tweetDate = (TextView) findViewById(R.id.tweetDate);
        retweetCount = (TextView) findViewById(R.id.retweetCount);
        favoriteCount = (TextView) findViewById(R.id.favoriteCount);

        btnAddUser = (ImageButton) findViewById(R.id.ibAddUser);
        btnRetweet = (ImageButton) findViewById(R.id.ibRetweet);
        btnFavorite = (ImageButton) findViewById(R.id.ibFavorite);

        twitterText.setText(status.getText());
        retweetCount.setText(String.valueOf(status.getRetweetCount()));
        favoriteCount.setText(String.valueOf(status.getFavoriteCount()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        tweetDate.setText(dateFormat.format(status.getCreatedAt()));

        MediaEntity[] media = status.getMediaEntities(); //get the media entities from the status
        LinearLayout linerVertical = (LinearLayout) findViewById(R.id.linerImage);
        if (media.length > 0) {
            tweetImage.setVisibility(View.INVISIBLE);
            if (media[0] != null) {
                for (MediaEntity loop : media) {
                    ImageView imageInTweet = new ImageView(this);
                    System.out.println(media.length);
                    try {
                        imageInTweet.setImageBitmap(new DownloadImageFromTweet().execute(loop.getMediaURL()).get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    linerVertical.addView(imageInTweet);
                }
            }
        } else {
            tweetImage.setVisibility(View.INVISIBLE);
        }

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFriendship().execute();
            }
        });
        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetweetFavorite().execute(EventStatus.RETWEET);
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetweetFavorite().execute(EventStatus.FAVORITE);
            }
        });


    }

    private class DownloadImageFromTweet extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap drawable) {
            super.onPostExecute(drawable);
        }

        private Bitmap downloadImage(String _url) {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return bMap;

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }

    private class CreateFriendship extends AsyncTask<Void, Void, Void> {
        private Twitter twitter = TwitterFactory.getSingleton();

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Following to @" + status.getUser().getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                twitter.createFriendship(status.getUser().getId());
            } catch (TwitterException e) {
                Log.e(LOG, "Don't create friendship!", e);
            }
            return null;
        }
    }

    private class RetweetFavorite extends AsyncTask<EventStatus, Void, Void> {
        private Twitter twitter = TwitterFactory.getSingleton();

        @Override
        protected Void doInBackground(EventStatus... params) {
            try {
                switch (params[0].name()) {
                    case "FAVORITE":
                        twitter.createFavorite(status.getId());
                        break;
                    case "RETWEET":
                        twitter.retweetStatus(status.getId());
                        break;
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private enum EventStatus {
        RETWEET, FAVORITE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

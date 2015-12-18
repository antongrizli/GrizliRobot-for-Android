package com.antongrizli.grizlirobot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.antongrizli.grizlirobot.model.TwitterAuth;
import com.antongrizli.grizlirobot.model.async_task.AsyncTaskTwitter;

import java.util.concurrent.ExecutionException;

import twitter4j.Twitter;

public class MainActivity extends AppCompatActivity {
    private final String MAIN = "MAIN_ACTIVITY";
    private TwitterAuth auth = TwitterAuth.getInstance();
    private Twitter twitter;


    private final static String PREFS_NAME = "config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth.setContext(getApplicationContext());
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean chkSave = settings.getBoolean("save", false);
        if (chkSave) {
            Log.i(MAIN, "Load saved values. CheckBox is " + chkSave);
            twitter = auth.getTwitter();
            try {
                Toast.makeText(getApplicationContext(), new AsyncTaskTwitter().execute(twitter).get(), Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void alertConnectDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("You don't authorized");
        ad.setMessage("Dear user, you need authorized or exit from this application");
        String bAuthorized = "Authorized";
        String bExit = "Exit";
        ad.setPositiveButton(bAuthorized, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        ad.setNegativeButton(bExit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getApplicationContext(), "You don't authorized, all function is blocked", Toast.LENGTH_SHORT).show();
            }
        });
        ad.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
        if (auth.isConnect()) {
            twitter = auth.getTwitterL();
            try {
                Toast.makeText(getApplicationContext(), new AsyncTaskTwitter().execute(twitter).get(), Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            alertConnectDialog();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i(MAIN, "Run settings");
                setContentView(R.layout.settings_layout);
                break;
            case R.id.action_close:
                Log.i(MAIN, "Closed application");
                Toast.makeText(this, String.format(getString(R.string.info_close), 3), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(MAIN, "Error when sleep 3 seconds", e);
                        }
                        finish();
                    }
                }).start();
                break;
            case R.id.action_auth:
                Log.i(MAIN, "Run auth layout");
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
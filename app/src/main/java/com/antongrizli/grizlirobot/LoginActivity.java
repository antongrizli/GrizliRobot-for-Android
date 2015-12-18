package com.antongrizli.grizlirobot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.antongrizli.grizlirobot.model.TwitterAuth;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bLogin;
    private Button bLogout;
    private EditText etLogin;
    private EditText etPassword;
    private CheckBox chkbxSaveAuth;
    private static final String MAIN_ACTIVITY = "MAIN_ACTIVITY";
    private TwitterAuth auth = TwitterAuth.getInstance();
    private Twitter twitter = null;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogout = (Button) findViewById(R.id.bLogout);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        chkbxSaveAuth = (CheckBox) findViewById(R.id.ckbSaveAuth);

        auth.setContext(getApplicationContext());

        bLogin.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        if (twitter == null) {
            bLogin.setEnabled(false);
        } else if (!auth.isConnect()) {
            bLogin.setEnabled(false);
        }

        etLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bLogin.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bLogin.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                bLogin.setEnabled(!s.toString().trim().isEmpty());
            }
        });

        autoRefreshLoginView();
    }


    private Runnable runnableAutoRefreshLoginView = new Runnable() {
        @Override
        public void run() {
            setVisibleBtn(!auth.isConnect());
            Log.d(MAIN_ACTIVITY, "Runnable Auto Refresh - " + auth.isConnect());
            handler.postDelayed(this, 1000);
            if (auth.isConnect()) {
                //finish();
                setVisibleBtn(false);
            }
        }
    };

    private void autoRefreshLoginView() {
        handler.postDelayed(runnableAutoRefreshLoginView, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i(MAIN_ACTIVITY, "Run settings");
                setContentView(R.layout.settings_layout);
                break;
            case R.id.action_close:
                Log.i(MAIN_ACTIVITY, "Closed application");
                Toast.makeText(this, String.format(getString(R.string.info_close), 3), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(MAIN_ACTIVITY, "Error when sleep 3 seconds", e);
                        }
                        finish();
                    }
                }).start();
                break;
            case R.id.action_auth:
                Log.i(MAIN_ACTIVITY, "Run auth layout");
                Intent loadMain = new Intent(this, LoginActivity.class);
                startActivity(loadMain);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bLogin:
                login();
                Log.d(MAIN_ACTIVITY, "Press login");
                break;
            case R.id.bLogout:
                logout();
                Log.d(MAIN_ACTIVITY, "Press logout");
                break;
        }
    }

    private void setVisibleBtn(Boolean visible) {
        etLogin.setEnabled(visible);
        etPassword.setEnabled(visible);
        bLogin.setEnabled(visible);
        bLogout.setEnabled(!visible);
    }

    @Override
    protected void onResume() {
        setVisibleBtn(!auth.isConnect());
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnableAutoRefreshLoginView);
        Log.d(MAIN_ACTIVITY, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(MAIN_ACTIVITY, "onStop");
        super.onStop();
    }

    protected void login() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.progress_dialog_auth);
        progressDialog.setMessage(this.getResources().getString(R.string.progress_dialog_login));
        progressDialog.show();

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            private String etLoginString = etLogin.getText().toString();
            private String etPasswordString = etPassword.getText().toString();
            private Boolean chkAuth = chkbxSaveAuth.isChecked();

            @Override
            protected Void doInBackground(Void... params) {
                if (etLoginString != null && etPasswordString != null) {
                    Log.i(MAIN_ACTIVITY, "Username=" + etLoginString + ", Password=" + etPasswordString);
                    try {
                        twitter = auth.getTwitter(etLoginString, etPasswordString, chkAuth);
                    } catch (TwitterException e) {
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.dismiss();
            }

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }
        };
        asyncTask.execute();
    }

    protected void logout() {
        auth.setConnect(false);
        auth.saveToken("", "", false);
    }
}

package com.example.wordplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wordplay.fragments.ChangePasswordDialog;
import com.example.wordplay.models.Response;
import com.example.wordplay.models.User;
import com.example.wordplay.network.NetworkUtil;
import com.example.wordplay.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, ChangePasswordDialog.Listener {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView mTvName;
    //    private TextView mTvLevel;
    private Button mBtChangePassword;
    private Button mBtLogout;

    private ProgressBar mProgressbar;

    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mLogin;
    private User userAut;

    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSubscriptions = new CompositeSubscription();
        initViews();
        initSharedPreferences();
        loadProfile();
        Button playBtn = findViewById(R.id.playBtn2);
        playBtn.setOnClickListener(this);

        Button mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        Button musicBtn = findViewById(R.id.musicBtn);
        musicBtn.setOnClickListener(this);
    }

    private void initViews() {

        mTvName = (TextView) findViewById(R.id.tv_name);
//        mTvLevel = (TextView) findViewById(R.id.tv_level);
        mBtChangePassword = (Button) findViewById(R.id.btn_change_password);
        mBtLogout = (Button) findViewById(R.id.btn_logout);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);
        mBtChangePassword.setOnClickListener(view -> showDialog());


        mBtLogout.setOnClickListener(view -> logout());
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN, "");
        mLogin = mSharedPreferences.getString(Constants.LOGIN, "");
    }

    private void logout() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.LOGIN, "");
        editor.putString(Constants.TOKEN, "");
        editor.apply();
        finish();
    }

    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mLogin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void showDialog() {

        ChangePasswordDialog fragment = new ChangePasswordDialog();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.LOGIN, mLogin);
        bundle.putString(Constants.TOKEN, mToken);
        fragment.setArguments(bundle);

        fragment.show(getFragmentManager(), ChangePasswordDialog.TAG);
    }

    private void handleResponse(User user) {

        mProgressbar.setVisibility(View.GONE);
        mTvName.setText(getResources().getString(R.string.user) + ": " + user.getLogin());
        userAut = user;
//        mTvLevel.setText(getResources().getString(R.string.level) + ": " +user.getLevel() );
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                try {
                    String mess = getResources().getString(R.string.class.getField(response.getMessage()).getInt(null));
                    showSnackBarMessage(mess);
                } catch (IllegalAccessException e) {
                    showSnackBarMessage(response.getMessage());
                } catch (NoSuchFieldException e) {
                    showSnackBarMessage(response.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage(getResources().getString(R.string.networkError));
        }
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_profile), message, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onPasswordChanged() {

        showSnackBarMessage(getResources().getString(R.string.changePassMessage));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playBtn2) {
            Intent playIntent = new Intent(this, GameActivity.class);
            this.startActivity(playIntent);
        }
        if (v.getId() == R.id.mapBtn) {
            Intent playIntent = new Intent(this, MapActivity.class);
            this.startActivity(playIntent);
        }
        if (v.getId() == R.id.musicBtn) {
            if (MyService.getTAG().equals("started")) {
                stopService(new Intent(this, MyService.class));
            } else
                startService(new Intent(this, MyService.class));
        }
    }
}


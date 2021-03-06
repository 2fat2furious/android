package com.example.wordplay.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wordplay.ProfileActivity;
import com.example.wordplay.models.Response;
import com.example.wordplay.models.User;
import com.example.wordplay.network.NetworkUtil;
import com.example.wordplay.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.wordplay.R;


import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.wordplay.utils.Validation.validateLogin;
import static com.example.wordplay.utils.Validation.validateFields;


public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private EditText mEtLogin;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvRegister;
    private TextInputLayout mTiLogin;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        initSharedPreferences();
        return view;
    }

    private void initViews(View v) {

        mEtLogin = (EditText) v.findViewById(R.id.et_login);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtLogin = (Button) v.findViewById(R.id.btn_login);
        mTiLogin = (TextInputLayout) v.findViewById(R.id.ti_login);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mTvRegister = (TextView) v.findViewById(R.id.tv_register);

        mBtLogin.setOnClickListener(view -> login());
        mTvRegister.setOnClickListener(view -> goToRegister());
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private void login() {

        setError();

        String login = mEtLogin.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateLogin(login)) {

            err++;
            mTiLogin.setError(getResources().getString(R.string.validateLog));
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError(getResources().getString(R.string.validatePass));
        }

        if (err == 0) {

            loginProcess(login, password);
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
            showSnackBarMessage(getResources().getString(R.string.validate));
        }
    }

    private void setError() {

        mTiLogin.setError(null);
        mTiPassword.setError(null);
    }

    private void loginProcess(String email, String password) {

        mSubscriptions.add(NetworkUtil.getRetrofit(email, password).login(new User(email, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN, response.getToken());
        editor.putString(Constants.LOGIN, response.getMessage());
        editor.apply();

        mEtLogin.setText(null);
        mEtPassword.setText(null);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

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

        if (getView() != null) {

            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToRegister() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.replace(R.id.fragmentFrame, fragment, RegisterFragment.TAG);
        ft.commit();
    }

//    private void showDialog(){
//
//        ResetPasswordDialog fragment = new ResetPasswordDialog();
//
//        fragment.show(getFragmentManager(), ResetPasswordDialog.TAG);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

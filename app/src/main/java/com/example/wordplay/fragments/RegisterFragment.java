package com.example.wordplay.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wordplay.R;
import com.example.wordplay.models.Response;
import com.example.wordplay.models.User;
import com.example.wordplay.network.NetworkUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.wordplay.utils.Validation.validateLogin;
import static com.example.wordplay.utils.Validation.validateFields;


public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mEtLogin;
    private EditText mEtPassword;
    private Button   mBtRegister;
    private TextView mTvLogin;
    private TextInputLayout mTiLogin;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressbar;

    private CompositeSubscription mSubscriptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register,container,false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        return view;
    }

    private void initViews(View v) {

        mEtLogin = (EditText) v.findViewById(R.id.et_login);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtRegister = (Button) v.findViewById(R.id.btn_register);
        mTvLogin = (TextView) v.findViewById(R.id.tv_login);
        mTiLogin = (TextInputLayout) v.findViewById(R.id.ti_login);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressbar = (ProgressBar) v.findViewById(R.id.progress);

        mBtRegister.setOnClickListener(view -> register());
        mTvLogin.setOnClickListener(view -> goToLogin());
    }

    private void register() {

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

            User user = new User(login,password);
            mProgressbar.setVisibility(View.VISIBLE);
            registerProcess(user);

        } else {
            showSnackBarMessage(getResources().getString(R.string.validate));
        }
    }

    private void setError() {
        mTiLogin.setError(null);
        mTiPassword.setError(null);
    }

    private void registerProcess(User user) {

        mSubscriptions.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressbar.setVisibility(View.GONE);
        try {
            String mess = getResources().getString(R.string.class.getField(response.getMessage()).getInt(null));
            showSnackBarMessage(mess);
        } catch (IllegalAccessException e) {
            showSnackBarMessage(response.getMessage());
        } catch (NoSuchFieldException e) {
            showSnackBarMessage(response.getMessage());
        }
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                try {
                    String mess = getResources().getString(R.string.class.getField(response.getMessage()).getInt(null));
                    showSnackBarMessage(mess);
                } catch (IllegalAccessException e) {
                    showSnackBarMessage(response.getMessage());
                } catch (NoSuchFieldException e) {
                    showSnackBarMessage(response.getMessage());
                }
                showSnackBarMessage(getResources().getString(R.string.networkError));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage(getResources().getString(R.string.networkError));
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToLogin(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

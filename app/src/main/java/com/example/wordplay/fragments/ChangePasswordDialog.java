package com.example.wordplay.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wordplay.ProfileActivity;
import com.example.wordplay.R;
import com.example.wordplay.models.Response;
import com.example.wordplay.models.User;
import com.example.wordplay.network.NetworkUtil;
import com.example.wordplay.utils.Constants;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.wordplay.utils.Validation.validateFields;

public class ChangePasswordDialog extends DialogFragment {

    public interface Listener {

        void onPasswordChanged();
    }

    public static final String TAG = ChangePasswordDialog.class.getSimpleName();

    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private Button mBtChangePassword;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldPassword;
    private TextInputLayout mTiNewPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;

    private String mToken;
    private String mLogin;

    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_password,container,false);
        mSubscriptions = new CompositeSubscription();
        getData();
        initViews(view);
        return view;
    }

    private void getData() {

        Bundle bundle = getArguments();

        mToken = bundle.getString(Constants.TOKEN);
        mLogin = bundle.getString(Constants.LOGIN);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ProfileActivity)context;
    }

    private void initViews(View v) {

        mEtOldPassword = (EditText) v.findViewById(R.id.et_old_password);
        mEtNewPassword = (EditText) v.findViewById(R.id.et_new_password);
        mTiOldPassword = (TextInputLayout) v.findViewById(R.id.ti_old_password);
        mTiNewPassword = (TextInputLayout) v.findViewById(R.id.ti_new_password);
        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtCancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        mBtChangePassword.setOnClickListener(view -> changePassword());
        mBtCancel.setOnClickListener(view -> dismiss());
    }

    private void changePassword() {

        setError();

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();

        int err = 0;

        if (!validateFields(oldPassword)) {

            err++;
            mTiOldPassword.setError(getResources().getString(R.string.validatePass));
        }

        if (!validateFields(newPassword)) {

            err++;
            mTiNewPassword.setError(getResources().getString(R.string.validatePass));
        }

        if (err == 0) {

            User user = new User();
            user.setPassword(oldPassword);
            user.setNewPassword(newPassword);
            changePasswordProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {
        mTiOldPassword.setError(null);
        mTiNewPassword.setError(null);
    }

    private void changePasswordProgress(User user) {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).changePassword(mLogin,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);
        mListener.onPasswordChanged();
        dismiss();
    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showMessage(getResources().getString(R.string.networkError));
        }
    }

    private void showMessage(String message) {

        mTvMessage.setVisibility(View.VISIBLE);
        try {
            String mess = getResources().getString(R.string.class.getField(message).getInt(null));
            mTvMessage.setText(mess);
        } catch (IllegalAccessException e) {
            mTvMessage.setText(message);
        } catch (NoSuchFieldException e) {
            mTvMessage.setText(message);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

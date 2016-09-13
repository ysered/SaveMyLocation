package com.ysered.savemylocation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ysered.savemylocation.login.LoginContract;
import com.ysered.savemylocation.login.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements
        LoginContract.View,
        View.OnClickListener {

    private LoginContract.Presenter mPresenter;

    private TextView mErrorTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        mPresenter = new LoginPresenter(
                context,
                this,
                Injector.provideLoginValidator(),
                Injector.provideDataSource(context)
        );

        if (mPresenter.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        setContentView(R.layout.activity_login);
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            mProgressBar.setVisibility(View.VISIBLE);
            mPresenter.validate(mEmailEditText.getText().toString(),
                    mPasswordEditText.getText().toString());
        }
    }

    @Override
    public void navigateToMapView() {
        mErrorTextView.setVisibility(View.GONE);
        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
    }

    @Override
    public void showLoginFailureError() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }
}

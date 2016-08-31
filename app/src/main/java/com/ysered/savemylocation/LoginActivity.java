package com.ysered.savemylocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.database.SqliteDataSource;
import com.ysered.savemylocation.facebook.SimpleFacebookLoginValidator;
import com.ysered.savemylocation.task.SimpleBackgroundTaskWithResult;
import com.ysered.savemylocation.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mErrorTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;

    private DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceUtils.getCurrentUser(this) != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }

        setContentView(R.layout.activity_login);
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);
        findViewById(R.id.login_button).setOnClickListener(this);
        mDataSource = new SqliteDataSource(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            mProgressBar.setVisibility(View.VISIBLE);
            new SimpleBackgroundTaskWithResult<Boolean>(this) {
                @Override
                protected Boolean onRun() {
                    final String email = mEmailEditText.getText().toString();
                    final String password = mPasswordEditText.getText().toString();
                    final boolean isValid = new SimpleFacebookLoginValidator()
                            .validate(email, password);
                    if (isValid) {
                        saveUser(mEmailEditText.getText().toString());
                    }
                    return isValid;
                }

                @Override
                protected void onSuccess(Boolean isValid) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (isValid) {
                        mErrorTextView.setVisibility(View.GONE);
                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    } else {
                        mErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }.execute();
        }
    }

    private void saveUser(String userEmail) {
        PreferenceUtils.saveCurrentUser(this, userEmail.trim());
        mDataSource.saveUser(userEmail.trim());
    }


}

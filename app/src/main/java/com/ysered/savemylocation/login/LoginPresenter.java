package com.ysered.savemylocation.login;

import android.content.Context;

import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.auth.LoginValidator;
import com.ysered.savemylocation.task.SimpleBackgroundTaskWithResult;
import com.ysered.savemylocation.utils.PreferenceUtils;

public class LoginPresenter implements LoginContract.Presenter {

    private final Context mContext;
    private final LoginContract.View mLoginView;
    private final LoginValidator mLoginValidator;
    private final DataSource mDataSource;

    public LoginPresenter(Context context,
                          LoginContract.View loginView,
                          LoginValidator validator,
                          DataSource dataSource) {
        mContext = context;
        mLoginView = loginView;
        mLoginValidator = validator;
        mDataSource = dataSource;
    }

    @Override
    public void validate(final String user, final String password) {
        new SimpleBackgroundTaskWithResult<Boolean>(this) {
            @Override
            protected Boolean onRun() {
                return mLoginValidator.validate(user, password);
            }

            @Override
            protected void onSuccess(Boolean result) {
                if (result) {
                    PreferenceUtils.saveCurrentUser(mContext, user);
                    mDataSource.saveUser(user);
                    mLoginView.navigateToMapView();
                } else {
                    mLoginView.showLoginFailureError();
                }
            }
        }.execute();
    }

    @Override
    public boolean isUserLoggedIn() {
        return PreferenceUtils.getCurrentUser(mContext) != null;
    }
}

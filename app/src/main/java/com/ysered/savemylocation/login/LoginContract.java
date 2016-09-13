package com.ysered.savemylocation.login;

public interface LoginContract {

    interface View {

        void navigateToMapView();

        void showLoginFailureError();

    }

    interface Presenter {

        void validate(String user, String password);

        boolean isUserLoggedIn();

    }

}

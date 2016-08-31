package com.ysered.savemylocation.task;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class SimpleBackgroundTaskWithResult<T> extends AsyncTask<Void, Void, T> {

    private final WeakReference<Activity> mActivity;

    public SimpleBackgroundTaskWithResult(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    protected final T doInBackground(Void... voids) {
        return onRun();
    }

    private boolean canContinue() {
        return mActivity.get() != null && !mActivity.get().isFinishing();
    }

    @Override
    protected void onPostExecute(T t) {
        if(canContinue()) {
            onSuccess(t);
        }
    }

    abstract protected T onRun();

    abstract protected void onSuccess(T result);

}

package com.ysered.savemylocation.task;

import android.os.AsyncTask;

public abstract class SimpleBackgroundTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected final Void doInBackground(Void... voids) {
        return onRun();
    }

    abstract protected Void onRun();

}

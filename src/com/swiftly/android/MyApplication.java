package com.swiftly.android;

import android.app.Application;

public class MyApplication extends Application {

    private ItemsDbAdapter dbAdapter;

    @Override
    public void onCreate() {
        dbAdapter = new ItemsDbAdapter(getApplicationContext());
        dbAdapter.open();
        super.onCreate();
    }

    public ItemsDbAdapter getDatabaseAdapter() {
        return dbAdapter;
    }
}
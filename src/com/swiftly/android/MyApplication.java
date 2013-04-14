package com.swiftly.android;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {
    private ItemsDbAdapter dbAdapter;
    private static Typeface customTypeface;
    private static final String PATH_TYPEFACE_CUSTOM = "typefaces/DancingScript-Regular.otf";

    @Override
    public void onCreate() {
        dbAdapter = new ItemsDbAdapter(getApplicationContext());
        dbAdapter.open();
        super.onCreate();
        getCustomTypeface();
    }

    public ItemsDbAdapter getDatabaseAdapter() {
        return dbAdapter;
    }

    public Typeface getCustomTypeface() {
        if(customTypeface == null){
            //Only do this once for each typeface used
            //or we will leak unnecessary memory.
            customTypeface = Typeface.createFromAsset(getAssets(), PATH_TYPEFACE_CUSTOM);
        }
        return customTypeface;
    }
}
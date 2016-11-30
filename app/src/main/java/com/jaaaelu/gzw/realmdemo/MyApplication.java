package com.jaaaelu.gzw.realmdemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import io.realm.Realm;

/**
 * Created by admin on 2016/11/8.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        Realm.init(mContext);
    }

    public static Context getContext() {
        return mContext;
    }

}

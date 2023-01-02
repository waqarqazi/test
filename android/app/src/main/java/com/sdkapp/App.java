package com.sdkapp;

import android.app.Application;
/**
 * Created by Administrator on 2017/12/7.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }
}

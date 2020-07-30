package com.mj.kwapidemo;

import android.app.Application;
import android.content.Context;

import com.haiziwang.base.InitPlugin;

public class MultiDexApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new InitPlugin().init(this);


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}

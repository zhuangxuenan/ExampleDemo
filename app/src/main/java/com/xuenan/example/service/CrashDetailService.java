package com.xuenan.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by xuenan on 2016/10/08 0011.
 * 崩溃时自动重启APP的服务
 */
public class CrashDetailService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.xuenan.example");
            startActivity(intent);
            stopSelf();
    }

}
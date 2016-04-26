package com.lingzhuo.coolweather01.rerceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lingzhuo.coolweather01.service.AutoUpdateService;

/**
 * 用于接收开启启动的广播，同时开启我们后台更新天气的服务
 * Created by Wang on 2016/4/24.
 */
public class AutoStartService extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AutoUpdateService.class));
    }
}

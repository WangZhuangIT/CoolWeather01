package com.lingzhuo.coolweather01.rerceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lingzhuo.coolweather01.service.AutoUpdateService;

/**
 * 接收服务自动更新天气完毕的广播，同时再次开启更新天气的服务，这样就形成了服务更新天气循环
 * Created by Wang on 2016/4/22.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}

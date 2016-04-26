package com.lingzhuo.coolweather01.rerceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 接收系统时间的变化的广播，度量是分钟，当系统时间的分钟改变，发送广播给我们的桌面小组件，同时更新小组件上的时间显示
 * Created by Wang on 2016/4/17.
 */
public class MyTimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
    }
}

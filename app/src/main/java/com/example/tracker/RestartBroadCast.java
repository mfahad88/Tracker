package com.example.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("RestartBroadCast","started...");

        Utils.startBroadcast(context);
    }
}

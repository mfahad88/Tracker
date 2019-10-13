package com.example.tracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;

import static android.Manifest.permission.RECEIVE_SMS;
import static android.content.Context.MODE_PRIVATE;

public class Utils {
    public static final String MY_PREFS_NAME="MY_PREFS_NAME";

    public static void startService(Context context){
        Intent intent=new Intent(context,LocationService.class);
        context.startService(intent);
    }

    public static void stopService(Context context){
        Intent intent=new Intent(context,LocationService.class);
        context.stopService(intent);
    }

    public static void startBroadcast(Context context){
        BroadcastReceiver receiver=new IncomingSms();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        context.getApplicationContext().registerReceiver(receiver,intentFilter);
    }

    public static void stopBroadcast(Context context){
        BroadcastReceiver receiver=new IncomingSms();
        context.unregisterReceiver(receiver);
    }

    public static void savePreferences(Context context,String key,String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.commit();
        editor.apply();
    }

    public static String getPreferences(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key,null);
    }

    public static void deletePreferences(Context context,String key){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(key);
    }

    public static void sendSMS(Context context,String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static boolean isBroadcastRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (IncomingSms.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}

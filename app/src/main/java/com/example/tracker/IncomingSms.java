package com.example.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    Location location;
    Bundle bundle;
    String savedNumber;
    public void onReceive(final Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        Log.wtf("IncomingSms","Started........");
        bundle = intent.getExtras();
        savedNumber=Utils.getPreferences(context,MainActivity.MOBILE);
        try {

            if (bundle != null) {
                location= (Location) bundle.get("location");
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber.replace("+92","0");
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; Saved: "+savedNumber+"; message: " + message);
                    if(senderNum.equals(savedNumber)) {
                        if (message.contains("location")) {

                            Utils.startService(context);
                        }/*if(message.contains("status")){
                            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = context.registerReceiver(null, ifilter);
                            // Are we charging / charged?
                            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                            float batteryPct = status / (float) scale;
                            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                    status == BatteryManager.BATTERY_STATUS_FULL;

                            // How are we charging?
                            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

//                            String statusMessage="Battery: "+level+"%\n Charging: "+chargePlug;
                            StringBuilder statusMessage=new StringBuilder();
                            statusMessage.append("Battery: ");
                            statusMessage.append(Math.round(batteryPct));
                            statusMessage.append("\n");
                            if(chargePlug>0){
                                statusMessage.append("Charging: ");
                            }
                            Utils.sendSMS(context,senderNum,statusMessage);
                        }*/
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }

    }
}
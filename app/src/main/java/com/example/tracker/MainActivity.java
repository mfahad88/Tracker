package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    public static final String MOBILE = "mobile";
    EditText edt_number;
    Button btn_add;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_number=findViewById(R.id.edt_number);
        btn_add=findViewById(R.id.btn_add);

        btn_add.setEnabled(false);
        if(!checkPermission()){
            requestPermission();
        }else{
            btn_add.setEnabled(true);
            Toast.makeText(this, "Please request permission.", Toast.LENGTH_SHORT).show();
        }
        btn_add.setOnClickListener(this);





    }

    @Override
    protected void onStart() {
        initBroadcast();
        /*locationBroadcast=new LocationBroadcast();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(RECEIVE_SMS);
        registerReceiver(locationBroadcast,intentFilter);*/
        super.onStart();
    }

    @Override
    protected void onStop() {
//        unregisterReceiver(locationBroadcast);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(receiver);
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        Log.e("OnDestoy","Started,...");
        super.onDestroy();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);


        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,RECEIVE_SMS,SEND_SMS,READ_SMS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recvsmsAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readsmsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean sentsmsAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && recvsmsAccepted && readsmsAccepted && sentsmsAccepted) {
                        btn_add.setEnabled(true);
                    }
                    else {
                        Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION,RECEIVE_SMS,SEND_SMS,READ_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_add){
            if(!TextUtils.isEmpty(edt_number.getText())){
                if(edt_number.getText().toString().length()==11){
                    if(!TextUtils.isEmpty(Utils.getPreferences(this,MOBILE))){
                        Utils.deletePreferences(this,MOBILE);
                    }
                    Utils.savePreferences(this,MOBILE,edt_number.getText().toString());
//                    if(Utils.isBroadcastRunning(this)){
//                        Utils.stopBroadcast(this);
//                    }
                    registerReceiver(receiver,intentFilter);
                    Toast.makeText(this, "Saved...", Toast.LENGTH_SHORT).show();
                    edt_number.getText().clear();
                    finish();
                }else{
                    Toast.makeText(this, "Please enter correct mobile number...", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Empty fields not allowed...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initBroadcast(){
        receiver = new IncomingSms();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
    }
}

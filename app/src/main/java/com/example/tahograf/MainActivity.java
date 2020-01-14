package com.example.tahograf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationService myLocationService;
    static boolean connectionStatus;
    LocationManager locationManager;
    static long startTime, endTime;
    ImageView image;
    static ProgressDialog locateDialog;
    static int p = 0;

    static TextView tv_speed;
    Button start, stop;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) iBinder;
            myLocationService = binder.getService();
            connectionStatus = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connectionStatus = false;
        }
    };

    void bindService() {
        if (connectionStatus) return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        connectionStatus = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (!connectionStatus) return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        connectionStatus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionStatus) {
            unbindService();
        }
    }

    @Override
    public void onBackPressed() {
        if (connectionStatus) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledAlertToUser();
        }
    }

    private void showGpsDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to user application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent callGPSSettingIntent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_speed = findViewById(R.id.tv_speed);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGps();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this,"locationManager not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!connectionStatus) {
                    bindService();
                }

                locateDialog = new ProgressDialog(MainActivity.this);
                locateDialog.setIndeterminate(true);
                locateDialog.setCancelable(false);
                locateDialog.setMessage("Getting location...");
                locateDialog.show();


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus) {
                    unbindService();
                }
                p = 0;
            }
        });

    }


//    private void updateSpeed(CLocation location) {
//        float currentSpeed = 0;
//
//        if (location != null) {
//            currentSpeed = location.getSpeed();
//        }
//
//        Formatter fmt = new Formatter(new StringBuilder());
//        Log.w("MAPE","currentSpeed = "+currentSpeed);
//        fmt.format(Locale.US,"%5.1f",currentSpeed);
//        String strCurrentSpeed = fmt.toString();
//        strCurrentSpeed = strCurrentSpeed.replace(" ","0");
//        tv_speed.setText(strCurrentSpeed + "m/s");
//    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1000) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                work();
//            } else {
//                finish();
//            }
//        }
//
//    }
}

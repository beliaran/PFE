package fr.eseo.pfemolkky.service.bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class BlePermission {


    //TODO PUT THIS FINAL ON PROPERTIES FILES
    private static final int REQUEST_PERMISSION_BLUETOOTH = 2;
    private static final int REQUEST_PERMISSION_BLUETOOTH_ADMIN = 3;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 4;
    private static final int REQUEST_PERMISSION_BLUETOOTH_SCAN=1;
    private static final int REQUEST_PERMISSION_BLUETOOTH_CONNECT = 5;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 6;

    public static boolean blePermission(Activity activity){

        //Permission necessaire pour le BLE
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "no permission ACCESS_FINE_LOCATION");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "no permission ACCESS_COARSE_LOCATION");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return false;
        }

        //Permission necessaire pour le BLE en api <30
        if(Build.VERSION.SDK_INT <=30){
            Log.d(TAG, "SDK<=30");
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_PERMISSION_BLUETOOTH);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth_Admin");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_PERMISSION_BLUETOOTH_ADMIN);
            }
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        //Permission necessaire pour le BLE en api >30
        else{
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth_scan");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_PERMISSION_BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission BLUETOOTH_CONNECT");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSION_BLUETOOTH_CONNECT);
            }
            if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}

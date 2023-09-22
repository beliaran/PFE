package fr.eseo.pfemolkky.service.bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class BlePermission {


    //TODO PUT THIS FINAL ON PROPERTIES FILES
    private static final int REQUEST_PERMISSION_BLUETOOTH = 2;
    private static final int REQUEST_PERMISSION_BLUETOOTH_ADMIN = 3;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 4;
    private static final int REQUEST_PERMISSION_BLUETOOTH_SCAN=1;
    private static final int REQUEST_PERMISSION_BLUETOOTH_CONNECT = 5;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 6;

    public static boolean blePermission(Activity activity){
        int permissionsCode = 42;
        ArrayList<String> permissions= new ArrayList<String>();
        boolean checkPermission;

        //Permission necessaire pour le BLE
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "no permission ACCESS_FINE_LOCATION");
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "no permission ACCESS_COARSE_LOCATION");
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        checkPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        //Permission necessaire pour le BLE en api <30
        if(Build.VERSION.SDK_INT <=30){
            Log.d(TAG, "SDK<=30");
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth");
                permissions.add(Manifest.permission.BLUETOOTH);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth_Admin");
                permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
            checkPermission = checkPermission||(ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED);
        }
        //Permission necessaire pour le BLE en api >30
        else{
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission Bluetooth_scan");
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no permission BLUETOOTH_CONNECT");
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            checkPermission = checkPermission||(ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED);
        }

        if(checkPermission){
            System.out.println(permissions);
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), permissionsCode);
            return false;
        }
        else{
            return true;
        }
    }


}

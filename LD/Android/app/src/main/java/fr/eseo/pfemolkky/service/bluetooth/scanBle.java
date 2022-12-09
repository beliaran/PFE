package fr.eseo.pfemolkky.service.bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.pfemolkky.MainActivity;

public class scanBle {


    private static BluetoothLeScanner bluetoothLeScanner;
    private static boolean scanning;
    private static Handler handler = new Handler();
    private static MainActivity main;

    // Stops scanning after 10 seconds.
    //TODO PUT THIS FINAL ON PROPERTIES FILES
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("MissingPermission")
    public static void scan(MainActivity mainActivity) {
        main = mainActivity;
        if(main.bluetoothAdapter.getBluetoothLeScanner() == null) {
            return;
        }
        bluetoothLeScanner = main.bluetoothAdapter.getBluetoothLeScanner();
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    Log.d(TAG, "scanning = false , run(), nb test : ");
                    bluetoothLeScanner.stopScan(leScanCallBack);
                }
            }, SCAN_PERIOD);

            scanning = true;
            Log.d(TAG, "scanning = true , lescancallback");
            bluetoothLeScanner.startScan(leScanCallBack);
        } else {
            scanning = false;
            Log.d(TAG,"scanning = false , lescancallback");
            bluetoothLeScanner.stopScan(leScanCallBack);
        }
    }



    private static ScanCallback leScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int scanSettings, ScanResult result){
            super.onScanResult(scanSettings,result);
            if(!main.bluetoothDevices.contains(result.getDevice())){
                main.bluetoothDevices.add(result.getDevice());
            }
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for(ScanResult i : results){
                if(!main.bluetoothDevices.contains(i.getDevice())){
                    main.bluetoothDevices.add(i.getDevice());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG,"on  scan fail.");
        }
    };
}

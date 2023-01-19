package fr.eseo.pfemolkky.service.bluetooth;


import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGatt.GATT_WRITE_NOT_PERMITTED;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.UUID;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.ui.addPin.AddPin;
import fr.eseo.pfemolkky.ui.main.GameFragment;

public final class BleDialogue {

    private static volatile BleDialogue instance = null;

    final Handler mHandler = new Handler();

    private BluetoothGattService service;
    public BluetoothGatt gattMollky;

    private BluetoothGattCharacteristic gattCharacteristic;
    private BluetoothGattCharacteristic gattCharacteristicTx;

    private BleDialogue(){
        super();
    }

    private BleDialogue(Fragment fragment){
        super();
        this.fragment = fragment;
    }

    private Fragment fragment;

    public static BleDialogue getInstance(Fragment fragment){
        if(BleDialogue.instance == null){
            synchronized (BleDialogue.class){
                if(BleDialogue.instance == null){
                    BleDialogue.instance = new BleDialogue(fragment);
                }
            }
        }
        else{
            BleDialogue.instance.fragment = fragment;
        }
        return BleDialogue.instance;
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connected");
                if (gattMollky.discoverServices()) {
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "disconnected");
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "service discovered");
            service = gattMollky.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
            if (service != null) {
                Log.d(TAG, "service ok");
                gattCharacteristic = service.getCharacteristic(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"));
                gattCharacteristicTx = service.getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));

                if (gattMollky.setCharacteristicNotification(gattCharacteristicTx, true)) {
                    Log.d(TAG, "Notification ok");
                }
                // 0x2902 org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
                //Activation de l'écoute des notifications
                UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
                BluetoothGattDescriptor descriptor = gattCharacteristicTx.getDescriptor(uuid);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);

            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite");
            switch (status) {
                case GATT_SUCCESS:
                    Log.d(TAG, "message send success");
                    break;
                case GATT_WRITE_NOT_PERMITTED:
                    Log.d(TAG, "GATT_WRITE_NOT_PERMITTED");
                    break;
                default:
                    Log.d(TAG, String.valueOf(status));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] trame = gattCharacteristicTx.getValue();
            //byte[] trameTest = {12,0,50,6,20,20};
            if (fragment.getClass().equals(GameFragment.class) && fragment.isVisible()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameFragment callBack = (GameFragment) fragment;
                        callBack.callBackBle(trame);
                    }
                });
            }
            else if(fragment.getClass().equals(AddPin.class) && fragment.isVisible()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddPin callBack = (AddPin) fragment;
                        callBack.callBackBle(trame);
                    }
                });
            }
        }

    };

    @SuppressLint("MissingPermission")
    public boolean connect(BluetoothDevice bleDevice){
        if(BlePermission.blePermission(fragment.getActivity())){
            //Disconnect old device
            if(gattMollky != null){
                gattMollky.disconnect();
            }
            //Element for connection
            gattMollky = bleDevice.connectGatt(fragment.getActivity(), false, bluetoothGattCallback);
            if(gattMollky.connect()){
                //Delet all device detected
                MainActivity main = (MainActivity) fragment.getActivity();
                main.bluetoothDevices.clear();

                return true;
            }
            else return false;
        }
        return false;
    }

    public final void runOnUiThread(Runnable action) {
        mHandler.post(action);
    }

    @SuppressLint("MissingPermission")
    public void updatePin(Pin pin, int num){
        byte[] trame = {0, (byte) pin.getNumber(), (byte) num};
        gattCharacteristic.setValue(trame);
        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        gattMollky.writeCharacteristic(gattCharacteristic);
    }
}

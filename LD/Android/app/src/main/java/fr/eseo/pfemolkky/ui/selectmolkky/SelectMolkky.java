package fr.eseo.pfemolkky.ui.selectmolkky;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGatt.GATT_WRITE_NOT_PERMITTED;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static fr.eseo.pfemolkky.service.bluetooth.ScanBle.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.service.bluetooth.BlePermission;
import fr.eseo.pfemolkky.service.bluetooth.BluetoothLeService;

public class SelectMolkky extends Fragment {

    //TODO CHANGER LOCATION OF THIS STATIC
    private static final int REQUEST_ENABLE_BT = 10;

    private MainActivity main;

    private ArrayList<BluetoothDevice> bluetoothMolkkyDevices = new ArrayList<>();

    private BluetoothGattService service;

    private BluetoothGattCharacteristic gattCharacteristic;
    private BluetoothGattCharacteristic gattCharacteristicTx;
    private BluetoothGatt gattMollky;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        fr.eseo.pfemolkky.databinding.FragmentMainBinding binding = FragmentMainBinding.inflate(inflater);
        View inputFragmentView = inflater.inflate(R.layout.fragment_select_molkky, container, false);
        Button bleScan = inputFragmentView.findViewById(R.id.scanBLEBtn);

        main = (MainActivity) getActivity();

        bleScan.setOnClickListener(view -> {
            if (!main.bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(main.bluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                if(BlePermission.blePermission(this.getActivity())){
                    scan(main, this);
                    Log.d(TAG,"taille de test" + main.bluetoothDevices.size());
                }
            }
        });
        return inputFragmentView;
    }

    @SuppressLint("MissingPermission")
    public void showDevice() {
        //TODO SHOW ALL DEVICE

        for (BluetoothDevice ble : main.bluetoothDevices) {
            if (ble.getName() != null) {
                if (ble.getName().equals("Molkky") && !bluetoothMolkkyDevices.contains(ble)) {
                    Log.d(TAG, "find device");
                    bluetoothMolkkyDevices.add(ble);
                    //Element for connection
                    gattMollky = ble.connectGatt(main, false, bluetoothGattCallback);
                    if(gattMollky.connect()){
                    }
                }
            }
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connected");
                if(gattMollky.discoverServices()){
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "disconnected");
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            Log.d(TAG, "service discovered");
            service = gattMollky.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
            if(service != null){
                Log.d(TAG, "service ok");
                gattCharacteristic = service.getCharacteristic(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"));
                gattCharacteristicTx = service.getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
                gattCharacteristic.setValue("3");
                gattCharacteristic.setWriteType(WRITE_TYPE_DEFAULT);
                gattMollky.writeCharacteristic(gattCharacteristic);

                BluetoothGattDescriptor descriptor = gattCharacteristicTx.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gattCharacteristicTx.addDescriptor(descriptor);
                gattCharacteristicTx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                gattMollky.writeDescriptor(descriptor);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                gattCharacteristicTx.addDescriptor(descriptor);
                gattMollky.writeDescriptor(descriptor);
                if(gattMollky.setCharacteristicNotification(gattCharacteristicTx, true)){
                    Log.d(TAG, "Notification ok");
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            switch (status){
                case GATT_SUCCESS:
                    Log.d(TAG, "message send success");
                    break;
                case GATT_WRITE_NOT_PERMITTED:
                    Log.d(TAG, "GATT_WRITE_NOT_PERMITTED");
                    break;
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            Log.d(TAG, "onCharacteristicRead");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic ){
           Log.d(TAG,"billy");
        }

    };
}

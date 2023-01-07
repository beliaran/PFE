package fr.eseo.pfemolkky.ui.selectmolkky;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGatt.GATT_WRITE_NOT_PERMITTED;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static fr.eseo.pfemolkky.service.bluetooth.ScanBle.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.service.bluetooth.BlePermission;
import fr.eseo.pfemolkky.service.bluetooth.BluetoothLeService;

public class SelectMolkky extends Fragment {

    //TODO CHANGER LOCATION OF THIS STATIC
    private static final int REQUEST_ENABLE_BT = 10;

    private MainActivity main;

    private ArrayList<BluetoothDevice> bluetoothMolkkyDevices = new ArrayList<>();
    private View inputFragmentView;
    private LinearLayout linearLayoutListOfDevice;
    private ViewGroup container;
    private LayoutInflater inflater;

    /**
     * Function called when fragment is created <br>
     * Inflate the fragment
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        fr.eseo.pfemolkky.databinding.FragmentMainBinding binding = FragmentMainBinding.inflate(inflater);
        inputFragmentView = inflater.inflate(R.layout.fragment_select_molkky, container, false);
        Button bleScan = inputFragmentView.findViewById(R.id.scanBLEBtn);
        ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        System.out.println(result.getResultCode());
                    }
                });
        main = (MainActivity) getActivity();
        Button buttonReturnToMenu = (Button) inputFragmentView.findViewById(R.id.buttonReturnToMenu);
        buttonReturnToMenu.setOnClickListener(view -> navController.navigate(R.id.nav_main));
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() != null) {
                    navController.navigate(R.id.nav_main);
                }
            }
        };
        this.inflater = inflater;
        this.container = container;
        linearLayoutListOfDevice = (LinearLayout) inputFragmentView.findViewById(R.id.listOfMolkky);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        bleScan.setOnClickListener(view -> {
            if (!main.bluetoothAdapter.isEnabled()) {
                if (BlePermission.blePermission(this.getActivity())) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityIntent.launch(enableBtIntent);
                }
            } else {
                if (BlePermission.blePermission(this.getActivity())) {
                    scan(main, this);
                    Log.d(TAG, "taille de test" + main.bluetoothDevices.size());
                }
            }
        });
        updatePage();
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
                    BluetoothGatt gatt = ble.connectGatt(main, false, bluetoothGattCallback);
                    if (gatt.connect()) {
                        if (gatt.discoverServices()) {
                            BluetoothGattService service = gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
                            BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"));
                            gattCharacteristic.setValue("3");
                            gattCharacteristic.setWriteType(WRITE_TYPE_DEFAULT);
                            gatt.writeCharacteristic(gattCharacteristic);

                            BluetoothGattCharacteristic gattCharacteristicTx = service.getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
                            BluetoothGattDescriptor descriptor = gattCharacteristicTx.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gattCharacteristicTx.addDescriptor(descriptor);
                            gatt.writeDescriptor(descriptor);
                            gatt.setCharacteristicNotification(gattCharacteristicTx, true);

                        }
                    }
                }
            }
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connected");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "disconnected");
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            switch (status) {
                case GATT_SUCCESS:
                    Log.d(TAG, "message send success");
                    break;
                case GATT_WRITE_NOT_PERMITTED:
                    Log.d(TAG, "GATT_WRITE_NOT_PERMITTED");
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, String.valueOf(gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")).getCharacteristic(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")).getValue()));
        }

    };

    public void updatePage() {
        linearLayoutListOfDevice.removeAllViews();
        if (bluetoothMolkkyDevices.isEmpty()) {
            View fragment = inflater.inflate(R.layout.fragment_device, container, false);
            TextView textPin = fragment.findViewById(R.id.textMolkky);
            textPin.setText(getResources().getString(R.string.deviceAnalyzedEmpty));
            linearLayoutListOfDevice.addView(fragment);
        }
        for (BluetoothDevice bluetoothDevice : bluetoothMolkkyDevices) {
            View fragment = inflater.inflate(R.layout.fragment_pin, container, false);
            TextView textPin = fragment.findViewById(R.id.textPin);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                textPin.setText(bluetoothDevice.getName());
                linearLayoutListOfDevice.addView(fragment);
            }
        }
    }
}

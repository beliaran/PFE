package fr.eseo.pfemolkky.ui.selectmolkky;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static fr.eseo.pfemolkky.service.bluetooth.ScanBle.scan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.service.bluetooth.BleDialogue;

public class SelectMolkky extends Fragment {

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
        scan(main, this);
        linearLayoutListOfDevice = (LinearLayout) inputFragmentView.findViewById(R.id.listOfMolkky);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        bleScan.setOnClickListener(view -> {
            scan(main, this);
        });
        updatePage();
        return inputFragmentView;
    }

    @SuppressLint("MissingPermission")
    public void showDevice() {
        for (BluetoothDevice ble : main.bluetoothDevices) {
            if (ble.getName() != null) {
                if (ble.getName().equals("Molkky") && !bluetoothMolkkyDevices.contains(ble)) {
                    Log.d(TAG, "find device");
                    bluetoothMolkkyDevices.add(ble);
                    updatePage();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
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
            textPin.setText(bluetoothDevice.getName());
            linearLayoutListOfDevice.addView(fragment);
            fragment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connect(bluetoothDevice);
                }
            });
        }
    }

    private void connect(BluetoothDevice bluetoothDevice){
        BleDialogue.getInstance(this).connect(bluetoothDevice);
    }

    public void callBack(){
        Toast.makeText(this.getActivity(), "Molkky connected",
                Toast.LENGTH_SHORT).show();
    }

    public void onStop() {
        super.onStop();
        BleDialogue.getInstance(null);
    }

    public void onResume(){
        super.onResume();
        BleDialogue.getInstance(this);
    }

    public void onPause(){
        super.onPause();
        BleDialogue.getInstance(null);
    }
}

package fr.eseo.pfemolkky.ui.selectmolkky;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static fr.eseo.pfemolkky.service.bluetooth.scanBle.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.service.bluetooth.BlePermission;

public class selectMolkky extends Fragment {

    //TODO CHANGER LOCATION OF THIS STATIC
    private static final int REQUEST_ENABLE_BT = 10;

    private MainActivity main;

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
                    scan(main);
                    Log.d(TAG,"taille de test" + main.bluetoothDevices.size());
                }
            }
        });
        return inputFragmentView;
    }
    //TODO SHOW ALL DEVICE
}

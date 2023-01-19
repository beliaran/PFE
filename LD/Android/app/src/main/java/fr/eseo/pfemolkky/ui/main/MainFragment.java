package fr.eseo.pfemolkky.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static fr.eseo.pfemolkky.service.bluetooth.ScanBle.scan;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.service.bluetooth.BlePermission;

/**
 * Class which is called when the User launch the app
 */
public class MainFragment extends Fragment {

    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     * 	&#x27A2 Inflate the fragment <br>
     * 	&#x27FE Navigate to the page to add players if button start a game is clicked <br>
     * 	&#x27FE Navigate to the page to log in if button Log in is clicked <br>
     * 	&#x27FE If button Connect to molkky is clicked <br>
     * 	<div style="padding-left : 10px">
     * 		&#x21a6 Display that bluetooth can not work on the telephone if the bluetooth is not supported on the phone
     * 		&#x21a6 Navigate to the page to select the molkky if the bluetooth is supported on the phone
     * 	</div>
     * 	&#x27FE Close the app when button back is clicked <br>
     * </div>
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
        View inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        Button buttonLogIn = inputFragmentView.findViewById(R.id.buttonLogInMain);
        Button chooseMolkkyBtn = inputFragmentView.findViewById(R.id.buttonConnectMolkky);

        ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        System.out.println(result.getResultCode());
                    }
                });

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setAllowBack(true);
            button.setOnClickListener(view -> {
                navController.navigate(R.id.nav_start_game);
                ((MainActivity) getActivity()).setGame(new Game(((MainActivity) getActivity()).getPins()));
            });
            buttonLogIn.setOnClickListener(view -> {
                navController.navigate(R.id.nav_user_connection);
            });
            chooseMolkkyBtn.setOnClickListener(view -> {

                if (((MainActivity) getActivity()).bluetoothAdapter == null) {
                    //TODO Rajouter au champ string
                    Toast.makeText(getContext(), getResources().getString(R.string.bluetoothNotSupported),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (!((MainActivity) getActivity()).bluetoothAdapter.isEnabled()) {
                        if (BlePermission.blePermission(this.getActivity())) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityIntent.launch(enableBtIntent);
                        }
                    } else {
                        if (BlePermission.blePermission(this.getActivity())) {
                            navController.navigate(R.id.select_molkky);
                            ((MainActivity) getActivity()).setAllowBack(true);
                        }
                    }
                }
            });
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getActivity() != null) {
                        System.out.println("called");
                        getActivity().finish();
                    }
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        }
        return inputFragmentView;
    }
}

package fr.eseo.pfemolkky.ui.battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Pin;

public class BatteryListFragment extends Fragment {
    NavController navController;

    /**
     * Function called when fragment is created <br>
     * Inflate the fragment
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_battery_list, container, false);
        navController = NavHostFragment.findNavController(this);
        if (getActivity() != null) {
            ArrayList<Pin> pins = ((MainActivity) getActivity()).getGame().getPins();
            LinearLayout playerList = root.findViewById((R.id.layout_showBatteryLevel));
            ((MainActivity) getActivity()).setAllowBack(true);
            for (Pin pinIteration : pins) {
                View fragment = inflater.inflate(R.layout.fragment_battery, container, false);
                fragment.findViewById(R.id.layoutBattery).setBackgroundResource(R.drawable.playerroundedcornerdarkpurple);
                TextView textName = fragment.findViewById(R.id.batteryNumber);
                textName.setText(getResources().getString(R.string.pinName, String.valueOf(pinIteration.getNumber())));
                TextView textDisconnected = fragment.findViewById(R.id.batteryDisconnected);
                ImageView imageBattery = fragment.findViewById(R.id.imageBattery);
                if (pinIteration.isConnected()) {
                    textDisconnected.setVisibility(View.INVISIBLE);
                    switch (pinIteration.getBattery()) {
                        case dead: {
                            imageBattery.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battery_low, null));
                            break;
                        }
                        case low: {
                            imageBattery.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battery_25, null));
                            break;
                        }
                        case medium: {
                            imageBattery.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battery_50, null));
                            break;
                        }
                        case excellent: {
                            imageBattery.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battery_75, null));
                            break;
                        }
                        case full: {
                            imageBattery.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.battery_full, null));
                            break;
                        }
                        default: {
                            textDisconnected.setVisibility(View.VISIBLE);
                            imageBattery.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                } else {
                    imageBattery.setVisibility(View.INVISIBLE);
                }
                fragment.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("pinNumber", pinIteration.getNumber());
                    navController.navigate(R.id.nav_add_pin, bundle);
                });
                playerList.addView(fragment);
            }

            Button backToGame = root.findViewById(R.id.buttonValidateRound);
            backToGame.setOnClickListener(view -> (getActivity()).onBackPressed());
        }
        return root;
    }
}

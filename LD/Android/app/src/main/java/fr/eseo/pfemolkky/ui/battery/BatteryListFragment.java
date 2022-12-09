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

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Pin;

public class BatteryListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_battery_list, container, false);
        if(getActivity()!=null){
            ArrayList<Pin> pins = ((MainActivity) getActivity()).getGame().getPins();
            LinearLayout playerList = root.findViewById((R.id.layout_showBatteryLevel));
            ((MainActivity)getActivity()).setAllowBack(true);
            for(Pin pinIteration : pins){
                View fragment = inflater.inflate(R.layout.fragment_battery, container, false);
                fragment.findViewById(R.id.layoutBattery).setBackgroundResource(R.drawable.playerroundedcornerdarkpurple);
                TextView textName = fragment.findViewById(R.id.batteryNumber);
                textName.setText(getResources().getString(R.string.pinName,String.valueOf(pinIteration.getNumber())));
                TextView textDisconnected = fragment.findViewById(R.id.batteryDisconnected);
                ImageView imageBattery = fragment.findViewById(R.id.imageBattery);
                if(pinIteration.isConnected()) {
                    textDisconnected.setVisibility(View.INVISIBLE);
                    switch(pinIteration.getBattery()) {
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
                }
                else {
                    imageBattery.setVisibility(View.INVISIBLE);
                }
                playerList.addView(fragment);
            }

            Button backToGame = root.findViewById(R.id.buttonValidateRound);
            backToGame.setOnClickListener(view -> (getActivity()).onBackPressed());
        }
        return root;
    }
}

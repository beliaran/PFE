package fr.eseo.pfemolkky.ui.addPin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Pin;

public class AddPin extends Fragment {
    private View root;
    private ArrayList<Pin> pinAnalyzed = new ArrayList<>();
    private LinearLayout pinsToAddLayout;
    private LayoutInflater inflater;
    private ViewGroup container;
    private int pinNumber = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_pin, container, false);
        TextView textModifyPin = root.findViewById(R.id.pinsModifiedText);
        pinsToAddLayout = (LinearLayout) root.findViewById(R.id.pinsToAddLayout);
        this.inflater = inflater;
        this.container = container;
        if(getArguments()!=null){
            pinNumber = getArguments().getInt("pinNumber");
            textModifyPin.setText(getResources().getString(R.string.modifyPin, String.valueOf(pinNumber)));
        }
        //Test de frame
        /*String frame = "000000000000000000000000000000000000000000000000";
        System.out.println(frame.length());
        frameAnalysis(frame);*/


        updatePage();
        return root;
    }
    public void updatePage(){
        if(pinAnalyzed.isEmpty()){
            View fragment = inflater.inflate(R.layout.fragment_pin, container,false);
            TextView textPin = fragment.findViewById(R.id.textPin);
            textPin.setText(getResources().getString(R.string.pinAnalyzedEmpty));
            pinsToAddLayout.addView(fragment);
        }
        for(Pin pinIteration : pinAnalyzed){
            View fragment = inflater.inflate(R.layout.fragment_pin, container, false);
            TextView textPin = fragment.findViewById(R.id.textPin);
            textPin.setText(getResources().getString(R.string.pin0));
            fragment.setOnClickListener( view -> {
                if(getActivity()!=null){
                    ((MainActivity)getActivity()).getPins().set(pinNumber-1, pinIteration);
                    // TODO: selectionner la quille et envoyer la trame bluetooth
                    ((MainActivity)getActivity()).onBackPressed();
                }

            });
            pinsToAddLayout.addView(fragment);
        }
    }
    public void frameAnalysis(String frame){
        if(frame.length()==48) {
            String num = frame.substring(0, 7);
            String mod = frame.substring(8, 15);
            String bat = frame.substring(16, 23);
            String accel = frame.substring(24, 31);
            String angle = frame.substring(32, 39);
            String distance = frame.substring(40, 47);
            int intNum = Integer.parseInt(num);
            int intMod = Integer.parseInt(mod);
            int intBat = Integer.parseInt(bat);
            int intAccel = Integer.parseInt(accel);
            int intAngle = Integer.parseInt(angle);
            int intDistance = Integer.parseInt(distance);
            if(intNum == 0 && intMod == 0){
                Pin pin = new Pin(pinNumber);
                if (intBat < 100) {
                    pin.setBattery(Pin.Battery.full);
                }
                if (intBat < 75) {
                    pin.setBattery(Pin.Battery.excellent);
                }
                if (intBat < 50) {
                    pin.setBattery(Pin.Battery.medium);
                }
                if (intBat < 25) {
                    pin.setBattery(Pin.Battery.low);
                }
                if (intBat < 5) {
                    pin.setBattery(Pin.Battery.dead);
                }
                pin.setConnected(true);
                pinAnalyzed.add(pin);
            }
        }

    }
}

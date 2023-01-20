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
import fr.eseo.pfemolkky.service.bluetooth.BleDialogue;
import fr.eseo.pfemolkky.service.bluetooth.BluetoothTrameReader;

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
        BleDialogue.getInstance(this);
        this.inflater = inflater;
        this.container = container;
        if(getArguments()!=null){
            pinNumber = getArguments().getInt("pinNumber");
            textModifyPin.setText(getResources().getString(R.string.modifyPin, String.valueOf(pinNumber)));
        }


        updatePage();
        return root;
    }

    /**
     * Function which update the page with the different pin that are analyzed<br>
     *     <div style="padding-left : 10px">
     *         &#x21a6; If no pin is found :<br>
     *         <div style="padding-left : 10px">
     *               &#x27A2; Method display a frame saying that no pin has been found <br>
     *         </div>
     *         &#8649; for each pins that are found : <br>
     *         <div style="padding-left : 10px">
     *              &#x27A2; Method display a list of frames with the name pin 0 <br>
     *              &#x27FE; Set the function called when the frame is clicked<br>
     *              <div style="padding-left : 10px">
     *                  &#x27A2; Replace the pin to change by the pin clicked<br>
     *                  &#x27A2; Return to menu<br>
     *              </div>
     *         </div>
     *     </div>
     */
    public void updatePage() {
        pinsToAddLayout.removeAllViews();
        if (pinAnalyzed.isEmpty()) {
            View fragment = inflater.inflate(R.layout.fragment_pin, container, false);
            TextView textPin = fragment.findViewById(R.id.textPin);
            textPin.setText(getResources().getString(R.string.pinAnalyzedEmpty));
            pinsToAddLayout.addView(fragment);
        }
        for (Pin pinIteration : pinAnalyzed) {

            View fragment = inflater.inflate(R.layout.fragment_pin, container, false);
            TextView textPin = fragment.findViewById(R.id.textPin);
            textPin.setText(getResources().getString(R.string.pin0));
            fragment.setOnClickListener(view -> {
                if (getActivity() != null) {
                    pinIteration.setNumber(pinNumber);
                    ((MainActivity) getActivity()).getPins().set(pinNumber - 1, pinIteration);
                    BleDialogue.getInstance(this).updatePin(pinIteration,pinNumber);
                    BleDialogue.getInstance(new Fragment());
                    ((MainActivity) getActivity()).onBackPressed();
                }

            });
            pinsToAddLayout.addView(fragment);
        }
    }

    public void callBackBle(byte[] trame){
        pinAnalyzed.add(BluetoothTrameReader.frameReader((MainActivity) this.getActivity(),trame));
        updatePage();
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

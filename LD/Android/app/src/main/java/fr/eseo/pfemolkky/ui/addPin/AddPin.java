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

/**
 * Class which is called when the User navigate to the page to add a pin or replace one
 */
public class AddPin extends Fragment {
    private View root;
    private ArrayList<Pin> pinAnalyzed = new ArrayList<>();
    private LinearLayout pinsToAddLayout;
    private LayoutInflater inflater;
    private ViewGroup container;
    private int pinNumber = 0;

    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     *      &#x27A2 Inflate the fragment <br>
     *      &#x27A2 Change the header <br>
     *      &#x27A2 Update the page <br>
     * </div>
     *
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
        root = inflater.inflate(R.layout.fragment_add_pin, container, false);
        TextView textModifyPin = root.findViewById(R.id.pinsModifiedText);
        pinsToAddLayout = (LinearLayout) root.findViewById(R.id.pinsToAddLayout);
        this.inflater = inflater;
        this.container = container;
        if (getArguments() != null) {
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
    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     *      &#x27A2 Inflate the fragment<br>
     *      &#x27A2 Add a player defined by a EditText and a Fragment fragment_added_player inflated<br>
     *      &#x27A2 Set the button to add player response when clicked <br>
     *
     * </div>
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    /**
     * Function which update the page with the different pin that are analyzed<br>
     *     <div style="padding-left : 10px">
     *         &#x27A2; If no pin is found :<br>
     *         <div style="padding-left : 10px">
     *              &#x21a6; Method display a frame saying that no pin has been found <br>
     *         </div>
     *         &#x27A2; If one or more pins are found : <br>
     *         <div style="padding-left : 10px">
     *              &#x21a6; Method display a list of frames with the name pin 0 <br>
     *              &#x21a6; Set the function called when the frame is clicked :<br>
     *              <div style="padding-left : 10px">
     *                  &#x27FE; Replace the pin to change by the pin clicked<br>
     *                  &#x27FE; Return to menu<br>
     *              </div>
     *         </div>
     *     </div>
     */
    public void updatePage() {
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
                    ((MainActivity) getActivity()).getPins().set(pinNumber - 1, pinIteration);
                    // TODO: selectionner la quille et envoyer la trame bluetooth
                    ((MainActivity) getActivity()).onBackPressed();
                }

            });
            pinsToAddLayout.addView(fragment);
        }
    }

    public void frameAnalysis(String frame) {
        if (frame.length() == 48) {
            String num = frame.substring(0, 7);
            String mod = frame.substring(8, 15);
            String bat = frame.substring(16, 23);
            String accel = frame.substring(24, 31);
            String angle = frame.substring(32, 39);
            String distance = frame.substring(40, 47);
            int intNum = Integer.parseInt(num, 2);
            int intMod = Integer.parseInt(mod, 2);
            int intBat = Integer.parseInt(bat, 2);
            int intAccel = Integer.parseInt(accel, 2);
            int intAngle = Integer.parseInt(angle, 2);
            int intDistance = Integer.parseInt(distance, 2);
            if (intNum == 0 && intMod == 0) {
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

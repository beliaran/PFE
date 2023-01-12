package fr.eseo.pfemolkky.utils;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.models.Pin;

public class BluetoothFrameReader {

    public  static void frameReader(MainActivity activity, byte[] trame){
        frameReader(activity, trame, new AtomicReference<>());
    }

    public static void frameReader(MainActivity activity, byte[] trame, AtomicReference<Boolean> nextTurn){
        ArrayList<Pin> pins = activity.getPins();
        if(trame.length == 6){
            int num = trame[0]-1;

            int mod = trame[1];
            int bat = trame[2];
            int accel = trame[3];
            int angle = trame[4];
            int distance = trame[5];

            pins.get(num).setConnected(true);

            if (mod == 1 && !nextTurn.get()) {
                pins.get(num).setFallen(true);
            }
            if (bat < 100) {
                pins.get(num).setBattery(Pin.Battery.full);
            }
            if (bat < 75) {
                pins.get(num).setBattery(Pin.Battery.excellent);
            }
            if (bat < 50) {
                pins.get(num).setBattery(Pin.Battery.medium);
            }
            if (bat < 25) {
                pins.get(num).setBattery(Pin.Battery.low);
            }
            if (bat < 5) {
                pins.get(num).setBattery(Pin.Battery.dead);
            }
            if (mod == 2) {
                pins.get(num).setConnected(false);
            }
        }
        activity.setPins(pins);
    }
}

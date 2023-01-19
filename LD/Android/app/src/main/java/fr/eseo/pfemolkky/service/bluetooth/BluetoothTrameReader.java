package fr.eseo.pfemolkky.service.bluetooth;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.models.Pin;

public class BluetoothTrameReader {

    public  static Pin frameReader(MainActivity activity, byte[] trame){
        return frameReader(activity, trame, new AtomicReference<>());
    }

    public static Pin frameReader(MainActivity activity, byte[] trame, AtomicReference<Boolean> nextTurn){
        System.out.println(activity);
        ArrayList<Pin> pins = activity.getPins();
        Pin pin = new Pin();
        System.out.println(Arrays.toString(trame));
        System.out.println(trame.length);
        Context context = activity.getApplicationContext();
        CharSequence text = Arrays.toString(trame) + "\n length : "+ String.valueOf(trame.length);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        if(trame.length >= 6 && trame.length <= 7 && trame[0] > 0 && trame[0] <= 12){
            int num = trame[0];
            int index = num -1;

            int mod = trame[1];
            int bat = trame[2];
            int accel = trame[3];
            int angle = trame[4];
            int distance = trame[5];


            pin.setNumber(num);
            pin.setConnected(true);
            if (bat < 100) {
                pin.setBattery(Pin.Battery.full);
            }
            if (bat < 75) {
                pin.setBattery(Pin.Battery.excellent);
            }
            if (bat < 50) {
                pin.setBattery(Pin.Battery.medium);
            }
            if (bat < 25) {
                pin.setBattery(Pin.Battery.low);
            }
            if (bat < 5) {
                pin.setBattery(Pin.Battery.dead);
            }
            if (mod == 2) {
                pin.setConnected(false);
                pins.set(index,pin);
            }
            if (mod == 1 && !nextTurn.get()) {
                pin.setFallen(true);
                pins.set(index,pin);
            }
            if(mod == 3){
                pins.set(index,pin);
            }

        }
        activity.setPins(pins);
        return pin;
    }
}

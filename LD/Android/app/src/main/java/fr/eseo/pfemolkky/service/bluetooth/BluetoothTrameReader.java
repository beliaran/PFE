package fr.eseo.pfemolkky.service.bluetooth;

import java.util.ArrayList;
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
        if(trame.length == 6){
            int num = trame[0]-1;

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
                pins.set(num,pin);
            }
            if (mod == 1 && !nextTurn.get()) {
                pin.setFallen(true);
                pins.set(num,pin);
            }
            if(mod == 3){
                pins.set(num,pin);
            }

        }
        activity.setPins(pins);
        return pin;
    }
}

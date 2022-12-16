package fr.eseo.pfemolkky.utils;

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.models.Pin;

public class BluetoothFrameReader {
    public void frameReader(MainActivity activity, String frame){
        ArrayList<Pin> pins = activity.getPins();
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
            pins.get(intNum).setConnected(true);
            if (intBat < 100) {
                pins.get(intNum).setBattery(Pin.Battery.full);
            }
            if (intBat < 75) {
                pins.get(intNum).setBattery(Pin.Battery.excellent);
            }
            if (intBat < 50) {
                pins.get(intNum).setBattery(Pin.Battery.medium);
            }
            if (intBat < 25) {
                pins.get(intNum).setBattery(Pin.Battery.low);
            }
            if (intBat < 5) {
                pins.get(intNum).setBattery(Pin.Battery.dead);
            }
            if (intMod == 2) {
                pins.get(intNum).setConnected(false);
            }
        }
    }
}

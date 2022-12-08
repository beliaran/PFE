package fr.eseo.pfemolkky.tests;

import java.util.ArrayList;

import fr.eseo.pfemolkky.models.Pin;

public class TestBattery {
    ArrayList<Pin> pins=new ArrayList<>();

    public ArrayList<Pin> getPins() {
        return pins;
    }

    public void setPins(ArrayList<Pin> pins) {
        this.pins = pins;
    }
    public TestBattery(){
        Pin pin1 = new Pin(1, Pin.Battery.full);
        Pin pin2 = new Pin(2, Pin.Battery.excellent);
        Pin pin3 = new Pin(3, Pin.Battery.medium);
        Pin pin4 = new Pin(4, Pin.Battery.low);
        Pin pin5 = new Pin(5);
        Pin pin6 = new Pin(6, Pin.Battery.full);
        Pin pin7 = new Pin(7, Pin.Battery.excellent);
        Pin pin8 = new Pin(8, Pin.Battery.medium);
        Pin pin9 = new Pin(9, Pin.Battery.low);
        Pin pin10 = new Pin(10);
        Pin pin11 = new Pin(11, Pin.Battery.full);
        Pin pin12 = new Pin(12, Pin.Battery.excellent);
        pins.add(pin1);
        pins.add(pin2);
        pins.add(pin3);
        pins.add(pin4);
        pins.add(pin5);
        pins.add(pin6);
        pins.add(pin7);
        pins.add(pin8);
        pins.add(pin9);
        pins.add(pin10);
        pins.add(pin11);
        pins.add(pin12);


    }
}

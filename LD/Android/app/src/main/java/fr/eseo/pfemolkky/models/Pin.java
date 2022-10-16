package fr.eseo.pfemolkky.models;

public class Pin {
    private int number;
    private boolean connected = false;
    private boolean fallen = false;
    private Battery battery = Battery.dead;

    public Pin(int i) {
        number=i;
    }

    public enum Battery {dead, low, medium, excellent, full};

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    public boolean hasFallen() {
        return fallen;
    }

    public void setFallen(boolean fallen) {
        this.fallen = fallen;
    }
}

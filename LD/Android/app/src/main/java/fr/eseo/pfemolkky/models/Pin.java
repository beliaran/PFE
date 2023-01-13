package fr.eseo.pfemolkky.models;

/**
 * Class that initialize one pin
 */
public class Pin {
    /**
     * The number displayed on the pin
     */
    private int number;
    /**
     * The value if the pin is connected
     */
    private boolean connected = false;
    /**
     * The value if the pin is fallen
     */
    private boolean fallen = false;
    /***
     * The battery level between the 5 states in the enum Battery
     */
    private Battery battery = Battery.dead;

    /**
     * An enumeration of the different state of battery that a pin can have
     */
    public enum Battery {dead, low, medium, excellent, full}

    /**
     * Constructor which will initialize a pin taking only the pin number<br>
     * The pin is considered not connected<br>
     * The pin is set not fallen<br>
     * The battery is considered dead<br>
     *
     * @param pinNumber the number of the pin
     */
    public Pin(int pinNumber) {
        setNumber(pinNumber);
    }

    /**
     * Constructor which will initialize a pin taking the pin number and its state of battery<br>
     * The pin is considered connected<br>
     * The pin is set not fallen<br>
     *
     * @param pinNumber the number of the pin
     * @param battery   the battery of the pin according to the state of the battery
     */
    public Pin(int pinNumber, Battery battery) {
        setNumber(pinNumber);
        setBattery(battery);
        setConnected(true);
    }

    /**
     * Return the state of connection of the pin
     *
     * @return true if the pin is connected, else false
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Replace the current state of connection of the Pin
     *
     * @param connected the state of connection between true or false
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Get the number of the Pin
     *
     * @return the number of the Pin
     */
    public int getNumber() {
        return number;
    }

    /**
     * Replace the number of the Pin by another
     *
     * @param number the number of the pin
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get the current state of battery
     *
     * @return the current state of battery
     */
    public Battery getBattery() {
        return battery;
    }

    /**
     * Replace the current state of the battery of the Pin by another
     *
     * @param battery the state of the battery
     */
    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    /**
     * Get if the Pin has fallen or not
     *
     * @return true if the Pin has fallen, else false
     */
    public boolean hasFallen() {
        return fallen;
    }

    /**
     * Set if the Pin has fallen or not
     *
     * @param fallen the state of the battery
     */
    public void setFallen(boolean fallen) {
        this.fallen = fallen;
    }
}

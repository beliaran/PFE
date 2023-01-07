package fr.eseo.pfemolkky.models;

import androidx.annotation.NonNull;

/**
 * Class which initialize a player
 */
public class Player {
    private String name;
    private int missed = 0;
    private int score = 0;
    private int uniquePin = 0;
    private int fallenPins = 0;

    /**
     * Get the number of Pins that the player has knocked over
     *
     * @return the number of Pins that the player knocked over
     */
    public int getFallenPins() {
        return fallenPins;
    }

    /**
     * Replace the current number of Pins that the player has knocked over by another
     *
     * @param fallenPins the new number of Pins that the player knocked over
     */
    public void setFallenPins(int fallenPins) {
        this.fallenPins = fallenPins;
    }

    /**
     * Get the number of times the player knocked over only one Pin
     *
     * @return the number of times the player knocked over only one Pin
     */
    public int getUniquePin() {
        return uniquePin;
    }

    /**
     * Replace the current number of times the player knocked over only one Pin by another
     *
     * @param uniquePin the new number of times the player knocked over only one Pin
     */
    public void setUniquePin(int uniquePin) {
        this.uniquePin = uniquePin;
    }

    /**
     * The number of times a player missed the Pins during a round
     *
     * @return the number of times a player missed the Pins during a round between 0 and 3
     */
    public int getMissed() {
        return missed;
    }

    /**
     * Replace the number of times a player missed the Pins during a round by another
     *
     * @param missed the number of times a player missed the Pins during a round between 0 and 3
     */
    public void setMissed(int missed) {
        this.missed = missed;
    }

    /**
     * Get the score of the player
     *
     * @return the score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Replace the previous score of the player by a new one
     *
     * @param score the new score of the player
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get the name of the player
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Replace the name of the player by another
     *
     * @param name the new name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Construtor of the class which take the name of the player only <br>
     * The score of the player is set to 0 <br>
     * The number of miss is set to 0 <br>
     * The number of stroke Pin is set to 0 <br>
     * The number of times only one pin has fallen is set to 0
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.setName(name);
    }

    /**
     * Get a string representation of the object
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", missed=" + missed +
                '}';
    }
}

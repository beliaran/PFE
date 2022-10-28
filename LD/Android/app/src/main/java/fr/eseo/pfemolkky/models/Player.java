package fr.eseo.pfemolkky.models;

public class Player {
    private String name;
    private int missed = 0;
    private int score = 0;
    private int uniquePin = 0;
    private int fallenPins = 0;

    public int getFallenPins() {
        return fallenPins;
    }

    public void setFallenPins(int fallenPins) {
        this.fallenPins = fallenPins;
    }

    public int getUniquePin() {
        return uniquePin;
    }

    public void setUniquePin(int uniquePin) {
        this.uniquePin = uniquePin;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", missed=" + missed +
                '}';
    }
}

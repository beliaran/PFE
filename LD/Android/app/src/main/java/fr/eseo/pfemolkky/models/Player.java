package fr.eseo.pfemolkky.models;

public class Player {
    private String name;
    private int missed = 0;
    private int score = 0;

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

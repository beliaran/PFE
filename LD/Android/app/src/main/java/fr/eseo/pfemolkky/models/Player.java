package fr.eseo.pfemolkky.models;

public class Player {
    private String name;
    private int missed = 0;

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

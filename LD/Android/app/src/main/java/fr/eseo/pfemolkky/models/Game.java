package fr.eseo.pfemolkky.models;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> playersList = new ArrayList<>();
    private ArrayList<Pin> pins = new ArrayList<>();
    private TypeOfGame typeOfGame;
    private int scoreToWin;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    private int round = 1;
    public enum TypeOfGame {tournament, classic, fast, pastis}

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public ArrayList<Player> getPlayersList() {
        return playersList;
    }
    public void addPlayer(Player player) {
        this.players.add(player);
        this.playersList.add(player);
    }

    public void setPins(ArrayList<Pin> pins) {
        this.pins = pins;
    }

    public ArrayList<Pin> getPins() {
        return pins;
    }

    @SuppressWarnings("unused")
    public Game(){
        for(int i=1;i<=12;i++){
            Pin pin = new Pin(i);
            pins.add(pin);
        }
    }
    public Game(ArrayList<Pin> pins){
        setPins(pins);
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playersList = players;
    }

    public TypeOfGame getTypeOfGame() {
        return typeOfGame;
    }

    public int getScoreToWin() {
        return scoreToWin;
    }

    public void setScoreToWin(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    public void setTypeOfGame(TypeOfGame typeOfGame) {
        if(typeOfGame==TypeOfGame.classic || typeOfGame==TypeOfGame.tournament){
            this.setScoreToWin(50);
        }
        if(typeOfGame==TypeOfGame.fast){
            this.setScoreToWin(30);
        }
        if(typeOfGame==TypeOfGame.pastis){
            this.setScoreToWin(51);
        }
        this.typeOfGame = typeOfGame;
    }
}

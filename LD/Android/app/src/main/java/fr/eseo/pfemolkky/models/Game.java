package fr.eseo.pfemolkky.models;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Pin> pins = new ArrayList<>();
    private TypeOfGame typeOfGame;
    private int scoreToWin;
    public enum TypeOfGame {tournament, classic, fast, pastis}

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public Game(){
        for(int i=1;i<=12;i++){
            Pin pin = new Pin(i);
        }
    }

    public void setPlayers(ArrayList<Player> players) {
        players = players;
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

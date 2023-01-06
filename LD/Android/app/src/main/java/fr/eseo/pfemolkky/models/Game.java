package fr.eseo.pfemolkky.models;

import java.util.ArrayList;

public class Game {

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> playersList = new ArrayList<>();
    private ArrayList<Pin> pins = new ArrayList<>();
    private TypeOfGame typeOfGame;
    private int scoreToWin;

    /**
     * Get the current round of the game
     * @return the current round of the game
     *
     */
    public int getRound() {
        return round;
    }

    /**
     * Set the round of the game
     * @param round The number of the round
     */
    public void setRound(int round) {
        this.round = round;
    }

    private int round = 1;

    /**
     * An enumeration of all the type of games
     */
    public enum TypeOfGame {tournament, classic, fast, pastis}

    /**
     * Get the list of players according if they are not disqualified
     * @return a list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }
    /**
     * Get the list of all players
     * @return a list of players
     */
    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    /**
     * Add a player to the list of all players of the game
     * @param player the player to add to the game
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        this.playersList.add(player);
    }

    /**
     * Replace the current Pins of the game to an array with all the pins
     * @param pins an arrayList containing all pins to add
     */
    public void setPins(ArrayList<Pin> pins) {
        this.pins = pins;
    }

    /**
     * Get all the pins
     * @return the pins of the game
     */
    public ArrayList<Pin> getPins() {
        return pins;
    }

    /**
     * Empty constructor which initialize a Game with deconnected Pins
     * @deprecated use Game(ArrayList<Pin> pins) instead
     */
    @SuppressWarnings("unused")
    public Game(){
        for(int i=1;i<=12;i++){
            Pin pin = new Pin(i);
            pins.add(pin);
        }
    }

    /**
     * Constructor which initialize a Game taking as parameter the Pins that are whever or not
     * connected
     * @param pins an ArrayList of Pins that have been initialized
     */
    public Game(ArrayList<Pin> pins){
        setPins(pins);
    }

    /**
     * Replace the current List of Players by another
     * @param players an ArrayList of Players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playersList = players;
    }

    /**
     * Get the type of the current game
     * @return the type of the game
     */
    public TypeOfGame getTypeOfGame() {
        return typeOfGame;
    }

    /**
     * Get the necessary Score to win depending on the type of game
     * @return the score to win
     */
    public int getScoreToWin() {
        return scoreToWin;
    }

    /**
     * Replace the current value of the necessary score to win
     * @param scoreToWin the score to win the game
     */
    public void setScoreToWin(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    /**
     *
     * @param typeOfGame 
     */
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

package fr.eseo.pfemolkky.models;

import java.util.ArrayList;

/**
 * Class which will rule the game and define its main characteristics
 */
public class Game {
    /**
     * The list of players which will be modified during the game
     */
    private ArrayList<Player> players = new ArrayList<>();
    /**
     * The list of players which will remain unchanged during the whole game
     */
    private ArrayList<Player> playersList = new ArrayList<>();
    /**
     * The list of pins of the game
     */
    private ArrayList<Pin> pins = new ArrayList<>();
    /**
     * The number of rounds the game takes
     */
    private int round = 1;
    /**
     * The type of game according to the TypeOfGame enumeration
     */
    private TypeOfGame typeOfGame;
    /**
     * The score needed to win the game according to the type of the game
     */
    private int scoreToWin;

    /**
     * Get the current round of the game
     *
     * @return the current round of the game
     */
    public int getRound() {
        return round;
    }

    /**
     * Set the round of the game
     *
     * @param round The number of the round
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Get if the end of the game is reached
     * @param player the current player
     * @return a boolean which check if the player won the game
     */
    public boolean checkIfEndGame(Player player) {
        return (this.getTypeOfGame() == Game.TypeOfGame.tournament && this.getPlayers().size() == 1) || player.getScore() == this.getScoreToWin();
    }

    /**
     * Get if the player is disqualified
     * @param player the current player
     * @return a boolean which check if the player is disqualified
     */
    public boolean checkIfDisqualified(Player player) {
        return this.getTypeOfGame() == Game.TypeOfGame.tournament && player.getMissed()==0 && player.getScore()==0;
    }


    /**
     * An enumeration of all the type of games
     */
    public enum TypeOfGame {tournament, classic, fast, pastis}

    /**
     * Get the list of players according if they are not disqualified
     *
     * @return a list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Get the list of all players
     *
     * @return a list of players
     */
    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    /**
     * Add a player to the list of all players of the game
     *
     * @param player the player to add to the game
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        this.playersList.add(player);
    }

    /**
     * Replace the current Pins of the game to an array with all the pins
     *
     * @param pins an arrayList containing all pins to add
     */
    public void setPins(ArrayList<Pin> pins) {
        this.pins = pins;
    }

    /**
     * Get all the pins
     *
     * @return the pins of the game
     */
    public ArrayList<Pin> getPins() {
        return pins;
    }

    /**
     * Empty constructor which initialize a Game with deconnected Pins
     *
     * @deprecated use Game(ArrayList<Pin> pins) instead
     */
    @SuppressWarnings("unused")
    public Game() {
        for (int i = 1; i <= 12; i++) {
            Pin pin = new Pin(i);
            pins.add(pin);
        }
    }

    /**
     * Constructor which initialize a Game taking as parameter the Pins that are whever or not
     * connected
     *
     * @param pins an ArrayList of Pins that have been initialized
     */
    public Game(ArrayList<Pin> pins) {
        setPins(pins);
    }

    /**
     * Replace the current List of Players by another
     *
     * @param players an ArrayList of Players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playersList = players;
    }

    /**
     * Get the type of the current game
     *
     * @return the type of the game
     */
    public TypeOfGame getTypeOfGame() {
        return typeOfGame;
    }

    /**
     * Get the necessary Score to win depending on the type of game
     *
     * @return the score to win
     */
    public int getScoreToWin() {
        return scoreToWin;
    }

    /**
     * Replace the current value of the necessary score to win
     *
     * @param scoreToWin the score to win the game
     */
    public void setScoreToWin(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    /**
     * Replace the current score to win by another
     *
     * @param typeOfGame the new type of game
     */
    public void setTypeOfGame(TypeOfGame typeOfGame) {
        if (typeOfGame == TypeOfGame.classic || typeOfGame == TypeOfGame.tournament) {
            this.setScoreToWin(50);
        }
        if (typeOfGame == TypeOfGame.fast) {
            this.setScoreToWin(30);
        }
        if (typeOfGame == TypeOfGame.pastis) {
            this.setScoreToWin(51);
        }
        this.typeOfGame = typeOfGame;
    }
}

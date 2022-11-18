package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Player;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Winner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Winner extends Fragment {
    private View root;
    private int winnerPlayerNumber;
    private Player player;
    private NavController navController;
    private ArrayList<Player> players;
    private int globalScore=0;
    private float averageScore;
    private float averageScorePerRound;

    public Winner() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Winner newInstance(String param1, String param2) {
        Winner fragment = new Winner();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_winner, container, false);
        navController = NavHostFragment.findNavController(this);
        winnerPlayerNumber = getArguments().getInt("winner");
        Game game = ((MainActivity) getActivity()).getGame();
        player = ((MainActivity)getActivity()).getGame().getPlayers().get(winnerPlayerNumber);
        players = ((MainActivity)getActivity()).getGame().getPlayersList();
        Player brute;
        brute=players.get(0);
        Player sniper;
        sniper=players.get(0);
        LinearLayout playerList = (LinearLayout) root.findViewById(R.id.playerList);
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player t1, Player t2) {
                return t1.getScore()-t2.getScore();
            }
        });
        Collections.reverse(players);
        for(Player playerIteration : players){
            globalScore+=playerIteration.getScore();
            if(brute.getFallenPins()<playerIteration.getFallenPins()){
                brute=playerIteration;
            }
            if(sniper.getUniquePin()<playerIteration.getUniquePin()){
                sniper=playerIteration;
            }
            View fragment = inflater.inflate(R.layout.fragment_player_score_board, container, false);
            TextView textName = fragment.findViewById(R.id.playerName);
            textName.setText(playerIteration.getName());
            TextView textScore = fragment.findViewById(R.id.playerScore);
            textScore.setText(String.valueOf(playerIteration.getScore())+"/"+String.valueOf(game.getScoreToWin()));
            playerList.addView(fragment);
        }
        averageScore=globalScore/players.size();
        averageScorePerRound=averageScore/((MainActivity)getActivity()).getGame().getRound();
        TextView textSniper= root.findViewById(R.id.playerSniper);
        TextView textBrute= root.findViewById(R.id.playerBrute);
        TextView textAverageScorePerRound= root.findViewById(R.id.averageScorePerRound);
        textSniper.setText(sniper.getName());
        textBrute.setText(brute.getName());
        textAverageScorePerRound.setText(String.valueOf(averageScorePerRound));
        Button backToMenu = root.findViewById(R.id.buttonReturnToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_main);
            }
        });
        return root;
    }
}
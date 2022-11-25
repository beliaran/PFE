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

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Player;

public class Winner extends Fragment {
    private NavController navController;
    private int globalScore=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_winner, container, false);
        navController = NavHostFragment.findNavController(this);
        if(getActivity()!=null){
            Game game = ((MainActivity) getActivity()).getGame();
            ArrayList<Player> players = game.getPlayersList();
            Player brute;
            brute= players.get(0);
            Player sniper;
            sniper= players.get(0);
            LinearLayout playerList = root.findViewById(R.id.playerList);
            players.sort(Comparator.comparingInt(Player::getScore));
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
                textScore.setText(getResources().getString(R.string.displayScore, String.valueOf(playerIteration.getScore()), String.valueOf(game.getScoreToWin())));
                playerList.addView(fragment);
            }
            float averageScore = (float)globalScore / (float)players.size();
            float averageScorePerRound = averageScore / ((MainActivity) getActivity()).getGame().getRound();
            TextView textSniper= root.findViewById(R.id.playerSniper);
            TextView textBrute= root.findViewById(R.id.playerBrute);
            TextView textAverageScorePerRound= root.findViewById(R.id.averageScorePerRound);
            textSniper.setText(sniper.getName());
            textBrute.setText(brute.getName());
            textAverageScorePerRound.setText(String.valueOf(averageScorePerRound));
            Button backToMenu = root.findViewById(R.id.buttonReturnToMenu);
            backToMenu.setOnClickListener(view -> navController.navigate(R.id.nav_main));
        }
        return root;
    }
}
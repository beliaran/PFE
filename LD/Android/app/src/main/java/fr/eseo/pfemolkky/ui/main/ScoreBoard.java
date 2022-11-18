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

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Player;


public class ScoreBoard extends Fragment {

    public ScoreBoard() {
        // Required empty public constructor
    }

    public static ScoreBoard newInstance(String param1, String param2) {
        ScoreBoard fragment = new ScoreBoard();
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
        View root = inflater.inflate(R.layout.fragment_score_board, container, false);
        LinearLayout linearLayoutPlayerScore = root.findViewById(R.id.layout_showPlayerScore);

        ArrayList<Player> players = ((MainActivity) getActivity()).getGame().getPlayers();
        LinearLayout playerList = (LinearLayout) root.findViewById((R.id.layout_showPlayerScore));
        ((MainActivity)getActivity()).setAllowBack(true);
        for(Player playerIteration : players){
            View fragment = inflater.inflate(R.layout.fragment_player_score_board, container, false);
            fragment.findViewById(R.id.layoutScoreBoard).setBackgroundResource(R.drawable.playerroundedcorner_dark_purple);
            TextView textName = fragment.findViewById(R.id.playerName);
            textName.setText(playerIteration.getName());
            TextView textScore = fragment.findViewById(R.id.playerScore);
            textScore.setText(String.valueOf(playerIteration.getScore()));
            playerList.addView(fragment);
        }

        Button backToGame = root.findViewById(R.id.buttonValidateRound);
        backToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });
        return root;
    }
}
package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Player;


public class ScoreBoard extends Fragment {

    private View root;
    private ArrayList<Player> players;
    private LinearLayout linearLayoutPlayerScore;

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
        root = inflater.inflate(R.layout.fragment_score_board, container, false);
        linearLayoutPlayerScore = root.findViewById(R.id.linearLayout_player_score);
        players = ((MainActivity)getActivity()).getGame().getPlayers();
        ((MainActivity)getActivity()).setAllowBack(true);
        for(Player player : players){
            LinearLayout linearLayoutHorizontal = new LinearLayout(getContext());
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayoutHorizontal.setLayoutParams(params);
            ConstraintLayout constraintLayoutPlayerName= new ConstraintLayout(getContext());
            TextView playerName = new TextView(getContext());
            playerName.setTextSize(20);
            playerName.setTextColor(getResources().getColor(R.color.white));
            playerName.setText(player.getName());
            constraintLayoutPlayerName.addView(playerName);
            ConstraintLayout.LayoutParams paramsLayoutName = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
            constraintLayoutPlayerName.setLayoutParams(paramsLayoutName);

            ConstraintLayout constraintLayoutPlayerScore= new ConstraintLayout(getContext());
            TextView playerScore = new TextView(getContext());
            playerScore.setTextSize(20);
            playerScore.setTextColor(getResources().getColor(R.color.white));
            playerScore.setText(String.valueOf(player.getScore()));
            constraintLayoutPlayerScore.addView(playerScore);
            ConstraintLayout.LayoutParams paramsLayoutScore = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
            constraintLayoutPlayerScore.setLayoutParams(paramsLayoutScore);

            linearLayoutHorizontal.addView(constraintLayoutPlayerName);
            linearLayoutHorizontal.addView(constraintLayoutPlayerScore);
            linearLayoutPlayerScore.addView(linearLayoutHorizontal);
        }
        return root;
    }
}
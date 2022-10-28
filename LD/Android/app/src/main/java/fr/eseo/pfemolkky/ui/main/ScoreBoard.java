package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
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

            //generate a linearLayoutHorizontal and set parameters
            LinearLayout linearLayoutHorizontal = new LinearLayout(getContext());
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayoutHorizontal.setLayoutParams(params);

            //generate two constraintLayout
            ConstraintLayout constraintLayoutPlayerName= new ConstraintLayout(getContext());
            constraintLayoutPlayerName.setId(View.generateViewId());
            ConstraintLayout constraintLayoutPlayerScore= new ConstraintLayout(getContext());
            constraintLayoutPlayerScore.setId(View.generateViewId());

            //add the constraint layout to the linear layout and add it to the linearLayoutPlayerScore
            linearLayoutHorizontal.addView(constraintLayoutPlayerName);
            linearLayoutHorizontal.addView(constraintLayoutPlayerScore);
            linearLayoutPlayerScore.addView(linearLayoutHorizontal);

            //Create two TextView with the name and score
            TextView playerName = new TextView(getContext());
            playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            playerName.setTextColor(getResources().getColor(R.color.white));
            playerName.setText(player.getName());
            playerName.setId(View.generateViewId());

            TextView playerScore = new TextView(getContext());
            playerScore.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            playerScore.setTextColor(getResources().getColor(R.color.white));
            playerScore.setText(String.valueOf(player.getScore()));
            playerScore.setId(View.generateViewId());

            //Add the TextViews to the ConstraintLayout
            constraintLayoutPlayerName.addView(playerName);
            constraintLayoutPlayerScore.addView(playerScore);

            //Set the constraintSet for the information to be displayed
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayoutPlayerName);
            cs.setVerticalBias(playerName.getId(),0.0f);
            cs.setHorizontalBias(playerName.getId(),0.108f);
            cs.connect(playerName.getId(), ConstraintSet.START,ConstraintSet.PARENT_ID, ConstraintSet.START,0);
            cs.connect(playerName.getId(), ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END,0);
            cs.connect(playerName.getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP,0);
            cs.connect(playerName.getId(), ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,0);
            cs.applyTo(constraintLayoutPlayerName);

            ConstraintSet cs2 = new ConstraintSet();
            cs2.clone(constraintLayoutPlayerScore);
            cs2.setVerticalBias(playerScore.getId(),0.0f);
            cs2.setHorizontalBias(playerScore.getId(),0.891f);
            cs2.connect(playerScore.getId(), ConstraintSet.START,ConstraintSet.PARENT_ID, ConstraintSet.START,0);
            cs2.connect(playerScore.getId(), ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END,0);
            cs2.connect(playerScore.getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP,0);
            cs2.connect(playerScore.getId(), ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,0);
            cs2.applyTo(constraintLayoutPlayerScore);

            //Set the layout params of the constraintLayout
            LinearLayout.LayoutParams paramsLayoutName = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            constraintLayoutPlayerName.setLayoutParams(paramsLayoutName);
            LinearLayout.LayoutParams paramsLayoutScore = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            constraintLayoutPlayerScore.setLayoutParams(paramsLayoutScore);

        }
        return root;
    }
}
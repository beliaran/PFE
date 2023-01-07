package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    /**
     * Function called when fragment is created <br>
     * Inflate the fragment
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_score_board, container, false);
        if (getActivity() != null) {
            ArrayList<Player> players = ((MainActivity) getActivity()).getGame().getPlayers();
            LinearLayout playerList = root.findViewById((R.id.layout_showPlayerScore));
            ((MainActivity) getActivity()).setAllowBack(true);
            for (Player playerIteration : players) {
                View fragment = inflater.inflate(R.layout.fragment_player_score_board, container, false);
                fragment.findViewById(R.id.layoutScoreBoard).setBackgroundResource(R.drawable.playerroundedcornerdarkpurple);
                TextView textName = fragment.findViewById(R.id.playerName);
                textName.setText(playerIteration.getName());
                TextView textScore = fragment.findViewById(R.id.playerScore);
                textScore.setText(String.valueOf(playerIteration.getScore()));
                playerList.addView(fragment);
            }

            Button backToGame = root.findViewById(R.id.buttonValidateRound);
            backToGame.setOnClickListener(view -> (getActivity()).onBackPressed());
        }
        return root;
    }
}
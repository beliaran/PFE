package fr.eseo.pfemolkky.ui.chooseGame;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Game.TypeOfGame;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chooseTypeGame#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chooseTypeGame extends Fragment {


    private View root;

    public chooseTypeGame() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static chooseTypeGame newInstance(String param1, String param2) {
        chooseTypeGame fragment = new chooseTypeGame();
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
        root = inflater.inflate(R.layout.fragment_choose_type_game, container, false);
        ConstraintLayout tournamentButton = (ConstraintLayout) root.findViewById(R.id.tournamentButton);
        tournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.tournament);
            }
        });
        ConstraintLayout classicButton = (ConstraintLayout) root.findViewById(R.id.classicButton);
        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.classic);
            }
        });
        ConstraintLayout quickGameButton = (ConstraintLayout) root.findViewById(R.id.quickGameButton);
        quickGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(Game.TypeOfGame.fast);
            }
        });
        ConstraintLayout pastisButton = (ConstraintLayout) root.findViewById(R.id.pastisButton);
        pastisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(Game.TypeOfGame.pastis);
            }
        });
        return root;
    }
}
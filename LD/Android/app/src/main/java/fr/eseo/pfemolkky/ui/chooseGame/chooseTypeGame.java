package fr.eseo.pfemolkky.ui.chooseGame;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
    private NavController navController;

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
        navController = NavHostFragment.findNavController(this);
        ConstraintLayout tournamentButton = (ConstraintLayout) root.findViewById(R.id.tournamentButton);
        Bundle bundle = new Bundle();
        bundle.putInt("player",0);
        tournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.tournament);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        ConstraintLayout classicButton = (ConstraintLayout) root.findViewById(R.id.classicButton);
        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.classic);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        ConstraintLayout quickGameButton = (ConstraintLayout) root.findViewById(R.id.quickGameButton);
        quickGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(Game.TypeOfGame.fast);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        ConstraintLayout pastisButton = (ConstraintLayout) root.findViewById(R.id.pastisButton);
        pastisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().setTypeOfGame(Game.TypeOfGame.pastis);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        return root;
    }
}
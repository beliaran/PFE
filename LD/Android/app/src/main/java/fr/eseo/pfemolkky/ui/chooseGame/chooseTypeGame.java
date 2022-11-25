package fr.eseo.pfemolkky.ui.chooseGame;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game.TypeOfGame;


public class chooseTypeGame extends Fragment {

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_choose_type_game, container, false);
        navController = NavHostFragment.findNavController(this);
        ConstraintLayout tournamentButton = (ConstraintLayout) root.findViewById(R.id.tournamentButton);
        Bundle bundle = new Bundle();
        bundle.putInt("player",0);
        tournamentButton.setOnClickListener(view -> {
            if(getActivity()!=null){
                tournamentButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.tournament);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        ConstraintLayout classicButton = (ConstraintLayout) root.findViewById(R.id.classicButton);
        classicButton.setOnClickListener(view -> {
            if(getActivity()!=null){
                classicButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.classic);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        ConstraintLayout quickGameButton = (ConstraintLayout) root.findViewById(R.id.quickGameButton);
        quickGameButton.setOnClickListener(view -> {
            if(getActivity()!=null) {
                quickGameButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity) getActivity()).getGame().setTypeOfGame(TypeOfGame.fast);
                navController.navigate(R.id.nav_game, bundle);
            }
        });
        ConstraintLayout pastisButton = (ConstraintLayout) root.findViewById(R.id.pastisButton);
        pastisButton.setOnClickListener(view -> {
            if(getActivity()!=null){
                pastisButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity)getActivity()).getGame().setTypeOfGame(TypeOfGame.pastis);
                navController.navigate(R.id.nav_game,bundle);
            }
        });
        return root;
    }
}
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

/**
 * Class which is called when the User navigate to the page to choose the type of game
 */
public class chooseTypeGame extends Fragment {
    /**
     * The navigation Controller
     */
    private NavController navController;

    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     *     &#x27A2 Inflate the fragment <br>
     *     &#x27A2 Set the parameter for next page as the first player of the list<br>
     *     &#x27FE Set the response if the player click on the tournament button<br>
     *     <div style="padding-left : 10px">
     *         &#x27A2 Set the type of game to tournament<br>
     *         &#x27A2 Navigate to the page of the game with the parameters<br>
     *     </div>
     *     &#x27FE Set the response if the player click on the classic button<br>
     *     <div style="padding-left : 10px">
     *         &#x27A2 Set the type of game to classic<br>
     *         &#x27A2 Navigate to the page of the game with the parameters<br>
     *     </div>
     *     &#x27FE Set the response if the player click on the quick button<br>
     *     <div style="padding-left : 10px">
     *         &#x27A2 Set the type of game to quick<br>
     *         &#x27A2 Navigate to the page of the game with the parameters<br>
     *     </div>
     *     &#x27FE Set the response if the player click on the pastis button<br>
     *     <div style="padding-left : 10px">
     *         &#x27A2 Set the type of game to pastis<br>
     *         &#x27A2 Navigate to the page of the game with the parameters<br>
     *     </div>
     * </div>
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
        View root = inflater.inflate(R.layout.fragment_choose_type_game, container, false);
        navController = NavHostFragment.findNavController(this);
        ConstraintLayout tournamentButton = root.findViewById(R.id.tournamentButton);
        Bundle bundle = new Bundle();
        bundle.putInt("player", 0);
        tournamentButton.setOnClickListener(view -> {
            if (getActivity() != null) {
                tournamentButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity) getActivity()).getGame().setTypeOfGame(TypeOfGame.tournament);
                navController.navigate(R.id.nav_game, bundle);
            }
        });
        ConstraintLayout classicButton = root.findViewById(R.id.classicButton);
        classicButton.setOnClickListener(view -> {
            if (getActivity() != null) {
                classicButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity) getActivity()).getGame().setTypeOfGame(TypeOfGame.classic);
                navController.navigate(R.id.nav_game, bundle);
            }
        });
        ConstraintLayout quickGameButton = root.findViewById(R.id.quickGameButton);
        quickGameButton.setOnClickListener(view -> {
            if (getActivity() != null) {
                quickGameButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity) getActivity()).getGame().setTypeOfGame(TypeOfGame.fast);
                navController.navigate(R.id.nav_game, bundle);
            }
        });
        ConstraintLayout pastisButton = root.findViewById(R.id.pastisButton);
        pastisButton.setOnClickListener(view -> {
            if (getActivity() != null) {
                pastisButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.selectedtypeofgameroundedcorner));
                ((MainActivity) getActivity()).getGame().setTypeOfGame(TypeOfGame.pastis);
                navController.navigate(R.id.nav_game, bundle);
            }
        });
        return root;
    }
}
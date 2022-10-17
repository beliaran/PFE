package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
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
        player = ((MainActivity)getActivity()).getGame().getPlayers().get(winnerPlayerNumber);
        TextView textWinner = root.findViewById(R.id.textWinner);
        textWinner.setText(player.getName());
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
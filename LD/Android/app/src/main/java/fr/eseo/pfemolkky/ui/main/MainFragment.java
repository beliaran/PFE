package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        View inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        if(getActivity()!=null){
            ((MainActivity)getActivity()).setAllowBack(false);
            button.setOnClickListener(view -> {
                navController.navigate(R.id.nav_start_game);
                if(((MainActivity)getActivity()).getPins().isEmpty()){
                    ((MainActivity)getActivity()).setGame(new Game());
                }
                else{
                    ((MainActivity)getActivity()).setGame(new Game(((MainActivity)getActivity()).getPins()));
                }
                ((MainActivity)getActivity()).setAllowBack(true);
            });
        }
        return inputFragmentView;
    }
}

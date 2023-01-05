package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        View inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        Button buttonLogIn = inputFragmentView.findViewById(R.id.buttonLogInMain);
        if(getActivity()!=null){
            ((MainActivity)getActivity()).setAllowBack(true);
            button.setOnClickListener(view -> {
                navController.navigate(R.id.nav_start_game);
                ((MainActivity)getActivity()).setGame(new Game(((MainActivity)getActivity()).getPins()));
            });
            buttonLogIn.setOnClickListener(view -> {
                System.out.println("changed");
                navController.navigate(R.id.nav_user_connection);
            });
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if(getActivity()!=null){
                        System.out.println("called");
                        getActivity().finish();
                    }
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        }
        return inputFragmentView;
    }
}

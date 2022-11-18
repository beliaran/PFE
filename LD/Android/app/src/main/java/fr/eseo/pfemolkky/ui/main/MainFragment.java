package fr.eseo.pfemolkky.ui.main;

import androidx.lifecycle.ViewModelProvider;

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
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.models.Game;

public class MainFragment extends Fragment {

    private NavController navController;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainFragment instance = this;
        navController = NavHostFragment.findNavController(this);
        fr.eseo.pfemolkky.databinding.FragmentMainBinding binding = FragmentMainBinding.inflate(inflater);
        View inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        ((MainActivity)getActivity()).setAllowBack(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_start_game);
                ((MainActivity)getActivity()).setGame(new Game());
                ((MainActivity)getActivity()).setAllowBack(true);
            }
        });
        return inputFragmentView;
    }
}

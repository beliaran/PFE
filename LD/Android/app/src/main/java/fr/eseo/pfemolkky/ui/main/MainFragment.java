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

    private FragmentMainBinding binding;
    private MainFragment instance;
    private NavController navController;
    private View inputFragmentView;

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
        instance= this;
        navController = NavHostFragment.findNavController(this);
        binding = FragmentMainBinding.inflate(inflater);
        inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_start_game);
                ((MainActivity)getActivity()).setGame(new Game());
            }
        });
        return inputFragmentView;
    }
}

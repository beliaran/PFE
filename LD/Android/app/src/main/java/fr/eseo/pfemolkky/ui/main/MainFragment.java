package fr.eseo.pfemolkky.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.databinding.FragmentMainBinding;
import fr.eseo.pfemolkky.models.Game;

public class MainFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainFragment instance = this;
        NavController navController = NavHostFragment.findNavController(this);
        fr.eseo.pfemolkky.databinding.FragmentMainBinding binding = FragmentMainBinding.inflate(inflater);
        View inputFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = inputFragmentView.findViewById(R.id.buttonStartGame);
        Button chooseMolkkyBtn = inputFragmentView.findViewById(R.id.buttonConnectMolkky);
        if(getActivity()!=null){
            ((MainActivity)getActivity()).setAllowBack(false);
            button.setOnClickListener(view -> {
                navController.navigate(R.id.nav_start_game);
                ((MainActivity)getActivity()).setGame(new Game());
                ((MainActivity)getActivity()).setAllowBack(true);
            });
            chooseMolkkyBtn.setOnClickListener(view -> {
                if(((MainActivity) getActivity()).bluetoothAdapter == null){
                    //TODO Rajouter au champ string
                    Toast.makeText(getContext(), "Le bluetooth n'est pas supporté par votre smarthphone",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    navController.navigate(R.id.select_molkky);
                    ((MainActivity) getActivity()).setAllowBack(true);
                }
            });
        }
        return inputFragmentView;
    }

}

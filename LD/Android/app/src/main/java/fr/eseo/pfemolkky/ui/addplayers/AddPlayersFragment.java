package fr.eseo.pfemolkky.ui.addplayers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.pfemolkky.R;


public class AddPlayersFragment extends Fragment {

    public AddPlayersFragment() {
        // Required empty public constructor
    }

    public static AddPlayersFragment newInstance(String param1, String param2) {
        AddPlayersFragment fragment = new AddPlayersFragment();
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
        View root = inflater.inflate(R.layout.fragment_add_players, container, false);
        return root;
    }
}
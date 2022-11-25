package fr.eseo.pfemolkky.ui.addplayers;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Player;


public class AddPlayersFragment extends Fragment {
    ArrayList<EditText> players = new ArrayList<>();
    HashMap<EditText,String> playerName = new HashMap<>();
    private ArrayList<View> playersFragment = new ArrayList<>();
    int count = 1;
    private View root;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        players = new ArrayList<>();
        playerName = new HashMap<>();
        playersFragment = new ArrayList<>();
        root = inflater.inflate(R.layout.fragment_add_players, container, false);
        navController = NavHostFragment.findNavController(this);
        players = new ArrayList<>();
        playerName = new HashMap<>();
        if(getActivity()!=null){
            if(!((MainActivity)getActivity()).getGame().getPlayers().isEmpty()){
                count=0;
                for(Player player : ((MainActivity)getActivity()).getGame().getPlayers()){
                    count+=1;
                    View fragment = getLayoutInflater().inflate(R.layout.fragment_added_player,container,false);
                    players.add(fragment.findViewById(R.id.editTextPersonName));
                    playersFragment.add(fragment);
                    playerName.put(fragment.findViewById(R.id.editTextPersonName),player.getName());
                    ((EditText)fragment.findViewById(R.id.editTextPersonName)).setText(player.getName());
                    update();
                }
            }
            else{
                View fragment = getLayoutInflater().inflate(R.layout.fragment_added_player,container,false);
                EditText editText = fragment.findViewById(R.id.editTextPersonName);
                players.add(editText);
                playersFragment.add(fragment);
            }
        }
        update();
        FloatingActionButton addPlayerButton = root.findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(view -> {
            count+=1;
            playerName= new HashMap<>();
            System.out.println(players);
            for(EditText player : players){
                playerName.put(player,String.valueOf(player.getText()));
            }
            View fragment = getLayoutInflater().inflate(R.layout.fragment_added_player,container,false);
            players.add(fragment.findViewById(R.id.editTextPersonName));
            playersFragment.add(fragment);
            update();
        });
        Button launchButton = root.findViewById(R.id.launchButton);
        launchButton.setOnClickListener(view -> {
            ((MainActivity)getActivity()).getGame().getPlayers().removeAll(((MainActivity)getActivity()).getGame().getPlayers());
            int i = 1;
            playerName= new HashMap<>();
            for(EditText playerEditText : players){
                if(!String.valueOf(playerEditText.getText()).matches("")){
                    playerName.put(playerEditText,String.valueOf(playerEditText.getText()));
                }
            }
            for(EditText player : players){
                String name = getResources().getString(R.string.askName,String.valueOf(i));
                if(playerName.get(player)!=null){
                    name = playerName.get(player);
                }
                Player newPlayer = new Player(name);
                ((MainActivity)getActivity()).getGame().addPlayer(newPlayer);
                i++;
            }
            System.out.println(((MainActivity)getActivity()).getGame().getPlayers());
            navController.navigate(R.id.nav_choose_type);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        LinearLayout layoutPlayers = root.findViewById(R.id.layoutPlayers);
        layoutPlayers.removeAllViews();
        if(getContext()!=null){
            ConstraintLayout clSpace= new ConstraintLayout(getContext());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            clSpace.setLayoutParams(layoutParams);
            layoutPlayers.addView(clSpace);
            for(int i=1;i<=count;i++){
                View fragment = playersFragment.get(i-1);

                if(fragment.getParent()!=null){
                    ((LinearLayout)(fragment.getParent())).removeAllViews();
                }

                layoutPlayers.addView(fragment);
                EditText editTextTemp = fragment.findViewById(R.id.editTextPersonName);
                ImageView imageView = fragment.findViewById(R.id.deletePlayerIcon);
                if(count!=1){
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setOnClickListener(view -> {
                        count-=1;
                        players.remove(editTextTemp);
                        playersFragment.remove(fragment);
                        playerName= new HashMap<>();
                        for(EditText player : players){
                            playerName.put(player,String.valueOf(player.getText()));
                        }
                        update();
                    });
                }
                else{
                    imageView.setVisibility(View.INVISIBLE);
                }
                if(playerName.get(players.get(i-1))!=null){
                    editTextTemp.setText(playerName.get(editTextTemp));
                }editTextTemp.setText(editTextTemp.getText());
                editTextTemp.setHint(getResources().getString(R.string.askName,String.valueOf(i)));

            }
        }
    }
}
package fr.eseo.pfemolkky.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.models.Player;

public class GameFragment extends Fragment {

    private int playerNumber;
    private ArrayList<Pin> pins = new ArrayList<>();
    private ArrayList<ImageView> imageViewPins = new ArrayList<>();
    private ImageView imageViewPin1;
    private ImageView imageViewPin2;
    private ImageView imageViewPin3;
    private ImageView imageViewPin4;
    private ImageView imageViewPin5;
    private ImageView imageViewPin6;
    private ImageView imageViewPin7;
    private ImageView imageViewPin8;
    private ImageView imageViewPin9;
    private ImageView imageViewPin10;
    private ImageView imageViewPin11;
    private ImageView imageViewPin12;
    private int[] listDrawableDisconnected = {R.drawable.molkky_disconnected_1,R.drawable.molkky_disconnected_2,R.drawable.molkky_disconnected_3,R.drawable.molkky_disconnected_4,R.drawable.molkky_disconnected_5,R.drawable.molkky_disconnected_6,
            R.drawable.molkky_disconnected_7, R.drawable.molkky_disconnected_8,R.drawable.molkky_disconnected_9,R.drawable.molkky_disconnected_10,R.drawable.molkky_disconnected_11,R.drawable.molkky_disconnected_12};
    private int[] listDrawableFallen = {R.drawable.molkky_fallen_1,R.drawable.molkky_fallen_2,R.drawable.molkky_fallen_3,R.drawable.molkky_fallen_4,R.drawable.molkky_fallen_5,R.drawable.molkky_fallen_6,
            R.drawable.molkky_fallen_7, R.drawable.molkky_fallen_8,R.drawable.molkky_fallen_9,R.drawable.molkky_fallen_10,R.drawable.molkky_fallen_11,R.drawable.molkky_fallen_12};
    private int[] listDrawableConnected = {R.drawable.molkky_1,R.drawable.molkky_2,R.drawable.molkky_3,R.drawable.molkky_4,R.drawable.molkky_5,R.drawable.molkky_6,
            R.drawable.molkky_7, R.drawable.molkky_8,R.drawable.molkky_9,R.drawable.molkky_10,R.drawable.molkky_11,R.drawable.molkky_12};
    private ArrayList<ImageView> listImageView= new ArrayList<>();

    private Player player;
    private NavController navController;
    private Game game;

    public static GameFragment newInstance() {
        return new GameFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        navController = NavHostFragment.findNavController(this);
        playerNumber = getArguments().getInt("player");
        game = ((MainActivity)getActivity()).getGame();
        player = ((MainActivity)getActivity()).getGame().getPlayers().get(playerNumber);
        ((MainActivity)getActivity()).setAllowBack(false);
        TextView textViewPlayer = root.findViewById(R.id.idPlayer);
        textViewPlayer.setText(player.getName());
        TextView textViewScore = root.findViewById(R.id.scorePlayer);
        textViewScore.setText(getResources().getString(R.string.scorePlayer)+" "+String.valueOf(player.getScore()));
        pins = ((MainActivity)getActivity()).getGame().getPins();
        imageViewPin1 = (ImageView) root.findViewById(R.id.pin1);
        imageViewPin2 = (ImageView) root.findViewById(R.id.pin2);
        imageViewPin3 = (ImageView) root.findViewById(R.id.pin3);
        imageViewPin4 = (ImageView) root.findViewById(R.id.pin4);
        imageViewPin5 = (ImageView) root.findViewById(R.id.pin5);
        imageViewPin6 = (ImageView) root.findViewById(R.id.pin6);
        imageViewPin7 = (ImageView) root.findViewById(R.id.pin7);
        imageViewPin8 = (ImageView) root.findViewById(R.id.pin8);
        imageViewPin9 = (ImageView) root.findViewById(R.id.pin9);
        imageViewPin10 = (ImageView) root.findViewById(R.id.pin10);
        imageViewPin11 = (ImageView) root.findViewById(R.id.pin11);
        imageViewPin12 = (ImageView) root.findViewById(R.id.pin12);
        listImageView.add(imageViewPin1);
        listImageView.add(imageViewPin2);
        listImageView.add(imageViewPin3);
        listImageView.add(imageViewPin4);
        listImageView.add(imageViewPin5);
        listImageView.add(imageViewPin6);
        listImageView.add(imageViewPin7);
        listImageView.add(imageViewPin8);
        listImageView.add(imageViewPin9);
        listImageView.add(imageViewPin10);
        listImageView.add(imageViewPin11);
        listImageView.add(imageViewPin12);
        updateInterface();
        Button buttonValidate= (Button) root.findViewById(R.id.buttonValidateRound);
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countFallen = 0;
                for(Pin pin : pins){
                    if(pin.hasFallen()){
                        countFallen+=1;
                    }
                }
                if(countFallen==1){
                    for(Pin pin : pins){
                        if(pin.hasFallen()){
                            player.setMissed(0);
                            player.setScore(player.getScore()+ pin.getNumber());
                        }
                    }
                }
                else if(countFallen==0){
                    player.setMissed(player.getMissed()+1);
                    if(player.getMissed()==3){
                        if(game.getTypeOfGame()!= Game.TypeOfGame.tournament){
                            player.setScore(0);
                            player.setMissed(0);
                        }
                        else{
                            game.getPlayers().remove(player);
                        }
                    }
                }else{
                    player.setMissed(0);
                    player.setScore(player.getScore()+ countFallen);
                }
                if((game.getTypeOfGame()== Game.TypeOfGame.tournament && game.getPlayers().size()==1)||player.getScore()==game.getScoreToWin()){
                    //To do when win
                    Bundle bundle = new Bundle();
                    if(game.getTypeOfGame()== Game.TypeOfGame.tournament){
                        bundle.putInt("winner",0);
                    }else{
                        bundle.putInt("winner",playerNumber);
                    }
                    navController.navigate(R.id.nav_winner,bundle);

                }else{
                    if(player.getScore()>game.getScoreToWin()){
                        player.setScore((int)game.getScoreToWin()/2);
                    }
                    for(Pin pin : pins){
                        pin.setFallen(false);
                    }
                    Bundle bundle = new Bundle();
                    if(game.getPlayers().indexOf(player)==game.getPlayers().size()-1){
                        bundle.putInt("player",0);
                    }else{
                        bundle.putInt("player",playerNumber+1);
                    }
                    navController.navigate(R.id.nav_game,bundle);
                }
            }
        });

        return root;
    }

    private void updateInterface() {
        for(int i=0;i<12;i++){
            if(!pins.get(i).isConnected()){
                listImageView.get(i).setImageDrawable(getResources().getDrawable(listDrawableDisconnected[i]));
            }else{
                listImageView.get(i).setImageDrawable(getResources().getDrawable(listDrawableConnected[i]));
            }
            if(pins.get(i).hasFallen()){
                listImageView.get(i).setImageDrawable(getResources().getDrawable(listDrawableFallen[i]));
            }
            int g = i;
            listImageView.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pins.get(g).setFallen(!pins.get(g).hasFallen());
                    updateInterface();
                }
            });
        }
    }

}
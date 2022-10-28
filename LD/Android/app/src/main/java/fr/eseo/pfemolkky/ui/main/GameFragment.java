package fr.eseo.pfemolkky.ui.main;

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

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.models.Player;

public class GameFragment extends Fragment {

    private int playerNumber;
    private ArrayList<Pin> pins = new ArrayList<>();
    private Button imageViewPin1;
    private Button imageViewPin2;
    private Button imageViewPin3;
    private Button imageViewPin4;
    private Button imageViewPin5;
    private Button imageViewPin6;
    private Button imageViewPin7;
    private Button imageViewPin8;
    private Button imageViewPin9;
    private Button imageViewPin10;
    private Button imageViewPin11;
    private Button imageViewPin12;
    private ArrayList<Button> listImageView= new ArrayList<Button>();

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
        imageViewPin1 = (Button) root.findViewById(R.id.pin1);
        imageViewPin2 = (Button) root.findViewById(R.id.pin2);
        imageViewPin3 = (Button) root.findViewById(R.id.pin3);
        imageViewPin4 = (Button) root.findViewById(R.id.pin4);
        imageViewPin5 = (Button) root.findViewById(R.id.pin5);
        imageViewPin6 = (Button) root.findViewById(R.id.pin6);
        imageViewPin7 = (Button) root.findViewById(R.id.pin7);
        imageViewPin8 = (Button) root.findViewById(R.id.pin8);
        imageViewPin9 = (Button) root.findViewById(R.id.pin9);
        imageViewPin10 = (Button) root.findViewById(R.id.pin10);
        imageViewPin11 = (Button) root.findViewById(R.id.pin11);
        imageViewPin12 = (Button) root.findViewById(R.id.pin12);
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
        ImageView imageCup = (ImageView) root.findViewById(R.id.imageCup);
        imageCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_scoreboard);
                listImageView= new ArrayList<Button>();
            }
        });
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
                            playerNumber=playerNumber-1;
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
                listImageView.get(i).setBackgroundResource(R.drawable.circlepinbuttondisconnected);
            }else{
                listImageView.get(i).setBackground(getResources().getDrawable(R.drawable.circlepinbutton));
            }
            if(pins.get(i).hasFallen()){
                listImageView.get(i).setBackground(getResources().getDrawable(R.drawable.circlepinbuttonfallen));
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
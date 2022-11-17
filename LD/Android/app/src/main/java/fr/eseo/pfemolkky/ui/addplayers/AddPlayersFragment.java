package fr.eseo.pfemolkky.ui.addplayers;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private ViewGroup container;

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
        players = new ArrayList<>();
        playerName = new HashMap<>();
        playersFragment = new ArrayList<>();
        root = inflater.inflate(R.layout.fragment_add_players, container, false);
        navController = NavHostFragment.findNavController(this);
        this.container=container;
        players = new ArrayList<>();
        playerName = new HashMap<>();
        if(!((MainActivity)getActivity()).getGame().getPlayers().isEmpty()){
            count=0;
            for(Player player : ((MainActivity)getActivity()).getGame().getPlayers()){
                count+=1;
                System.out.println(player);
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
        update();
        FloatingActionButton addPlayerButton = (FloatingActionButton) root.findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        Button launchButton = (Button) root.findViewById(R.id.launchButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getGame().getPlayers().removeAll(((MainActivity)getActivity()).getGame().getPlayers());
                int i = 1;
                playerName= new HashMap<>();
                for(EditText playerEditText : players){
                    if(!String.valueOf(playerEditText.getText()).matches("")){
                        playerName.put(playerEditText,String.valueOf(playerEditText.getText()));
                    }
                }
                for(EditText player : players){
                    String name = getResources().getString(R.string.askName)+" "+i;
                    if(playerName.get(player)!=null){
                         name = playerName.get(player);
                    }
                    Player newPlayer = new Player(name);
                    ((MainActivity)getActivity()).getGame().addPlayer(newPlayer);
                    i++;
                }
                System.out.println(((MainActivity)getActivity()).getGame().getPlayers());
                navController.navigate(R.id.nav_choose_type);
            }
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
        ConstraintLayout clspace= new ConstraintLayout(getContext());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
        clspace.setLayoutParams(layoutParams);
        layoutPlayers.addView(clspace);
        for(int i=1;i<=count;i++){
            System.out.println(i+"/"+count);
            View fragment = playersFragment.get(i-1);

            if(fragment.getParent()!=null){
                ((LinearLayout)(fragment.getParent())).removeAllViews();
            }

            layoutPlayers.addView(fragment);
            EditText editTextTemp = fragment.findViewById(R.id.editTextPersonName);
            System.out.println("called for "+editTextTemp.getText()+" supposed to be n° "+String.valueOf(i));
            ImageView imageView = fragment.findViewById(R.id.deletePlayerIcon);
            if(count!=1){
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count-=1;
                        players.remove(editTextTemp);
                        playersFragment.remove(fragment);
                        playerName= new HashMap<>();
                        System.out.println(players);
                        for(EditText player : players){
                            playerName.put(player,String.valueOf(player.getText()));
                        }
                        update();
                    }
                });
            }
            else{
                imageView.setVisibility(View.INVISIBLE);
            }
            if(playerName.get(players.get(i-1))!=null){
                editTextTemp.setText(playerName.get(editTextTemp));
            }
//
//
//            ConstraintLayout clDown= new ConstraintLayout(getContext());
//            ConstraintLayout clUp= new ConstraintLayout(getContext());
//            editTextInstance.setId(View.generateViewId());
//            clDown.addView(editTextInstance);
//
//            if(count!=1){
//                ImageView imageDelete = new ImageView(getContext());
//                imageDelete.setId(View.generateViewId());
//                ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(
//                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
//                imageDelete.setLayoutParams(layout);
//                imageDelete.setImageResource(getResources().getIdentifier("@android:drawable/ic_delete",null,null));
//                clDown.addView(imageDelete);
//
//                ConstraintSet csimage = new ConstraintSet();
//                csimage.clone(clDown);
//                csimage.connect(imageDelete.getId(), ConstraintSet.START,editTextInstance.getId(), ConstraintSet.END,0);
//                csimage.connect(imageDelete.getId(), ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END,0);
//                csimage.connect(imageDelete.getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP,0);
//                csimage.connect(imageDelete.getId(), ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,0);
//                csimage.applyTo(clDown);
//                imageDelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        count-=1;
//                        players.remove(editTextInstance);
//                        playerName= new HashMap<>();
//                        for(EditText player : players){
//                            playerName.put(player,String.valueOf(player.getText()));
//                        }
//                        update();
//                    }
//                });
//            }
//
//            if(playerName.get(editTextInstance)!=null){
//                editTextInstance.setText(playerName.get(editTextInstance));
//            }
//
//            ConstraintSet cs = new ConstraintSet();
//            cs.clone(clDown);
//            cs.setVerticalBias(editTextInstance.getId(),0.5f);
//            cs.setHorizontalBias(editTextInstance.getId(),0.2f);
//            cs.connect(editTextInstance.getId(), ConstraintSet.START,ConstraintSet.PARENT_ID, ConstraintSet.START,0);
//            cs.connect(editTextInstance.getId(), ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END,0);
//            cs.connect(editTextInstance.getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP,0);
//            cs.connect(editTextInstance.getId(), ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,0);
//            cs.applyTo(clDown);
//            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) editTextInstance.getLayoutParams();
//
//            editTextInstance.setLayoutParams(params);
//
//            ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(
//                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290, getResources().getDisplayMetrics()),
//                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics()));
//            ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(
//                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 370, getResources().getDisplayMetrics()),
//                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
//            ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_PARENT,
//                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 76, getResources().getDisplayMetrics()));
//
//
//            editTextInstance.setLayoutParams(layoutParams1);
//            editTextInstance.setHintTextColor(getResources().getColor(R.color.light_grey));
            editTextTemp.setText(editTextTemp.getText());
            editTextTemp.setHint(getResources().getString(R.string.askName)+" "+String.valueOf(i));
//            editTextInstance.setTextColor(getResources().getColor(R.color.black));
//
//
//            clUp.setId(View.generateViewId());
//            clUp.setLayoutParams(layoutParams3);
//
//            clDown.setId(View.generateViewId());
//            clDown.setLayoutParams(layoutParams2);
//            clUp.addView(clDown);
//
//            ConstraintSet cs2 = new ConstraintSet();
//            cs2.clone(clUp);
//            cs2.connect(clDown.getId(), ConstraintSet.START,ConstraintSet.PARENT_ID, ConstraintSet.START,0);
//            cs2.connect(clDown.getId(), ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END,0);
//            cs2.connect(clDown.getId(), ConstraintSet.TOP,ConstraintSet.PARENT_ID, ConstraintSet.TOP,0);
//            cs2.connect(clDown.getId(), ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,0);
//            cs2.applyTo(clUp);
//
//            clDown.setBackground(getResources().getDrawable(R.drawable.addplayerroundedcorners));
//            layoutPlayers.addView(clUp);
            
        }
    }
}
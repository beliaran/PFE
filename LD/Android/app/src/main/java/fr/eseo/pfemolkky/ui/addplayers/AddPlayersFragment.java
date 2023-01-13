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

/**
 * Class which is called when the User navigate to the page to add players to the game
 */
public class AddPlayersFragment extends Fragment {
    /**
     * A list of the Displayed Text representing the players
     */
    ArrayList<EditText> players;
    /**
     * A hashmap linking the Displayed Text and the string it should contain
     */
    HashMap<EditText, String> playersName;
    /**
     * The list of fragments representing the players on the display
     */
    private ArrayList<View> playersFragment;
    /**
     * The number of added players (at least 1)
     */
    int count = 1;
    /**
     * The inflated fragment
     */
    private View root;
    /**
     * The navigation Controller
     */
    private NavController navController;
    /**
     * a group of view containing the page
     */
    private ViewGroup container;
    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     *      &#x27A2 Inflate the fragment<br>
     *      &#x27A2 Add a player defined by a EditText and a Fragment fragment_added_player inflated<br>
     *      &#x27A2 Set the button response when clicked to add player<br>
     *      &#x27A2 Set the button response when clicked to launch the game<br>
     *      &#x27A2 Update the page<br>
     *
     * </div>
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instance state between the pages
     * @return the inflated fragment with all elements
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        playersFragment = new ArrayList<>();
        playersName = new HashMap<>();
        players = new ArrayList<>();
        root = inflater.inflate(R.layout.fragment_add_players, container, false);
        navController = NavHostFragment.findNavController(this);
        this.container = container;
        if (this.getActivity() != null) {
            if (!((MainActivity) this.getActivity()).getGame().getPlayers().isEmpty()) {
                count=0;
                for (int i = 0; i < ((MainActivity) this.getActivity()).getGame().getPlayers().size(); i++) {
                    count+=1;
                    EditText text = addAPlayer();
                    Player player = ((MainActivity) this.getActivity()).getGame().getPlayers().get(i);
                    if (!player.getName().equals(getResources().getString(R.string.askName, String.valueOf(i + 1)))) {
                        playersName.put(text, player.getName());
                        text.setText(player.getName());
                    }
                    else{
                        playersName.put(text, "");
                        text.setText("");
                    }
                }
            } else {
                addAPlayer();
            }
        }

        FloatingActionButton addPlayerButton = root.findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(view -> {
            count += 1;
            playersName = new HashMap<>();
            for (EditText player : players) {
                if (!String.valueOf(player.getText()).matches("")) {
                    playersName.put(player, String.valueOf(player.getText()));
                }
            }
            addAPlayer();
            update();
        });
        Button launchButton = root.findViewById(R.id.launchButton);
        launchButton.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getGame().getPlayers().removeAll(((MainActivity) getActivity()).getGame().getPlayers());
            int i = 1;
            playersName = new HashMap<>();
            for (EditText playerEditText : players) {
                if (!String.valueOf(playerEditText.getText()).matches("")) {
                    playersName.put(playerEditText, String.valueOf(playerEditText.getText()));
                }
            }
            for (EditText player : players) {
                String name = getResources().getString(R.string.askName, String.valueOf(i));
                if (playersName.get(player) != null) {
                    name = playersName.get(player);
                }
                Player newPlayer = new Player(name);
                ((MainActivity) getActivity()).getGame().addPlayer(newPlayer);
                i++;
            }
            navController.navigate(R.id.nav_choose_type);
        });
        System.out.println(playersName);
        System.out.println(players.get(0).getText());
        System.out.println("<====================================>");
        update();
        System.out.println("<====================================>");
        System.out.println(players.get(0).getText());
        return root;
    }

    /**
     * Add a fragment and its Edit Text as a player to the list of players
     * @return an Edit text which will contain the player's name
     */
    private EditText addAPlayer() {
        View fragment = getLayoutInflater().inflate(R.layout.fragment_added_player, container, false);
        EditText et = fragment.findViewById(R.id.editTextPersonName);
        players.add(et);
        playersFragment.add(fragment);
        return et;
    }

    /**
     * Function called when the game master click on the back button which allow to return the saved instance
     */
    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    /**
     * Function called to update the page
     * <div style="padding-left : 10px">
     *     &#x27A2 Remove all views of the list of players<br>
     *     &#x27A2 Add a space before the list of players<br>
     *     &#8649 for each player
     *     <div style="padding-left : 10px">
     *          &#x27A2 Make sure the fragment is not already in a layout<br>
     *          &#x27A2 Add the fragment to the displayed layout of player<br>
     *          &#x21a6 If there is more than one player<br>
     *          <div style="padding-left : 10px">
     *              &#x27A2 Make the button to delete a player visible<br>
     *              &#x27FE; Set the function called when the button to delete  is clicked<br>
     *              <div style="padding-left : 10px">
     *                  &#x27A2 delete the player from the list<br>
     *                  &#x27A2 Move up the bottom of the list
     *              </div>
     *              &#x27A2 Set the grey text of the button to the player name<br>
     *              &#x27A2 Set the edit text according to the player name<br>
     *          </div>
     *     </div>
     * </div>
     */
    public void update() {
        LinearLayout layoutPlayers = root.findViewById(R.id.layoutPlayers);
        layoutPlayers.removeAllViews();
        if (getContext() != null) {
            ConstraintLayout clSpace = new ConstraintLayout(getContext());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
            clSpace.setLayoutParams(layoutParams);
            layoutPlayers.addView(clSpace);
            for (int i = 1; i <= count; i++) {
                View fragment = playersFragment.get(i - 1);
                if (fragment.getParent() != null) {
                    ((LinearLayout) (fragment.getParent())).removeAllViews();
                }
                layoutPlayers.addView(fragment);
                ImageView imageView = fragment.findViewById(R.id.deletePlayerIcon);
                if (count != 1) {
                    int g = i;
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setOnClickListener(view -> {
                        count -= 1;
                        players.remove(players.get(g - 1));
                        playersFragment.remove(fragment);
                        playersName = new HashMap<>();
                        for (EditText player : players) {
                            if (!String.valueOf(player.getText()).matches("")) {
                                playersName.put(player, String.valueOf(player.getText()));
                            }
                        }
                        update();
                    });
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                }
                players.get(i - 1).setHint(getResources().getString(R.string.askName, String.valueOf(i)));

                if (playersName.get(players.get(i - 1)) != null) {
                    System.out.println(players);
                    System.out.println(i-1);
                    System.out.println(playersName.get(players.get(i - 1)));
                    System.out.println(players.get(i - 1).getId());
                    (players.get(i - 1)).setText(playersName.get(players.get(i - 1)));
                }
            }
        }
    }
}
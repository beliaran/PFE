package fr.eseo.pfemolkky.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;
import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.models.Player;
import fr.eseo.pfemolkky.service.bluetooth.BleDialogue;
import fr.eseo.pfemolkky.service.bluetooth.BluetoothFrameReader;

/**
 * Class which is called when the User play the game
 */
public class GameFragment extends Fragment {
    /**
     * The player index in the list of players
     */
    private int playerNumber;
    /**
     * The list of the pins of the game
     */
    private ArrayList<Pin> pins = new ArrayList<>();
    /**
     * The list of the image view representing the pin in the display
     */
    private ArrayList<Button> listImageView = new ArrayList<>();
    /**
     * The actual player
     */
    private Player player;
    /**
     * The navigation controller
     */
    private NavController navController;
    /**
     * The game instance in the Main activity
     */
    private Game game;
    /**
     * The button to validate the score and go to the next turn
     */
    private Button buttonValidate;
    /**
     * The boolean representing if the game is on pause between two throws
     */
    AtomicReference<Boolean> nextTurn = new AtomicReference<>(false);
    /**
     * The score displayed on the screen
     */
    private TextView textViewScore;
    /**
     * The layout containing the representation of the number of times player missed all pins
     */
    private LinearLayout croix;

    /**
     * Function called when fragment is created <br>
     * <div style="padding-left : 10px">
     *      &#x27A2 Inflate the fragment <br>
     *      &#x27A2 Get the current player <br>
     *      &#x27A2 Change the text which display the name by the name of the current player <br>
     *      &#x27A2 Display the score of the player <br>
     *      &#x27A2 Get all the buttons representing the pin and add them to a list <br>
     *      &#8649 For all pins<br>
     *      <div style="padding-left : 10px">
     *          &#x27A2 Adapt the display to the width of the phone <br>
     *      </div>
     *      &#x27FE Set the function called when players click on the podium <br>
     *      <div style="padding-left : 10px">
     *          &#x27A2 Navigate to the scoreboard page <br>
     *      </div>
     *      &#x27FE Set the function called when players click on the battery <br>
     *      <div style="padding-left : 10px">
     *          &#x27A2 Navigate to the battery listing page <br>
     *      </div>
     *      &#x27FE Set the function called when players click on the validate button<br>
     *      <div style="padding-left : 10px">
     *          &#x21a6 If the game is not on pause<br>
     *
     *          &#x21a6 If the game is on pause<br>
     *          <div style="padding-left : 10px">
     *               &#x27A2 Reset the state of all pins <br>
     * 			  &#x27A2 Set the parameter as the next player to play<br>
     * 			  &#x27A2 Navigate to a new page game with the parameter the next player<br>
     *          </div>
     *      </div>
     * 	 &#x27FE Set the function called when players click on the back button<br>
     *      <div style="padding-left : 10px">
     * 		&#x27A2 Create an alert dialog which will ask if the User really want to leave the current game, if he selects yes he will be redirected to the main menu, otherwise the game continues <br>
     * 	 </div>
     * </div>
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        navController = NavHostFragment.findNavController(this);
        BleDialogue.getInstance(this);
        if (getArguments() != null && getActivity() != null) {
            playerNumber = getArguments().getInt("player");
            game = ((MainActivity) getActivity()).getGame();
            player = ((MainActivity) getActivity()).getGame().getPlayers().get(playerNumber);
            TextView textViewPlayer = root.findViewById(R.id.idPlayer);
            textViewPlayer.setText(player.getName());
            textViewScore = root.findViewById(R.id.scorePlayer);
            pins = ((MainActivity) getActivity()).getGame().getPins();
            Button imageViewPin1 = root.findViewById(R.id.pin1);
            Button imageViewPin2 = root.findViewById(R.id.pin2);
            Button imageViewPin3 = root.findViewById(R.id.pin3);
            Button imageViewPin4 = root.findViewById(R.id.pin4);
            Button imageViewPin5 = root.findViewById(R.id.pin5);
            Button imageViewPin6 = root.findViewById(R.id.pin6);
            Button imageViewPin7 = root.findViewById(R.id.pin7);
            Button imageViewPin8 = root.findViewById(R.id.pin8);
            Button imageViewPin9 = root.findViewById(R.id.pin9);
            Button imageViewPin10 = root.findViewById(R.id.pin10);
            Button imageViewPin11 = root.findViewById(R.id.pin11);
            Button imageViewPin12 = root.findViewById(R.id.pin12);
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
            croix = (LinearLayout) root.findViewById(R.id.croix);
            buttonValidate = root.findViewById(R.id.buttonValidateRound);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            for (Button imageViewPin : listImageView) {
                System.out.println(imageViewPin.getText());
                System.out.println(displayMetrics.widthPixels / 4);
                System.out.println(imageViewPin.getLayoutParams());
                ViewGroup.LayoutParams layoutParams = imageViewPin.getLayoutParams();
                layoutParams.width = (displayMetrics.widthPixels - 50) / 4;
                layoutParams.height = (displayMetrics.widthPixels - 50) / 4;
                imageViewPin.setLayoutParams(layoutParams);
            }

            updateInterface();
            ImageView imageCup = root.findViewById(R.id.imageCup);
            imageCup.setOnClickListener(view -> {
                BleDialogue.getInstance(null);
                navController.navigate(R.id.nav_scoreboard);
                listImageView = new ArrayList<>();
            });
            ImageView imageBattery = root.findViewById(R.id.imageBattery);
            imageBattery.setOnClickListener(view -> {
                BleDialogue.getInstance(null);
                navController.navigate(R.id.nav_battery);
                listImageView = new ArrayList<>();
            });
            buttonValidate.setOnClickListener(view -> {
                if (!nextTurn.get()) {
                    countPoints();
                } else {
                    for (Pin pin : pins) {
                        pin.setFallen(false);
                    }
                    Bundle bundle = new Bundle();
                    if (game.getPlayers().indexOf(player) == game.getPlayers().size() - 1) {
                        bundle.putInt("player", 0);
                        game.setRound(game.getRound() + 1);
                    } else {
                        bundle.putInt("player", playerNumber + 1);
                    }
                    navController.navigate(R.id.nav_game, bundle);
                }
            });
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getActivity() != null) {
                        LinearLayout view = new LinearLayout(getContext());
                        ImageView iv = new ImageView(getContext());
                        iv.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
                        iv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.orange));
                        iv.setPadding(20, 0, 20, 0);
                        TextView tv = new TextView(getContext());
                        tv.setText(getResources().getText(R.string.returnToMenu));
                        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.orange));
                        tv.setTextSize(20);

                        view.setPadding(0, 60, 0, 10);
                        view.addView(iv);
                        view.addView(tv);
                        view.setGravity(Gravity.CENTER);
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setCustomTitle(view)
                                .setMessage(getResources().getText(R.string.textLeave))
                                .setPositiveButton(getResources().getText(R.string.yes), (dialog1, which) -> {
                                    navController.navigate(R.id.nav_main);
                                    for (Pin pin : pins) {
                                        pin.setFallen(false);
                                    }
                                })
                                .setNegativeButton(getResources().getText(R.string.no), null).create();
                        dialog.setOnShowListener(arg0 -> {
                            TypedValue typedValue = new TypedValue();
                            Resources.Theme theme = getActivity().getTheme();
                            theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
                            int color = typedValue.data;
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
                        });
                        dialog.show();
                    }
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        }
        return root;
    }

    /**
     * Function called to calculate the points of the player
     *          <div style="padding-left : 10px">
     *             &#x27A2 Calculate the number of pin that are fallen <br>
     *             &#x21a6  If the count is equal to 1 <br>
     *           	<div style="padding-left : 10px">
     *               	&#x27A2 Add the value of the pin to the player score <br>
     *               	&#x27A2 Set the value of the number of times player missed all pins to 0 <br>
     *               	&#x27A2 Add 1 to the number of times the player tackled one pin<br>
     *               	&#x27A2 Add 1 to the number of pin the player tackled<br>
     *          	</div>
     * 			&#x21a6 If the count is equal to 0 <br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 Add 1 to the number of times player missed all pins<br>
     * 				&#x21a6 If the number of times player missed all pins is equal to 3 <br>
     * 				<div style="padding-left : 10px">
     * 					&#x21a6 If the type of game is different than tournament <br>
     * 					<div style="padding-left : 10px">
     * 						&#x27A2 Set the value of the number of times player missed all pins to 0 <br>
     * 						&#x27A2 Set the score value to 0 <br>
     * 					</div>

     * 				</div>
     * 			</div>
     * 			&#x21a6 If the count is different than 0 & 1 <br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 Add the value of count to the player score <br>
     * 				&#x27A2 Set the value of the number of times player missed all pins to 0 <br>
     * 				&#x27A2 Add the count to the number of pin the player tackled<br>
     * 			</div>
     * 			&#x21a6 If the score is more than the score to win <br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 Player score is divided by 2 <br>
     * 			</div>
     * 					&#x21a6 If the player is disqualified<br>
     * 					<div style="padding-left : 10px">
     * 						&#x27A2 Remove the player from the list (he is disqualified) <br>
     * 					</div>
     * 			&#x21a6 If the end of the game is reached<br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 Player number is passed as parameter <br>
     * 				&#x27A2 Navigate to the end of game page <br>
     * 			</div>
     * 			&#x21a6 If end of game is not reached <br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 The game is paused between two rounds and interface is updated with scores<br>
     * 			</div>
     * 		</div>
     */
    private void countPoints() {
        int countFallen = 0;
        for (Pin pin : pins) {
            if (pin.hasFallen()) {
                countFallen += 1;
            }
        }
        if (countFallen == 1) {
            for (Pin pin : pins) {
                if (pin.hasFallen()) {
                    player.setMissed(0);
                    player.setScore(player.getScore() + pin.getNumber());
                    player.setUniquePin(player.getUniquePin() + 1);
                    player.setFallenPins(player.getFallenPins() + 1);
                }
            }
        } else if (countFallen == 0) {
            player.setMissed(player.getMissed() + 1);
            if (player.getMissed() == 3) {
                player.setScore(0);
                player.setMissed(0);
            }
        } else {
            player.setMissed(0);
            player.setScore(player.getScore() + countFallen);
            player.setFallenPins(player.getFallenPins() + countFallen);
        }
        if (player.getScore() > game.getScoreToWin()) {
            player.setScore(game.getScoreToWin() / 2);
        }
        if(game.checkIfDisqualified(player)){
            game.getPlayers().remove(player);
            playerNumber = playerNumber - 1;
        }
        if (game.checkIfEndGame(player)) {
            for (Pin pin : pins) {
                pin.setFallen(false);
            }
            navController.navigate(R.id.nav_winner);

        } else {
            nextTurn.set(true);
            updateInterface();
        }
    }

    /**
     * Function to update the button validate
     * <div style="padding-left : 10px">
     * 	&#x21a6 If the game is not on pause<br>
     * 	<div style="padding-left : 10px">
     * 		&#x27A2 Count the number of fallen pins <br>
     * 		&#x21a6 If only one pin fell <br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Set the text of the button according to the pin number<br>
     * 		</div>
     * 		&#x21a6 If no pin fell <br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Set the text of the button according to the count<br>
     * 			&#x21a6 If the player already missed 2 times and the type of game is a tournament <br>
     * 			<div style="padding-left : 10px">
     * 				&#x27A2 Button asks if player is disqualified
     * 			</div>
     * 		</div>
     * 		&#x21a6 If more than one pin fell <br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Set the text of the button according to the count<br>
     * 		</div>
     * 	</div>
     * 	&#x21a6 If the game is on pause<br>
     * 	<div style="padding-left : 10px">
     * 		&#x27A2 Set the text of the button to Next Turn
     * 	</div>
     * </div>
     */
    private void updateButton() {
        if (!nextTurn.get()) {
            int countFallen = 0;
            int scoreButton = 0;
            for (Pin pin : pins) {
                if (pin.hasFallen()) {
                    countFallen += 1;
                }
            }
            if (countFallen == 1) {
                for (Pin pin : pins) {
                    if (pin.hasFallen()) {
                        scoreButton = pin.getNumber();
                        buttonValidate.setText(getResources().getString(R.string.validatePoints, String.valueOf(scoreButton)));
                    }
                }
            } else if (countFallen == 0) {
                buttonValidate.setText(getResources().getString(R.string.validate0Point, String.valueOf(scoreButton)));
                if (game.getTypeOfGame() == Game.TypeOfGame.tournament && player.getMissed() == 2) {
                    buttonValidate.setText(getResources().getString(R.string.disqualifyPlayer, String.valueOf(player.getName())));
                }
            } else {
                scoreButton = countFallen;
                buttonValidate.setText(getResources().getString(R.string.validatePoints, String.valueOf(scoreButton)));
            }
        } else {
            buttonValidate.setText(getResources().getString(R.string.nextTurn));
        }
    }

    /**
     * Function to update the page
     * <div style="padding-left : 10px">
     * 	&#x27A2 Set the displayed score to the score of the player<br>
     * 	&#8649 Display cross for the number of times a player missed<br>
     * 	&#8649 for each pin <br>
     * 	<div style="padding-left : 10px">
     * 		&#x21a6 If the game is not on pause <br>
     * 		<div style="padding-left : 10px">
     * 			&#x27FE Change the state of the pin when clicked <br>
     * 		</div>
     * 		&#x21a6 If the game is on pause <br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Make the button not clickable <br>
     * 		</div>
     * 		&#x21a6 If the pin is not connected<br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Display the drawable of disconnected pin <br>
     * 		</div>
     * 		&#x21a6 If the pin is connected<br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Display the drawable of connected pin <br>
     * 		</div>
     * 		&#x21a6 If the pin is not connected<br>
     * 		<div style="padding-left : 10px">
     * 			&#x27A2 Display the drawable of disconnected pin <br>
     * 		</div>
     * 		&#x27A2 Update the button <br>
     * 	</div>
     * </div>
     */
    private void updateInterface() {
        if (getActivity() != null) {
            textViewScore.setText(getResources().getString(R.string.scorePlayer, String.valueOf(player.getScore()), String.valueOf(game.getScoreToWin())));
            croix.removeAllViews();
            for (int i = 0; i < player.getMissed(); i++) {
                ImageView iv = new ImageView(getContext());
                iv.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_delete));
                int pxWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                int pxHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
                iv.setLayoutParams(new ViewGroup.LayoutParams(pxWidth, pxHeight));
                croix.addView(iv);
            }
            for (int i = 0; i < 12; i++) {
                if (!nextTurn.get()) {
                    int g = i;
                    listImageView.get(i).setOnClickListener(view -> {
                        pins.get(g).setFallen(!pins.get(g).hasFallen());
                        updateInterface();
                    });
                } else {
                    int g = i;
                    listImageView.get(i).setClickable(false);
                }
                if (!pins.get(i).isConnected()) {
                    listImageView.get(i).setBackgroundResource(R.drawable.circlepinbuttondisconnected);
                } else {
                    listImageView.get(i).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.circlepinbutton));
                }
                if (pins.get(i).hasFallen()) {
                    listImageView.get(i).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.circlepinbuttonfallen));
                }
            }
            updateButton();
        }
    }

    public void callBackBle(byte[] trame){
        if(this.isVisible()){
            BluetoothFrameReader.frameReader((MainActivity) this.getActivity(),trame, nextTurn);

            Log.d(TAG, "update");
            updateInterface();
        }
    }

    public void onStop() {
        super.onStop();
        BleDialogue.getInstance(null);
    }

    public void onResume(){
        super.onResume();
        BleDialogue.getInstance(this);
        updateInterface();
    }

    public void onPause(){
        super.onPause();
        BleDialogue.getInstance(null);
    }
}
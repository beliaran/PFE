package fr.eseo.pfemolkky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;
import fr.eseo.pfemolkky.tests.TestBattery;

/**
 * Class called when the Application is launched. It will rule the whole App and will save the common attributes of the Application
 */
public class MainActivity extends AppCompatActivity {
    /***
     * The game instance that the application creates
     */
    Game game;
    /**
     * The list of pins which are initialized and will be set by the user
     */
    ArrayList<Pin> pins = new ArrayList<>();

    /**
     * Get if the user is allowed to press back on a fragment
     * @return the value if the user is allowed to press back on a fragment
     */
    public boolean isAllowBack() {
        return allowBack;
    }

    /**
     * Set if the user is allowed to press back on a fragment
     * @param allowBack the boolean if the user is allowed to press back on a fragment
     */
    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    /**
     * The value if the user is allowed to press back on a fragment
     */
    private boolean allowBack =true;

    /**
     * Get the list of Pins of the game
     * @return the list of Pins of the game
     */
    public ArrayList<Pin> getPins(){ return pins; }

    /**
     * set list of Pins of the game
     * @param pins the list of Pins of the game
     */
    public void setPins(ArrayList<Pin> pins) { this.pins = pins; }

    /**
     * Get the instance of the game
     * @return the instance of the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Set the instance of the game
     * @param game the instance of the game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * The bluetooth adaptater
     */
    public android.bluetooth.BluetoothAdapter bluetoothAdapter;
    /**
     * The list of bluetooth devices analyzed by the phone
     */
    public ArrayList<android.bluetooth.BluetoothDevice> bluetoothDevices = new ArrayList<>();

    /**
     * The function called when the application is launched and which will create the main activity
     * <div style="padding-left : 10px">
     *  &#8649 Initialize all pins
     *  &#x27A2 Create the navigator of the app
     *  &#x27A2 Set the application in portrait mode
     *  &#x27A2 Navigate to the menu
     * </div>
     * @param savedInstanceState the instance state of the application
     */
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int i=0;i<12;i++){
            pins.add(new Pin(i+1));
        }

        //Test pour voir l'affichage des batteries
        //TestBattery test = new TestBattery();
        //setPins(test.getPins());
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        NavigationView navView = findViewById(R.id.nav_view);

        android.bluetooth.BluetoothManager bluetoothManager = getSystemService(android.bluetooth.BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment!=null){
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_main)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            this.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(getSupportActionBar()!=null){
                getSupportActionBar().hide();
            }
            if (savedInstanceState == null) {
                navController.navigate(R.id.nav_main);
            }
        }
    }

    /**
     * Set the function when the back is pressed
     */
    @Override
    public void onBackPressed() {
        if (isAllowBack()) {
            super.onBackPressed();
        }
    }
}
package fr.eseo.pfemolkky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import fr.eseo.pfemolkky.models.Game;
import fr.eseo.pfemolkky.models.Pin;


public class MainActivity extends AppCompatActivity {
    Game game;
    ArrayList<Pin> pins = new ArrayList<>();

    public boolean isAllowBack() {
        return allowBack;
    }

    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    private boolean allowBack =true;
    public ArrayList<Pin> getPins(){ return pins; }
    public void setPins(ArrayList<Pin> pins) { this.pins = pins; }
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        NavigationView navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(navHostFragment!=null){
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_main)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if(getSupportActionBar()!=null){
                getSupportActionBar().hide();
            }
            if (savedInstanceState == null) {
                navController.navigate(R.id.nav_main);
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (isAllowBack()) {
            super.onBackPressed();
        }
    }
}
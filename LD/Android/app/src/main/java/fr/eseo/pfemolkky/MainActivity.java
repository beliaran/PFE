package fr.eseo.pfemolkky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;

import fr.eseo.pfemolkky.models.Game;


public class MainActivity extends AppCompatActivity {
    Game game;

    public boolean isAllowBack() {
        return allowBack;
    }

    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    private boolean allowBack =true;

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
        setContentView(R.layout.activity_main);
        NavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
            navController.navigate(R.id.nav_main);
        }
    }
    @Override
    public void onBackPressed() {
        if (isAllowBack()) {
            super.onBackPressed();
        } else {
            //do nothing
        }
    }
}
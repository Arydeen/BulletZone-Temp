package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.PlayerData;

/**
 * Made by Alec Rydeen
 * Activity that acts as an intermediary between logging in and joining the game.
 * Takes the join game responsibility away from the ClientActivity, and moves it between here and
 * MenuController.
 */

@EActivity(R.layout.activity_menu)
public class MenuActivity extends Activity {

    private static final String TAG = "MenuActivity";

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @Bean
    protected GridAdapter mGridAdapter;

    @Bean
    protected GameEventProcessor eventProcessor;

    private long userId = -1;
    private long tankId = -1;

    @Bean
    MenuController menuController;

    PlayerData playerData = PlayerData.getPlayerData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    /**
     * After the view is injected, get the USER_ID passed from authentication activity
     */
    @AfterViews
    protected void afterViewInjection() {
        Log.d(TAG, "afterViewInjection");
//        userId = getIntent().getLongExtra("USER_ID", -1);
        userId = playerData.getUserId();
    }

    /**
     * Join the game using the same functionality from the ClientActivity, joining, create the
     * new Intent, and pass USER_ID and TANK_ID to it, and start the ClientActivity
     */
    @Click(R.id.joinButton)
    @Background
    void join() {
        try {
            tankId = menuController.joinAsync();
            // Start the Client activity
            Intent intent = new Intent(this, ClientActivity_.class);
            playerData.setTankId(tankId);
//            Log.d("MenuActivity", "Starting ClientActivity_");
            startActivity(intent);
//            Log.d("MenuActivity", "ClientActivity_ started");
            finish();
        } catch (Exception e) {
//            Log.e(TAG, "Error joining game", e);
        }
    }

    @Click(R.id.replayButton)
    @Background
    void replays() {
        try {
            Intent intent = new Intent(this, ReplayActivity_.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {

        }

    }

    public void joinTest() {
        this.join();
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
}

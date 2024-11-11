package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.util.ReplayData;

@EActivity(R.layout.activity_replay_instance)
public class ReplayInstanceActivity extends Activity {

    private static final String TAG = "ReplayInstanceActivity";

    @ViewById
    protected GridView replayGridView;
    @ViewById
    protected GridView replaytGridView;
    @ViewById
    protected Spinner speedMenu;

    @Bean
    SimBoardView simBoardView;

    @Bean
    protected GameEventProcessor gameEventProcessor;

    ReplayData replayData = ReplayData.getReplayData();

    int replayPaused = 0;
    int replaySpeed = 0;

    private ArrayList<?> speedSelections = new ArrayList<>(Arrays.asList("1x", "2x", "3x", "4x"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    // Stuff in here needs to change
    // TODO
    @AfterViews
    protected void afterViewInjection() {
        Log.d(TAG, "afterViewInjection");
        simBoardView.replayAttach(replayGridView, replaytGridView);
        gameEventProcessor.start();
        gameEventProcessor.setBoard(
                replayData.getInitialGrid().getGrid(), replayData.getInitialTerrainGrid().getGrid()
        );
        speedMenu.setAdapter(new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,speedSelections));
    }

    @ItemSelect({R.id.speedMenu})
    protected void onPlayableSelect(boolean checked, int position){
        Log.d(TAG,"spinnerpositon = " + position);
        replaySpeed = position + 1;
    }

    @Click(R.id.backToReplaysButton)
    void backToReplays() {
        Intent intent = new Intent(this, ReplayActivity_.class);
        startActivity(intent);
        finish();
    }

    @Click(R.id.playPauseButton)
    void playPause() {
        if (replayPaused == 0) {
            replayPaused = 1;
        } else if (replayPaused == 1) {
            replayPaused = 0;
        }
    }


}

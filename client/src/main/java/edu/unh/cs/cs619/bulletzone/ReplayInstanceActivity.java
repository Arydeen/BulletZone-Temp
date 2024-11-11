package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.util.ReplayData;

@EActivity(R.layout.activity_replay_instance)
public class ReplayInstanceActivity extends Activity {

    private static final String TAG = "ReplayInstanceActivity";

    @ViewById
    protected GridView replayGridView;

    @Bean
    SimBoardView simBoardView;

    @Bean
    protected GameEventProcessor gameEventProcessor;

    ReplayData replayData = ReplayData.getReplayData();

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
        simBoardView.replayAttach(replayGridView);
        gameEventProcessor.start();
        gameEventProcessor.setBoard(replayData.getInitialGrid().getGrid());
    }

    @Click(R.id.backToReplaysButton)
    void backToReplays() {
        Intent intent = new Intent(this, ReplayActivity_.class);
        startActivity(intent);
        finish();
    }


}

package edu.unh.cs.cs619.bulletzone;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_replay)
public class ReplayActivity extends Activity {

    private static final String TAG = "MenuActivity";

    @Bean
    ReplayController replayController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }
}

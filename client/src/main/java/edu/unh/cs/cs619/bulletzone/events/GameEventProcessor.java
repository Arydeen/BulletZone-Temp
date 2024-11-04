package edu.unh.cs.cs619.bulletzone.events;

import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EBean
public class GameEventProcessor {
    private static final String TAG = "GameEventProcessor";
    private int[][] board;
    private boolean isRegistered = false;

    public void setBoard(int[][] newBoard) {
        board = newBoard;
        Log.d(TAG, "Board updated");
    }

    public void start() {
        if (!isRegistered) {
            Log.d(TAG, "Attempting to register with EventBus");
            EventBus.getDefault().register(this);
            isRegistered = true;
            Log.d(TAG, "Successfully registered with EventBus");
        } else {
            Log.d(TAG, "Already registered, skipping registration");
        }
    }

    public void stop() {
        if (isRegistered) {
            Log.d(TAG, "Attempting to unregister from EventBus");
            try {
                EventBus.getDefault().unregister(this);
                Log.d(TAG, "Successfully unregistered from EventBus");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error unregistering: " + e.getMessage());
            }
            isRegistered = false;
        } else {
            Log.d(TAG, "Not registered, skipping unregister");
        }
    }

    @Subscribe
    public void onNewEvent(GameEvent event) {
        if (board != null) {
            Log.d(TAG, "Applying " + event);
            event.applyTo(board);
        } else {
            Log.w(TAG, "Board is null, cannot apply event: " + event);
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}
package edu.unh.cs.cs619.bulletzone.rest;

import android.os.SystemClock;
import android.util.Log;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.events.UpdateBoardEvent;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;

import org.greenrobot.eventbus.EventBus;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.events.GameEvent;
import edu.unh.cs.cs619.bulletzone.util.GameEventCollectionWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class GridPollerTask {
    private static final String TAG = "GridPollerTask";

    @RestService
    BulletZoneRestClient restClient;

    private long previousTimeStamp = -1;
    private boolean updateUsingEvents = false;
    private GameEventProcessor currentProcessor = null;
    private boolean isRunning = true;

    public boolean toggleEventUsage() {
        updateUsingEvents = !updateUsingEvents;
        return updateUsingEvents;
    }

    @Background(id = "grid_poller_task")
    public void doPoll(GameEventProcessor eventProcessor) {
        try {
            Log.d(TAG, "Starting GridPollerTask");
            currentProcessor = eventProcessor;

            // Get initial grid state
            GridWrapper grid = restClient.grid();
            onGridUpdate(grid);
            previousTimeStamp = grid.getTimeStamp();

            // Set up board but DON'T start the processor
            eventProcessor.setBoard(grid.getGrid());

            while (isRunning) {
                Log.d(TAG, "Polling for updates");
                try {
                    GameEventCollectionWrapper events = restClient.events(previousTimeStamp);
                    boolean haveEvents = false;

                    for (GameEvent event : events.getEvents()) {
                        Log.d(TAG, "Processing event: " + event);
                        if (currentProcessor != null && currentProcessor.isRegistered()) {
                            EventBus.getDefault().post(event);
                            previousTimeStamp = event.getTimeStamp();
                            haveEvents = true;
                        }
                    }

                    if (haveEvents) {
                        EventBus.getDefault().post(new UpdateBoardEvent());
                    }

                } catch (RestClientException e) {
                    Log.e(TAG, "Error fetching events", e);
                }

                SystemClock.sleep(100);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in doPoll", e);
        } finally {
            Log.d(TAG, "GridPollerTask stopped");
            currentProcessor = null;
        }
    }

    public void stop() {
        Log.d(TAG, "Stopping GridPollerTask");
        isRunning = false;
        currentProcessor = null;
    }

    @UiThread
    public void onGridUpdate(GridWrapper gw) {
        EventBus.getDefault().post(new GridUpdateEvent(gw));
    }
}
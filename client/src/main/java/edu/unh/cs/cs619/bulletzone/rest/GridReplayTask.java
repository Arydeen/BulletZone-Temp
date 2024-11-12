package edu.unh.cs.cs619.bulletzone.rest;

import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;

import edu.unh.cs.cs619.bulletzone.events.GameEvent;
import edu.unh.cs.cs619.bulletzone.events.ItemPickupEvent;
import edu.unh.cs.cs619.bulletzone.events.ReplayEventProcessor;
import edu.unh.cs.cs619.bulletzone.events.UpdateBoardEvent;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.ReplayData;

@EBean
public class GridReplayTask {
    private static final String TAG = "GridReplayTask";

    private ReplayEventProcessor currentProcessor;
    ReplayData replayData = ReplayData.getReplayData();
    private int replayIndex = 0;
    private long diffStamp = 0; //Used to calculate the difference between time stamps

    @Background(id = "grid_replay_task")
    public void doReplay(ReplayEventProcessor eventProcessor) {
        try {
            Log.d(TAG, "Stating GridReplayTask");
            currentProcessor = eventProcessor;

            GridWrapper grid = replayData.getInitialGrid();
            GridWrapper tGrid = replayData.getInitialTerrainGrid();
            onGridUpdate(grid, tGrid);

            eventProcessor.setBoard(grid.getGrid(), tGrid.getGrid());

            while (replayData.getEventAt(replayIndex) != null) {

                GameEvent currEvent = replayData.getEventAt(replayIndex);

//                Log.d(TAG, "DiffStamp: " + diffStamp);
//                Log.d(TAG, "Curr Event Delta: " + currEvent.getDeltaTimeStamp());
                long waitForMillis = currEvent.getDeltaTimeStamp() - diffStamp;
                diffStamp = currEvent.getDeltaTimeStamp();
//                Log.d(TAG, "Waiting for: " + waitForMillis + " Milliseconds");

                Thread.sleep(waitForMillis);

                EventBus.getDefault().post(currEvent);
                EventBus.getDefault().post(new UpdateBoardEvent());
                Log.d(TAG, currEvent.toString());
                replayIndex++;
            }
            Log.d(TAG, "Exited While Loop");
        } catch (Exception exe) {
            Log.e(TAG, "Unexpected error in doReplay", exe);
        }
    }

    @UiThread
    public void onGridUpdate(GridWrapper gw, GridWrapper tw) {
        EventBus.getDefault().post(new GridUpdateEvent(gw, tw));
    }
}

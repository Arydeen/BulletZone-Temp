package edu.unh.cs.cs619.bulletzone.util;

import java.util.ArrayList;

import edu.unh.cs.cs619.bulletzone.events.GameEvent;

public class ReplayData {

    private static ReplayData replayData = null;

    private GridWrapper initialGrid;
    private GridWrapper initialTerrainGrid;

    private long initialTimeStamp = -1;

    private GameEvent[] eventHistoryArray;
    private ArrayList<GameEvent> eventHistory = new ArrayList<>();

    private ReplayData() {}

    public static synchronized ReplayData getReplayData() {
        if (replayData == null) {
            replayData = new ReplayData();
        }
        return replayData;
    }

    public void setInitialGrids(GridWrapper initialGrid, GridWrapper initialTerrainGrid) {
        this.initialGrid = initialGrid;
        this.initialTerrainGrid = initialTerrainGrid;
    }

    public GridWrapper getInitialGrid() {
        return initialGrid;
    }

    public GridWrapper getInitialTerrainGrid() {
        return initialTerrainGrid;
    }

    public void addGameEvent(GameEvent event) {
        eventHistory.add(event);
    }

    public GameEvent getEventAt(int index) {
        if (index < eventHistory.size()) {
            if (eventHistory.get(index) != null) {
                return eventHistory.get(index);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setInitialTimeStamp(long initialTimestamp) {
        this.initialTimeStamp = initialTimestamp;
    }

    public long getInitialTimestamp() {
        return initialTimeStamp;
    }
}

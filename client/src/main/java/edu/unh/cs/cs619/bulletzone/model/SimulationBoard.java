package edu.unh.cs.cs619.bulletzone.model;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;

public class SimulationBoard extends GameEventProcessor {
    private int numRows, numCols;
    private BoardCell[] cells;
    private BoardCell badCell = new BoardCell(0, -1, -1);
    private static SimulationBoard singleInstance = null;
    public boolean boardUpdated = false;

    private SimulationBoard(int rows, int cols) {
        numRows = rows;
        numCols = cols;
        cells = new BoardCell[numRows * numCols];
    }

    public static synchronized SimulationBoard getInstance(int rows, int cols) {
        if (singleInstance == null) {
            singleInstance = new SimulationBoard(rows, cols);
        }

        return singleInstance;
    }

    public BoardCell getCell(int index) {
        if (index < 0 || index >= numRows * numCols || cells[index] == null)
            return badCell;
        return cells[index];
    }

    public BoardCell getCell(int row, int col) {
        return getCell(row * numCols + col);
    }

    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }

    public void setCell(int index, BoardCell cell) {
        if (index >= 0 && index < numRows * numCols)
            cells[index] = cell;
    }

    public void setCell(int row, int col, BoardCell cell) {
        setCell(row * numCols + col, cell);
    }

    public int size() {return numRows * numCols;}

//    public void setUsingJSON(JSONArray arr) throws JSONException { // Not using JSON should react to events I think?
//        int index = 0;
//        BoardCellFactory factory = new BoardCellFactory();
//        for (int i = 0; i < arr.length(); i++) {
//            JSONArray row = arr.getJSONArray(i);
//
//            for (int j = 0; j < row.length(); j++) {
//                int rawVal = row.getInt(j);
//                BoardCell newCell = factory.makeCell(rawVal, i, j);
//                setCell(index, newCell);
//                index++;
//            }
//        }
//    }
}

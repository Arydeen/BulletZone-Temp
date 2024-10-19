package edu.unh.cs.cs619.bulletzone.model;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;

/**
 * SimulationBoard Class.
 * Holds Cell information about all cells in board at a time which is known from updates in GridAdapter
 * from event updates to board int array.
 * Sam Harris 10/19/2024.
 */
public class SimulationBoard extends GameEventProcessor {
    private int numRows, numCols;
    private BoardCell[] cells;
    private BoardCell badCell = new BoardCell(0, -1, -1);

    public SimulationBoard(int rows, int cols) {
        numRows = rows;
        numCols = cols;
        cells = new BoardCell[numRows * numCols];
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

    /**
     * Called in GridAdapter from listener to Event Bus and sets Simulation Board based on int array board.
     * @param board Integer array board used by events.
     */
    public void setUsingBoard(int[][] board) {
        int index = 0;
        BoardCellFactory factory = new BoardCellFactory();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                BoardCell newCell = factory.makeCell(board[i][j], i, j);
                setCell(index, newCell);
                index++;
            }
        }
    }
}

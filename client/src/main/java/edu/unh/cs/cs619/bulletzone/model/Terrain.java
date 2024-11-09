package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.R;

/**
 * Class to define a wall from the rawValue from the server and determine
 * if it should be destructible and what it should look like.
 */
public class Terrain extends BoardCell {
    protected String cellType;
    public final int meadow = 4000;
    public final int rocky = 4001;
    public final int hilly = 4002;
    public final int forest = 4003;

    public Terrain(int val, int r, int c) {
        super(val, r, c);

        if (val == meadow) {
            resourceID = R.drawable.blank;
            cellType = "Meadow";
        } else if (val == rocky){
            resourceID = R.drawable.rocky;
            cellType = "Rocky";
        } else if (val == hilly){
            resourceID = R.drawable.hilly;
            cellType = "Rocky";
        } else {
            resourceID = R.drawable.forest;
            cellType = "Rocky";
        }
    }

    public String getCellType() {
        return cellType;
    }
}
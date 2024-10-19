package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.R;

public class Wall extends BoardCell{
    protected String cellType;
    public final int indestructibleWallType = 1000;

    public Wall(int val, int r, int c) {
        super(val, r, c);

        if (val == indestructibleWallType) {
            resourceID = R.drawable.tree;
            cellType = "IndestructibleWall";
        } else {
            resourceID = R.drawable.tree;
            cellType = "DestructibleWall";
        }
    }

    public String getCellType() {
        return cellType;
    }
}
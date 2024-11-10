package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.R;

/**
 * Template class for all simulation board, board cell.
 * Holds information for the Simboard about the type of cell, its position, and its rawValue from the server
 */
public class BoardCell {
    protected int resourceID; /// The resource ID for the image to display
    protected int rawValue; /// The value as represented on the server
    protected int row, col; /// The location of this cell on the grid

    public BoardCell(int val, int r, int c) {
        rawValue = val;
        row = r;
        col = c;

        // Set resource ID based on value
        // Power-ups and items are in range 3000-3003
        if (val >= 3000 && val <= 3003) {
            switch (val - 3000) {
                case 1: // Thingamajig
                    resourceID = R.drawable.thingamajig_icon;
                    break;
                case 2: // Anti-grav
                    resourceID = R.drawable.anti_grav_icon;
                    break;
                case 3: // Fusion reactor
                    resourceID = R.drawable.fusion_reactor_icon;
                    break;
                default:
                    resourceID = R.drawable.blank;
            }
        } else {
            resourceID = R.drawable.blank;
        }
    }

    public Integer getResourceID() {
        // Special handling for power-ups
        if (rawValue >= 3000 && rawValue <= 3003) {
            switch (rawValue - 3000) {
                case 1:
                    return R.drawable.thingamajig_icon;
                case 2:
                    return R.drawable.anti_grav_icon;
                case 3:
                    return R.drawable.fusion_reactor_icon;
                default:
                    return R.drawable.blank;
            }
        }
        return resourceID;
    }

    public int getRotation() { return 0; }

    public int getRawValue() { return rawValue; }

    public String getCellType() {
        if (rawValue >= 3000 && rawValue <= 3003) {
            switch (rawValue - 3000) {
                case 1:
                    return "Thingamajig";
                case 2:
                    return "AntiGrav";
                case 3:
                    return "FusionReactor";
                default:
                    return "Unknown Power-up";
            }
        }
        return "Empty";
    }

    public String getCellInfo() {
        String baseInfo = "Location: (" + this.col + ", " + this.row + ")";
        if (rawValue >= 3000 && rawValue <= 3003) {
            return baseInfo + " - " + getCellType();
        }
        return baseInfo;
    }
}
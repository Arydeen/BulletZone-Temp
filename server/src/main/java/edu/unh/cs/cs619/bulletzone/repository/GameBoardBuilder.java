//package edu.unh.cs.cs619.bulletzone.repository;
//
//import java.awt.Point;
//import java.util.HashMap;
//
//import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
//import edu.unh.cs.cs619.bulletzone.model.GameBoard;
//
//public class GameBoardBuilder {
//    private int width;
//    private int height;
//    private boolean wrapHorizontal;
//    private boolean wrapVertical;
//    private TerrainType baseTerrain;
//    private TerrainType[][] customTerrain;
//    private GameBoard gameBoard;
//
//    public GameBoardBuilder setDimensions(int width, int height) {
//        this.width = width;
//        this.height = height;
//        gameBoard = new GameBoard(width, height);
//        customTerrain = new TerrainType[width][height];
//        return this;
//    }
//
//    // Enables horizontal wrapping
//    public GameBoardBuilder wrapHorizontal() {
//        this.wrapHorizontal = true;
//        return this;
//    }
//
//    // Enables vertical wrapping
//    public GameBoardBuilder wrapVertical() {
//        this.wrapVertical = true;
//        return this;
//    }
//
//    public GameBoardBuilder withBaseTerrain(TerrainType t) {
//        this.baseTerrain = t;
//        return this;
//    }
//
//
//    public GameBoardBuilder withTerrain(int x, int y, TerrainType t) {
//        customTerrain[x][y] = t;
//        return this;
//    }
//
//    public GameBoard getBoard() {
//        GameBoard board = new GameBoard(width, height, wrapHorizontal, wrapVertical);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                TerrainType terrain = customTerrain[x][y] != null ? customTerrain[x][y] : baseTerrain;
//                board.setTerrain(x, y, terrain);
//            }
//        }
//        return board;
//    }
//}
//

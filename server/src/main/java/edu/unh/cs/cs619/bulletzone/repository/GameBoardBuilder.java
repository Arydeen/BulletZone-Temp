package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.awt.Point;
import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameBoard;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import jdk.jfr.Category;

@Component()
public class GameBoardBuilder {
    private int width;
    private int height;
    private boolean wrapHorizontal;
    private boolean wrapVertical;
    private Game game;
    private TerrainType baseTerrain;
    private TerrainType[][] customTerrain;
    private GameBoard gameBoard;

    public GameBoardBuilder setDimensions(int width, int height, Game game) {
        this.width = width;
        this.height = height;
        this.game = game;
        gameBoard = new GameBoard(width, height);
        customTerrain = new TerrainType[width][height];
        return this;
    }

    public GameBoard getBoard() {
        return gameBoard;
    }

    public void setupGame(Game game) {
        gameBoard = new GameBoard(16, 16);
        gameBoard.createFieldHolderGrid(game);

        // Placing walls on the game board, can be extracted to another method for cleaner code.
        gameBoard.getHolderGrid().get(1).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(2).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(3).setFieldEntity(new Wall());

        gameBoard.getHolderGrid().get(17).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(33).setFieldEntity(new Wall(1500, 33));
        gameBoard.getHolderGrid().get(49).setFieldEntity(new Wall(1500, 49));
        gameBoard.getHolderGrid().get(65).setFieldEntity(new Wall(1500, 65));

        gameBoard.getHolderGrid().get(34).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(66).setFieldEntity(new Wall(1500, 66));

        gameBoard.getHolderGrid().get(35).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(51).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(67).setFieldEntity(new Wall(1500, 67));

        gameBoard.getHolderGrid().get(5).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(21).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(37).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(53).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(69).setFieldEntity(new Wall(1500, 69));

        gameBoard.getHolderGrid().get(7).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(23).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(39).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(71).setFieldEntity(new Wall(1500, 71));

        gameBoard.getHolderGrid().get(8).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(40).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(72).setFieldEntity(new Wall(1500, 72));

        gameBoard.getHolderGrid().get(9).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(25).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(41).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(57).setFieldEntity(new Wall());
        gameBoard.getHolderGrid().get(73).setFieldEntity(new Wall());
    }

    // Enables horizontal wrapping
    public GameBoardBuilder wrapHorizontal() {
        this.wrapHorizontal = true;
        return this;
    }

    // Enables vertical wrapping
    public GameBoardBuilder wrapVertical() {
        this.wrapVertical = true;
        return this;
    }

    public GameBoardBuilder withBaseTerrain(TerrainType t) {
        this.baseTerrain = t;
        return this;
    }


    public GameBoardBuilder withTerrain(int x, int y, TerrainType t) {
        customTerrain[x][y] = t;
        return this;
    }

}


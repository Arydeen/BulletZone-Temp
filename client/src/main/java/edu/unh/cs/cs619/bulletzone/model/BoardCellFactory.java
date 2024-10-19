package edu.unh.cs.cs619.bulletzone.model;

public class BoardCellFactory {
    public BoardCell makeCell(int val, int row, int col) {
        if (val >= 30000000){
            return new BoardCell(val, row, col);
        } else if (val >= 10000000 && val < 20000000) {
            return new TurnableGoblin(val, row, col);
        } else if (val >= 2000000 && val < 3000000) {
            return new TankItem(val, row, col);
        } else if (val < 2000000 && val >= 2000) {
            return new BoardCell(val, row, col);
        } else if (val < 2000 && val > 1000) {
            return new Wall(val, row, col);
        } else if (val == 1000) {
            return new Wall(val, row, col);
        } else {
            return new BoardCell(val, row, col);
        }
    }

}
package edu.unh.cs.cs619.bulletzone.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

public class DeployCommand implements Command {
    Game game;
    long tankId;
    Direction direction;
    long millis;
    boolean isDeployed;
    private static final int FIELD_DIM = 16;

    /**
     * Constructor for EjectCommand called each time
     * eject() is called in InGameMemoryRepository
     *
     * @param tankId       tank to eject power-up
     */
    public DeployCommand(long tankId, Game game, Direction direction, long currentTimeMillis) {
        this.tankId = tankId;
        this.game = game;
        this.direction = direction;
        this.millis = currentTimeMillis;
    }

    /**
     * execute command for eject
     *
     * @return true if eject is successful
     * @throws TankDoesNotExistException if tank doesn't exist
     */
    @Override
    public boolean execute() throws TankDoesNotExistException {
        Tank tank = game.getTanks().get(tankId);
        if (tank == null) {
            throw new TankDoesNotExistException(tankId);
        }

        FieldHolder currentField = tank.getParent();
        Direction direction = Direction.Up;
        if (tank.getDirection() == Direction.Up) {
            if (!(currentField.getNeighbor(Direction.Down).isPresent())) {
                direction = Direction.Down;
            } else if (!(currentField.getNeighbor(Direction.Left).isPresent())) {
                direction = Direction.Left;
            } else if (!(currentField.getNeighbor(Direction.Right).isPresent())) {
                direction = Direction.Right;
            } else if ((currentField.getNeighbor(Direction.Up).isPresent())) {
                return false;
            }
        } else if (tank.getDirection() == Direction.Right) {
            if (!(currentField.getNeighbor(Direction.Left).isPresent())) {
                direction = Direction.Left;
            } else if (!(currentField.getNeighbor(Direction.Down).isPresent())) {
                direction = Direction.Down;
            } else if (!(currentField.getNeighbor(Direction.Right).isPresent())) {
                direction = Direction.Right;
            } else {
                return false;
            }
        } else if (tank.getDirection() == Direction.Left) {
            if (!(currentField.getNeighbor(Direction.Right).isPresent())) {
                direction = Direction.Right;
            } else if (!(currentField.getNeighbor(Direction.Down).isPresent())) {
                direction = Direction.Down;
            }else if (!(currentField.getNeighbor(Direction.Left).isPresent())) {
                direction = Direction.Left;
            } else {
                return false;
            }
        } else {//down
            if (!(currentField.getNeighbor(Direction.Up).isPresent())) {
                direction = Direction.Up;
            } else if (!(currentField.getNeighbor(Direction.Right).isPresent())) {
                direction = Direction.Right;
            } else if (!(currentField.getNeighbor(Direction.Left).isPresent())) {
                direction = Direction.Left;
            } else if ((currentField.getNeighbor(Direction.Down).isPresent())) {
                direction = Direction.Down;
            } else {
                return false;
            }
        }

        boolean isVisible = currentField.isPresent()
                && (currentField.getEntity() == tank);

        FieldHolder nextField = currentField.getNeighbor(direction);

        // Check if the destination field is empty
        if (!nextField.isPresent()) {
            // Place soldier in the intended neighboring field
            return ejectSoldierToField(tank, direction, nextField);
        } else {
            // Try to find any available empty neighboring field
            Optional<FieldHolder> emptyNeighbor = getEmptyNeighbor(currentField);
            if (emptyNeighbor.isPresent()) {
                FieldHolder alternativeField = emptyNeighbor.get();
                return ejectSoldierToField(tank, direction, alternativeField);
            }
        }
        return false;
    }

    private boolean ejectSoldierToField(Tank tank, Direction direction, FieldHolder targetField) {
        if (targetField == null || targetField.isPresent()) {
            return false;
        }

        int fieldIndex = tank.getPosition();
        int row = fieldIndex / FIELD_DIM;
        int col = fieldIndex % FIELD_DIM;

        // Check if the tank is at the gameboard edges and trying to move out of bounds
        boolean isAtLeftEdge = (col == 0) && direction == Direction.Left;
        boolean isAtRightEdge = (col == FIELD_DIM - 1) && direction == Direction.Right;
        boolean isAtTopEdge = (row == 0) && direction == Direction.Up;
        boolean isAtBottomEdge = (row == FIELD_DIM - 1) && direction == Direction.Down;

        if (isAtLeftEdge || isAtRightEdge || isAtTopEdge || isAtBottomEdge) {
            System.out.println("Next field is out of bounds, eject blocked.");
            return false;
        }

        // Create and eject the soldier
        Soldier soldier = new Soldier(tankId, direction, tank.getIp());

        // Place the soldier on the grid
        int oldPos = tank.getPosition();
        targetField.setFieldEntity(soldier);
        soldier.setParent(targetField);
        int newPos = soldier.getPosition();

        // ////////////Insert Event logic here///////////////

        return true;
    }

    /**
     * Finds open surrounding squares around the tank's current field.
     *
     * @param fieldHolder The current field of the tank.
     * @return A list of open FieldHolders in the surrounding squares.
     */
    private Optional<FieldHolder> getEmptyNeighbor(FieldHolder fieldHolder) {
        Map<Direction, FieldHolder> neighbors = fieldHolder.getNeighborsMap();
        for (Map.Entry<Direction, FieldHolder> entry : neighbors.entrySet()) {
            FieldHolder neighbor = entry.getValue();
            // Check if the neighbor is non-null and has no entity present
            if (neighbor != null && !neighbor.isPresent()) {
                return Optional.of(neighbor); // Return the first empty neighbor found
            }
        }
        return Optional.empty(); // No empty neighbors found
    }


    @Override
    public Long executeJoin() {
        return null;
    }
}

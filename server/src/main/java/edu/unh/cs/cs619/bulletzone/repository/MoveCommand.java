package edu.unh.cs.cs619.bulletzone.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

import edu.unh.cs.cs619.bulletzone.model.Builder;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Improvement;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

public class MoveCommand implements Command {

    Game game;
    long tankId;
    Direction direction;
    long millis;
    private static final int FIELD_DIM = 16;

    /**
     * Constructor for MoveCommand called each time
     * move() is called in InGameMemoryRepository
     *
     * @param tankId    id of tank to move
     * @param direction direction for tank to move
     */
    public MoveCommand(long tankId, Game game, Direction direction, long currentTimeMillis) {
        this.tankId = tankId;
        this.direction = direction;
        this.game = game;
        this.millis = currentTimeMillis;
    }

    /**
     * Command to move a tank with tankId in given direction
     *
     * @return true if moved, false otherwise
     * @throws TankDoesNotExistException  throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException     unsure, not thrown
     */
    @Override
    public boolean execute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        Tank tank = game.getTanks().get(tankId);
        if (millis < tank.getLastFireTime()) {
            return false;
        }
        FieldHolder currentField = tank.getParent();
        System.out.println("DIRECTION TO MOVE:" + direction);
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neightbor is not available");

        boolean isVisible = currentField.isPresent()
                && (currentField.getEntity() == tank);

        // Get the current direction of the tank
        Direction currentDirection = tank.getDirection();

        if (currentDirection != direction) {
            // Check if the direction is a valid turn (sideways)
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                // Turn the tank and trigger a TurnEvent
                tank.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(tank.getIntValue(), tank.getPosition()));  // Trigger turn event
                System.out.println("Tank is turning to " + direction);
                return true;  // Tank has turned, no movement yet
            }
        }
        if (!nextField.isPresent()) {
            // If the next field is empty move the user
            int fieldIndex = currentField.getPosition();
            int row = fieldIndex / FIELD_DIM;
            int col = fieldIndex % FIELD_DIM;

            // Check if the tank is at the gameboard edges and trying to move out of bounds
            boolean isAtLeftEdge = (col == 0) && direction == Direction.Left;
            boolean isAtRightEdge = (col == FIELD_DIM - 1) && direction == Direction.Right;
            boolean isAtTopEdge = (row == 0) && direction == Direction.Up;
            boolean isAtBottomEdge = (row == FIELD_DIM - 1) && direction == Direction.Down;

            if (isAtLeftEdge || isAtRightEdge || isAtTopEdge || isAtBottomEdge) {
                System.out.println("Next field is out of bounds, movement blocked.");
                return false;
            }

            // Check if the tank is visible on the field (just to prevent weird cases)
            if (!isVisible) {
                System.out.println("You have already been eliminated.");
                return false;
            }

            int oldPos = tank.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(tank);
            tank.setParent(nextField);
            int newPos = tank.getPosition();
            tank.setDirection(direction);
            EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, newPos));

        } else if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                System.out.println("Next field contains a wall, movement blocked.");
                tank.setDirection(direction);
                return false;
            }

        } else if (nextField.getEntity() instanceof Tank) {
            System.out.println("Next field contains a tank, movement blocked.");
            tank.setDirection(direction);
            return false;
        }
        tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

        return true;
    }

    /**
     * Unused, needed to override for Join command
     *
     * @return stub null value
     */
    @Override
    public Long executeJoin() {
        return null;
    }

//    private boolean surroundedByWalls(FieldHolder fieldHolder) {
//        Map<Direction, FieldHolder> neighbors = fieldHolder.getNeighborsMap();
//        for (Map.Entry<Direction, FieldHolder> entry : neighbors.entrySet()) {
//            if (!(entry.getValue().getEntity() instanceof Wall)) {
//                return false;
//            }
//        }
//        return true;
//    }

}

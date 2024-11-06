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
import edu.unh.cs.cs619.bulletzone.model.Item;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;
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
        this.game = game;
        this.direction = direction;
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
        if (millis < tank.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = tank.getParent();
        System.out.println("DIRECTION TO MOVE:" + direction);
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neighbor is not available");

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == tank);

        Direction currentDirection = tank.getDirection();

        if (currentDirection != direction) {
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                tank.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(tank.getIntValue(), tank.getPosition()));
                System.out.println("Tank is turning to " + direction);
                return true;
            }
        }

        if (!nextField.isPresent()) {
            // If the next field is empty move the user
            int oldPos = tank.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(tank);
            tank.setParent(nextField);
            int newPos = tank.getPosition();
            tank.setDirection(direction);
            EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, newPos));
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());
            return true;
        } else if (nextField.getEntity() instanceof Item) {
            Item item = (Item) nextField.getEntity();
            handleItemPickup(item, tank);
            nextField.clearField();
            moveUnit(currentField, nextField, tank, direction);
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());
            return true;
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
        return false;
    }

    private void handleItemPickup(Item item, Tank tank) {
        if (item.getType() == 1) { // Thingamajig
            double credits = item.getCredits();
            game.addCredits(tank.getId(), credits);
        } else if (item.isAntiGrav() || item.isFusionReactor()) {
            tank.addPowerUp(item);
        }
    }

    private void moveUnit(FieldHolder currentField, FieldHolder nextField, Tank tank, Direction direction) {
        int oldPos = tank.getPosition();
        currentField.clearField();
        nextField.setFieldEntity(tank);
        tank.setParent(nextField);
        int newPos = tank.getPosition();
        tank.setDirection(direction);
        EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, newPos));
    }

    private boolean tryEjectPowerUp(Tank tank) {
        if (!tank.hasPowerUps()) {
            return false;
        }

        // Try to find an empty adjacent square
        FieldHolder currentField = tank.getParent();
        Direction[] directions = {Direction.Up, Direction.Right, Direction.Down, Direction.Left};

        for (Direction dir : directions) {
            FieldHolder neighbor = currentField.getNeighbor(dir);
            if (!neighbor.isPresent()) {
                Item powerUp = tank.ejectPowerUp();
                if (powerUp != null) {
                    neighbor.setFieldEntity(powerUp);
                    powerUp.setParent(neighbor);
                    EventBus.getDefault().post(new SpawnEvent(powerUp.getIntValue(), neighbor.getPosition()));
                    return true;
                }
            }
        }

        // If no empty square found, just destroy the power-up
        tank.ejectPowerUp();
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
}
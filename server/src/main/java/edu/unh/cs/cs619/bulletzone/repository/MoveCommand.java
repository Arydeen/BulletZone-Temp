package edu.unh.cs.cs619.bulletzone.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import edu.unh.cs.cs619.bulletzone.model.Playable;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.RemoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

public class MoveCommand {
    private static final Logger log = LoggerFactory.getLogger(MoveCommand.class);
    Game game;
    long tankId;
    Direction direction;
    long millis;
    int unitType;
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
        this.unitType = unitType;
    }

    private Object getUnitByType(long id, int unitType) {
        switch (unitType) {
            case 0:
                return game.getTanks().get(id);
            case 1:
                return game.getSoldiers().get(id);
            case 2:
                return game.getBuilders().get(id);
            default:
                return null; // Invalid unitType
        }
    }

    /**
     * Command to move a tank with tankId in given direction
     *
     * @return true if moved, false otherwise
     * @throws TankDoesNotExistException  throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException     unsure, not thrown
     */
    public boolean tankExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        Tank tank = game.getTanks().get(tankId);
        if (millis < tank.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = tank.getParent();
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neighbor is not available");

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == tank);

        Direction currentDirection = tank.getDirection();

        // Handle turning
        if (currentDirection != direction) {
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                tank.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(tank.getIntValue(), tank.getPosition()));
                return true;
            }
        }

        // Handle movement to empty space
        if (!nextField.isPresent()) {
            int oldPos = tank.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(tank);
            tank.setParent(nextField);
            tank.setDirection(direction);
            EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, nextField.getPosition()));
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());
            return true;
        }
        // Handle item pickups
        else if (nextField.getEntity() instanceof Item) {
            Item item = (Item) nextField.getEntity();
            log.debug("Tank {} picking up item type {}", tankId, item.getType());

            // Capture item info before clearing
            int itemValue = item.getIntValue();
            int itemPos = nextField.getPosition();

            // Process the item
            if (item.getType() == 1) { // Thingamajig
                log.debug("Processing Thingamajig pickup for tank {}", tankId);
                double credits = item.getCredits();
                game.addCredits(tank.getId(), credits);
            } else if (item.isAntiGrav()) {
                log.debug("Processing AntiGrav pickup for tank {}", tankId);
                tank.addPowerUp(item);
            } else if (item.isFusionReactor()) {
                log.debug("Processing FusionReactor pickup for tank {}", tankId);
                tank.addPowerUp(item);
            }

            // Move tank and clear item
            nextField.clearField();
            int oldPos = tank.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(tank);
            tank.setParent(nextField);
            tank.setDirection(direction);

            // Post events in correct order
            EventBus.getDefault().post(new RemoveEvent(itemValue, itemPos));
            EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, nextField.getPosition()));

            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());
            return true;
        }
        // Handle wall collisions
        else if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                tank.setDirection(direction);
                return false;
            }
        }
        // Handle tank collisions
        else if (nextField.getEntity() instanceof Tank) {
            tank.setDirection(direction);
            return false;
        }

        tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());
        return false;
    }

    /**
     * Command to move a tank with tankId in given direction
     *
     * @return true if moved, false otherwise
     * @throws TankDoesNotExistException  throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException     unsure, not thrown
     */
    public boolean soldierExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        Soldier soldier = game.getSoldiers().get(tankId);
        if (millis < soldier.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = soldier.getParent();
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neighbor is not available");

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == soldier);

        Direction currentDirection = soldier.getDirection();

        // Handle turning
        if (currentDirection != direction) {
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                soldier.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(soldier.getIntValue(), soldier.getPosition()));
                return true;
            }
        }

        // Handle movement to empty space
        if (!nextField.isPresent()) {
            int oldPos = soldier.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(soldier);
            soldier.setParent(nextField);
            soldier.setDirection(direction);
            EventBus.getDefault().post(new MoveEvent(soldier.getIntValue(), oldPos, nextField.getPosition()));
            soldier.setLastMoveTime(millis + soldier.getAllowedMoveInterval());
            return true;
        }
        // Handle item pickups
        else if (nextField.getEntity() instanceof Item) {
            Item item = (Item) nextField.getEntity();
            log.debug("Tank {} picking up item type {}", tankId, item.getType());

            // Capture item info before clearing
            int itemValue = item.getIntValue();
            int itemPos = nextField.getPosition();

            // Process the item
            if (item.getType() == 1) { // Thingamajig
                log.debug("Processing Thingamajig pickup for tank {}", tankId);
                double credits = item.getCredits();
                game.addCredits(soldier.getId(), credits);
            } else if (item.isAntiGrav()) {
                log.debug("Processing AntiGrav pickup for tank {}", tankId);
                soldier.addPowerUp(item);
            } else if (item.isFusionReactor()) {
                log.debug("Processing FusionReactor pickup for tank {}", tankId);
                soldier.addPowerUp(item);
            }

            // Move tank and clear item
            nextField.clearField();
            int oldPos = soldier.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(soldier);
            soldier.setParent(nextField);
            soldier.setDirection(direction);

            // Post events in correct order
            EventBus.getDefault().post(new RemoveEvent(itemValue, itemPos));
            EventBus.getDefault().post(new MoveEvent(soldier.getIntValue(), oldPos, nextField.getPosition()));

            soldier.setLastMoveTime(millis + soldier.getAllowedMoveInterval());
            return true;
        }
        // Handle wall collisions
        else if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                soldier.setDirection(direction);
                return false;
            }
        }
        // Handle tank collisions
        else if (nextField.getEntity() instanceof Tank) {
            soldier.setDirection(direction);
            return false;
        }

        soldier.setLastMoveTime(millis + soldier.getAllowedMoveInterval());
        return false;
    }

    public boolean builderExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        Builder builder = game.getBuilders().get(tankId);
        if (millis < builder.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = builder.getParent();
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neighbor is not available");

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == builder);

        Direction currentDirection = builder.getDirection();

        // Handle turning
        if (currentDirection != direction) {
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                builder.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(builder.getIntValue(), builder.getPosition()));
                return true;
            }
        }

        // Handle movement to empty space
        if (!nextField.isPresent()) {
            int oldPos = builder.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(builder);
            builder.setParent(nextField);
            builder.setDirection(direction);
            EventBus.getDefault().post(new MoveEvent(builder.getIntValue(), oldPos, nextField.getPosition()));
            builder.setLastMoveTime(millis + builder.getAllowedMoveInterval());
            return true;
        }
        // Handle wall collisions
        else if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                builder.setDirection(direction);
                return false;
            }
        }
        // Handle tank collisions
        else if (nextField.getEntity() instanceof Tank) {
            builder.setDirection(direction);
            return false;
        }

        builder.setLastMoveTime(millis + builder.getAllowedMoveInterval());
        return false;
    }
}
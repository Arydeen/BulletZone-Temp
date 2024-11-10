package edu.unh.cs.cs619.bulletzone.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.juli.logging.Log;
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
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.RemoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

public class MoveCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(MoveCommand.class);
    Game game;
    long playableId;
    int playableType;
    Direction direction;
    long millis;
    private static final int FIELD_DIM = 16;

    /**
     * Constructor for MoveCommand called each time
     * move() is called in InGameMemoryRepository
     *
     * @param playableId    id of playable to move
     *
     * @param direction direction for tank to move
     */
    public MoveCommand(long playableId, int playableType, Game game, Direction direction, long currentTimeMillis) {
        this.playableId = playableId;
        this.playableType = playableType;
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
        Playable playable;
        if (playableType == 1){
            playable = game.getTanks().get(playableId);
        } else if (playableType == 2){
            System.out.println("Attempting to get builder");
            playable = game.getBuilders().get(playableId);
        } else {
            //code to get soldier (do we want a soldier list too?
            playable = null;
        }
        if (millis < playable.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = playable.getParent();
        FieldHolder nextField = currentField.getNeighbor(direction);
        checkNotNull(currentField.getNeighbor(direction), "Neighbor is not available");

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == playable);

        Direction currentDirection = playable.getDirection();

        // Handle turning
        if (currentDirection != direction) {
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                playable.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(playable.getIntValue(), playable.getPosition()));
                return true;
            }
        }

        // Handle movement to empty space
        if (!nextField.isPresent()) {
            moveUnit(currentField, nextField, playable, direction);
            playable.setLastMoveTime(millis + playable.getAllowedMoveInterval());
            return true;
        }
        // Handle item pickups
        else if (nextField.getEntity() instanceof Item) {
            Item item = (Item) nextField.getEntity();
            log.debug("Playable {} picking up item type {}", playableId, item.getType());

            // Capture item info before clearing
            int itemValue = item.getIntValue();
            int itemPos = nextField.getPosition();

            // Process the item
            handleItemPickup(item, playable);

            // Move tank and clear item
            nextField.clearField();
            int oldPos = playable.getPosition();
            currentField.clearField();
            nextField.setFieldEntity(playable);
            playable.setParent(nextField);
            playable.setDirection(direction);

            // Post events in correct order
            EventBus.getDefault().post(new RemoveEvent(itemValue, itemPos));
            EventBus.getDefault().post(new MoveEvent(playable.getIntValue(), oldPos, nextField.getPosition()));

            playable.setLastMoveTime(millis + playable.getAllowedMoveInterval());
            return true;
        }
        // Handle wall collisions
        else if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                playable.setDirection(direction);
                return false;
            }
        }
        // Handle tank collisions
        else if (nextField.getEntity() instanceof Tank) {
            playable.setDirection(direction);
            return false;
        }

        playable.setLastMoveTime(millis + playable.getAllowedMoveInterval());
        return false;
    }

    /**
     * Handle pickup of items
     * @param item Item being picked up
     * @param playable playable picking up the item
     */
    private void handleItemPickup(Item item, Playable playable) {
        if (item.getType() == 1) { // Thingamajig
            log.debug("Processing Thingamajig pickup for tank {}", playableId);
            double credits = item.getCredits();
            game.addCredits(playable.getId(), credits);
        } else if (item.isAntiGrav()) {
            log.debug("Processing AntiGrav pickup for tank {}", playableId);
            playable.addPowerUp(item);
        } else if (item.isFusionReactor()) {
            log.debug("Processing FusionReactor pickup for tank {}", playableId);
            playable.addPowerUp(item);
        }
    }

    /**
     * Move unit from one field to another
     * @param currentField Current position
     * @param nextField Target position
     * @param playable playable to move
     * @param direction Direction of movement
     */
    private void moveUnit(FieldHolder currentField, FieldHolder nextField, Playable playable, Direction direction) {
        int oldPos = playable.getPosition();
        currentField.clearField();
        nextField.setFieldEntity(playable);
        playable.setParent(nextField);
        playable.setDirection(direction);
        EventBus.getDefault().post(new MoveEvent(playable.getIntValue(), oldPos, nextField.getPosition()));
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
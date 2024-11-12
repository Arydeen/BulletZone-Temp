package edu.unh.cs.cs619.bulletzone.repository;
import static com.google.common.base.Preconditions.checkNotNull;

import org.greenrobot.eventbus.EventBus;

import edu.unh.cs.cs619.bulletzone.model.Builder;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

public class TurnCommand {

    Game game;
    long tankId;
    Direction direction;
    long millis;

    /**
     * Constructor for TurnCommand called each time
     * turn() is called in InGameMemoryRepository
     * @param tankId of tank to turn
     * @param direction direction to move tank
     */
    public TurnCommand(long tankId, Game game, Direction direction, long currentTimeMillis) {
        this.tankId = tankId;
        this.game = game;
        this.direction = direction;
        this.millis = currentTimeMillis;
    }

    /**
     *
     * @return true if move is successful, false otherwise
     * @throws TankDoesNotExistException throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException unsure, not thrown
     */
    public boolean tankExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException  {
        Tank tank = game.getTanks().get(tankId);
        if (millis < tank.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = tank.getParent();
        System.out.println("DIRECTION TO TURN:" + direction);
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

        if (!isVisible) {
            System.out.println("You have already been eliminated.");
            return false;
        }

        tank.setLastMoveTime(millis+tank.getAllowedMoveInterval());


        return false;
    }

    /**
     *
     * @return true if move is successful, false otherwise
     * @throws TankDoesNotExistException throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException unsure, not thrown
     */
    public boolean soldierExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException  {
        Soldier soldier = game.getSoldiers().get(tankId);
        FieldHolder currentField = soldier.getParent();
        System.out.println("DIRECTION TO TURN:" + direction);
        checkNotNull(currentField.getNeighbor(direction), "Neightbor is not available");

        boolean isVisible = currentField.isPresent()
                && (currentField.getEntity() == soldier);

        // Get the current direction of the tank
        Direction currentDirection = soldier.getDirection();

        if (currentDirection != direction) {
            // Check if the direction is a valid turn (sideways)
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                // Turn the tank and trigger a TurnEvent
                soldier.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(soldier.getIntValue(), soldier.getPosition()));  // Trigger turn event
                System.out.println("Tank is turning to " + direction);
                return true;  // Tank has turned, no movement yet
            }
        }

        if (!isVisible) {
            System.out.println("You have already been eliminated.");
            return false;
        }

        soldier.setLastMoveTime(millis+soldier.getAllowedMoveInterval());


        return false;
    }

    /**
     *
     * @return true if move is successful, false otherwise
     * @throws TankDoesNotExistException throws error if tank does not exist
     * @throws IllegalTransitionException unsure, not thrown
     * @throws LimitExceededException unsure, not thrown
     */

    public boolean builderExecute() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException  {
        Builder builder = game.getBuilders().get(tankId);
        if (millis < builder.getLastMoveTime()) {
            return false;
        }
        FieldHolder currentField = builder.getParent();
        System.out.println("DIRECTION TO TURN:" + direction);
        checkNotNull(currentField.getNeighbor(direction), "Neightbor is not available");

        boolean isVisible = currentField.isPresent()
                && (currentField.getEntity() == builder);

        // Get the current direction of the tank
        Direction currentDirection = builder.getDirection();

        if (currentDirection != direction) {
            // Check if the direction is a valid turn (sideways)
            if ((currentDirection == Direction.Up && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Down && (direction == Direction.Left || direction == Direction.Right))
                    || (currentDirection == Direction.Left && (direction == Direction.Up || direction == Direction.Down))
                    || (currentDirection == Direction.Right && (direction == Direction.Up || direction == Direction.Down))) {
                // Turn the tank and trigger a TurnEvent
                builder.setDirection(direction);
                EventBus.getDefault().post(new TurnEvent(builder.getIntValue(), builder.getPosition()));  // Trigger turn event
                System.out.println("Tank is turning to " + direction);
                return true;  // Tank has turned, no movement yet
            }
        }

        if (!isVisible) {
            System.out.println("You have already been eliminated.");
            return false;
        }

        builder.setLastMoveTime(millis+builder.getAllowedMoveInterval());


        return false;
    }

}

package edu.unh.cs.cs619.bulletzone.repository;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;

public class Constraints {

    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;


    public boolean canMove(long tankId) {
        Tank tank = game.getTanks().get(tankId);
        System.out.println("Active Bullet: " + tank.getNumberOfBullets() + "---- Bullet ID: " + tank.getIntValue());
        FieldHolder currentField = tank.getParent();

        Direction direction = tank.getDirection();
        FieldHolder nextField = currentField
                .getNeighbor(direction);

        // Is the bullet visible on the field?
        boolean isVisible = currentField.isPresent()
                && (currentField.getEntity() == tank);


        if (nextField.getEntity() instanceof Wall) {
            Wall w = (Wall) nextField.getEntity();
            if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                return false;
            }
        }
        // Check if the next field contains another Tank
        if (nextField.getEntity() instanceof Tank) {
            // Can't move into another tank
            System.out.println("Next field contains a tank, movement blocked.");
            return false;
        }

        // Check if the next field is out of bounds (border of the gameboard)
//        if () {
//            // Can't move outside the gameboard
//            System.out.println("Next field is out of bounds, movement blocked.");
//            return false;
//        }

        // Check if the tank is visible on the field (just to prevent weird cases)
        if (isVisible) {
            // Current field still contains the tank (something went wrong)
            return false;
        }


        return true;
    }

    public boolean canFire(Tank tank, long currentTimeMillis) {
        // Check if the tank is allowed to fire (0.5-second interval)
        return currentTimeMillis >= tank.getLastFireTime();
    }

    public boolean canFireMoreBullets(Tank tank) {
        // Check if the tank has reached the maximum number of bullets in the game (Z = 2)
        return tank.getNumberOfBullets() < tank.getAllowedNumberOfBullets();
    }

    public boolean isValidTurn(Tank tank, Direction newDirection) {
        Direction currentDirection = tank.getDirection();
        // Allow only one valid turn (e.g., no opposite directions like NORTH -> SOUTH)
        if ((currentDirection == Direction.Up && newDirection != Direction.Left && newDirection != Direction.Right) ||
                (currentDirection == Direction.Right && newDirection != Direction.Up && newDirection != Direction.Down) ||
                (currentDirection == Direction.Down && newDirection != Direction.Left && newDirection != Direction.Right) ||
                (currentDirection == Direction.Left && newDirection != Direction.Up && newDirection != Direction.Down)) {
            return false;
        }
        return true;
    }

    public boolean isValidMove(Tank tank, Direction moveDirection) {
        // Only allow forward or backward movement based on the current direction
        Direction currentDirection = tank.getDirection();
        if ((currentDirection == Direction.Up || currentDirection == Direction.Down) &&
                (moveDirection != Direction.Up && moveDirection != Direction.Down)) {
            return false; // Invalid move, sideways movement
        }
        if ((currentDirection == Direction.Left || currentDirection == Direction.Right) &&
                (moveDirection != Direction.Left && moveDirection != Direction.Right)) {
            return false; // Invalid move, sideways movement
        }
        return true;
    }
}


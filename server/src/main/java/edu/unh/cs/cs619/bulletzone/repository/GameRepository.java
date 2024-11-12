package edu.unh.cs.cs619.bulletzone.repository;

import org.javatuples.Pair;

import edu.unh.cs.cs619.bulletzone.model.Builder;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

public interface GameRepository {

    Pair<Tank, Builder> join(String ip);


    Game getGame();

    boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, int strength)
            throws TankDoesNotExistException, LimitExceededException;

    boolean build(long builderId, String entity)
            throws TankDoesNotExistException, LimitExceededException;

    boolean deploy(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean ejectPowerUp(long tankId)
            throws TankDoesNotExistException;

    public void leave(long tankId)
            throws TankDoesNotExistException;
}
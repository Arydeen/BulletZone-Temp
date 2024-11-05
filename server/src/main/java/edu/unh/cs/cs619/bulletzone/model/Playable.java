/**
 * Made by Kyle Goodwin, 11/4/2024
 * Creates an implementable abstract class that allows for the instantiation of new
 * playable objects ingame. Will make move, turn, etc. code much cleaner and allow
 * for easier enforcement of constraints, and adding new playables if need be.
 */
package edu.unh.cs.cs619.bulletzone.model;

import javax.management.ListenerNotFoundException;

public abstract class Playable extends FieldEntity {
    private static final String TAG = "Playable";

    protected final long id;

    protected final String ip;

    protected long lastMoveTime;
    protected int allowedMoveInterval;

    protected long lastTurnTime;
    protected int allowedTurnInterval;

    protected long lastFireTime;
    protected int allowedFireInterval;

    protected int numberOfBullets;
    protected int allowedNumberOfBullets;

    protected int life;

    protected Direction direction;

    public Playable(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
    }

    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    public void hit(int damage) {
        life -= damage;
        if (life <= 0) {
            //handle game over scenario
        }
    }

    //Getters
    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public long getAllowedMoveInterval() {
        return allowedMoveInterval;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public long getAllowedFireInterval() {
        return allowedFireInterval;
    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets;
    }

    public Direction getDirection() {
        return direction;
    }

    public long getId() {
        return id;
    }

    public abstract int getIntValue();

    public int getLife() {
        return life;
    }

    public String getIp() {
        return ip;
    }

    //Setters
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public void setAllowedMoveInterval(int allowedMoveInterval) {
        this.allowedMoveInterval = allowedMoveInterval;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }

    public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
        this.allowedNumberOfBullets = allowedNumberOfBullets;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    public void setLife(int life) {
        this.life = life;
    }

    public String toString() {
        return "";
    }
}

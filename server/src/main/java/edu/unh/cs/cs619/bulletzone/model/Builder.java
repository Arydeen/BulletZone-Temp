/**
 * Builder class that extends the playable abstract class
 * Made by Flynn O'Sullivan and Kyle Goodwin
 */
package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Builder extends FieldEntity {

    private static final String TAG = "Builder";

    private final long id;

    private final String ip;

    private long lastMoveTime;
    private int allowedMoveInterval;

    private long lastFireTime;
    private int allowedFireInterval;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private int moveMultiplier;
    private int allowedTurnInterval;
    private int lastTurnTime;

    private int life;

    private int damage = 15;

    private Direction direction;

    public Builder(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        life = 80; // Builders start with 80 life points

        numberOfBullets = 0;
        allowedFireInterval = 1000;  // Minimum 1 second between shots
        allowedNumberOfBullets = 3; // Builders can fire up to 3 bullets
        lastFireTime = 0;

        allowedTurnInterval = 300; // 300ms between turns
        lastTurnTime = 0;

        allowedMoveInterval = 1000; // Builders can move no faster than once per second
        lastMoveTime = 0;
        moveMultiplier = 1;
    }

    @Override
    public FieldEntity copy(){
        return new Builder(id, direction, ip);
    }

    @JsonIgnore

    @Override
    public int getIntValue() {
        return (int) (20000000 + 10000 * id + 10 * life + Direction.toByte(direction));
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getIp(){
        return ip;
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getAllowedMoveInterval() {
        return allowedMoveInterval;
    }

    public void setAllowedMoveInterval(int allowedMoveInterval) {
        this.allowedMoveInterval = allowedMoveInterval;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public String toString(){
        return "B";
    }

}

/**
 * Builder class that extends the playable abstract class
 * Made by Flynn O'Sullivan and Kyle Goodwin
 */
package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Builder extends Playable {

    private static final String TAG = "Builder";


    public Builder(long id, Direction direction, String ip) {
        super(id, direction, ip);
        life = 80; // Builders start with 80 life points

        numberOfBullets = 0;
        allowedFireInterval = 1000;  // Minimum 1 second between shots
        allowedNumberOfBullets = 3; // Builders can fire up to 3 bullets
        lastFireTime = 0;

        allowedTurnInterval = 300; // 300ms between turns
        lastTurnTime = 0;

        lastBuildTime = 0;
        allowBuildInterval = 2000;

        allowedMoveInterval = 1000; // Builders can move no faster than once per second
        lastMoveTime = 0;
        moveMultiplier = 1;

        powerUpManager = new PowerUpManager(allowedMoveInterval, allowedFireInterval);
    }

    @Override
    public FieldEntity copy(){
        return new Builder(id, direction, ip);
    }

    @JsonIgnore

    @Override
    public int getIntValue() {
        return (int) (20000000 + (10000 * getId()) + (10 * getLife()) + Direction.toByte(getDirection()));
    }

    @Override
    public String toString(){
        return "B";
    }

}

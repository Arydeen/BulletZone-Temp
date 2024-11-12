/**
 * Builder class that extends the playable abstract class
 * Made by Flynn O'Sullivan and Kyle Goodwin
 */
package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Soldier extends Playable {

    private static final String TAG = "Soldier";

    private boolean recentlyEnteredTank;  // Tracks if the soldier recently re-entered a tank

    public Soldier(long id, Direction direction, String ip) {
        super(id, direction, ip);
        life = 25;  // Soldiers start with 25 life points

        numberOfBullets = 0;
        allowedFireInterval = 250;  // Minimum 250 ms between shots
        allowedNumberOfBullets = 6; // Soldiers can fire up to 6 bullets
        lastFireTime = 0;

        allowedMoveInterval = 1000; // Soldiers can move no faster than once per second
        lastMoveTime = 0;
        moveMultiplier = 1;

        allowedTurnInterval = 0; // Soldiers can turn as fast as they want
        lastTurnTime = 0;

        recentlyEnteredTank = false;
        powerUpManager = new PowerUpManager(allowedMoveInterval, allowedFireInterval);
    }

    // Copy method for Soldier
    @Override
    public FieldEntity copy() {
        Soldier copy = new Soldier(id, direction, ip);
        copy.life = this.life;
        return copy;
    }

    // Method to apply damage to the Soldier
    @Override
    public void hit(int damage) {
        life -= damage;
        System.out.println("Soldier life: " + id + " : " + life);
        if (life <= 0) {
            System.out.println("Soldier has been eliminated.");
            // Handle game over scenario
        }
    }

    // Method to handle re-entering a tank
    public void enterTank() {
        life = 25;  // Restore to full health on re-entry
        recentlyEnteredTank = true;
        System.out.println("Soldier re-entered tank with full health.");
    }

    // Helper method to check if the soldier can exit the tank
    public boolean canExitTank(long currentTimeMillis) {
        // Soldier cannot be ejected for 3 seconds after re-entering
        return !recentlyEnteredTank || (currentTimeMillis - lastMoveTime >= 3000);
    }

    // Reset recently entered status after enough time has passed
    public void resetEntryStatus(long currentTimeMillis) {
        if (recentlyEnteredTank && (currentTimeMillis - lastMoveTime >= 3000)) {
            recentlyEnteredTank = false;
        }
    }

    @JsonIgnore

    @Override
    public int getIntValue() {
        return (int) (20000000 + 10000 * id + 10 * life + Direction.toByte(direction));
    }

    @Override
    public String toString() {
        return "S";
    }


}


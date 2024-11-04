package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Soldier extends FieldEntity {

    private static final String TAG = "Soldier";

    private final long id;
    private final String ip;

    private long lastMoveTime;
    private final int allowedMoveInterval = 1000;  // Soldiers can move no faster than once per second

    private long lastFireTime;
    private final int allowedFireInterval = 250;  // Minimum 250 ms between shots

    private int numberOfBullets;
    private final int allowedNumberOfBullets = 6;  // Soldiers can fire up to 6 bullets

    private int life = 25;  // Soldiers start with 25 life points

    private Direction direction;

    private boolean recentlyEnteredTank;  // Tracks if the soldier recently re-entered a tank

    public Soldier(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        this.numberOfBullets = 0;
        this.lastMoveTime = 0;
        this.lastFireTime = 0;
        this.recentlyEnteredTank = false;
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
    public long getId() {
        return id;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    @Override
    public int getIntValue() {
        return (int) (20000000 + 10000 * id + 10 * life + Direction.toByte(direction));
    }

    @Override
    public String toString() {
        return "S";
    }

    public String getIp() {
        return ip;
    }
}

package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.greenrobot.eventbus.EventBus;

import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;

public class Tank extends FieldEntity {
    private static final String TAG = "Tank";
    private final PowerUpManager powerUpManager;

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

    public Tank(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        life = 100;

        numberOfBullets = 0;
        allowedFireInterval = 1500;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;

        allowedTurnInterval = 0;
        lastTurnTime = 0;

        allowedMoveInterval = 500;
        lastMoveTime = 0;
        moveMultiplier = 1;  // Initialize move multiplier

        powerUpManager = new PowerUpManager(allowedMoveInterval, allowedFireInterval);
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    @Override
    public void hit(int damage) {
        System.out.println("Tank life: " + id + " : " + life);
    }

    @JsonIgnore
    @Override
    public int getIntValue() {
        return (int) (10000000 + 10000 * id + 10 * life + Direction.toByte(direction));
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

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "T";
    }

    public long getMoveMultiplier(){
        return moveMultiplier;
    }

    public void setMoveMultiplier(int moveMultiplier){
        this.moveMultiplier = moveMultiplier;
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

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public long getAllowedFireInterval() {
        return allowedFireInterval;
    }

    public void setAllowedFireInterval(int allowedFireInterval) {
        this.allowedFireInterval = allowedFireInterval;
    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }

    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets;
    }

    public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
        this.allowedNumberOfBullets = allowedNumberOfBullets;
    }

    public void addPowerUp(Item powerUp) {
        powerUpManager.addPowerUp(powerUp);
        updateIntervals();
        if (powerUp.isAntiGrav()) {
            setMoveMultiplier((int)(getMoveMultiplier() * 2)); // Double movement speed
        } else if (powerUp.isFusionReactor()) {
            setMoveMultiplier((int)(getMoveMultiplier() * 0.75)); // Reduce speed by 25%
        }
    }

    public Item ejectPowerUp() {
        Item powerUp = powerUpManager.ejectLastPowerUp();
        updateIntervals();
        if (powerUp != null) {
            if (powerUp.isAntiGrav()) {
                setMoveMultiplier((int)(getMoveMultiplier() / 2)); // Revert speed boost
            } else if (powerUp.isFusionReactor()) {
                setMoveMultiplier((int)(getMoveMultiplier() / 0.75)); // Revert speed reduction
            }
        }
        return powerUp;
    }

    private void updateIntervals() {
        allowedMoveInterval = powerUpManager.getCurrentMovementDelay();
        allowedFireInterval = powerUpManager.getCurrentFireDelay();
    }

    public boolean hasPowerUps() {
        return powerUpManager.hasPowerUps();
    }

    public boolean tryEjectPowerUp(FieldHolder currentField) {
        if (!hasPowerUps()) {
            return false;
        }

        Direction[] directions = {Direction.Up, Direction.Right, Direction.Down, Direction.Left};

        for (Direction dir : directions) {
            FieldHolder neighbor = currentField.getNeighbor(dir);
            if (!neighbor.isPresent()) {
                Item powerUp = ejectPowerUp();
                if (powerUp != null) {
                    neighbor.setFieldEntity(powerUp);
                    powerUp.setParent(neighbor);
                    EventBus.getDefault().post(new SpawnEvent(powerUp.getIntValue(), neighbor.getPosition()));
                    return true;
                }
            }
        }

        // If no empty square found, just destroy the power-up
        ejectPowerUp();
        return true;
    }
}
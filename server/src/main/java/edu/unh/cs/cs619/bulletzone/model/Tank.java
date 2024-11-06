package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.greenrobot.eventbus.EventBus;

import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;

public class Tank extends Playable {
    private static final String TAG = "Tank";
    private final PowerUpManager powerUpManager;

    public Tank(long id, Direction direction, String ip) {
        super(id, direction, ip);
        life = 100;

        numberOfBullets = 0;
        allowedFireInterval = 1500;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;

        allowedTurnInterval = 0;
        lastTurnTime = 0;

        allowedMoveInterval = 500;
        lastMoveTime = 0;

        powerUpManager = new PowerUpManager(allowedMoveInterval, allowedFireInterval);
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

    @Override
    public String toString() {
        return "T";
    }

    public void addPowerUp(Item powerUp) {
        powerUpManager.addPowerUp(powerUp);
        updateIntervals();
    }

    public Item ejectPowerUp() {
        Item powerUp = powerUpManager.ejectLastPowerUp();
        updateIntervals();
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
package edu.unh.cs.cs619.bulletzone.model;

import java.util.ArrayDeque;
import java.util.Deque;

public class PowerUpManager {
    private final Deque<Item> powerUps = new ArrayDeque<>();
    private int baseMovementDelay;
    private int baseFireDelay;

    public PowerUpManager(int baseMovementDelay, int baseFireDelay) {
        this.baseMovementDelay = baseMovementDelay;
        this.baseFireDelay = baseFireDelay;
    }

    public void addPowerUp(Item powerUp) {
        powerUps.addLast(powerUp);
        recalculateDelays();
    }

    public Item ejectLastPowerUp() {
        if (!powerUps.isEmpty()) {
            Item powerUp = powerUps.removeLast();
            recalculateDelays();
            return powerUp;
        }
        return null;
    }

    private void recalculateDelays() {
        double movementMultiplier = 1.0;
        double fireRateMultiplier = 1.0;
        int movementPenalty = 0;
        int fireRatePenalty = 0;

        for (Item powerUp : powerUps) {
            if (powerUp.isAntiGrav()) {
                movementMultiplier *= 0.5; // Double speed (half delay)
                fireRatePenalty += 100; // Add 0.1s to fire rate
            } else if (powerUp.isFusionReactor()) {
                fireRateMultiplier *= 0.5; // Double fire rate (half delay)
                movementPenalty += 100; // Add 0.1s to movement
            }
        }

        currentMovementDelay = (int)((baseMovementDelay * movementMultiplier) + movementPenalty);
        currentFireDelay = (int)((baseFireDelay * fireRateMultiplier) + fireRatePenalty);
    }

    private int currentMovementDelay;
    private int currentFireDelay;

    public int getCurrentMovementDelay() {
        return currentMovementDelay;
    }

    public int getCurrentFireDelay() {
        return currentFireDelay;
    }

    public boolean hasPowerUps() {
        return !powerUps.isEmpty();
    }

    public int getPowerUpCount() {
        return powerUps.size();
    }
}
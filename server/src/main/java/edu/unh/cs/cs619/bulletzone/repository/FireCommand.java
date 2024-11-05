package edu.unh.cs.cs619.bulletzone.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Builder;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Improvement;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.RemoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.TurnEvent;

public class FireCommand {

    Game game;
    long tankId;
    Direction direction;
    long millis;
    private static final int FIELD_DIM = 16;

    public boolean canFire(Tank tank, long currentTimeMillis, int bulletType, int[] bulletDelay) {
        // Check if the tank is allowed to fire (0.5-second interval)

        if (currentTimeMillis < tank.getLastFireTime()) {
            return false;
        }
        if (tank.getNumberOfBullets() == (tank.getAllowedNumberOfBullets())) {
            return false;
        }
        if (bulletType < 1 || bulletType > 3) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }

        // Update the tank's last fire time
        tank.setLastFireTime(currentTimeMillis + bulletDelay[bulletType - 1]);
        return true;
    }

    public int assignBulletId(int[] trackActiveBullets) {
        int bulletId = -1;
        if (trackActiveBullets[0] == 0) {
            bulletId = 0;
            trackActiveBullets[0] = 1;
        } else if (trackActiveBullets[1] == 0) {
            bulletId = 1;
            trackActiveBullets[1] = 1;
        }
        return bulletId;
    }

    public void moveBulletAndHandleCollision(Game game, Bullet bullet, Tank tank, int[] trackActiveBullets, TimerTask timerTask) {
        FieldHolder currentField = bullet.getParent();
        Direction direction = bullet.getDirection();
        FieldHolder nextField = currentField.getNeighbor(direction);

        boolean isVisible = currentField.isPresent() && (currentField.getEntity() == bullet);

        if (nextField.isPresent()) {
            nextField.getEntity().hit(bullet.getDamage());

            if (nextField.getEntity() instanceof Tank) {
                Tank t = (Tank) nextField.getEntity();
                System.out.println("Tank is hit, tank life: " + t.getLife());
                if (t.getLife() <= 0) {
                    t.getParent().clearField();
                    t.setParent(null);
                    game.removeTank(t.getId());
                }
            } else if (nextField.getEntity() instanceof Wall) {
                Wall w = (Wall) nextField.getEntity();
                if (w.getIntValue() > 1000 && w.getIntValue() <= 2000) {
                    game.getHolderGrid().get(w.getPos()).clearField();
                }
            }

            if (isVisible) {
                currentField.clearField();
            }
            EventBus.getDefault().post(new RemoveEvent(bullet.getIntValue(), bullet.getPosition()));
            trackActiveBullets[bullet.getBulletId()] = 0;
            tank.setNumberOfBullets(tank.getNumberOfBullets() - 1);
            timerTask.cancel();
        } else {
            if (isVisible) {
                currentField.clearField();
            }

            int oldPos = bullet.getPosition();
            nextField.setFieldEntity(bullet);
            bullet.setParent(nextField);
            int newPos = bullet.getPosition();
            if(oldPos == tank.getPosition()){
                System.out.println("Spawning");
                EventBus.getDefault().post(new MoveEvent(bullet.getIntValue(), newPos, newPos));
            } else {
                System.out.println("Moving");
                EventBus.getDefault().post(new MoveEvent(bullet.getIntValue(), oldPos, newPos));
            }
        }
    }

}

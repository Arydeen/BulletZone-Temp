package edu.unh.cs.cs619.bulletzone.repository;

import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.events.MoveEvent;
import edu.unh.cs.cs619.bulletzone.model.events.SpawnEvent;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private final int[] bulletDamage = {10, 30, 50};
    private final int[] bulletDelay = {500, 1000, 1500};
    private final int[] trackActiveBullets = {0, 0};

    private final Constraints tankConstraintChecker;

    @Autowired
    public InMemoryGameRepository(Constraints tankConstraintChecker) {
        this.tankConstraintChecker = tankConstraintChecker;
    }

    @Override
    public Tank join(String ip) {
        synchronized (this.monitor) {
            Tank tank;
            if (game == null) {
                this.create();
            }

            if( (tank = game.getTank(ip)) != null){
                return tank;
            }

            Long tankId = this.idGenerator.getAndIncrement();

            tank = new Tank(tankId, Direction.Up, ip);
            tank.setLife(TANK_LIFE);

            Random random = new Random();
            int x;
            int y;

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(tank);
                    tank.setParent(fieldElement);
                    break;
                }
            }

            game.addTank(ip, tank);

            return tank;
        }
    }

    @Override
    public Game getGame() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game;
    }

    @Override
    public boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }

            long millis = System.currentTimeMillis();
            if (!tankConstraintChecker.isValidTurn(tank, direction)) {
                return false;
            }
            if(millis < tank.getLastMoveTime())
                return false;

            tank.setLastMoveTime(millis+tank.getAllowedMoveInterval());

            /*try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/

            tank.setDirection(direction);

            return true; // TODO check
        }
    }

    @Override
    public boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            // Find tank

            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }

            long millis = System.currentTimeMillis();
            if (!tankConstraintChecker.canMove(tankId)) {
                return false;
            }

            if (!tankConstraintChecker.isValidMove(tank, direction)) {
                return false;
            }
            if(millis < tank.getLastMoveTime())
                return false;

            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

            boolean isCompleted;
            if (!nextField.isPresent()) {
                // If the next field is empty move the user

                /*try {
                    Thread.sleep(500);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }*/

                int oldPos = tank.getPosition();
                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);
                int newPos = tank.getPosition();
                EventBus.getDefault().post(new MoveEvent(tank.getIntValue(), oldPos, newPos));

                isCompleted = true;
            } else {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

    @Override
    public boolean fire(long tankId, int bulletType)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {

            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }

            if(tankConstraintChecker.canFireMoreBullets(tank))
                return false;

            long millis = System.currentTimeMillis();
            if(tankConstraintChecker.canFire(tank, millis)){
                return false;
            }

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

            if(!(bulletType>=1 && bulletType<=3)) {
                System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
                bulletType = 1;
            }

            tank.setLastFireTime(millis + bulletDelay[bulletType - 1]);

            int bulletId=0;
            if(trackActiveBullets[0]==0){
                bulletId = 0;
                trackActiveBullets[0] = 1;
            }else if(trackActiveBullets[1]==0){
                bulletId = 1;
                trackActiveBullets[1] = 1;
            }

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, bulletDamage[bulletType-1]);
            // Set the same parent for the bullet.
            // This should be only a one way reference.
            bullet.setParent(parent);
            bullet.setBulletId(bulletId);
            EventBus.getDefault().post(new SpawnEvent(bullet.getIntValue(), bullet.getPosition()));

            // TODO make it nicer
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    synchronized (monitor) {
                        System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField
                                .getNeighbor(direction);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);


                            if (nextField.isPresent()) {
                                // Something is there, hit it
                                nextField.getEntity().hit(bullet.getDamage());

                                if ( nextField.getEntity() instanceof  Tank){
                                    Tank t = (Tank) nextField.getEntity();
                                    System.out.println("tank is hit, tank life: " + t.getLife());
                                    if (t.getLife() <= 0 ){
                                        t.getParent().clearField();
                                        t.setParent(null);
                                        game.removeTank(t.getId());
                                    }
                                }
                                else if ( nextField.getEntity() instanceof  Wall){
                                    Wall w = (Wall) nextField.getEntity();
                                    if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                        game.getHolderGrid().get(w.getPos()).clearField();
                                    }
                                }
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }
                            trackActiveBullets[bullet.getBulletId()]=0;
                            tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                            cancel();

                        } else {
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }

                            int oldPos = bullet.getPosition();
                            nextField.setFieldEntity(bullet);
                            bullet.setParent(nextField);
                            int newPos = bullet.getPosition();
                            EventBus.getDefault().post(new MoveEvent(bullet.getIntValue(), oldPos, newPos));
                        }
                    }
                }
            }, 0, BULLET_PERIOD);

            return true;
        }
    }

    @Override
    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            Tank tank = game.getTanks().get(tankId);
            FieldHolder parent = tank.getParent();
            parent.clearField();
            game.removeTank(tankId);
        }
    }

    public void create() {
        if (game != null) {
            return;
        }
        synchronized (this.monitor) {

            this.game = new Game();

            createFieldHolderGrid(game);

            // Test // TODO Move to more appropriate place (and if desired, integrate map loader)
            game.getHolderGrid().get(1).setFieldEntity(new Wall());
            game.getHolderGrid().get(2).setFieldEntity(new Wall());
            game.getHolderGrid().get(3).setFieldEntity(new Wall());

            game.getHolderGrid().get(17).setFieldEntity(new Wall());
            game.getHolderGrid().get(33).setFieldEntity(new Wall(1500, 33));
            game.getHolderGrid().get(49).setFieldEntity(new Wall(1500, 49));
            game.getHolderGrid().get(65).setFieldEntity(new Wall(1500, 65));

            game.getHolderGrid().get(34).setFieldEntity(new Wall());
            game.getHolderGrid().get(66).setFieldEntity(new Wall(1500, 66));

            game.getHolderGrid().get(35).setFieldEntity(new Wall());
            game.getHolderGrid().get(51).setFieldEntity(new Wall());
            game.getHolderGrid().get(67).setFieldEntity(new Wall(1500, 67));

            game.getHolderGrid().get(5).setFieldEntity(new Wall());
            game.getHolderGrid().get(21).setFieldEntity(new Wall());
            game.getHolderGrid().get(37).setFieldEntity(new Wall());
            game.getHolderGrid().get(53).setFieldEntity(new Wall());
            game.getHolderGrid().get(69).setFieldEntity(new Wall(1500, 69));

            game.getHolderGrid().get(7).setFieldEntity(new Wall());
            game.getHolderGrid().get(23).setFieldEntity(new Wall());
            game.getHolderGrid().get(39).setFieldEntity(new Wall());
            game.getHolderGrid().get(71).setFieldEntity(new Wall(1500, 71));

            game.getHolderGrid().get(8).setFieldEntity(new Wall());
            game.getHolderGrid().get(40).setFieldEntity(new Wall());
            game.getHolderGrid().get(72).setFieldEntity(new Wall(1500, 72));

            game.getHolderGrid().get(9).setFieldEntity(new Wall());
            game.getHolderGrid().get(25).setFieldEntity(new Wall());
            game.getHolderGrid().get(41).setFieldEntity(new Wall());
            game.getHolderGrid().get(57).setFieldEntity(new Wall());
            game.getHolderGrid().get(73).setFieldEntity(new Wall());
        }
    }

    private void createFieldHolderGrid(Game game) {
        synchronized (this.monitor) {
            game.getHolderGrid().clear();
            for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
                game.getHolderGrid().add(new FieldHolder(i));
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;
            FieldHolder leftHolder;
            FieldHolder upHolder;

            // Build connections
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
//                    // Check if the current holder is at the grid edge
//                    boolean isRightEdge = (j == FIELD_DIM - 1);  // Rightmost column
//                    boolean isLeftEdge = (j == 0);               // Leftmost column
//                    boolean isTopEdge = (i == 0);                // Top row
//                    boolean isBottomEdge = (i == FIELD_DIM - 1); // Bottom row
//
//                    // Only add neighbors if not on the edge
//                    if (!isRightEdge) {
//                        rightHolder = game.getHolderGrid().get(i * FIELD_DIM + ((j + 1) % FIELD_DIM));
//                        targetHolder.addNeighbor(Direction.Right, rightHolder);
//                        rightHolder.addNeighbor(Direction.Left, targetHolder);
//                    }
//
//                    if (!isBottomEdge) {
//                        downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM) * FIELD_DIM + j);
//                        targetHolder.addNeighbor(Direction.Down, downHolder);
//                        downHolder.addNeighbor(Direction.Up, targetHolder);
//                    }
//
//                    if (!isLeftEdge) {
//                        leftHolder = game.getHolderGrid().get(i * FIELD_DIM + ((j - 1 + FIELD_DIM) % FIELD_DIM));
//                        targetHolder.addNeighbor(Direction.Left, leftHolder);
//                        leftHolder.addNeighbor(Direction.Right, targetHolder);
//                    }
//
//                    if (!isTopEdge) {
//                        upHolder = game.getHolderGrid().get(((i - 1 + FIELD_DIM) % FIELD_DIM) * FIELD_DIM + j);
//                        targetHolder.addNeighbor(Direction.Up, upHolder);
//                        upHolder.addNeighbor(Direction.Down, targetHolder);
//                    }

                    rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                            + ((j + 1) % FIELD_DIM));
                    downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                            * FIELD_DIM + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);
                }
            }
        }
    }

}

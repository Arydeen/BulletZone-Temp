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
import edu.unh.cs.cs619.bulletzone.repository.GameBoardBuilder;
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
    private GameBoardBuilder gameBoardBuilder;

    @Autowired
    public InMemoryGameRepository(Constraints tankConstraintChecker) {
        this.tankConstraintChecker = new Constraints();
//        this.gameBoardBuilder = new GameBoardBuilder();
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
            if(millis < tank.getLastMoveTime())
                return false;
            if (!tankConstraintChecker.isValidTurn(tank, direction)) {
                return false;
            }

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

            if(millis < tank.getLastMoveTime())
                return false;

            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            if (!tankConstraintChecker.isValidMove(tank, direction)) {
                return false;
            } else {
                tank.setDirection(direction);
            }
            if (!tankConstraintChecker.canMove(tankId, game)) {
                return false;
            }


            return true;
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
            long millis = System.currentTimeMillis();

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);
            if(!tankConstraintChecker.canFire(tank, millis, bulletType, bulletDelay)){
                return false;
            }

            int bulletId = tankConstraintChecker.assignBulletId(trackActiveBullets);
            if (bulletId == -1) {
                // No available bullet slots
                return false;
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
                        tankConstraintChecker.moveBulletAndHandleCollision(bullet, tank, trackActiveBullets, this);
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
            // Placing walls on the game board, can be extracted to another method for cleaner code.
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

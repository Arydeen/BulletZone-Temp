package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides tailored access to objects that are needed by the REST API/Controller
 * classes. The idea is that it will interface with a BulletZoneData instance as well as
 * any other objects it needs to answer requests having to do with users, items, accounts,
 * permissions, and other things that are related to what is stored in the database.
 *
 * The convention is that actual objects will be returned by the DataRepository so that internal
 * objects can make effective use of the results as well as the Controllers. This means that
 * all API/Controller classes will need to translate these objects into the strings they need
 * to communicate information back to the caller.
 */
//Note that the @Component annotation below causes an instance of a DataRepository to be
//created and used for the Controller classes in the "web" package.
@Component
public class DataRepository {
    private BulletZoneData bzdata;

    DataRepository() {
        bzdata = new BulletZoneData(); // just use in-memory database
    }

    /**
     * Stub for a method that would create a user or validate the user. [You don't have
     * to do it this way--feel free to make other methods if you like!]
     * @param username Username for the user to create or validate
     * @param password Password for the user
     * @param create true if the user should be created, or false otherwise
     * @return GameUser corresponding to the username/password if successful, null otherwise
     */
    public GameUser validateUser(String username, String password, boolean create) {
        if (create) {
            return bzdata.users.createUser(username, username, password);
        } else {
            return bzdata.users.validateLogin(username, password);
        }
    }

    public BulletZoneData getBulletZoneData() {
        return bzdata;
    }

    public GameUser getUserById(int userId) {
        return bzdata.users.getUser(userId);
    }

    public double getUserBalance(long userId) {
        GameUser user = bzdata.users.getUser((int)userId);
        if (user == null) {
            return 0;
        }
        Collection<BankAccount> accounts = user.getOwnedAccounts();
        if (accounts.isEmpty()) {
            return 0;
        }
        return accounts.iterator().next().getBalance();
    }
}
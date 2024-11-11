package edu.unh.cs.cs619.bulletzone.util;

/**
 * Made by Alec Rydeen
 * Singleton class to keep track of client side player data, such as tankId, userId, health etc.
 * will be used instead of passing more intent variables when swapping activities.
 */
public class PlayerData {

    private static PlayerData playerData = null;

    private long tankId = -1;
    private long userId = -1;

    //Unused for future implementation
    private long tankHealth;
    private long tankSpeed;
    private long tankUseRate;

    private long builderId;
    private long builderHealth;
    private long builderSpeed;
    private long builderUseRate;

    private long soldierId;
    private long soldierHealth;
    private long soldierSpeed;
    private long soldierUseRate;


    private PlayerData() {}

    /**
     * @return
     * PlayerData singleton constructor, returns singleton class, or creates if does not already exist.
     */
    public static synchronized PlayerData getPlayerData() {
        if (playerData == null) {
            playerData = new PlayerData();
        }
        return playerData;
    }

    public long getTankId() {
        return tankId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}

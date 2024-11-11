package edu.unh.cs.cs619.bulletzone;

import android.util.Log;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import edu.unh.cs.cs619.bulletzone.events.ItemPickupEvent;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;

/**
 * Made by Alec Rydeen
 * This class takes the responsibility of communicating with the Rest Client away from the ClientActivity,
 * and moves it here, which is then used inside of ClientActivity. focused on Client interactions
 */
@EBean
public class ClientController {

    @RestService
    BulletZoneRestClient restClient;

    public ClientController() {}

    @Background
    void leaveGameAsync(long playableId) {
        restClient.leave(playableId);
    }

    @Background
    void setErrorHandler(BZRestErrorhandler bzRestErrorhandler) {
        restClient.setRestErrorHandler(bzRestErrorhandler);
    }

    @Background
    public void getLifeAsync(long playableId) { //TODO: Need to call this after the server says there's been a hit
        Long newLifeBuilder = restClient.getLife(playableId, 1).getResult();
        Long newLifeTank = restClient.getLife(playableId, 2).getResult();

        PlayerData.getPlayerData().setTankLife(newLifeTank);
        PlayerData.getPlayerData().setBuilderLife(newLifeBuilder);
    }

    double getBalance(long userId) {
        return restClient.getBalance(userId);
    }

//    BooleanWrapper deductBalance(long userId, double amount) {
//        return restClient.deductBalance(userId, amount);
//    }

    @Background
    public void handleItemPickup(int itemType) {
        if (itemType == 1) { // Thingamajig
            // Random amount between 100 and 1000
            double amount = 100 + new Random().nextInt(901);
            try {
                // Add credits to user's account
                BooleanWrapper result = restClient.depositBalance(PlayerData.getPlayerData().getUserId(), amount);
                if (result != null && result.isResult()) {
                    EventBus.getDefault().post(new ItemPickupEvent(itemType, amount));
                }
            } catch (Exception e) {
                Log.e("ClientController", "Error handling item pickup", e);
            }
        }
    }
}
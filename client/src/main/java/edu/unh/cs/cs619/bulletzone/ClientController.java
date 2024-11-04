package edu.unh.cs.cs619.bulletzone;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService;

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
    void leaveGameAsync(long tankId) {
        restClient.leave(tankId);
    }

    @Background
    void setErrorHandler(BZRestErrorhandler bzRestErrorhandler) {
        restClient.setRestErrorHandler(bzRestErrorhandler);
    }

    double getBalance(long userId) {
        return restClient.getBalance(userId);
    }

    BooleanWrapper deductBalance(long userId, double amount) {
        return restClient.deductBalance(userId, amount);
    }
}

package edu.unh.cs.cs619.bulletzone;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;

@EBean
public class PowerUpController {

    @RestService
    BulletZoneRestClient restClient;

    @Background
    public void ejectPowerUpAsync(long tankId) {
        restClient.ejectPowerUp(tankId);
    }
}
package edu.unh.cs.cs619.bulletzone;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

/**
 * Made by Alec Rydeen
 *
 * This class takes the responsibility of communicating with the Rest Client away from the ClientActivity,
 * and moves it here, which is then used inside of ClientActivity focused on Tank Controls
 */

// Controller Class to move rest client calls for tank controls outside of ClientActivity
@EBean
public class TankEventController {

    @RestService
    BulletZoneRestClient restClient;
    private int lastPressedButtonId = -1;

    public TankEventController() {}

    @Background
    public void moveAsync(long tankId, byte direction) {
        restClient.move(tankId, direction);
    }

    @Background
    public void turnAsync(long tankId, byte direction) {
        restClient.turn(tankId, direction);
    }

    private boolean onePointTurn(int currentButtonId) {
        // Check if the previous and current directions are 90-degree turns
        if ((lastPressedButtonId == R.id.buttonUp && currentButtonId == R.id.buttonLeft) ||
                (lastPressedButtonId == R.id.buttonUp && currentButtonId == R.id.buttonRight) ||
                (lastPressedButtonId == R.id.buttonDown && currentButtonId == R.id.buttonLeft) ||
                (lastPressedButtonId == R.id.buttonDown && currentButtonId == R.id.buttonRight) ||
                (lastPressedButtonId == R.id.buttonLeft && currentButtonId == R.id.buttonUp) ||
                (lastPressedButtonId == R.id.buttonLeft && currentButtonId == R.id.buttonDown) ||
                (lastPressedButtonId == R.id.buttonRight && currentButtonId == R.id.buttonUp) ||
                (lastPressedButtonId == R.id.buttonRight && currentButtonId == R.id.buttonDown)) {
            return true;
        }
        return false;
    }

    @Background
    public void turnOrMove(int currentButtonId, long tankId, byte direction) {
        if (lastPressedButtonId != -1 && onePointTurn(currentButtonId)) {
//            Log.d(TAG, "One-point turn detected: from " + lastPressedButtonId + " to " + viewId);
            this.turnAsync(tankId, direction);
        } else {
            this.moveAsync(tankId, direction);
        }
        lastPressedButtonId = currentButtonId;
    }

    @Background
    public void fire(long tankId) {
        restClient.fire(tankId);
    }

}
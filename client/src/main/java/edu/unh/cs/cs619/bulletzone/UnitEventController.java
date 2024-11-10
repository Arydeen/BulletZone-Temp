package edu.unh.cs.cs619.bulletzone;

import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

/**
 * Made by Alec Rydeen
 * This class takes the responsibility of communicating with the Rest Client away from the ClientActivity,
 * and moves it here, which is then used inside of ClientActivity. Focused on Tank Controls
 */

// Controller Class to move rest client calls for tank controls outside of ClientActivity
@EBean
public class UnitEventController {

    @RestService
    BulletZoneRestClient restClient;
    private int lastPressedButtonId = -1;
    PlayerData playerData;

    public UnitEventController() {}

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
    public void turnOrMove(int currentButtonId) {
        long unitId = playerData.getCurId();
        byte direction = 0;
        switch (currentButtonId) {
            case R.id.buttonUp:
                direction = 0;
                break;
            case R.id.buttonDown:
                direction = 4;
                break;
            case R.id.buttonLeft:
                direction = 6;
                break;
            case R.id.buttonRight:
                direction = 2;
                break;
        }
        if (lastPressedButtonId != -1 && onePointTurn(currentButtonId)) {
//            Log.d(TAG, "One-point turn detected: from " + lastPressedButtonId + " to " + viewId);
            this.turnAsync(unitId, direction);
        } else {
            this.moveAsync(unitId, direction);
        }
        lastPressedButtonId = currentButtonId;
    }

    @Background
    public void fire(long tankId) {
        restClient.fire(tankId);
    }

    /**
     * Sends a build or dismantle command depending on what unit is currently selected
     *
     * @param curId  unit id currently selected
     * @param entity currently selected improvement type
     */
    @Background
    public void buildOrDismantle(long curId, String entity) {
        if (curId == playerData.getBuilderId()) {
            // send build
            if (((playerData.getCurEntity().equals("destructibleWall") || playerData.getCurEntity().equals("indestructibleWall")) ||
                    (playerData.getCurEntity().equals("facility")))) {
                restClient.build(playerData.getBuilderId(), playerData.getCurEntity());
            } else if (playerData.getCurEntity().equals("power-up")) {
                restClient.build(playerData.getBuilderId(), playerData.getCurEntity());
            }
        } else {
//            Log.d(TAG, "Cannot build while controlling another vehicle");
        }
    }


}
package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.api.BackgroundExecutor;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.ClientActivityShakeDriver;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.AuthenticateActivity;
import edu.unh.cs.cs619.bulletzone.util.ClientActivityShakeDriver;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    protected GridAdapter mGridAdapter;

    @Bean
    protected GameEventProcessor eventProcessor;

    @ViewById
    protected GridView gridView;

    @ViewById
    protected TextView userIdTextView;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    @Bean
    TankEventController tankEventController;

    @Bean
    ClientController clientController;

    ClientActivityShakeDriver shakeDriver;

    /**
     * Remote tank identifier
     */
    private long tankId = -1;
    private long userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes the shake driver / listener and defines what action to take when device is shaken
        shakeDriver = new ClientActivityShakeDriver(this, new ClientActivityShakeDriver.OnShakeListener() {
            @Override
            public void onShake() {
                onButtonFire();
            }
        });

        Log.e(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(gridEventHandler);

        //Un-attaches the shakeDriver and listener when activity is destroyed
        shakeDriver.stop();
        Log.e(TAG, "onDestroy");
    }

    /**
     * Otto has a limitation (as per design) that it will only find
     * methods on the immediate class type. As a result, if at runtime this instance
     * actually points to a subclass implementation, the methods registered in this class will
     * not be found. This immediately becomes a problem when using the AndroidAnnotations
     * framework as it always produces a subclass of annotated classes.
     *
     * To get around the class hierarchy limitation, one can use a separate anonymous class to
     * handle the events.
     */
    private Object gridEventHandler = new Object() {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw);
        }
    };

    @AfterViews
    protected void afterViewInjection() {
        Log.d(TAG, "afterViewInjection");
        userId = getIntent().getLongExtra("USER_ID", -1);
        tankId = getIntent().getLongExtra("TANK_ID", -1);
        if (userId != -1) {
            userIdTextView.setText("User ID: " + userId);
        } else {
            userIdTextView.setText("User ID: Not logged in");
        }
        SystemClock.sleep(500);
        // Set the TankID to be used when determining if it is the user's tank
        mGridAdapter.setTankId(tankId);
        gridView.setAdapter(mGridAdapter);
    }


    @AfterInject
    void afterInject() {
        Log.d(TAG, "afterInject");
        clientController.setErrorHandler(bzRestErrorhandler);
        EventBus.getDefault().register(gridEventHandler);
        gridPollTask.doPoll(eventProcessor);
    }

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
    }

    //Remove functionality for now

    /*@Click(R.id.eventSwitch)
    protected void onEventSwitch() {

        if (gridPollTask.toggleEventUsage()) {
            Log.d("EventSwitch", "ON");
            eventProcessor.setBoard(mGridAdapter.getBoard());
            eventProcessor.start();
        } else {
            Log.d("EventSwitch", "OFF");
            eventProcessor.stop();
        }
    }*/

    @Click({R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight})
    protected void onButtonMove(View view) {
        final int viewId = view.getId();
        byte direction = 0;
        switch (viewId) {
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
            default:
//                Log.e(TAG, "Unknown movement button id: " + viewId);
                break;
        }
        tankEventController.turnOrMove(viewId, tankId, direction);
    }

    @Click(R.id.buttonFire)
    protected void onButtonFire() {
        tankEventController.fire(tankId);
    }

    @Click(R.id.buttonLeave)
    void leaveGame() {
        Log.d(TAG, "leaveGame() called, tank ID: " + tankId);
        BackgroundExecutor.cancelAll("grid_poller_task", true);
        clientController.leaveGameAsync(tankId);
        leaveUI();
    }

    @Click(R.id.buttonLogin)
    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.buttonLogout)
    void logout() {
        Log.d(TAG, "logout() called");
        logoutUI();
    }

    @UiThread
    void leaveUI() {
        Log.d(TAG, "leaveUI() called");
        Intent intent = new Intent(this, MenuActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @UiThread
    void logoutUI() {
        Log.d(TAG, "logoutUI() called");
        userId = -1;
        if (userIdTextView != null) {
            userIdTextView.setText("User ID: Not logged in");
        }
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void moveTest(View view) {
        this.onButtonMove(view);
    }

    public void fireTest() {
        this.onButtonFire();
    }

    public void setTankEventController(TankEventController tankEventController) {
        this.tankEventController = tankEventController;
    }
}
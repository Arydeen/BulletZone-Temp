package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import org.androidannotations.annotations.*;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.api.BackgroundExecutor;
import org.greenrobot.eventbus.ThreadMode;

import edu.unh.cs.cs619.bulletzone.events.GameEventProcessor;
import edu.unh.cs.cs619.bulletzone.events.ItemPickupEvent;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.ClientActivityShakeDriver;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.AuthenticateActivity;
import edu.unh.cs.cs619.bulletzone.util.ClientActivityShakeDriver;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

//    @Bean
//    protected GridAdapter mGridAdapter;

    @Bean
    protected GameEventProcessor eventProcessor;

    @ViewById
    protected GridView gridView;

    @ViewById
    protected TextView userIdTextView;

    @ViewById
    protected TextView balanceTextView;

    @ViewById
    protected TextView statusTextView;

    @ViewById
    protected TextView eventBusStatus;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    @Bean
    TankEventController tankEventController;

    @Bean
    ClientController clientController;

    @Bean
    SimBoardView simBoardView;

    ClientActivityShakeDriver shakeDriver;

    PlayerData playerData = PlayerData.getPlayerData();

    /**
     * Remote tank identifier
     */
    private long tankId = -1;
    private long userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Log.d(TAG, "onDestroy called");

        // Stop the grid poller
        gridPollTask.stop();
        BackgroundExecutor.cancelAll("grid_poller_task", true);

        // Clean up event bus registrations
//        if (gridEventHandler != null) {
//            EventBus.getDefault().unregister(gridEventHandler);
//        }
        simBoardView.detach();
        if (eventProcessor != null) {
            eventProcessor.stop();
        }

        // Clean up other resources
        shakeDriver.stop();
        Log.d(TAG, "onDestroy completed");
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
//    private Object gridEventHandler = new Object() {
//        @Subscribe
//        public void onUpdateGrid(GridUpdateEvent event) {
//            updateGrid(event.gw);
//        }
//    };

    @AfterViews
    protected void afterViewInjection() {
        Log.d(TAG, "afterViewInjection");
        userId = playerData.getUserId();
        tankId = playerData.getTankId();
        if (userId != -1) {
            userIdTextView.setText("User ID: " + userId);
            fetchAndUpdateBalance();
        } else {
            userIdTextView.setText("User ID: Not logged in");
            updateBalanceUI(null);
        }
        SystemClock.sleep(500);
        simBoardView.attach(gridView, tankId);
        // Set the TankID to be used when determining if it is the user's tank
//        mGridAdapter.setTankId(tankId);
//        gridView.setAdapter(mGridAdapter);
    }

    @Background
    void fetchAndUpdateBalance() {
        try {
            // Add debug logging
            Log.d(TAG, "Fetching balance for userId: " + userId);
            Double balance = clientController.getBalance(userId);
            Log.d(TAG, "Received balance: " + balance);
            updateBalanceUI(balance);
        } catch (Exception e) {
            // Enhanced error logging
            Log.e(TAG, "Error fetching balance for userId: " + userId, e);
            updateBalanceUI(null);
        }
    }

    @AfterInject
    void afterInject() {
        Log.d(TAG, "afterInject");
        clientController.setErrorHandler(bzRestErrorhandler);
//        EventBus.getDefault().register(gridEventHandler);
        // Start the event processor before starting the poller
        eventProcessor.start();

        // Now start polling
        gridPollTask.doPoll(eventProcessor);
    }

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

    @UiThread
    void updateBalanceUI(Double balance) {
        if (balanceTextView != null) {
            if (balance != null) {
                balanceTextView.setText(String.format("Balance: $%.2f", balance));
            } else {
                balanceTextView.setText("Balance: Unavailable");
            }
        }
    }

    @Click(R.id.buttonTest)
    void testDeduction() {
        deductBalanceAsync();
    }

    @Background
    void deductBalanceAsync() {
        try {
            Log.d(TAG, "Attempting to deduct 100 credits for user: " + userId);
            // Try to deduct 100 credits
            BooleanWrapper result = clientController.deductBalance(userId, 100.0);
            if (result != null && result.isResult()) {
                Log.d(TAG, "Successfully deducted 100 credits");
                showStatus("Successfully deducted 100 credits");
                fetchAndUpdateBalance();
            } else {
                Log.d(TAG, "Failed to deduct balance, result: " + (result != null ? result.isResult() : "null"));
                showStatus("Failed to deduct balance");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deducting balance", e);
            showStatus("Error: " + e.getMessage());
        }
    }

    @UiThread
    void showStatus(String message) {
        if (statusTextView != null) {
            statusTextView.setText(message);
        }
        // Also show as a Toast for better visibility
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.buttonTestEventBus)
    protected void runEventBusTest() {
        Log.d(TAG, "Starting EventBus tests");
        updateEventBusStatus("Starting tests...");
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runTestSequence();
                } catch (Exception e) {
                    Log.e(TAG, "Test sequence failed", e);
                    updateEventBusStatus("Tests failed: " + e.getMessage());
                }
            }
        });
    }

    private void runTestSequence() {
        try {
            // Test 1: Basic Registration
            updateEventBusStatus("Test 1: Basic Registration");
            Log.d(TAG, "=== Test 1: Basic Registration ===");
            eventProcessor.start();
            SystemClock.sleep(500);

            // Test 2: Double Registration
            updateEventBusStatus("Test 2: Double Registration");
            Log.d(TAG, "=== Test 2: Double Registration ===");
            eventProcessor.start();
            SystemClock.sleep(500);

            // Test 3: Stop and Start
            updateEventBusStatus("Test 3: Stop and Start");
            Log.d(TAG, "=== Test 3: Stop and Start ===");
            eventProcessor.stop();
            SystemClock.sleep(100);
            eventProcessor.start();
            SystemClock.sleep(500);

            // Test 4: Rapid Toggle
            updateEventBusStatus("Test 4: Rapid Toggle");
            Log.d(TAG, "=== Test 4: Rapid Toggle ===");
            for (int i = 0; i < 5; i++) {
                eventProcessor.stop();
                eventProcessor.start();
                SystemClock.sleep(100);
            }

            // Test 5: Event Processing
            updateEventBusStatus("Test 5: Event Processing");
            Log.d(TAG, "=== Test 5: Event Processing ===");
            tankEventController.moveAsync(tankId, (byte)0);
            SystemClock.sleep(1000);

            updateEventBusStatus("All tests completed successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Test sequence failed", e);
            updateEventBusStatus("Tests failed: " + e.getMessage());
        }
    }

    @UiThread
    void updateEventBusStatus(final String status) {
        if (eventBusStatus != null) {
            eventBusStatus.setText(status);
            Log.d(TAG, "Status updated: " + status);
        }
    }

    @Bean
    PowerUpController powerUpController;

    @Click(R.id.buttonEject)
    protected void onButtonEject() {
        powerUpController.ejectPowerUpAsync(tankId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemPickup(ItemPickupEvent event) {
        if (event.getItemType() == 1) { // Thingamajig
            // Show toast
            String message = String.format("Picked up Thingamajig! Added $%.2f credits", event.getAmount());
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Update balance display
            fetchAndUpdateBalance();
        }
    }
}
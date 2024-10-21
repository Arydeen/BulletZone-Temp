package edu.unh.cs.cs619.bulletzone;

import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;

@EBean
public class MenuController {

    private static final String TAG = "MenuController";


    @RestService
    BulletZoneRestClient restClient;

    public MenuController() {}

    long joinAsync() {
        return restClient.join().getResult();
    }
}

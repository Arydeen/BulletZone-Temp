package edu.unh.cs.cs619.bulletzone;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.ResultWrapper;

@EBean
public class AuthenticationController {
    @RestService
    BulletZoneRestClient restClient;

    /**
     * Constructor for InputHandler
     * [Feel free to add arguments and initialization as needed]
     */
    public AuthenticationController() {
        //note: any work that needs to be done with an annotated item like @RestService or @Bean
        //      will not work here, but should instead go into a method below marked
        //      with the @AfterInject annotation.
    }

    @AfterInject
    public void afterInject() {
        //Any initialization involving components annotated with things like @RestService or @Bean
        //goes here.
    }
    
    /**
     * Uses restClient to login.
     *
     * @param username Username provided by user.
     * @param password Password for account provided by user.
     */
    public ResultWrapper login(String username, String password) {
        try {
            LongWrapper result = restClient.login(username, password);
            if (result == null) {
                return new ResultWrapper(false, "Server error: No response", null);
            }
            return new ResultWrapper(true, "Login successful", result.getResult());
        } catch (RestClientException e) {
            return new ResultWrapper(false, "Network error: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ResultWrapper(false, "Unexpected error: " + e.getMessage(), null);
        }
    }

    /**
     * Uses restClient to register.
     *
     * @param username New username provided by user.
     * @param password Password for new account provided by user.
     */
    public ResultWrapper register(String username, String password) {
        try {
            ResultWrapper result = restClient.register(username, password);
            if (result == null) {
                return new ResultWrapper(false, "Server error: No response", null);
            }
            return result;
        } catch (RestClientException e) {
            return new ResultWrapper(false, "Network error: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ResultWrapper(false, "Unexpected error: " + e.getMessage(), null);
        }
    }

    /**
     * Helper for testing
     *
     * @param restClientPassed tested restClient
     */
    public void initialize(BulletZoneRestClient restClientPassed) {
        restClient = restClientPassed;
    }

}

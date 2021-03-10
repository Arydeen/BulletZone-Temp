package edu.unh.cs.cs619.bulletzone;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_authenticate)
public class AuthenticateActivity extends AppCompatActivity {
    @ViewById
    EditText username_editText;

    @ViewById
    EditText password_editText;

    @ViewById
    TextView status_message;

    @Bean
    AuthenticationController controller;

    long userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
    }

    /**
     * Registers a new user and logs them in
     */
    @Click(R.id.register_button)
    @Background
    protected void onButtonRegister() {
        String username = username_editText.getText().toString();
        String password = password_editText.getText().toString();

        boolean status = controller.register(username, password);

        if (!status) {
            setStatus("User " + username + " already exists or server error.\nPlease login or try with a different username.");
        } else { //register successful
            setStatus("Registration successful.");
            //Do you want to log in automatically, or force them to do it?
            userID = controller.login(username, password);
            if (userID < 0) {
                setStatus("Registration unsuccessful--inconsistency with server.");
            }
            //do other login things?
        }
    }

    /**
     * Logs in an existing user
     */
    @Click(R.id.login_button)
    @Background
    protected void onButtonLogin() {
        String username = username_editText.getText().toString();
        String password = password_editText.getText().toString();

        userID = controller.login(username, password);
        if (userID < 0) {
            setStatus("Invalid username and/or password.\nPlease try again.");
        } else { //register successful
            setStatus("Login successful.");
            //do other login things?
        }
    }

    @UiThread
    protected void setStatus(String message) {
        status_message.setText(message);
    }
}
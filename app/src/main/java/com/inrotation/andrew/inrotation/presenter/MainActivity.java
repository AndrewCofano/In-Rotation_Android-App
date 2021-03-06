package com.inrotation.andrew.inrotation.presenter;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.inrotation.andrew.inrotation.R;

/**
 * Represents the activity that handles the login initiation.
 * Created by Andrew Cofano
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button authenticateButton = (Button) findViewById(R.id.spotifyAuthenticateButton);

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAuthenticateProcess();
            }
        });
    }

    protected void runAuthenticateProcess() {
        Intent homeIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(homeIntent);
    }


}


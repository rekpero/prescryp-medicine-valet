package com.prescryp.deliveryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.prescryp.deliveryapp.SessionManager.UserSessionManager;

import java.util.HashMap;

public class UserAccountActivity extends AppCompatActivity {

    private TextView profile_name_text, phone_number_text;
    private ConstraintLayout editProfile, logOutAction, accountSettings, notificationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profile_name_text = findViewById(R.id.profile_name_text);
        phone_number_text = findViewById(R.id.phone_number_text);
        editProfile = findViewById(R.id.editProfile);
        logOutAction = findViewById(R.id.logOutAction);
        accountSettings = findViewById(R.id.accountSettings);
        notificationSettings = findViewById(R.id.notificationSettings);

        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        final HashMap<String, String> user = userSessionManager.getUserDetails();
        String session_mob = user.get(UserSessionManager.KEY_MOB);
        String session_name = user.get(UserSessionManager.KEY_NAME);

        profile_name_text.setText(session_name);
        phone_number_text.setText(session_mob);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserAccountActivity.this, EditProfileActivity.class));
            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserAccountActivity.this, AccountSettingsActivity.class));
            }
        });

        notificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserAccountActivity.this, NotificationSettingsActivity.class));
            }
        });

        logOutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });



    }

    public void logoutDialog(){


        final AlertDialog.Builder builder = new AlertDialog.Builder(UserAccountActivity.this);

        builder.setTitle("Log Out");
        builder.setMessage("Do you want to log out?");

        builder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LogOut();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
        builder.show();
    }

    private void LogOut(){
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.logoutUser();
    }

}

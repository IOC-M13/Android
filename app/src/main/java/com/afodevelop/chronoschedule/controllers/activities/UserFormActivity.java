package com.afodevelop.chronoschedule.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;

public class UserFormActivity extends AppCompatActivity {

    // DEMO DATA
    private final String[] DEMO_USERS = {
            "administrador",
            "Alejandro Olivan",
            "Oscar Membrilla",
            "Armando Bronca",
    };

    // CONSTANTS


    // CLASS-WIDE VARIABLES
    private EditText userNameEditText;
    private TextView userNameText;
    private int idUser;
    private String user;
    private boolean isNew = false;

    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        isNew = getIntent().getExtras().getBoolean("isNew");


        if (isNew) {
            setTitle("Create User");
            userNameEditText = (EditText) findViewById(R.id.edit_user_username_edittext);
            userNameEditText.setVisibility(View.VISIBLE);

        } else {
            setTitle("Edit User");
            user = getIntent().getExtras().getString("user");
            userNameText = (TextView) findViewById(R.id.edit_user_username_text);
            userNameText.setText(user);
            userNameText.setVisibility(View.VISIBLE);

        }

    }
}

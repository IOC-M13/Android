package com.afodevelop.chronoschedule.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.afodevelop.chronoschedule.R;

public class SettingsActivity extends AppCompatActivity {

    // CONSTANTS
    private static final String SP_KEY_DBHOST = "dbHost";
    private static final String SP_KEY_DBPORT = "dbPort";

    // CLASS_WIDE VARIABLES
    private EditText hostEditText, portEditText;
    private FloatingActionButton saveButton;
    private String host, port;

    // CONSTRUCTOR

    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle extras = getIntent().getExtras();
        host = extras.getString(SP_KEY_DBHOST);
        port = extras.getString(SP_KEY_DBPORT);

        hostEditText = (EditText) findViewById(R.id.hostname_text);
        hostEditText.setText(host);
        portEditText = (EditText) findViewById(R.id.port_text);
        portEditText.setText(port);

        saveButton = (FloatingActionButton) findViewById(R.id.save_settings_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Bundle extras = new Bundle();
                extras.putString(SP_KEY_DBHOST, hostEditText.getText().toString());
                extras.putString(SP_KEY_DBPORT, portEditText.getText().toString());
                i.putExtras(extras);
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }


}

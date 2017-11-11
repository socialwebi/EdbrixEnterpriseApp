package com.edbrix.enterprise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edbrix.enterprise.Activities.BottomTabMenuActivity;
import com.edbrix.enterprise.Activities.LoginActivity;
import com.edbrix.enterprise.Activities.SignupActivity;

public class MainActivity extends AppCompatActivity {

    Button _main_button_login;
    Button _main_button_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _main_button_login = findViewById(R.id.main_button_login);
        _main_button_register = findViewById(R.id.main_button_register);

        _main_button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        _main_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(MainActivity.this, BottomTabMenuActivity.class);
                startActivity(registerIntent);
            }
        });
    }

}

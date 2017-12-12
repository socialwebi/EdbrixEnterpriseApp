package com.edbrix.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edbrix.enterprise.Activities.DashboardActivity;
import com.edbrix.enterprise.Activities.LoginActivity;
import com.edbrix.enterprise.Activities.SignupActivity;
import com.edbrix.enterprise.Volley.SettingsMy;
import com.edbrix.enterprise.baseclass.BaseActivity;

public class MainActivity extends BaseActivity {

    Button _main_button_login;
    Button _main_button_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _main_button_login = findViewById(R.id.main_button_login);
        _main_button_register = findViewById(R.id.main_button_register);

        if (SettingsMy.getActiveUser() != null) {
            Intent loginIntent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(loginIntent);
            finish();
        }

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
                Intent registerIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(registerIntent);
            }
        });
    }

}

package com.example.dating.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dating.activity.Login.Login;
import com.example.dating.activity.Login.RegisterBasicInfo;
import com.example.dating.R;
import com.example.dating.activity.Main.MainActivity;
import com.example.dating.activity.Profile.Profile_Activity;
import com.example.dating.dataholder.SessionManager;


public class IntroductionMain extends AppCompatActivity {

    private Button signupButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(IntroductionMain.this).isLoggedIn();
                String token = new SessionManager(IntroductionMain.this).getAccessToken();
                if (isLoggedIn && token != null) {
                    Toast.makeText(getBaseContext(), "IntroductionMain", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), Profile_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);

        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailAddressEntryPage();
            }
        });

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });
    }

    public void openLoginPage() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void openEmailAddressEntryPage() {
        Intent intent = new Intent(this, RegisterBasicInfo.class);
        startActivity(intent);
    }


}

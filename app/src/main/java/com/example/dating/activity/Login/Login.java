package com.example.dating.activity.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.dating.R;
import com.example.dating.activity.Main.MainActivity;
import com.example.dating.activity.Profile.Profile_Activity;
import com.example.dating.databinding.ActivityLoginBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.service.SocketIOService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;

    private Context mContext;
    private String email, password;
    final User user = new User();
    private String fcmToken = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = Login.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(Login.this).isLoggedIn();
                String token = new SessionManager(Login.this).getAccessToken();
                user.setLatitude(new SessionManager(Login.this).getLatitude());
                user.setLongitude(new SessionManager(Login.this).getLongitude());

                if (isLoggedIn && token != null) {
                    Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
//        FirebaseApp.initializeApp(this);

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//                        fcmToken = task.getResult().getToken();
//                    }
//                });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( Login.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                Log.e("Token",fcmToken);
            }
        });
        init();
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");

        return string.equals("");
    }

    //----------------------------------------Firebase----------------------------------------

    private void init() {
        //initialize the button for logging in
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");

                email = binding.inputEmail.getText().toString();
                password = binding.inputPassword.getText().toString();

                user.setEmail(email);
                user.setPassword(password);
                user.setDeviceModel(Build.MODEL);
                user.setFcmKey(fcmToken);

                requestLogin();
            }
        });


        binding.linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterBasicInfo.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {

    }

    private void requestLogin() {
        RetrofitClient.getNetworkConfiguration().requestPostLogin(user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        new SessionManager(Login.this).setAccessToken(data.getData().getToken());
                        new SessionManager(Login.this).setUniqueIdentifier(data.getData().getUser().getId());
                        new SessionManager(Login.this).setLogin(true);
//                        Intent service = new Intent(getApplicationContext(), SocketIOService.class);
//                        startService(service);

                        Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
                        Toast.makeText(Login.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("errorBody requestPostLogin", errorData.getMessage());
                    } catch (IOException e) {
//                        Log.e("IOException requestPostLogin", e.toString());
                        Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Log.e("onFailure requestPostLogin", t.getMessage());
//                Toast.makeText(Login.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

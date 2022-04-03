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
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.dating.R;
import com.example.dating.Utils.GPS;
import com.example.dating.activity.Main.MainActivity;
import com.example.dating.databinding.ActivityRegisterBasicInfoBinding;
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


public class RegisterBasicInfo extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    ActivityRegisterBasicInfoBinding binding;
    GPS gps;
    private Context mContext;
    private String email, username, password, passwordConfirm;
    private String fcmToken = null;
    private User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_basic_info);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(RegisterBasicInfo.this).isLoggedIn();
                String token = new SessionManager(RegisterBasicInfo.this).getAccessToken();
                if (isLoggedIn && token != null) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);

        mContext = RegisterBasicInfo.this;
        Log.d(TAG, "onCreate: started");

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
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( RegisterBasicInfo.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                Log.e("Token",fcmToken);
            }
        });


        gps = new GPS(getApplicationContext());

        init();
    }

    private void init() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = binding.inputEmail.getText().toString();
                username = binding.inputUsername.getText().toString();
                password = binding.inputPassword.getText().toString();
                passwordConfirm = binding.inputPasswordConfirm.getText().toString();


                user.setName(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setPasswordConfirm(passwordConfirm);
                user.setDeviceModel(Build.MODEL);
                user.setFcmKey(fcmToken);

                requestRegister();
            }
        });
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));

    }

    private void requestRegister() {
        RetrofitClient.getNetworkConfiguration().requestPostRegister(user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    new SessionManager(RegisterBasicInfo.this).setAccessToken(data.getData().getToken());
                    new SessionManager(RegisterBasicInfo.this).setUniqueIdentifier(data.getData().getUser().getId());
                    new SessionManager(RegisterBasicInfo.this).setLogin(true);

//                    Intent service = new Intent(getApplicationContext(), SocketIOService.class);
//                    startService(service);

                    Intent intent = new Intent(RegisterBasicInfo.this, RegisterGender.class);
                    startActivity(intent);
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
                        Toast.makeText(RegisterBasicInfo.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.e("errorData requestPostRegister", errorData.getMessage());
                    } catch (IOException e) {
                        Toast.makeText(RegisterBasicInfo.this, e.toString(), Toast.LENGTH_LONG).show();
//                        Log.e("onFailure requestPostRegister", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(RegisterBasicInfo.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostRegister", t.getMessage());

            }
        });
    }
}

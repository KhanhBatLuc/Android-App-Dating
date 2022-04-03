package com.example.dating.activity.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.dating.R;
import com.example.dating.databinding.ActivityRegisterGenderPrefectionBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterGenderPrefection extends AppCompatActivity {
    ActivityRegisterGenderPrefectionBinding binding;

    private User user = new User();
    private boolean preferMale = true;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_gender_prefection);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(RegisterGenderPrefection.this).isLoggedIn();
                token = new SessionManager(RegisterGenderPrefection.this).getAccessToken();
            }
        }, 1500);
        //By default male has to be selected so below code is added

        binding.femaleSelectionButton.setAlpha(.5f);
        binding.femaleSelectionButton.setBackgroundColor(Color.GRAY);


        binding.maleSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleButtonSelected();
            }
        });

        binding.femaleSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleButtonSelected();
            }
        });

        binding.preferenceContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgeEntryPage();
            }
        });


    }

    public void maleButtonSelected() {
        preferMale = true;
        binding.maleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
        binding.maleSelectionButton.setAlpha(1.0f);
        binding.femaleSelectionButton.setAlpha(.5f);
        binding.femaleSelectionButton.setBackgroundColor(Color.GRAY);
    }

    public void femaleButtonSelected() {
        preferMale = false;
        binding.femaleSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
        binding.femaleSelectionButton.setAlpha(1.0f);
        binding.maleSelectionButton.setAlpha(.5f);
        binding.maleSelectionButton.setBackgroundColor(Color.GRAY);
    }

    public void openAgeEntryPage() {
        String preferSex = preferMale ? "Nam" : "Nữ";
        user.setPreferSex(preferSex);

        requestProfile();
    }

    private void requestProfile() {
        RetrofitClient.getNetworkConfiguration().requestPostProfile("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {

                    Intent intent = new Intent(getApplicationContext(), RegisterAge.class);
                    startActivity(intent);
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(RegisterGenderPrefection.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(RegisterGenderPrefection.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostProfile", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(RegisterGenderPrefection.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostProfile", t.getMessage());

            }
        });
    }
}

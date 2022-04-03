package com.example.dating.activity.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.dating.R;
import com.example.dating.activity.Main.MainActivity;
import com.example.dating.activity.Profile.Profile_Activity;
import com.example.dating.databinding.ActivityRegisterHobbyBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterHobby extends AppCompatActivity {
    private static final String TAG = "RegisterHobby";
    ActivityRegisterHobbyBinding binding;
    private Context mContext;
    private User user = new User();
    List<String> interests = new ArrayList<>();
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_hobby);
        mContext = RegisterHobby.this;

        Log.d(TAG, "onCreate: started");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(RegisterHobby.this).isLoggedIn();
                token = new SessionManager(RegisterHobby.this).getAccessToken();
                user.setLatitude(new SessionManager(RegisterHobby.this).getLatitude());
                user.setLongitude(new SessionManager(RegisterHobby.this).getLongitude());
            }
        }, 1500);

        initWidgets();

        init();
    }

    private void initWidgets() {

        // Initially all the buttons needs to be grayed out so this code is added, on selection we will enable it later
        binding.sportsSelectionButton.setAlpha(.5f);
        binding.sportsSelectionButton.setBackgroundColor(Color.GRAY);

        binding.travelSelectionButton.setAlpha(.5f);
        binding.travelSelectionButton.setBackgroundColor(Color.GRAY);

        binding.musicSelectionButton.setAlpha(.5f);
        binding.musicSelectionButton.setBackgroundColor(Color.GRAY);

        binding.fishingSelectionButton.setAlpha(.5f);
        binding.fishingSelectionButton.setBackgroundColor(Color.GRAY);


        binding.sportsSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportsButtonClicked();
            }
        });

        binding.travelSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelButtonClicked();
            }
        });

        binding.musicSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artButtonClicked();
            }
        });

        binding.fishingSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieButtonClicked();
            }
        });


    }

    public void sportsButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (binding.sportsSelectionButton.getAlpha() == 1.0f) {
            binding.sportsSelectionButton.setAlpha(.5f);
            binding.sportsSelectionButton.setBackgroundColor(Color.GRAY);
//            userInfo.setSports(false);

        } else {
            binding.sportsSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            binding.sportsSelectionButton.setAlpha(1.0f);
//            userInfo.setSports(true);
            interests.add("Thể thao");
        }
    }

    public void travelButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (binding.travelSelectionButton.getAlpha() == 1.0f) {
            binding.travelSelectionButton.setAlpha(.5f);
            binding.travelSelectionButton.setBackgroundColor(Color.GRAY);
//            userInfo.setTravel(false);
        } else {
            binding.travelSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            binding.travelSelectionButton.setAlpha(1.0f);
//            userInfo.setTravel(true);
            interests.add("Du lịch");
        }

    }

    public void artButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (binding.musicSelectionButton.getAlpha() == 1.0f) {
            binding.musicSelectionButton.setAlpha(.5f);
            binding.musicSelectionButton.setBackgroundColor(Color.GRAY);
//            userInfo.setMusic(false);
        } else {
            binding.musicSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            binding.musicSelectionButton.setAlpha(1.0f);
//            userInfo.setMusic(true);
            interests.add("Hội họa");
        }

    }

    public void movieButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (binding.fishingSelectionButton.getAlpha() == 1.0f) {
            binding.fishingSelectionButton.setAlpha(.5f);
            binding.fishingSelectionButton.setBackgroundColor(Color.GRAY);
//            userInfo.setFishing(false);
        } else {
            binding.fishingSelectionButton.setBackgroundColor(Color.parseColor("#FF4081"));
            binding.fishingSelectionButton.setAlpha(1.0f);
//            userInfo.setFishing(true);
            interests.add("Điện ảnh");
        }

    }

    public void init() {
        binding.hobbiesContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setInterests(interests);
                requestProfile();

            }
        });
    }


    private void requestProfile() {
        RetrofitClient.getNetworkConfiguration().requestPostProfile("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {

                    Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                    startActivity(intent);
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(RegisterHobby.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        e.printStackTrace();
                        Log.e("IOException requestPostProfile", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(RegisterHobby.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostProfile", t.getMessage());

            }
        });
    }


    //----------------------------------------Firebase----------------------------------------


}

package com.example.dating.activity.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.dating.databinding.ActivityBtnLikeBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Match;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dating.R;
import com.example.dating.ui.TopNavigationViewHelper;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BtnLikeActivity extends AppCompatActivity {
    private static final String TAG = "BtnLikeActivity";
    private ActivityBtnLikeBinding binding;
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = BtnLikeActivity.this;
    private ImageView like;
    private String token;
    private Match match;
    private String profileUrl, id, notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_btn_like);

        setupTopNavigationView();

        Intent intent = getIntent();
        profileUrl = intent.getStringExtra("url");
        id = intent.getStringExtra("id");
        notify = intent.getStringExtra("notify");

        if (id != null)
            match = new Match(id, MainActivity.MATCH_TYPE_WAIT);
        if (notify != null)
            match = new Match(id, MainActivity.MATCH_TYPE_MATCH);

        Glide.with(mContext).load(profileUrl).into(binding.like);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(BtnLikeActivity.this).getAccessToken();
                if (notify == null)
                    requestPostMatch();
                else
                    requestPostUpdateMatch();
            }
        }, 500);
    }

    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationView tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void requestPostMatch() {
        RetrofitClient.getNetworkConfiguration().requestPostMatch("Bearer " + token, match).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Intent mainIntent = new Intent(BtnLikeActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                            }
                        }).start();
                        Log.e("BtnDislikeActivity requestPostMatch: ", data.getMessage());
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(BtnLikeActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorData requestPostMatch", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(BtnLikeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostMatch", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(BtnLikeActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostMatch", t.getMessage());
            }
        });
    }

    private void requestPostUpdateMatch() {
        RetrofitClient.getNetworkConfiguration().requestPostUpdateMatch("Bearer " + token, match).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Intent mainIntent = new Intent(BtnLikeActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                            }
                        }).start();
                        Log.e("BtnDislikeActivity requestPostMatch: ", data.getMessage());
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(BtnLikeActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onFailure requestPostUpdateMatch", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(BtnLikeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("onFailure requestPostUpdateMatch", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(BtnLikeActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostUpdateMatch", t.getMessage());
            }
        });
    }
}

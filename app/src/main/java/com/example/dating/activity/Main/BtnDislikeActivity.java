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
import com.example.dating.databinding.ActivityBtnDislikeBinding;
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


public class BtnDislikeActivity extends AppCompatActivity {
    private static final String TAG = "BtnDislikeActivity";
    private ActivityBtnDislikeBinding binding;
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = BtnDislikeActivity.this;
    private ImageView dislike;
    private Match match;
    private String token;
    private String profileUrl, id, notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_btn_dislike);

        setupTopNavigationView();

        Intent intent = getIntent();
        profileUrl = intent.getStringExtra("url");
        id = intent.getStringExtra("id");
        notify = intent.getStringExtra("notify");
        if (id != null)
            match = new Match(id, MainActivity.MATCH_TYPE_UNMATCH);
        if (notify != null)
            match = new Match(id, MainActivity.MATCH_TYPE_MATCH);

        Glide.with(mContext).load(profileUrl).into(binding.dislike);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(BtnDislikeActivity.this).getAccessToken();
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

                                Intent mainIntent = new Intent(BtnDislikeActivity.this, MainActivity.class);
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
//                        Toast.makeText(BtnDislikeActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostMatch", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(BtnDislikeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostMatch", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(BtnDislikeActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("errorBody requestPostMatch", t.getMessage());
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

                                Intent mainIntent = new Intent(BtnDislikeActivity.this, MainActivity.class);
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
//                        Toast.makeText(BtnDislikeActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostUpdateMatch", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(BtnDislikeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostUpdateMatch", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(BtnDislikeActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostUpdateMatch", t.getMessage());
            }
        });
    }
}

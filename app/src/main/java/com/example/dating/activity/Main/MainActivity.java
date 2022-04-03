package com.example.dating.activity.Main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dating.Utils.NotificationHelper;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.ActivityMainBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Match;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
//import com.example.dating.service.SocketIOService;
import com.example.dating.ui.PulsatorLayout;
import com.example.dating.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.example.dating.R;
import com.example.dating.ui.TopNavigationViewHelper;
import com.example.dating.adapter.PhotoAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    public static final int MATCH_TYPE_MATCH = 1, MATCH_TYPE_UNMATCH = 0, MATCH_TYPE_WAIT = 2;
    private static final int ACTIVITY_NUM = 1;
    private static final int REQUEST_LOCATION = 1;
    public List<User> rowItems = new ArrayList<>();
    private Context mContext = MainActivity.this;
    private PhotoAdapter arrayAdapter;
    private String token;
    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // start pulsator
        binding.pulsator.start();
        setupTopNavigationView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(MainActivity.this).getAccessToken();
                requestGetUserList();
            }
        }, 500);

    }


    private void checkRowItem() {
        if (rowItems.isEmpty()) {
            binding.moreFrame.setVisibility(View.VISIBLE);
            binding.cardFrame.setVisibility(View.GONE);
        }
    }


    private void updateSwipeCard() {
        binding.frame.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
//                rowItems.remove(0);
//                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                User obj = (User) dataObject;
                match = new Match(obj.getId(), MATCH_TYPE_UNMATCH);
                requestPostMatch();
                checkRowItem();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                User obj = (User) dataObject;
                match = new Match(obj.getId(), MATCH_TYPE_WAIT);
                requestPostMatch();
                checkRowItem();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here


            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = binding.frame.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

    }


    public void DislikeBtn(View v) {
        if (rowItems.size() != 0) {
            User user = rowItems.get(0);
            match = new Match(user.getId(), MATCH_TYPE_UNMATCH);
            requestPostMatch();

            Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
            btnClick.putExtra("url", AppConfig.IMAGE_URL + user.getAvatar());
            startActivity(btnClick);
        }
    }

    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {
            User user = rowItems.get(0);
            match = new Match(user.getId(), MATCH_TYPE_WAIT);
            requestPostMatch();
//            joinMatch();

            Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
            btnClick.putExtra("url", AppConfig.IMAGE_URL + user.getAvatar());
            startActivity(btnClick);
        }
    }


    /**
     * setup top tool bar
     */
    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationView tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    @Override
    public void onBackPressed() {

    }


    private void requestGetUserList() {
        RetrofitClient.getNetworkConfiguration().requestGetUserList("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        rowItems = data.getData().getUsersList();
                        arrayAdapter = new PhotoAdapter(mContext, R.layout.item, rowItems);
                        binding.frame.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                        checkRowItem();
                        updateSwipeCard();
                        Log.e("rowItems", rowItems.size() + "");
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
//                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(MainActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetUserList", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestGetUserList", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestGetUserList", t.getMessage());
            }
        });
    }


    private void requestPostMatch() {
        RetrofitClient.getNetworkConfiguration().requestPostMatch("Bearer " + token, match).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        rowItems.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                        Log.e("requestPostMatch: ", data.getMessage());
                        checkRowItem();

                        match = data.getData().getMatch();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
//                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(MainActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostMatch", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostMatch", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostMatch", t.getMessage());
            }
        });
    }

}

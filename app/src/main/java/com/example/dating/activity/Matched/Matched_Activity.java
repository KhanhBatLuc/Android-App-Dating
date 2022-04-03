package com.example.dating.activity.Matched;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating.activity.SearchActivity;
import com.example.dating.adapter.MatchAdapter;
import com.example.dating.adapter.RoomAdapter;
import com.example.dating.databinding.ActivityMatchedBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Room;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.service.SocketIOService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dating.R;
import com.example.dating.ui.TopNavigationViewHelper;
import com.example.dating.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Matched_Activity extends AppCompatActivity {
    ActivityMatchedBinding binding;
    public static final String ON_CHANGE_DATA_RECEIVER = "com.example.dating.activity.Matched.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "com.example.dating.activity.Matched.ON_SOCKET_CONNECTION";

    private static final String TAG = "Matched_Activity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext = Matched_Activity.this;
    private List<User> usersMatchedList = new ArrayList<>();
    private List<Room> roomList = new ArrayList<>();
    private MatchAdapter mAdapter;
    private RoomAdapter rAdapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_matched);

        init();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(Matched_Activity.this).getAccessToken();
                prepareMatchedData();
                prepareRoomChatData();

                Intent service = new Intent(mContext, SocketIOService.class);
                startService(service);
            }
        }, 500);

        setupTopNavigationView();
//        searchFunc();

        binding.searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(starter);
            }
        });

    }

    public static void start(Context context) {
        Intent starter = new Intent(context, Matched_Activity.class);
        context.startActivity(starter);
    }

    private void init() {
        mAdapter = new MatchAdapter(usersMatchedList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        binding.activeRecyclerView.setLayoutManager(mLayoutManager);
        binding.activeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.activeRecyclerView.setAdapter(mAdapter);

        rAdapter = new RoomAdapter(roomList, this);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        binding.matcheRecyclerView.setLayoutManager(mLayoutManager1);
        binding.matcheRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.matcheRecyclerView.setAdapter(rAdapter);
    }

    private BroadcastReceiver onChangeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onChangeDataReceiver ", "onChangeDataReceiver");
            Gson gson = new Gson();
            Room r = gson.fromJson(intent.getExtras().getString("room"), Room.class);
            for (int i = 0; i < rAdapter.getRoomList().size(); i++) {
                if (rAdapter.getRoomList().get(i).getId().equals(r.getId())) {
                    Room room = rAdapter.getRoomList().get(i);
                    room.setLastMessage(r.getLastMessage());

                    User user = r.getUser();
                    if (user.getId().equalsIgnoreCase(new SessionManager(Matched_Activity.this).getUniqueIdentifier())) {
                        user = r.getAdmin();
                        room.setAdmin(user);
                    } else
                        room.setUser(user);
                    rAdapter.getRoomList().set(i, room);

//                    user.setLastSeen(r.getUser().getLastSeen());
                    rAdapter.notifyDataSetChanged();

                    break;
                }
            }

        }
    };

    private BroadcastReceiver onSocketConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getExtras().getBoolean("status");
            Log.e("isConnected ", isConnected ? "true" : "false");
//            binding.txtToolbarTitle.setText(isConnected ? "Connected" : "Connecting...");

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(onChangeDataReceiver, new IntentFilter(ON_CHANGE_DATA_RECEIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(onSocketConnect, new IntentFilter(ON_SOCKET_CONNECTION));
        if (rAdapter != null) rAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onChangeDataReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSocketConnect);
    }


    private void prepareMatchedData() {
        Log.e("prepareMatchedData token ", "Bearer " + token);
        RetrofitClient.getNetworkConfiguration().requestGetUserMatched("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        List<User> list = data.getData().getUsersList();
                        if (list != null) {
                            usersMatchedList.addAll(list);
                            mAdapter.notifyDataSetChanged();
                        }
//                        Log.e("usersMatchedList ", usersMatchedList == null ? "null" : usersMatchedList.toString());
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(Matched_Activity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetUserMatched", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(Matched_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestGetUserMatched", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(Matched_Activity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestGetUserMatched", t.getMessage());
            }
        });

    }

    private void prepareRoomChatData() {
        Log.e("prepareRoomChatData token ", "Bearer " + token);
        RetrofitClient.getNetworkConfiguration().requestGetRooms("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        List<Room> list = response.body().getData().getRoomList();
                        if (list != null) {
                            roomList.addAll(list);
                            rAdapter.notifyDataSetChanged();
                        }
//                        Log.e("roomList ", roomList == null ? "null" : roomList.toString());
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody roomList", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(Matched_Activity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody usersMatchedList", err);
                    } catch (IOException e) {
//                        Toast.makeText(Matched_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody usersMatchedList", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(Matched_Activity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("errorBody usersMatchedList", t.getMessage());
            }
        });
    }

    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationView tvEx = findViewById(R.id.topNavViewBar);
//        tvEx.getOrCreateBadge(R.id.ic_profile).setNumber(2);

        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}

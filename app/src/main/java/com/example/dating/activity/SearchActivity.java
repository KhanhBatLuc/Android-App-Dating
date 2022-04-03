package com.example.dating.activity;

import com.example.dating.R;
import com.example.dating.adapter.AdapterUser;
import com.example.dating.databinding.ActivitySearchUserBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements TextWatcher {
   private ActivitySearchUserBinding binding;
   private List<User> userList;
   private RecyclerView myRecylerView;
   private AdapterUser adapterUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_search_user);
        binding.edSearch.addTextChangedListener(this);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerConfiguration();
    }

    private void recyclerConfiguration(){
        myRecylerView = binding.recyclerUser;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void requestGetUsers(String query){
        RetrofitClient.getNetworkConfiguration().requestGetUserName("Bearer " + new SessionManager(this).getAccessToken() , query).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body().getData().getUsersList();
                    userList = new ArrayList<>();
                    userList.addAll(users);
                    Log.e("userList requestGetUserName", userList.toString());
                    // add the new updated list to the adapter
                    // notify the adapter to update the recycler view
                    adapterUser = new AdapterUser(userList, SearchActivity.this);
                    myRecylerView.setAdapter(adapterUser);
                  } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
//                        Toast.makeText(SearchActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("errorBody requestGetUserName", errorData.getMessage());
                    } catch (IOException e) {
//                        e.printStackTrace();
                        Log.e("IOException requestGetUserName", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure requestGetUserName", t.getMessage());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = binding.edSearch.getText().toString();
        if (text.length() > 2){
            requestGetUsers(text);
        }
    }
}

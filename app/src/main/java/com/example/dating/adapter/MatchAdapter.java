package com.example.dating.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.MatchUserItemBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyViewHolder> {
    List<User> usersList;
    Activity activity;

    public MatchAdapter(List<User> usersList, Activity activity) {
        this.usersList = usersList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MatchUserItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.match_user_item, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.binding.matchName.setText(user.getName());
        Picasso.get().load(AppConfig.IMAGE_URL + user.getAvatar()).into(holder.binding.matchImage);

        holder.binding.matchStatus.setVisibility(user.getLastSeen() == 0 ? View.VISIBLE : View.GONE);

    }

    public List<User> getUserList() {
        return this.usersList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MatchUserItemBinding binding;

        public MyViewHolder(@NonNull MatchUserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            requestPostCreateRoom();
        }

        private void requestPostCreateRoom() {
            Room room = new Room();
            room.setUser(usersList.get(getItemViewType()));
            RetrofitClient.getNetworkConfiguration().requestPostCreateRoom("Bearer " + new SessionManager(activity).getAccessToken(), room).enqueue(new Callback<ResponseMain>() {
                @Override
                public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        String room = gson.toJson(response.body().getData().getRoom(), Room.class);
                        ChatBoxActivity.start(activity, room);
                        activity.finish();
                    } else {
                        try {
                            Gson gson = new Gson();
                            ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
//                            Toast.makeText(activity, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("errorBody requestPostCreateRoom", errorData.getMessage());
                        } catch (IOException e) {
                            Log.e("IOException requestPostCreateRoom", e.toString());
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMain> call, Throwable t) {
//                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e("onFailure requestPostCreateRoom", t.getMessage());
                }
            });
        }
    }
}

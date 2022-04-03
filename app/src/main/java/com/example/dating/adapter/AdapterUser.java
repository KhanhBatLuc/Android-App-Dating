package com.example.dating.adapter;

import android.app.Activity;
import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.CustomRowUserBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.databinding.CustomRowUserBinding;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {

    private List<User> list;
    private Activity activity;

    public AdapterUser(List<User> list, Activity activity) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomRowUserBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.custom_row_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User user = list.get(position);
        holder.binding.txtName.setText(user.getName());
        holder.binding.txtStatus.setText(user.getGetLastSeen());
        Picasso.get().load(AppConfig.IMAGE_URL + user.getAvatar()).into(holder.binding.imgAvatar);
        if(user.getLastSeen() == 0)
            holder.binding.status.setVisibility(View.VISIBLE);
        else
            holder.binding.status.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomRowUserBinding binding;

        ViewHolder(@NonNull CustomRowUserBinding binding) {
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
            room.setUser(list.get(getItemViewType()));
            RetrofitClient.getNetworkConfiguration().requestPostCreateRoom("Bearer " + new SessionManager(activity).getAccessToken(), room).enqueue(new Callback<ResponseMain>() {
                @Override
                public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        String room = gson.toJson(response.body().getData().getRoom() , Room.class);
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

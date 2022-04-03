package com.example.dating.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating.R;
import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.RoomItemBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    List<Room> roomsList;
    Activity activity;

    public RoomAdapter(List<Room> roomsList, Activity activity) {
        this.roomsList = roomsList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RoomItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.room_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = roomsList.get(position);
        User user = room.getAdmin();

        if (user.getId().equalsIgnoreCase(new SessionManager(activity).getUniqueIdentifier()))
            user = room.getUser();
//        Log.e("user RoomAdapter",user.toString() );

        holder.binding.roomName.setText(user.getName());
        holder.binding.lastMessage.setText(room.getLastMessage().getContent());
        Picasso.get().load(AppConfig.IMAGE_URL + user.getAvatar()).into(holder.binding.roomImage);


        holder.binding.viewNewMessage.setVisibility(user.getLastSeen() == 0 ? View.VISIBLE : View.GONE);
    }

    public List<Room> getRoomList() {
        return this.roomsList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoomItemBinding binding;

        public MyViewHolder(@NonNull RoomItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Gson gson = new Gson();
            String room = gson.toJson(roomsList.get(getItemViewType()), Room.class);
            ChatBoxActivity.start(activity, room);
        }
    }
}

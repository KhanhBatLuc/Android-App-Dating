package com.example.dating.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.dating.R;
import com.example.dating.activity.Main.ProfileCheckinMain;
import com.example.dating.application.AppConfig;
import com.example.dating.model.User;

import java.util.List;


public class PhotoAdapter extends ArrayAdapter<User> {
    Context mContext;


    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);
//        CardView cardView = convertView.findViewById(R.id.cardView);
        ImageButton btnInfo = convertView.findViewById(R.id.checkInfoBeforeMatched);

        Log.e("PhotoAdapter user", AppConfig.IMAGE_URL + user.getAvatar());
        Glide.with(mContext).load(AppConfig.IMAGE_URL + user.getAvatar()).into(image);
        name.setText(user.getName() + ", " + user.getAge() + " tuổi");

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileCheckinMain.class);
                intent.putExtra("id", user.getId());
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }
}

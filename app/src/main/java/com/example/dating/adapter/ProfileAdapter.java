package com.example.dating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.dating.R;
import com.example.dating.model.User;

import java.util.List;


public class ProfileAdapter extends ArrayAdapter<User> {
    private int resourceId;
    private Context mContext;

    public ProfileAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        //improve the efficiency
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.personPic = view.findViewById(R.id.person_image);
            viewHolder.personName = view.findViewById(R.id.person_name);
            viewHolder.imageButton = view.findViewById(R.id.videoCalBtn);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        String profileImageUrl = user.getAvatar();
        switch (profileImageUrl) {
            case "defaultFemale":
                Glide.with(mContext).load(R.drawable.default_woman).into(viewHolder.personPic);
                break;
            case "defaultMale":
                Glide.with(mContext).load(R.drawable.default_man).into(viewHolder.personPic);
                break;
            default:
                Glide.with(mContext).load(profileImageUrl).into(viewHolder.personPic);
                break;
        }
        viewHolder.personName.setText(user.getName());
        viewHolder.imageButton.setFocusable(false);

        return view;
    }


    class ViewHolder {
        ImageView personPic;
        TextView personName;
        ImageButton imageButton;
    }
}

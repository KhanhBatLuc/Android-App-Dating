package com.example.dating.activity.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.dating.R;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.ActivityProfileCheckinMainBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Photo;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.ui.SquareImageView;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileCheckinMain extends AppCompatActivity {
    private ActivityProfileCheckinMainBinding binding;
    private Context mContext;
    private String token, id, distance;
    private User user = new User();
    private List<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_checkin_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        user.setId(id);


        mContext = ProfileCheckinMain.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(ProfileCheckinMain.this).getAccessToken();
                requestGetProfile();
            }
        }, 500);

        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeBtn();
            }
        });
        binding.dislikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DislikeBtn();
            }
        });

    }

    void detail() {
        Glide.with(mContext).load(AppConfig.IMAGE_URL + user.getAvatar()).into(binding.profileImage);
        binding.nameMain.setText(user.getName() + ", " + user.getAge() + " tuổi");
        binding.distanceMain.setText("Cách " + distance + " Km");

        binding.bioBeforematch.setText(user.getDescription());
        binding.interestsBeforematch.setText(user.getInterests());
        binding.AddressMatch.setText(user.getAddress());
        binding.HeightMatch.setText(user.getHeight());
        binding.CountryMatch.setText(user.getCountry());
        binding.WeightMatch.setText(user.getWeight());
        binding.LiteracyMatch.setText(user.getLiteracy());
    }

    public void DislikeBtn() {

        Intent btnClick = new Intent(mContext, BtnDislikeActivity.class);
        btnClick.putExtra("url", AppConfig.IMAGE_URL + user.getAvatar());
        btnClick.putExtra("id", user.getId());
        startActivity(btnClick);
        finish();

    }

    public void LikeBtn() {
        Intent btnClick = new Intent(mContext, BtnLikeActivity.class);
        btnClick.putExtra("url", AppConfig.IMAGE_URL + user.getAvatar());
        btnClick.putExtra("id", user.getId());
        startActivity(btnClick);
        finish();

    }


    private void requestGetProfile() {
        RetrofitClient.getNetworkConfiguration().requestGetUserDetail("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        distance = data.getData().getDistance();
                        user = data.getData().getUser();
                        photoList = data.getData().getPhotoList();
                        detail();
                        getPhotos();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(mContext, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetUserDetail", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetUserDetail", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(mContext, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestGetUserDetail", t.getMessage());
            }
        });
    }

    private void getPhotos() {
        for (Photo p : photoList) {

            CardView cardView = new CardView(this);
            cardView.setCardElevation(4);
            cardView.setLayoutParams(new CardView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            ImageView imageView = new SquareImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            cardView.addView(imageView);
            Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(imageView);
            binding.info.addView(cardView);


            LinearLayout linearLayout= new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30));
            binding.info.addView(linearLayout);
        }
    }

}

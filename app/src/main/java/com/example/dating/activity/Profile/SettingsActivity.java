package com.example.dating.activity.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;

import com.example.dating.activity.ChatBoxActivity;
import com.example.dating.activity.IntroductionMain;
import com.example.dating.R;
import com.example.dating.activity.Matched.Matched_Activity;
import com.example.dating.activity.SplashActivity;
import com.example.dating.databinding.ActivitySettingsBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.service.SocketIOService;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    ActivitySettingsBinding binding;
    private String token = null;
    private TextView toolbartag;
    private User user;
    private Boolean begin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                token = new SessionManager(SettingsActivity.this).getAccessToken();
                requestGetProfile();
            }
        }, 500);

        toolbartag = findViewById(R.id.toolbartag);
        toolbartag.setText("Cài đặt tài khoản");

        binding.distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.distanceText.setText(progress + " Km");
                user.setInterestDistance(progress);
                Log.e("binding.distance.setOnSeekBarChangeListener", "binding.distance.setOnSeekBarChangeListener");

                requestPostProfile();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.switchMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    man();
                    user.setPreferSex("Nam");
                    Log.e("binding.switchMan.setOnCheckedChangeListener", "binding.switchMan.setOnCheckedChangeListener");

                    requestPostProfile();

                }
            }
        });
        binding.switchWoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    woman();
                    user.setPreferSex("Nữ");
                    Log.e("binding.switchWoman.setOnCheckedChangeListener", "binding.switchWoman.setOnCheckedChangeListener");

                    requestPostProfile();
                }
            }
        });
        binding.rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                binding.ageRange.setText(minValue + " - " + maxValue);
                user.setInterestAgeMax(Integer.parseInt(maxValue.toString()));
                user.setInterestAgeMin(Integer.parseInt(minValue.toString()));
                Log.e("binding.rangeSeekbar.setOnRangeSeekBarChangeListener", "binding.rangeSeekbar.setOnRangeSeekBarChangeListener");

                requestPostProfile();
            }
        });

        binding.alertMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.alertMessage.setChecked(true);
                    user.setAlertMessage(true);
                } else
                    user.setAlertMessage(false);
                Log.e("alertMessage isChecked", isChecked + " ");
                Log.e("binding.alertMessage.setOnCheckedChangeListener", "binding.alertMessage.setOnCheckedChangeListener");

                requestPostProfile();
            }
        });

        binding.alertLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.alertLike.setChecked(true);
                    user.setAlertLiked(true);
                } else
                    user.setAlertLiked(false);
                Log.e("alertLike isChecked", isChecked + " ");
                Log.e("binding.alertLike.setOnCheckedChangeListener", "binding.alertLike.setOnCheckedChangeListener");

                requestPostProfile();
            }
        });

        binding.alertMatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.alertMatch.setChecked(true);
                    user.setAlertMatch(true);
                } else
                    user.setAlertMatch(false);
                Log.e("alertMatch isChecked", isChecked + " ");
                Log.e("binding.alertMatch.setOnCheckedChangeListener", "binding.alertMatch.setOnCheckedChangeListener");

                requestPostProfile();
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogout();
            }
        });

        binding.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlert();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                begin = false;
            }
        }, 1500);
    }


    private void requestGetProfile() {
        RetrofitClient.getNetworkConfiguration().requestGetProfile("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {

                        user = data.getData().getUser();

                        if (user.getPreferSex().equalsIgnoreCase("Nam"))
                            man();
                        else
                            woman();

                        if (user.getInterestAgeMax() != 0 && user.getInterestAgeMin() != 0) {
                            binding.rangeSeekbar.setSelectedMaxValue(user.getInterestAgeMax());
                            binding.rangeSeekbar.setSelectedMinValue(user.getInterestAgeMin());
                            binding.ageRange.setText(user.getInterestAgeMin() + " - " + user.getInterestAgeMax());
                        }

                        if (user.getInterestDistance() != 0) {
                            binding.distance.setProgress(user.getInterestDistance());
                            binding.distanceText.setText(user.getInterestDistance() + " Km");
                        }

                        if (user.isAlertMatch())
                            binding.alertMatch.setChecked(true);
                        else
                            binding.alertMatch.setChecked(false);

                        if (user.isAlertLiked())
                            binding.alertLike.setChecked(true);
                        else
                            binding.alertLike.setChecked(false);

                        if (user.isAlertMessage())
                            binding.alertMessage.setChecked(true);
                        else
                            binding.alertMessage.setChecked(false);


                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(SettingsActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestGetProfile", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(SettingsActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestGetProfile", t.getMessage());
            }
        });
    }


    private void requestLogout() {
        Log.e("requestPostLogout", "Bearer " + token);
        RetrofitClient.getNetworkConfiguration().requestPostLogout("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    new SessionManager(SettingsActivity.this).setAccessToken(null);
                    new SessionManager(SettingsActivity.this).setUniqueIdentifier(null);
                    new SessionManager(SettingsActivity.this).setLogin(false);

                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    try {
                        String err = response.errorBody().string();
//                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(SettingsActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostLogout", errorData.getMessage());

                        new SessionManager(SettingsActivity.this).setAccessToken(null);
                        new SessionManager(SettingsActivity.this).setUniqueIdentifier(null);
                        new SessionManager(SettingsActivity.this).setLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
//                        Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostLogout", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(SettingsActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostLogout", t.getMessage());

            }
        });
    }


    private void requestDeleteAccount() {
        RetrofitClient.getNetworkConfiguration().requestPostDeleteAccount("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    new SessionManager(SettingsActivity.this).setAccessToken(null);
                    new SessionManager(SettingsActivity.this).setUniqueIdentifier(null);
                    new SessionManager(SettingsActivity.this).setLogin(false);

                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(SettingsActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(SettingsActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("errorBody", t.getMessage());

            }
        });
    }


    private void requestPostProfile() {
        Log.e("requestPostProfile: user", user.toString());
        if (!begin)
            RetrofitClient.getNetworkConfiguration().requestPostProfile("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
                @Override
                public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                    if (response.isSuccessful()) {
                        ResponseMain data = response.body();
                        if (data.getStatus() == 1) {
                            user = data.getData().getUser();
//
//                            stopService(new Intent(Matched_Activity.class, SocketIOService.class ) );
//                            stopService(new Intent(ChatBoxActivity.class, SocketIOService.class ) );

                            Intent service = new Intent(getApplicationContext(), SocketIOService.class);
                            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_LOG_OUT);
                            startService(service);

                            Toast.makeText(SettingsActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    } else {
                        try {
                            String err = response.errorBody().string();
//                            Log.e("errorBody", err);
                            Gson gson = new Gson();
                            ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                            Toast.makeText(SettingsActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("errorBody requestPostProfile", errorData.getMessage());
                        } catch (IOException e) {
//                            Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            Log.e("IOException requestPostProfile", e.toString());
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseMain> call, Throwable t) {
//                    Toast.makeText(SettingsActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("onFailure requestPostProfile", t.getMessage());

                }
            });
    }


    private void displayAlert() {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Cảnh báo")
                .setMessage("Bạn chắc chắn muốn xóa tài khoản? Tài khoản sau khi xóa sẽ không thể phục hồi!")
                .setIcon(R.drawable.logo)
                .setPositiveButton("Tôi chắc chắn",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                requestDeleteAccount();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }


    private void man() {
        binding.manText.setTextColor(R.color.colorPrimaryDark);
        binding.womanText.setTextColor(R.color.black);
        binding.switchMan.setChecked(true);
        binding.switchWoman.setChecked(false);
    }

    private void woman() {
        binding.womanText.setTextColor(R.color.colorPrimaryDark);
        binding.manText.setTextColor(R.color.black);
        binding.switchWoman.setChecked(true);
        binding.switchMan.setChecked(false);
    }
}

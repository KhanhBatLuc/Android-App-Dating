package com.example.dating.activity.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.dating.R;
import com.example.dating.databinding.ActivityRegisterAgeBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterAge extends AppCompatActivity {
    ActivityRegisterAgeBinding binding;

    private String token;
    private User user = new User();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
    // age limit attribute
    private int ageLimit = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_age);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(RegisterAge.this).isLoggedIn();
                token = new SessionManager(RegisterAge.this).getAccessToken();
            }
        }, 1500);


        binding.ageContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHobbiesEntryPage();
            }
        });


    }

    public void openHobbiesEntryPage() {
        int age = getAge(binding.ageSelectionPicker.getYear(), binding.ageSelectionPicker.getMonth(), binding.ageSelectionPicker.getDayOfMonth());

        // if user is above 13 years old then only he/she will be allowed to register to the system.
        if (age > ageLimit) {
            // code for converting date to string
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, binding.ageSelectionPicker.getYear());
            cal.set(Calendar.MONTH, binding.ageSelectionPicker.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, binding.ageSelectionPicker.getDayOfMonth());
            Date dateOfBirth = cal.getTime();
            String strDateOfBirth = dateFormatter.format(dateOfBirth);

            // code to set the dateOfBirthAttribute.
            user.setBirth(strDateOfBirth);
            requestProfile();

        } else {
            Toast.makeText(getApplicationContext(), "Ứng dụng chỉ dành cho người trên " + ageLimit + " tuổi!!!", Toast.LENGTH_SHORT).show();
        }

    }

    // method to get the current age of the user.
    private int getAge(int year, int month, int day) {
        Calendar dateOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dateOfBirth.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }


    private void requestProfile() {
        RetrofitClient.getNetworkConfiguration().requestPostProfile("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {

                    Intent intent = new Intent(getApplicationContext(), RegisterHobby.class);
                    startActivity(intent);
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(RegisterAge.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorData requestPostLogin", errorData.getMessage());
                    } catch (IOException e) {
                        Log.e("onFailure requestPostLogin", e.toString());
//                        Toast.makeText(RegisterAge.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
                Log.e("onFailure requestPostLogin", t.getMessage());
//                Toast.makeText(RegisterAge.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}

package com.example.dating.activity;

import com.example.dating.R;
import com.example.dating.activity.Login.Login;
import com.example.dating.activity.Main.MainActivity;
import com.example.dating.activity.Profile.Profile_Activity;
import com.example.dating.databinding.ActivitySplashBinding;
import com.example.dating.dataholder.SessionManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.dating.activity.Main.MainActivity;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.service.SocketIOService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    final private int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private String latitude, longitude;
    private User user = new User();
    private String token;
    private FusedLocationProviderClient mFusedLocationClient;
    private String la, lo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.get("room") != null) {

            ChatBoxActivity.start(getApplicationContext(), bundle.get("room").toString());
            finish();

//            Log.e("activity SplashActivity", activity);
//            try {
//                Class<?> myClass = Class.forName(activity);
//                Intent intent = new Intent(getApplicationContext(), myClass);
//                intent.putExtra("room", data);
//                startActivity(intent);
//            } catch (ClassNotFoundException e) {
//                Log.e("ClassNotFoundException SplashActivity", e.toString());
//            }
//            Log.e("data SplashActivity", data);
//            Toast.makeText(SplashActivity.this, "data SplashActivity: " + data, Toast.LENGTH_LONG).show();
        }
//        updateLocation();



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }


    //GPS

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hãy bật vị trí GPS")
                .setCancelable(false)
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            user.setLatitude(location.getLatitude() + "");
                            user.setLongitude(location.getLongitude() + "");
                            new SessionManager(SplashActivity.this).setLatitude(user.getLatitude());
                            new SessionManager(SplashActivity.this).setLongitude(user.getLongitude());
                            done();
                        }
                    }
                });
            } else {
                OnGPS();
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            user.setLatitude(mLastLocation.getLatitude() + "");
            user.setLongitude(mLastLocation.getLongitude() + "");
            new SessionManager(SplashActivity.this).setLatitude(user.getLatitude());
            new SessionManager(SplashActivity.this).setLongitude(user.getLongitude());
            done();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_LOCATION);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void done() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                boolean isLoggedIn = new SessionManager(SplashActivity.this).isLoggedIn();
                token = new SessionManager(SplashActivity.this).getAccessToken();
                if (isLoggedIn && token != null) {
                    requestProfile();
//                    Intent service = new Intent(getApplicationContext(), SocketIOService.class);
//                    startService(service);

                    intent = new Intent(getApplicationContext(), Profile_Activity.class);
                } else {
                    intent = new Intent(getApplicationContext(), IntroductionMain.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }


    private void requestProfile() {
        RetrofitClient.getNetworkConfiguration().requestPostLocation("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        Toast.makeText(SplashActivity.this, "Đã xác định được ví trí của bạn", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(SplashActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(SplashActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostProfile", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(SplashActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostProfile", t.getMessage());

            }
        });
    }

}

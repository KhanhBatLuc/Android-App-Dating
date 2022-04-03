package com.example.dating.activity.Profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.ActivityProfileBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dating.R;
import com.example.dating.ui.PulsatorLayout;
import com.example.dating.ui.TopNavigationViewHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Profile_Activity extends AppCompatActivity {
    private static final String TAG = "Profile_Activity";
    ActivityProfileBinding binding;
    private static final int ACTIVITY_NUM = 0;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    CircleImageView imageView;
    Bitmap myBitmap;

    String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private Context mContext = Profile_Activity.this;
    private User user = new User();
    private String token = null;
    private String id = null;

    private RequestBody reqFile;
    private MultipartBody.Part body;
    private RequestBody name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: create the page");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        requestMultiplePermissions();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                id = new SessionManager(Profile_Activity.this).getUniqueIdentifier();
                token = new SessionManager(Profile_Activity.this).getAccessToken();
                requestAccount();
            }
        }, 500);

        setupTopNavigationView();
        binding.pulsator.start();

        binding.pulsator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.circleProfileImage;
                proceedAfterPermission();

                binding.pulsator.start();
            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Activity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resume to the page");

    }


    private void setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationView tvEx = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvEx);
        TopNavigationViewHelper.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void requestAccount() {
        Log.e("requestGetAccount", "Bearer " + token);
        RetrofitClient.getNetworkConfiguration().requestGetAccount("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        user = data.getData().getUser();

                        Glide.with(mContext).load(AppConfig.IMAGE_URL + user.getAvatar()).into(binding.circleProfileImage);
                        binding.profileName.setText(user.getName());
                        new SessionManager(Profile_Activity.this).setName(user.getName());
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
//                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(Profile_Activity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetAccount", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(Profile_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestGetAccount", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(Profile_Activity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestGetAccount", t.getMessage());
            }
        });
    }

    private boolean checkSelfPermission() {
        return ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Profile_Activity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermissions() {
        if (checkSelfPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Profile_Activity.this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Bộ nhớ.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Profile_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)
                    || permissionStatus.getBoolean(permissionsRequired[1], false)
                    || permissionStatus.getBoolean(permissionsRequired[2], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Bộ nhớ.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(), "Cấp quyền Camera và Vị trí.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(Profile_Activity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.putBoolean(permissionsRequired[1], true);
            editor.putBoolean(permissionsRequired[2], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {


        final CharSequence[] options = {"Chụp ảnh", "Chọn ảnh từ bộ sưu tập", "Hủy"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);

        builder.setTitle("Tải ảnh!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Chụp ảnh")) {

                    cameraIntent();

                } else if (options[item].equals("Chọn ảnh từ bộ sưu tập")) {

                    galleryIntent();


                } else if (options[item].equals("Hủy")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (checkSelfPermission()) {
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        uploadImage(thumbnail);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                uploadImage(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        imageView.setImageBitmap(bm);
    }

    private void uploadImage(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        imageView.setImageBitmap(thumbnail);
        File destination = new File(Environment.getExternalStorageDirectory(), id + System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();

            reqFile = RequestBody.create(MediaType.parse("image/*"), destination);
            body = MultipartBody.Part.createFormData("file", destination.getName(), reqFile);
            name = RequestBody.create(MediaType.parse("text/plain"), "file");
            requestPostAvatar();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (checkSelfPermission()) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


    private void requestPostAvatar() {
        RetrofitClient.getNetworkConfiguration().requestPostAvatar("Bearer " + token, body, name).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        user = data.getData().getUser();
                        Toast.makeText(Profile_Activity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(Profile_Activity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostAvatar", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(Profile_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostAvatar", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(Profile_Activity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostAvatar", t.getMessage());
            }
        });
    }

}
